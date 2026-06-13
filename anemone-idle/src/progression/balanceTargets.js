export const CHAPTER1_BALANCE_TARGETS = [
  {
    id: 'target-60s-interns',
    timeSeconds: 60,
    metric: 'producer.driftPolyps',
    min: 3,
    source: 'docs/advanced_roadmap.md:272-277'
  },
  {
    id: 'target-180s-shrimp-min',
    timeSeconds: 180,
    metric: 'producer.cleanerShrimp',
    min: 2,
    source: 'docs/advanced_roadmap.md:127-137'
  },
  {
    id: 'target-180s-shrimp-max',
    timeSeconds: 180,
    metric: 'producer.cleanerShrimp',
    max: 4,
    source: 'docs/advanced_roadmap.md:272-278'
  },
  {
    id: 'target-300s-crab-wall-visible',
    timeSeconds: 300,
    metric: 'canSeeCrabWall',
    min: 1,
    source: 'docs/advanced_roadmap.md:135,278'
  },
  {
    id: 'target-900s-first-crab',
    timeSeconds: 900,
    metric: 'producer.crabBranchBoss',
    min: 1,
    source: 'docs/advanced_roadmap.md:137,281'
  },
  {
    id: 'target-900s-production-cap',
    timeSeconds: 900,
    metric: 'rate.plankton',
    max: 20000,
    source: 'docs/advanced_roadmap.md:282'
  }
];

export function validateBalanceTargets(targets = CHAPTER1_BALANCE_TARGETS) {
  return targets.flatMap((target) => {
    const errors = [];
    if (!target.id) errors.push('invalid balance target: missing id');
    if (!Number.isFinite(target.timeSeconds) || target.timeSeconds < 0) errors.push(`invalid balance target: ${target.id}`);
    if (!target.metric) errors.push(`invalid balance target metric: ${target.id}`);
    if (!target.source) errors.push(`invalid balance target source: ${target.id}`);
    if ('min' in target && !Number.isFinite(target.min)) errors.push(`invalid balance target min: ${target.id}`);
    if ('max' in target && !Number.isFinite(target.max)) errors.push(`invalid balance target max: ${target.id}`);
    if (!('min' in target) && !('max' in target)) errors.push(`invalid balance target threshold: ${target.id}`);
    return errors;
  });
}
