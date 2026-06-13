import { describe, it, expect } from 'vitest';
import { Arrow } from '../Arrow.js';

const CONFIG = { cellSize: 50, lineWidth: 26 };

describe('Arrow Geometry & Collision', () => {
    it('should NOT overlap if two 1-cell arrows face away from each other', () => {
        // 등 맞대고 있는 두 1칸짜리 화살표
        // 화살표 1: x=5, y=5, 왼쪽(-1, 0) 방향
        const arrow1 = new Arrow({
            id: 1, color: '#ff4444',
            pts: [
                [5 - (-1)*0.15, 5],
                [5 + (-1)*0.3, 5]
            ]
        }, CONFIG);

        // 화살표 2: x=6, y=5, 오른쪽(1, 0) 방향
        const arrow2 = new Arrow({
            id: 2, color: '#44ff44',
            pts: [
                [6 - (1)*0.15, 5],
                [6 + (1)*0.3, 5]
            ]
        }, CONFIG);

        const allArrows = [arrow1, arrow2];
        expect(arrow1.canEscape(allArrows)).toBe(true);
        expect(arrow2.canEscape(allArrows)).toBe(true);
    });

    it('should correctly block if one arrow points INTO another', () => {
        // 화살표 1: x=5, y=5, 오른쪽(1, 0) 방향
        const arrow1 = new Arrow({
            id: 1, color: '#ff4444',
            pts: [
                [5 - (1)*0.15, 5],
                [5 + (1)*0.3, 5]
            ]
        }, CONFIG);

        // 화살표 2: x=6, y=5, 오른쪽(1, 0) 방향
        const arrow2 = new Arrow({
            id: 2, color: '#44ff44',
            pts: [
                [6 - (1)*0.15, 5],
                [6 + (1)*0.3, 5]
            ]
        }, CONFIG);

        const allArrows = [arrow1, arrow2];
        // arrow2 앞에 아무것도 없음 → 탈출 가능
        expect(arrow2.canEscape(allArrows)).toBe(true);
        // arrow1 앞에 arrow2 있음 → 막힘
        expect(arrow1.canEscape(allArrows)).toBe(false);
    });

    it('should store color and gridPts correctly', () => {
        const arrow = new Arrow({
            id: 99, color: '#ff8844',
            pts: [[1, 2], [3, 4]]
        }, CONFIG);

        expect(arrow.color).toBe('#ff8844');
        expect(arrow.gridPts).toEqual([[1, 2], [3, 4]]);
        expect(arrow.id).toBe(99);
    });

    it('should use custom config values', () => {
        const smallConfig = { cellSize: 20, lineWidth: 10 };
        const arrow = new Arrow({
            id: 1, color: '#fff',
            pts: [[0, 0], [1, 0]]
        }, smallConfig);

        // cellSize=20 → point[0] = 0*20+10=10, point[1] = 1*20+10=30
        expect(arrow.points[0].x).toBe(10);
        expect(arrow.points[1].x).toBe(30);
        expect(arrow.lineWidth).toBe(10);
    });
});