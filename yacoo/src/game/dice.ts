import type { Dice, DieValue } from './categories';

export type RandomSource = () => number;
export type DiceRoller = () => Dice;

export const EMPTY_HELD = [false, false, false, false, false] as const;

export function rollDie(random: RandomSource = Math.random): DieValue {
  return (Math.floor(random() * 6) + 1) as DieValue;
}

export function rollAllDice(random: RandomSource = Math.random): Dice {
  return [rollDie(random), rollDie(random), rollDie(random), rollDie(random), rollDie(random)];
}

export function mergeHeldDice(current: Dice, next: Dice, held: readonly boolean[]): Dice {
  return [
    held[0] ? current[0] : next[0],
    held[1] ? current[1] : next[1],
    held[2] ? current[2] : next[2],
    held[3] ? current[3] : next[3],
    held[4] ? current[4] : next[4]
  ];
}
