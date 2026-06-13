import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { beforeEach, describe, expect, it } from 'vitest';
import App from './App';

describe('App', () => {
  beforeEach(() => localStorage.clear());

  async function openCombat(user: ReturnType<typeof userEvent.setup>) {
    render(<App />);
    await user.click(screen.getByRole('button', { name: 'Start Combat' }));
    expect(screen.getByRole('heading', { name: '전투' })).toBeInTheDocument();
  }

  /** Roll initial dice (ROLL_ANIM_MS=0 in Vitest → instant settle) */
  async function rollInitial(user: ReturnType<typeof userEvent.setup>) {
    await user.click(screen.getByRole('button', { name: /주사위 굴리기/ }));
    // After instant settle the Reroll button appears
    expect(screen.getByRole('button', { name: /리롤/ })).toBeInTheDocument();
  }

  /** Apply a forced dice fixture (bypasses animation, rollPhase→settled immediately) */
  async function forcePairFixture(user: ReturnType<typeof userEvent.setup>) {
    await user.click(screen.getByRole('button', { name: 'Force Pair Fixture' }));
    expect(screen.getByRole('button', { name: /Pair attack x1\.2/ })).toBeInTheDocument();
  }

  it('renders the Home screen and core status labels', () => {
    render(<App />);
    expect(screen.getByRole('heading', { name: 'Home' })).toBeInTheDocument();
    expect(screen.getAllByText('Stage')[0]).toBeInTheDocument();
    expect(screen.getAllByText('Coins')[0]).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Start Combat' })).toBeInTheDocument();
  });

  it('navigates to equipment and upgrade screens without a router', async () => {
    const user = userEvent.setup();
    render(<App />);
    await user.click(screen.getAllByRole('button', { name: 'Equipment' })[0]);
    expect(screen.getByRole('heading', { name: 'Equipment' })).toBeInTheDocument();
    await user.click(screen.getAllByRole('button', { name: 'Upgrade' })[0]);
    expect(screen.getByRole('heading', { name: 'Upgrade' })).toBeInTheDocument();
  });

  it('shows a single Roll button on combat entry — no dice yet', async () => {
    const user = userEvent.setup();
    await openCombat(user);

    // Before rolling: single "주사위 굴리기" button, no Reroll/리롤
    expect(screen.getByRole('button', { name: /주사위 굴리기/ })).toBeInTheDocument();
    expect(screen.queryByRole('button', { name: /리롤/ })).not.toBeInTheDocument();

    // Dice show "?" placeholders
    expect(screen.getAllByRole('button', { name: 'Empty die' })).toHaveLength(5);

    // Category buttons exist but are disabled (no dice yet)
    expect(screen.getByRole('button', { name: /Pair attack x1\.2/ })).toBeDisabled();
  });

  it('after rolling, dice values appear and Reroll button shows with count', async () => {
    const user = userEvent.setup();
    await openCombat(user);
    await rollInitial(user);

    // Roll button gone, Reroll shown
    expect(screen.queryByRole('button', { name: /주사위 굴리기/ })).not.toBeInTheDocument();
    expect(screen.getByRole('button', { name: /리롤.*2/ })).toBeInTheDocument();

    // Dice now show numeric values (at least one non-"Empty die")
    expect(screen.queryAllByRole('button', { name: 'Empty die' })).toHaveLength(0);
  });

  it('enables only valid categories for the forced pair fixture', async () => {
    const user = userEvent.setup();
    await openCombat(user);
    await forcePairFixture(user);

    // [2,2,2,2,5]: pair + fourKind + chance are valid; fullHouse and yahtzee are not
    expect(screen.getByRole('button', { name: /Pair attack x1\.2 → \d+/ })).toBeEnabled();
    expect(screen.getByRole('button', { name: /Four of a Kind attack x3\.2 → \d+/ })).toBeEnabled();
    expect(screen.getByRole('button', { name: /Full House attack x2\.5 - Not matched/ })).toBeDisabled();
    expect(screen.getByRole('button', { name: /Yahtzee attack x6 - Not matched/ })).toBeDisabled();
  });

  it('shows predicted damage on valid category buttons', async () => {
    const user = userEvent.setup();
    await openCombat(user);
    await forcePairFixture(user);

    // [2,2,2,2,5]: pipSum=13, attack=13, pair x1.2, diceBonus=0.05 → floor(26*1.2*1.05)=32
    expect(screen.getByRole('button', { name: /Pair attack x1\.2 → 32/ })).toBeInTheDocument();
  });

  it('resolves dice attack and shows dealt damage in feedback', async () => {
    const user = userEvent.setup();
    await openCombat(user);
    await forcePairFixture(user);

    await user.click(screen.getByRole('button', { name: /Pair attack x1\.2 → 32/ }));

    await waitFor(() => {
      expect(screen.getByText(/32 피해/)).toBeInTheDocument();
    });
  });

  it('basic attack button is enabled after rolling, disabled during rolling animation', async () => {
    const user = userEvent.setup();
    await openCombat(user);

    // In idle state: basic attack is enabled (can skip rolling)
    expect(screen.getByRole('button', { name: /Basic Attack/ })).toBeEnabled();

    // After rolling: still enabled
    await rollInitial(user);
    expect(screen.getByRole('button', { name: /Basic Attack/ })).toBeEnabled();
  });

  it('reroll button decrements counter and disables at 0', async () => {
    const user = userEvent.setup();
    await openCombat(user);
    await rollInitial(user); // rollsLeft: 3→2

    expect(screen.getByText(/리롤 \(2회 남음\)/)).toBeInTheDocument();
    const rerollBtn = screen.getByRole('button', { name: /리롤/ });
    expect(rerollBtn).toBeEnabled();

    await user.click(rerollBtn); // 2→1
    expect(screen.getByText(/리롤 \(1회 남음\)/)).toBeInTheDocument();

    await user.click(rerollBtn); // 1→0
    expect(screen.getByText(/리롤 \(0회 남음\)/)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /리롤/ })).toBeDisabled();
  });

  it('held dice do not change on reroll', async () => {
    const user = userEvent.setup();
    await openCombat(user);
    await forcePairFixture(user);

    const firstDie = screen.getAllByRole('button', { name: /^Die [1-6]$/ })[0];
    const valueBefore = firstDie.textContent;

    await user.click(firstDie); // hold it
    expect(screen.getAllByRole('button', { name: /Die .* held/ })).toHaveLength(1);

    await user.click(screen.getByRole('button', { name: /리롤/ }));

    const heldDieAfter = screen.getAllByRole('button', { name: /Die .* held/ })[0];
    expect(heldDieAfter.textContent).toBe(valueBefore);
  });

  it('force win transitions to result screen', async () => {
    const user = userEvent.setup();
    await openCombat(user);

    await user.click(screen.getByRole('button', { name: 'Force Win' }));
    await waitFor(() => {
      expect(screen.getByRole('heading', { name: 'Result' })).toBeInTheDocument();
    });
    await user.click(screen.getByRole('button', { name: 'Claim Reward' }));
    expect(screen.getByRole('heading', { name: 'Home' })).toBeInTheDocument();
  });
});
