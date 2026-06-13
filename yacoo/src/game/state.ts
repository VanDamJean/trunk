import { CATEGORY_ORDER, type CategoryId } from './categories';
import { EMPTY_HELD, mergeHeldDice, rollAllDice, type DiceRoller } from './dice';
import { scoreCategory, totalScore } from './scoring';
import type { GameResult, GameState, PlayerId, Winner } from './types';

const INITIAL_LOG = ['새 게임을 시작했습니다.'];

export function createInitialGame(): GameState {
  return {
    players: {
      human: { id: 'human', name: '나', scorecard: {} },
      bot: { id: 'bot', name: '봇', scorecard: {} }
    },
    activePlayer: 'human',
    dice: null,
    held: EMPTY_HELD,
    rollCount: 0,
    phase: 'ready',
    log: INITIAL_LOG
  };
}

export function rollDice(state: GameState, roller: DiceRoller = rollAllDice): GameResult {
  if (state.phase === 'gameOver') {
    return fail(state, '게임이 이미 끝났습니다.');
  }

  if (state.rollCount >= 3) {
    return fail(state, '한 턴에는 최대 세 번만 굴릴 수 있습니다.');
  }

  const nextRoll = roller();
  const dice = state.dice ? mergeHeldDice(state.dice, nextRoll, state.held) : nextRoll;
  const rollCount = state.rollCount + 1;

  return succeed({
    ...state,
    dice,
    rollCount,
    phase: 'selectingCategory',
    log: [...state.log, `${playerName(state.activePlayer)} ${rollCount}번째 굴림: ${dice.join(', ')}`]
  });
}

export function toggleHold(state: GameState, index: number): GameResult {
  if (!state.dice || state.rollCount === 0) {
    return fail(state, '굴린 뒤에만 주사위를 고정할 수 있습니다.');
  }

  if (state.phase !== 'selectingCategory') {
    return fail(state, '지금은 주사위를 고정할 수 없습니다.');
  }

  if (!Number.isInteger(index) || index < 0 || index > 4) {
    return fail(state, '주사위 위치가 올바르지 않습니다.');
  }

  const held = state.held.map((value, heldIndex) => (heldIndex === index ? !value : value));

  return succeed({
    ...state,
    held: toHeldTuple(held),
    log: [...state.log, `${index + 1}번 주사위 ${held[index] ? '고정' : '해제'}`]
  });
}

export function scoreTurn(state: GameState, category: CategoryId): GameResult {
  if (!state.dice || state.rollCount === 0) {
    return fail(state, '점수는 한 번 이상 굴린 뒤에만 기록할 수 있습니다.');
  }

  if (state.phase !== 'selectingCategory') {
    return fail(state, '지금은 점수를 기록할 수 없습니다.');
  }

  const currentPlayer = state.players[state.activePlayer];

  if (currentPlayer.scorecard[category] !== undefined) {
    return fail(state, '이미 사용한 칸입니다.');
  }

  const score = scoreCategory(category, state.dice);
  const updatedPlayers = {
    ...state.players,
    [state.activePlayer]: {
      ...currentPlayer,
      scorecard: { ...currentPlayer.scorecard, [category]: score }
    }
  };
  const scoredState: GameState = {
    ...state,
    players: updatedPlayers,
    log: [...state.log, `${currentPlayer.name} ${category} ${score}점 기록`]
  };

  if (areAllScorecardsFilled(scoredState)) {
    return succeed({
      ...scoredState,
      dice: null,
      held: EMPTY_HELD,
      rollCount: 0,
      phase: 'gameOver',
      log: [...scoredState.log, gameOverMessage(scoredState)]
    });
  }

  return succeed(advanceTurn(scoredState));
}

export function advanceTurn(state: GameState): GameState {
  const activePlayer = otherPlayer(state.activePlayer);

  return {
    ...state,
    activePlayer,
    dice: null,
    held: EMPTY_HELD,
    rollCount: 0,
    phase: 'ready',
    log: [...state.log, `${playerName(activePlayer)} 차례입니다.`]
  };
}

export function isGameOver(state: GameState): boolean {
  return state.phase === 'gameOver' || areAllScorecardsFilled(state);
}

export function getWinner(state: GameState): Winner | null {
  if (!isGameOver(state)) {
    return null;
  }

  const humanScore = totalScore(state.players.human.scorecard);
  const botScore = totalScore(state.players.bot.scorecard);

  if (humanScore === botScore) {
    return 'tie';
  }

  return humanScore > botScore ? 'human' : 'bot';
}

function areAllScorecardsFilled(state: GameState): boolean {
  return (['human', 'bot'] as const).every((player) =>
    CATEGORY_ORDER.every((category) => state.players[player].scorecard[category] !== undefined)
  );
}

function otherPlayer(player: PlayerId): PlayerId {
  return player === 'human' ? 'bot' : 'human';
}

function playerName(player: PlayerId): string {
  return player === 'human' ? '나' : '봇';
}

function gameOverMessage(state: GameState): string {
  const winner = getWinner({ ...state, phase: 'gameOver' });

  if (winner === 'tie') {
    return '무승부입니다.';
  }

  return `${playerName(winner ?? 'human')} 승리입니다.`;
}

function succeed(state: GameState): GameResult {
  return { state, ok: true };
}

function fail(state: GameState, error: string): GameResult {
  return { state, ok: false, error };
}

function toHeldTuple(values: boolean[]): readonly [boolean, boolean, boolean, boolean, boolean] {
  return [values[0] ?? false, values[1] ?? false, values[2] ?? false, values[3] ?? false, values[4] ?? false];
}
