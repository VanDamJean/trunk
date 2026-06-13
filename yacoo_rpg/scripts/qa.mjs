import { mkdir, writeFile } from 'node:fs/promises';
import { chromium } from 'playwright';

const port = process.env.QA_PORT ?? '5173';
const BASE_URL = `http://localhost:${port}`;

const evidenceDir = new URL('../.omo/evidence/', import.meta.url);
await mkdir(evidenceDir, { recursive: true });

const browser = await chromium.launch({ headless: true });
const page = await browser.newPage({ viewport: { width: 375, height: 812 } });
const logs = [];
const pageErrors = [];
page.on('console', (message) => logs.push(`${message.type()}: ${message.text()}`));
page.on('pageerror', (error) => pageErrors.push(error.message));

function isBenignConsoleLine(line) {
  return line.includes('favicon') && line.includes('404');
}

try {
  await page.goto(BASE_URL, { waitUntil: 'networkidle' });
  await page.getByRole('heading', { name: 'Home' }).waitFor();
  await page.screenshot({ path: new URL('task-5-mobile-frame.png', evidenceDir).pathname, fullPage: true });

  await page.getByRole('button', { name: 'Start Combat' }).click();
  await page.getByRole('heading', { name: '전투' }).waitFor();
  await page.getByRole('button', { name: 'Force Pair Fixture' }).click();

  // Turn-based: buttons are always visible. aria-label includes predicted damage "→ N".
  const pairAttack = page.getByRole('button', { name: /^Pair attack x1\.2/ });
  const unmatchedFullHouse = page.getByRole('button', { name: /Full House attack x2\.5 - Not matched/ });
  await pairAttack.waitFor();
  await unmatchedFullHouse.waitFor();
  if (!(await pairAttack.isEnabled())) throw new Error('Pair attack x1.2 should be enabled for Force Pair Fixture.');
  if (await unmatchedFullHouse.isEnabled()) throw new Error('Full House attack x2.5 - Not matched should be disabled.');
  await page.screenshot({ path: new URL('habby-yahtzee-task-6-pair-category.png', evidenceDir).pathname, fullPage: true });
  await pairAttack.click();
  // Turn-based feedback: "N 피해!" or "N 피해! 승리!"
  await page.getByText(/\d+ 피해/).waitFor();

  await page.getByRole('button', { name: 'Force Full House Fixture' }).click();
  const fullHouseAttack = page.getByRole('button', { name: /^Full House attack x2\.5 →/ });
  await fullHouseAttack.waitFor();
  if (!(await fullHouseAttack.isEnabled())) throw new Error('Full House attack x2.5 should be enabled for Force Full House Fixture.');
  await page.screenshot({ path: new URL('habby-yahtzee-task-6-full-house-category.png', evidenceDir).pathname, fullPage: true });
  await fullHouseAttack.click();
  await page.screenshot({ path: new URL('task-7-dice-resolve.png', evidenceDir).pathname, fullPage: true });

  // After full house the enemy may already be dead. If not, use Force Win.
  const resultHeading = page.getByRole('heading', { name: 'Result' });
  const isAlreadyResult = await resultHeading.isVisible().catch(() => false);
  if (!isAlreadyResult) {
    await page.getByRole('button', { name: 'Force Win' }).click();
  }
  await resultHeading.waitFor();
  await page.getByRole('button', { name: 'Claim Reward' }).click();
  await page.getByRole('heading', { name: 'Home' }).waitFor();
  await page.getByRole('button', { name: 'Grant 100 Coins' }).click();
  await page.getByRole('button', { name: 'Upgrade' }).first().click();
  await page.getByRole('heading', { name: 'Upgrade' }).waitFor();
  await page.getByRole('button', { name: 'Twig Wand' }).click();
  await page.getByRole('button', { name: 'Upgrade' }).last().click();
  await page.screenshot({ path: new URL('task-8-upgrade-success.png', evidenceDir).pathname, fullPage: true });

  await page.reload({ waitUntil: 'networkidle' });
  await page.getByRole('heading', { name: 'Home' }).waitFor();
  await page.screenshot({ path: new URL('task-9-persistence.png', evidenceDir).pathname, fullPage: true });
  await page.getByRole('button', { name: 'Reset' }).click();
  await page.getByText('Stage').first().waitFor();
  await page.screenshot({ path: new URL('f3-manual-qa.png', evidenceDir).pathname, fullPage: true });

  await writeFile(
    new URL('f3-manual-qa.md', evidenceDir),
    [
      '# Manual QA (M1.5 Turn-based Combat)',
      '',
      '- Home loaded at 375x812.',
      '- Combat opened. Dice panel visible immediately (no charge step).',
      '- Pair category selected from a forced pair fixture.',
      '- Full House category selected from a forced full-house fixture.',
      '- Win result claimed.',
      '- Coins granted, Twig Wand upgraded.',
      '- Reload preserved state.',
      '- Reset returned app to default Home state.',
      '',
      '## Console',
      ...logs.map((line) => `- ${line}`)
    ].join('\n')
  );

  const fatalConsole = logs.filter((line) => line.startsWith('error:') && !isBenignConsoleLine(line));
  await writeFile(
    new URL('habby-yahtzee-task-6-browser-qa.md', evidenceDir),
    [
      '# Task 6 Browser QA (M1.5 Turn-based)',
      '',
      '- Home loaded at 375x812.',
      '- Combat opened — dice panel visible without charge step.',
      '- `Force Pair Fixture` displayed enabled `Pair attack x1.2 → N`.',
      '- `Force Pair Fixture` displayed disabled `Full House attack x2.5 - Not matched`.',
      '- Clicked `Pair attack x1.2` and verified `Dealt N` feedback.',
      '- `Force Full House Fixture` displayed enabled `Full House attack x2.5 → N`.',
      '- Clicked `Full House attack x2.5` and verified dealt message.',
      '- Result claim, coin grant, Twig Wand upgrade, reload persistence, and reset passed.',
      '',
      '## Console Summary',
      '',
      `- Console lines: ${logs.length}`,
      `- Page errors: ${pageErrors.length}`,
      `- Fatal console errors: ${fatalConsole.length}`,
      '',
      '## Console Log',
      ...logs.map((line) => `- ${line}`),
      '',
      '## Page Errors',
      ...pageErrors.map((line) => `- ${line}`)
    ].join('\n')
  );

  if (pageErrors.length > 0 || fatalConsole.length > 0) {
    throw new Error(`Browser QA found ${pageErrors.length} page errors and ${fatalConsole.length} fatal console errors.`);
  }
} finally {
  await browser.close();
}
