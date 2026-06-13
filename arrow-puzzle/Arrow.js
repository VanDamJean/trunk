export const MOVE_SPEED = 1200;

export class Arrow {
    constructor(data, config = {}) {
        const cellSize = config.cellSize || 50;
        this.lineWidth = config.lineWidth || 26;

        this.id = data.id;
        this.color = data.color;
        this.gridPts = data.pts;

        this.points = data.pts.map(p => ({ x: p[0] * cellSize + cellSize / 2, y: p[1] * cellSize + cellSize / 2 }));
        this.progress = 0;
        this.isMoving = false;
        this.isJiggling = false;
        this.jiggleTime = 0;
        this.done = false;

        this.totalLength = 0;
        this.segments = [];
        for (let i = 0; i < this.points.length - 1; i++) {
            let p1 = this.points[i]; let p2 = this.points[i + 1];
            let dx = p2.x - p1.x; let dy = p2.y - p1.y;
            let len = Math.hypot(dx, dy);
            this.totalLength += len;
            this.segments.push({ p1, p2, len, dx, dy, dirX: dx / len, dirY: dy / len });
        }
    }

    getSegmentsAt(prog) {
        let startD = prog; let endD = prog + this.totalLength;
        let curSegs = []; let currentD = 0;
        for (let seg of this.segments) {
            let segStart = currentD; let segEnd = currentD + seg.len;
            currentD = segEnd;
            if (segEnd <= startD) continue;
            if (segStart >= endD) break;
            let oS = Math.max(startD, segStart); let oE = Math.min(endD, segEnd);
            let p1 = { x: seg.p1.x + seg.dirX * (oS - segStart), y: seg.p1.y + seg.dirY * (oS - segStart) };
            let p2 = { x: seg.p1.x + seg.dirX * (oE - segStart), y: seg.p1.y + seg.dirY * (oE - segStart) };
            curSegs.push({ p1, p2, dirX: seg.dirX, dirY: seg.dirY });
        }
        if (endD > this.totalLength) {
            let last = this.segments[this.segments.length - 1];
            let exS = Math.max(this.totalLength, startD); let exE = endD;
            let p1 = { x: last.p2.x + last.dirX * (exS - this.totalLength), y: last.p2.y + last.dirY * (exS - this.totalLength) };
            let p2 = { x: last.p2.x + last.dirX * (exE - this.totalLength), y: last.p2.y + last.dirY * (exE - this.totalLength) };
            curSegs.push({ p1, p2, dirX: last.dirX, dirY: last.dirY });
        }
        return curSegs;
    }

    getRects(prog = this.progress) {
        const lw = this.lineWidth;
        return this.getSegmentsAt(prog).map(s => {
            let minX = Math.min(s.p1.x, s.p2.x); let maxX = Math.max(s.p1.x, s.p2.x);
            let minY = Math.min(s.p1.y, s.p2.y); let maxY = Math.max(s.p1.y, s.p2.y);
            return { left: minX - lw / 2 + 2, right: maxX + lw / 2 - 2, top: minY - lw / 2 + 2, bottom: maxY + lw / 2 - 2 };
        });
    }

    canEscape(allArrows) {
        for (let p = 0; p < 1200; p += 25) {
            const rects = this.getRects(p);
            for (let other of allArrows) {
                if (other === this || other.done || other.isMoving) continue;
                const otherRects = other.getRects(0);
                for (let r1 of rects) {
                    for (let r2 of otherRects) {
                        if (!(r2.left >= r1.right || r2.right <= r1.left || r2.top >= r1.bottom || r2.bottom <= r1.top)) return false;
                    }
                }
            }
        }
        return true;
    }

    update(dt) {
        if (this.isJiggling) {
            this.jiggleTime -= dt;
            if (this.jiggleTime <= 0) this.isJiggling = false;
        }
        if (!this.isMoving) return;
        this.progress += MOVE_SPEED * dt;
        if (this.progress > 1000 && !this.done) {
            this.done = true; this.isMoving = false;
        }
    }

    draw(ctx) {
        if (this.done) return;
        let segs = this.getSegmentsAt(this.progress);
        if (segs.length === 0) return;

        const lw = this.lineWidth;
        ctx.save();
        if (this.isJiggling) { ctx.translate(Math.sin(this.jiggleTime * 40) * 4, 0); }

        ctx.shadowBlur = 10; ctx.shadowColor = this.color;
        ctx.strokeStyle = this.color; ctx.lineWidth = lw;
        ctx.lineCap = 'round'; ctx.lineJoin = 'round';

        ctx.beginPath(); ctx.moveTo(segs[0].p1.x, segs[0].p1.y);
        for (let s of segs) ctx.lineTo(s.p2.x, s.p2.y);
        ctx.stroke();

        let lastSeg = segs[segs.length - 1];
        ctx.translate(lastSeg.p2.x, lastSeg.p2.y);
        ctx.rotate(Math.atan2(lastSeg.dirY, lastSeg.dirX));

        // 음각(Cutout) 화살촉
        ctx.fillStyle = '#1c1c28';
        ctx.shadowBlur = 0;
        const hw = lw * 0.55;
        const hl = lw * 0.4;
        ctx.beginPath(); ctx.moveTo(0, 0); ctx.lineTo(-hw, -hl); ctx.lineTo(-hl, 0); ctx.lineTo(-hw, hl); ctx.fill();
        ctx.restore();
    }

    isPointInside(x, y) {
        let rects = this.getRects();
        for (let r of rects) { if (x >= r.left && x <= r.right && y >= r.top && y <= r.bottom) return true; }
        return false;
    }
}