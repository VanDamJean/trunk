import { mkdirSync, writeFileSync } from 'node:fs';
import { validateProgressionContent } from '../src/progression/validators.js';

const errors = validateProgressionContent();
const output = {
  valid: errors.length === 0,
  errors,
  checkedAt: new Date().toISOString()
};

mkdirSync('.omo/evidence', { recursive: true });
writeFileSync('.omo/evidence/progression-content-validation.json', `${JSON.stringify(output, null, 2)}\n`);

if (errors.length > 0) {
  console.error(errors.join('\n'));
  process.exit(1);
}

console.log('progression content valid');
