import { describe, expect, it } from 'vitest';
import { CATEGORY_ORDER, type Dice, type Scorecard } from './categories';
import { createInitialGame, getWinner, isGameOver, rollDice, scoreTurn, toggleHold } from './state';
import type { GameState } from './types';

describe('game state', () => {
  it('runs a human turn with held dice and then switches to bot', () => {
    const firstRoll: Dice = [1, 2, 3, 4, 5];
    const secondRoll: Dice = [6, 6, 6, 6, 6];
    const rolled = rollDice(createInitialGame(), () => firstRoll).state;
    const held = toggleHold(rolled, 0).state;
    const rerolled = rollDice(held, () => secondRoll).state;

    expect(rerolled.dice).toEqual([1, 6, 6, 6, 6]);
    expect(rerolled.rollCount).toBe(2);

    const scored = scoreTurn(rerolled, 'fourKind');

    expect(scored.ok).toBe(true);
    expect(scored.state.players.human.scorecard.fourKind).toBe(25);
    expect(scored.state.activePlayer).toBe('bot');
    expect(scored.state.rollCount).toBe(0);
    expect(scored.state.dice).toBeNull();
  });

  it('rejects a fourth roll', () => {
    const dice: Dice = [1, 1, 1, 1, 1];
    const one = rollDice(createInitialGame(), () => dice).state;
    const two = rollDice(one, () => dice).state;
    const three = rollDice(two, () => dice).state;
    const rejected = rollDice(three, () => dice);

    expect(rejected.ok).toBe(false);
    expect(rejected.error).toContain('최대 세 번');
    expect(rejected.state).toBe(three);
  });

  it('rejects scoring before rolling', () => {
    const result = scoreTurn(createInitialGame(), 'choice');

    expect(result.ok).toBe(false);
    expect(result.error).toContain('한 번 이상');
  });

  it('rejects already used categories', () => {
    const rolled = rollDice(createInitialGame(), () => [2, 2, 2, 2, 2]).state;
    const scored = scoreTurn(rolled, 'yacht').state;
    const botRolled = rollDice(scored, () => [1, 1, 1, 1, 1]).state;
    const botScored = scoreTurn(botRolled, 'choice').state;
    const humanRolled = rollDice(botScored, () => [3, 3, 3, 3, 3]).state;
    const rejected = scoreTurn(humanRolled, 'yacht');

    expect(rejected.ok).toBe(false);
    expect(rejected.error).toContain('이미 사용');
  });

  it('rejects hold toggles before rolling and invalid indexes', () => {
    expect(toggleHold(createInitialGame(), 0).ok).toBe(false);

    const rolled = rollDice(createInitialGame(), () => [1, 2, 3, 4, 5]).state;

    expect(toggleHold(rolled, 5).ok).toBe(false);
  });

  it('detects game over and winner after all categories are filled', () => {
    const state = createFilledState(filledScorecard(1), filledScorecard(2));

    expect(isGameOver(state)).toBe(true);
    expect(getWinner(state)).toBe('bot');
  });

  it('handles tied final scores', () => {
    const state = createFilledState(filledScorecard(3), filledScorecard(3));

    expect(getWinner(state)).toBe('tie');
  });
});

function filledScorecard(score: number): Scorecard {
  return Object.fromEntries(CATEGORY_ORDER.map((category) => [category, score]));
}

function createFilledState(human: Scorecard, bot: Scorecard): GameState {
  return {
    ...createInitialGame(),
    players: {
      human: { id: 'human', name: '나', scorecard: human },
      bot: { id: 'bot', name: '봇', scorecard: bot }
    },
    phase: 'gameOver'
  };
}
