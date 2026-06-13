import { expect, test } from '@playwright/test';

test('renders Yachoo starter page', async ({ page }) => {
  await page.goto('/');

  await expect(page.getByRole('heading', { name: '야추' })).toBeVisible();
  await expect(page.locator('.score-panel')).toBeVisible();
  await expect(page.locator('.dice-panel')).toBeVisible();
  await expect(page.getByLabel('남은 굴림 3회')).toContainText('3 left');
});

test('human can roll, hold, score, and trigger bot turn', async ({ page }) => {
  await page.addInitScript(() => {
    Object.assign(window, {
      __YACOO_TEST_ROLLS__: [
        [1, 2, 3, 4, 5],
        [6, 6, 6, 6, 6],
        [6, 6, 6, 2, 1],
        [6, 6, 6, 5, 4],
        [6, 6, 6, 6, 1]
      ]
    });
  });
  await page.goto('/');

  await expect(page.getByLabel('에이스 대기')).toBeDisabled();
  await page.getByRole('button', { name: '굴리기' }).click();
  await expect(page.getByRole('button', { name: /\d번 주사위 [1-6]/ })).toHaveCount(5);
  await expect(page.locator('.die-cube')).toHaveCount(5);
  await expect(page.locator('.die-pip')).toHaveCount(105);
  const firstDie = page.getByRole('button', { name: '1번 주사위 1' });
  await expect(firstDie).toHaveAttribute('data-value', '1');
  await firstDie.click();
  await expect(firstDie).toHaveAttribute('aria-pressed', 'true');
  await expect(firstDie).toHaveAttribute('data-held', 'true');
  await page.getByRole('button', { name: '굴리기' }).click();
  await page.getByLabel(/초이스 \d+점 기록/).click();

  await expect(page.getByText('현재 차례').locator('..')).toContainText('나');
});

test('reset clears an active match', async ({ page }) => {
  await page.goto('/');

  await page.getByRole('button', { name: '굴리기' }).click();
  await page.getByRole('button', { name: '새 게임' }).click();

  await expect(page.getByText('현재 차례').locator('..')).toContainText('나');
  await expect(page.getByText('굴림').locator('..')).toContainText('0 / 3');
  await expect(page.getByLabel('에이스 대기')).toBeDisabled();
});

test('mobile layout keeps main controls visible', async ({ page }) => {
  await page.setViewportSize({ width: 375, height: 812 });
  await page.goto('/');
  await page.getByRole('button', { name: '굴리기' }).click();

  await expect(page.getByRole('heading', { name: '야추' })).toBeVisible();
  await expect(page.getByRole('button', { name: '굴리기' })).toBeVisible();
  await expect(page.getByLabel('주사위 고정 선택')).toBeVisible();
  await expect(page.locator('.score-panel')).toBeVisible();
  await expect(page.locator('.dice-panel')).toBeVisible();
  await expect(page.locator('.die-cube')).toHaveCount(5);
  await expect(page.getByRole('table', { name: '야추 점수판' })).toBeVisible();
  await expect(page.getByRole('button', { name: '새 게임' }).first()).toBeVisible();
  expect(await page.evaluate(() => document.documentElement.scrollWidth <= window.innerWidth)).toBe(true);
});

test('reduced motion keeps dice static while rolling state is accessible', async ({ page }) => {
  await page.emulateMedia({ reducedMotion: 'reduce' });
  await page.addInitScript(() => {
    Object.assign(window, { __YACOO_TEST_ROLLS__: [[1, 2, 3, 4, 5]] });
  });
  await page.goto('/');

  await page.getByRole('button', { name: '굴리기' }).click();
  const firstDie = page.getByRole('button', { name: '1번 주사위 1' });

  await expect(firstDie).toHaveAttribute('data-rolling', 'true');
  expect(await firstDie.evaluate((element) => getComputedStyle(element).animationName)).toBe('none');
  expect(await firstDie.locator('.die-cube').evaluate((element) => getComputedStyle(element).animationName)).toBe('none');
});

test('keyboard can focus controls and toggle held state', async ({ page }) => {
  await page.addInitScript(() => {
    Object.assign(window, { __YACOO_TEST_ROLLS__: [[1, 2, 3, 4, 5]] });
  });
  await page.goto('/');

  await page.keyboard.press('Tab');
  await page.keyboard.press('Enter');
  await page.keyboard.press('Tab');
  await page.keyboard.press('Tab');
  await page.keyboard.press('Enter');

  await expect(page.getByRole('button', { name: '1번 주사위 1' })).toHaveAttribute('aria-pressed', 'true');
});

test('near-game-over state can finish and reset', async ({ page }) => {
  await page.addInitScript(() => {
    const filled = {
      twos: 2,
      threes: 2,
      fours: 2,
      fives: 2,
      sixes: 2,
      choice: 2,
      fourKind: 2,
      fullHouse: 2,
      smallStraight: 2,
      largeStraight: 2,
      yacht: 2
    };
    Object.assign(window, {
      __YACOO_TEST_ROLLS__: [[1, 1, 1, 1, 1]],
      __YACOO_PRELOADED_STATE__: {
        players: {
          human: { id: 'human', name: '나', scorecard: filled },
          bot: { id: 'bot', name: '봇', scorecard: { ones: 2, ...filled } }
        },
        activePlayer: 'human',
        dice: null,
        held: [false, false, false, false, false],
        rollCount: 0,
        phase: 'ready',
        log: ['테스트 종료 직전 상태입니다.']
      }
    });
  });
  await page.goto('/');

  await page.getByRole('button', { name: '굴리기' }).click();
  await page.getByLabel(/에이스 \d+점 기록/).click();
  await expect(page.getByRole('heading', { name: /승리입니다|무승부입니다/ })).toBeVisible();
  await page.getByRole('button', { name: '새 게임' }).first().click();
  await expect(page.getByText('굴림').locator('..')).toContainText('0 / 3');
});
