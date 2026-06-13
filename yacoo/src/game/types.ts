import type { CategoryId, Dice, Scorecard } from './categories';

export type PlayerId = 'human' | 'bot';
export type GamePhase = 'ready' | 'selectingCategory' | 'gameOver';
export type Winner = PlayerId | 'tie';

export type PlayerState = {
  id: PlayerId;
  name: string;
  scorecard: Scorecard;
};

export type GameState = {
  players: Record<PlayerId, PlayerState>;
  activePlayer: PlayerId;
  dice: Dice | null;
  held: readonly [boolean, boolean, boolean, boolean, boolean];
  rollCount: number;
  phase: GamePhase;
  log: readonly string[];
};

export type GameResult = {
  state: GameState;
  ok: boolean;
  error?: string;
};

export type ScoreAction = {
  category: CategoryId;
};
