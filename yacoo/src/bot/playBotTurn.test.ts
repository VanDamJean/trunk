import { describe, expect, it } from 'vitest';
import { CATEGORY_ORDER, type Dice, type Scorecard } from '../game/categories';
import { createInitialGame, rollDice, scoreTurn } from '../game/state';
import type { GameState } from '../game/types';
import { playBotTurn } from './playBotTurn';

describe('playBotTurn', () => {
  it('automates one legal bot turn and returns control to the human', () => {
    const humanRolled = rollDice(createInitialGame(), () => [1, 2, 3, 4, 5]).state;
    const botStart = scoreTurn(humanRolled, 'largeStraight').state;
    const result = playBotTurn(botStart, sequenceRoller([[6, 6, 6, 2, 1], [6, 6, 6, 5, 4], [6, 6, 6, 6, 1]]));

    expect(result.ok).toBe(true);
    expect(result.state.activePlayer).toBe('human');
    expect(result.state.players.bot.scorecard.fourKind).toBe(25);
    expect(result.state.log.some((entry) => entry.includes('봇'))).toBe(true);
  });

  it('scores the only remaining bot category and ends the game when scorecards are full', () => {
    const botScorecard = filledScorecard(2, ['ones']);
    const humanScorecard = filledScorecard(2, []);
    const state: GameState = {
      ...createInitialGame(),
      players: {
        human: { id: 'human', name: '나', scorecard: humanScorecard },
        bot: { id: 'bot', name: '봇', scorecard: botScorecard }
      },
      activePlayer: 'bot'
    };
    const result = playBotTurn(state, sequenceRoller([[1, 1, 1, 1, 1]]));

    expect(result.ok).toBe(true);
    expect(result.state.players.bot.scorecard.ones).toBe(5);
    expect(result.state.phase).toBe('gameOver');
  });
});

function sequenceRoller(sequence: readonly Dice[]) {
  const fallback = sequence[sequence.length - 1];
  if (!fallback) {
    throw new Error('Dice sequence must not be empty');
  }

  let index = 0;

  return () => {
    const dice = sequence[index] ?? fallback;
    index += 1;
    return dice;
  };
}

function filledScorecard(score: number, omitted: readonly string[]): Scorecard {
  return Object.fromEntries(CATEGORY_ORDER.filter((category) => !omitted.includes(category)).map((category) => [category, score]));
}
