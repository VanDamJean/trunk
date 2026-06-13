import { describe, expect, it } from 'vitest';
import type { Dice } from './categories';
import { isCategorySatisfied, scoreCategory, totalScore } from './scoring';

describe('scoreCategory', () => {
  it('scores upper categories by matching dice sum', () => {
    const dice: Dice = [1, 2, 2, 4, 6];

    expect(scoreCategory('ones', dice)).toBe(1);
    expect(scoreCategory('twos', dice)).toBe(4);
    expect(scoreCategory('threes', dice)).toBe(0);
    expect(scoreCategory('fours', dice)).toBe(4);
    expect(scoreCategory('fives', dice)).toBe(0);
    expect(scoreCategory('sixes', dice)).toBe(6);
  });

  it('scores choice as all dice', () => {
    expect(scoreCategory('choice', [2, 2, 3, 4, 5])).toBe(16);
  });

  it('scores four of a kind only when a face appears at least four times', () => {
    expect(scoreCategory('fourKind', [6, 6, 6, 6, 1])).toBe(25);
    expect(scoreCategory('fourKind', [6, 6, 6, 6, 6])).toBe(30);
    expect(scoreCategory('fourKind', [1, 1, 1, 2, 3])).toBe(0);
  });

  it('scores full house only for exact three plus two counts', () => {
    expect(scoreCategory('fullHouse', [2, 2, 3, 3, 3])).toBe(13);
    expect(scoreCategory('fullHouse', [6, 6, 6, 6, 6])).toBe(0);
    expect(scoreCategory('fullHouse', [4, 4, 4, 4, 1])).toBe(0);
  });

  it('scores small straight for any four-long sequence and handles duplicates', () => {
    expect(scoreCategory('smallStraight', [1, 2, 3, 4, 4])).toBe(15);
    expect(scoreCategory('smallStraight', [2, 3, 4, 5, 2])).toBe(15);
    expect(scoreCategory('smallStraight', [6, 3, 4, 5, 6])).toBe(15);
    expect(scoreCategory('smallStraight', [1, 1, 2, 3, 5])).toBe(0);
  });

  it('scores large straight only for exact one through five or two through six sets', () => {
    expect(scoreCategory('largeStraight', [1, 2, 3, 4, 5])).toBe(30);
    expect(scoreCategory('largeStraight', [2, 3, 4, 5, 6])).toBe(30);
    expect(scoreCategory('largeStraight', [1, 2, 3, 4, 6])).toBe(0);
  });

  it('scores yacht only for five matching dice', () => {
    expect(scoreCategory('yacht', [6, 6, 6, 6, 6])).toBe(50);
    expect(scoreCategory('yacht', [6, 6, 6, 6, 5])).toBe(0);
  });
});

describe('isCategorySatisfied', () => {
  it('reports condition categories by their actual pattern', () => {
    expect(isCategorySatisfied('choice', [1, 1, 1, 1, 1])).toBe(true);
    expect(isCategorySatisfied('fullHouse', [1, 1, 1, 2, 2])).toBe(true);
    expect(isCategorySatisfied('fullHouse', [1, 1, 1, 1, 1])).toBe(false);
    expect(isCategorySatisfied('largeStraight', [1, 2, 3, 4, 6])).toBe(false);
  });
});

describe('totalScore', () => {
  it('sums only filled scorecard values', () => {
    expect(totalScore({ ones: 3, choice: 20, yacht: 50 })).toBe(73);
  });
});
