import { describe, expect, it } from 'vitest';
import { QUEST_DEFINITIONS } from '../src/progression/content.js';
import { validateProgressionContent } from '../src/progression/validators.js';

describe('progression content', () => {
  it('validates chapter 1 progression content', () => {
    expect(validateProgressionContent()).toEqual([]);
    expect(QUEST_DEFINITIONS.map((quest) => quest.id)).toEqual(expect.arrayContaining([
      'quest-first-interns',
      'quest-first-shrimp',
      'quest-first-capsule',
      'quest-first-upgrade',
      'quest-first-crab',
      'quest-chapter1-complete'
    ]));
  });

  it('rejects duplicate quest ids and missing references', () => {
    const duplicate = { ...QUEST_DEFINITIONS[0] };
    const missing = { ...QUEST_DEFINITIONS[1], id: 'quest-missing', completionConditions: [{ type: 'producerCount', producerId: 'missingProducerId', target: 1 }] };
    const errors = validateProgressionContent({ quests: [...QUEST_DEFINITIONS, duplicate, missing] });

    expect(errors.join('\n')).toContain('duplicate quest id');
    expect(errors.join('\n')).toContain('missingProducerId');
  });
});
