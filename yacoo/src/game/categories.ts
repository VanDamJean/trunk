export const UPPER_CATEGORY_IDS = ['ones', 'twos', 'threes', 'fours', 'fives', 'sixes'] as const;

export const COMBINATION_CATEGORY_IDS = [
  'choice',
  'fourKind',
  'fullHouse',
  'smallStraight',
  'largeStraight',
  'yacht'
] as const;

export const CATEGORY_ORDER = [...UPPER_CATEGORY_IDS, ...COMBINATION_CATEGORY_IDS] as const;

export type UpperCategoryId = (typeof UPPER_CATEGORY_IDS)[number];
export type CombinationCategoryId = (typeof COMBINATION_CATEGORY_IDS)[number];
export type CategoryId = (typeof CATEGORY_ORDER)[number];
export type DieValue = 1 | 2 | 3 | 4 | 5 | 6;
export type Dice = readonly [DieValue, DieValue, DieValue, DieValue, DieValue];
export type Scorecard = Partial<Record<CategoryId, number>>;

export type CategoryInfo = {
  id: CategoryId;
  label: string;
  description: string;
};

export const CATEGORY_INFO: Record<CategoryId, CategoryInfo> = {
  ones: { id: 'ones', label: '에이스', description: '1 눈의 합' },
  twos: { id: 'twos', label: '듀스', description: '2 눈의 합' },
  threes: { id: 'threes', label: '트레이', description: '3 눈의 합' },
  fours: { id: 'fours', label: '포스', description: '4 눈의 합' },
  fives: { id: 'fives', label: '파이브', description: '5 눈의 합' },
  sixes: { id: 'sixes', label: '식스', description: '6 눈의 합' },
  choice: { id: 'choice', label: '초이스', description: '모든 주사위 눈의 합' },
  fourKind: { id: 'fourKind', label: '포카인드', description: '같은 눈 4개 이상이면 모든 눈의 합' },
  fullHouse: { id: 'fullHouse', label: '풀하우스', description: '정확히 3개와 2개 조합이면 모든 눈의 합' },
  smallStraight: { id: 'smallStraight', label: '스몰 스트레이트', description: '연속된 눈 4개면 15점' },
  largeStraight: { id: 'largeStraight', label: '라지 스트레이트', description: '1-5 또는 2-6이면 30점' },
  yacht: { id: 'yacht', label: '요트', description: '같은 눈 5개면 50점' }
};

export function isUpperCategory(category: CategoryId): category is UpperCategoryId {
  return UPPER_CATEGORY_IDS.includes(category as UpperCategoryId);
}

export function categoryFace(category: UpperCategoryId): DieValue {
  return (UPPER_CATEGORY_IDS.indexOf(category) + 1) as DieValue;
}
