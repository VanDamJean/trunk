import { describe, expect, it } from 'vitest';
import { readFileSync } from 'node:fs';

const mainSource = readFileSync(new URL('../main.js', import.meta.url), 'utf8');
const imagePlan = readFileSync(new URL('../IMAGE_TO_PUZZLE_PLAN.md', import.meta.url), 'utf8');
const indexHtml = readFileSync(new URL('../index.html', import.meta.url), 'utf8');

describe('main.js cleanup guardrails', () => {
    it('does not import unused MOVE_SPEED into main.js', () => {
        expect(mainSource).not.toMatch(/import\s*\{\s*Arrow\s*,\s*MOVE_SPEED\s*\}/);
    });

    it('does not silently swallow audio failures with an empty catch block', () => {
        expect(mainSource).not.toMatch(/catch\s*\([^)]*\)\s*\{\s*\}/);
    });

    it('shows explicit user-facing messages for generation fallback states', () => {
        expect(mainSource).toContain('Squiggly 생성 실패: 1-Cell 방식으로 전환했습니다.');
        expect(mainSource).toContain('이미지 마스크 생성 실패: 기본 하트 맵으로 복귀했습니다.');
    });

    it('does not report handled generation fallback states as console errors', () => {
        expect(mainSource).not.toContain('console.error');
    });

    it('rejects escape directions that pass through the current candidate path', () => {
        expect(mainSource).toContain('path.some(p => p.x === ray.x && p.y === ray.y)');
    });
});

describe('documentation alignment guardrails', () => {
    it('marks Reverse Injection as not implemented current behavior', () => {
        expect(imagePlan).toContain('아직 별도 구현되어 있지 않다');
    });
});

describe('browser surface guardrails', () => {
    it('declares a favicon so the browser does not request missing /favicon.ico', () => {
        expect(indexHtml).toMatch(/<link\s+rel="icon"/);
    });
});
