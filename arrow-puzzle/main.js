import './style.css';
import { Arrow } from './Arrow.js';

const canvas = document.getElementById('gameCanvas');
const ctx = canvas.getContext('2d');
canvas.width = 600;
canvas.height = 600;

// 동적 크기 변수 (마스크 크기에 따라 자동 변경)
let cellSize = 50;
let lineWidth = 26;

let audioCtx = null;
function playSound(type) {
    try {
        if (!audioCtx) audioCtx = new (window.AudioContext || window.webkitAudioContext)();
        if (audioCtx.state === 'suspended') audioCtx.resume();
        const osc = audioCtx.createOscillator();
        const gain = audioCtx.createGain();
        osc.connect(gain); gain.connect(audioCtx.destination);
        const now = audioCtx.currentTime;
        if (type === 'pop') {
            osc.type = 'sine'; osc.frequency.setValueAtTime(400, now); osc.frequency.exponentialRampToValueAtTime(1000, now + 0.1);
            gain.gain.setValueAtTime(0.4, now); gain.gain.exponentialRampToValueAtTime(0.01, now + 0.1);
            osc.start(now); osc.stop(now + 0.1);
        } else if (type === 'bump') {
            osc.type = 'triangle'; osc.frequency.setValueAtTime(150, now); osc.frequency.exponentialRampToValueAtTime(80, now + 0.1);
            gain.gain.setValueAtTime(0.3, now); gain.gain.exponentialRampToValueAtTime(0.01, now + 0.1);
            osc.start(now); osc.stop(now + 0.1);
        }
    } catch (error) {
        if (import.meta.env.DEV) console.warn('Audio playback unavailable:', error);
    }
}

const maskHeart = [
    [0,0,0,0,0,0,0,0,0,0],
    [0,0,1,1,0,0,1,1,0,0],
    [0,1,1,1,1,1,1,1,1,0],
    [0,1,1,1,1,1,1,1,1,0],
    [0,1,1,1,1,1,1,1,1,0],
    [0,0,1,1,1,1,1,1,0,0],
    [0,0,0,1,1,1,1,0,0,0],
    [0,0,0,0,1,1,0,0,0,0],
    [0,0,0,0,0,0,0,0,0,0]
];

let currentMask = maskHeart;
let currentGenType = 'mixed';
let maxGridSize = 15;
let lastUploadedImg = null;
let generationNotice = '';

function getFallbackNotice(type) {
    return type === 'squiggly'
        ? 'Squiggly 생성 실패: 1-Cell 방식으로 전환했습니다.'
        : 'Mixed 생성 실패: 1-Cell 방식으로 전환했습니다.';
}

function processImageToMask(img, maxDim) {
    let gw = maxDim, gh = maxDim;
    if (img.width > img.height) gh = Math.round((img.height / img.width) * maxDim);
    else gw = Math.round((img.width / img.height) * maxDim);
    if(gw < 3) gw = 3; if(gh < 3) gh = 3;

    const off = document.createElement('canvas');
    off.width = gw; off.height = gh;
    const oCtx = off.getContext('2d');
    // 흰 배경 깔기 (투명 PNG의 투명 영역 구분용)
    oCtx.fillStyle = '#ffffff';
    oCtx.fillRect(0, 0, gw, gh);
    oCtx.drawImage(img, 0, 0, gw, gh);
    const data = oCtx.getImageData(0, 0, gw, gh).data;
    const total = gw * gh;

    // 투명도 체크 — 원본 이미지를 별도 캔버스에서 확인
    const offRaw = document.createElement('canvas');
    offRaw.width = gw; offRaw.height = gh;
    const rawCtx = offRaw.getContext('2d');
    rawCtx.drawImage(img, 0, 0, gw, gh);
    const rawData = rawCtx.getImageData(0, 0, gw, gh).data;

    let opaqueCount = 0;
    for (let i = 3; i < rawData.length; i += 4) {
        if (rawData[i] > 250) opaqueCount++;
    }
    const hasTransparency = opaqueCount < total * 0.95;

    if (hasTransparency) {
        // 투명 이미지 → alpha 기반 마스크
        const mask = [];
        for (let y = 0; y < gh; y++) {
            const row = [];
            for (let x = 0; x < gw; x++) {
                row.push(rawData[(y * gw + x) * 4 + 3] > 127 ? 1 : 0);
            }
            mask.push(row);
        }
        return mask;
    }

    // 불투명 이미지 (JPEG 등) → 밝기 기반 Otsu 임계값
    const grays = [];
    for (let i = 0; i < total; i++) {
        const idx = i * 4;
        grays.push(Math.round(data[idx] * 0.299 + data[idx + 1] * 0.587 + data[idx + 2] * 0.114));
    }

    // Otsu 이진화
    const histogram = new Array(256).fill(0);
    for (const g of grays) histogram[g]++;
    let sumAll = 0;
    for (let i = 0; i < 256; i++) sumAll += i * histogram[i];
    let sumBg = 0, wBg = 0, maxVariance = 0, threshold = 128;
    for (let t = 0; t < 256; t++) {
        wBg += histogram[t];
        if (wBg === 0) continue;
        const wFg = total - wBg;
        if (wFg === 0) break;
        sumBg += t * histogram[t];
        const meanBg = sumBg / wBg;
        const meanFg = (sumAll - sumBg) / wFg;
        const variance = wBg * wFg * (meanBg - meanFg) ** 2;
        if (variance > maxVariance) { maxVariance = variance; threshold = t; }
    }

    // 배경 판별: 모서리 4개 밝기 평균
    const corners = [grays[0], grays[gw - 1], grays[(gh - 1) * gw], grays[total - 1]];
    const bgBright = corners.reduce((a, b) => a + b) / 4;
    const subjectIsDark = bgBright > threshold;

    const mask = [];
    for (let y = 0; y < gh; y++) {
        const row = [];
        for (let x = 0; x < gw; x++) {
            const g = grays[y * gw + x];
            row.push((subjectIsDark ? g < threshold : g >= threshold) ? 1 : 0);
        }
        mask.push(row);
    }
    return mask;
}

function generateLevel(type, maskData) {
    const H = maskData.length;
    const W = maskData[0].length;

    // 마스크에 채울 셀이 없으면 즉시 실패
    let totalCells = 0;
    for (let y = 0; y < H; y++) for (let x = 0; x < W; x++) if (maskData[y][x]) totalCells++;
    if (totalCells === 0) return [];

    cellSize = Math.min(canvas.width / (W + 2), canvas.height / (H + 2));
    lineWidth = cellSize * 0.52;
    const offsetX = 1;
    const offsetY = 1;

    for(let r = 0; r < 100; r++) {
        let filled = maskData.map(row => row.map(() => 0));
        let placedArrows = [];
        let stuck = false;

        while(true) {
            let unfilled = [];
            for(let y = 0; y < H; y++) {
                for(let x = 0; x < W; x++) {
                    if(maskData[y][x] && !filled[y][x]) unfilled.push({x, y});
                }
            }
            if(unfilled.length === 0) break;

            let placedOne = false;
            for(let t = 0; t < 800; t++) {
                let start = unfilled[Math.floor(Math.random() * unfilled.length)];
                let path = [start];

                let len = 1;
                if (type === '1cell') len = 1;
                else if (type === 'mixed') len = Math.random() < 0.5 ? 1 : Math.floor(Math.random() * 4) + 2;
                else if (type === 'squiggly') len = Math.floor(Math.random() * 5) + 3;

                let curr = start;
                for(let i = 1; i < len; i++) {
                    let neighbors = [
                        {x: curr.x+1, y: curr.y}, {x: curr.x-1, y: curr.y},
                        {x: curr.x, y: curr.y+1}, {x: curr.x, y: curr.y-1}
                    ].filter(n => n.x >= 0 && n.x < W && n.y >= 0 && n.y < H &&
                                  maskData[n.y][n.x] && !filled[n.y][n.x] &&
                                  !path.some(p => p.x === n.x && p.y === n.y));
                    if(neighbors.length === 0) break;
                    curr = neighbors[Math.floor(Math.random() * neighbors.length)];
                    path.push(curr);
                }

                let head = path[path.length - 1];
                let dirs = [{x:1,y:0}, {x:-1,y:0}, {x:0,y:1}, {x:0,y:-1}].sort(() => Math.random() - 0.5);

                let escapeDir = null;
                for(let d of dirs) {
                    let canEscape = true;
                    let ray = {x: head.x + d.x, y: head.y + d.y};
                    while(ray.x >= -2 && ray.x <= W+1 && ray.y >= -2 && ray.y <= H+1) {
                        if(ray.x >= 0 && ray.x < W && ray.y >= 0 && ray.y < H) {
                            if(filled[ray.y][ray.x] || path.some(p => p.x === ray.x && p.y === ray.y)) { canEscape = false; break; }
                        }
                        ray.x += d.x; ray.y += d.y;
                    }
                    if(canEscape) { escapeDir = d; break; }
                }

                if(escapeDir) {
                    let pts = [];
                    if(path.length === 1) {
                        pts = [
                            [path[0].x + offsetX - escapeDir.x * 0.15, path[0].y + offsetY - escapeDir.y * 0.15],
                            [path[0].x + offsetX + escapeDir.x * 0.3, path[0].y + offsetY + escapeDir.y * 0.3]
                        ];
                    } else {
                        pts = path.map(p => [p.x + offsetX, p.y + offsetY]);
                        pts.push([head.x + offsetX + escapeDir.x * 0.3, head.y + offsetY + escapeDir.y * 0.3]);
                    }

                    const colors = ['#ff4444', '#44ff44', '#ffff44', '#4444ff', '#ff44ff', '#44ffff', '#ff8844', '#ffffff'];
                    placedArrows.push({ color: colors[Math.floor(Math.random() * colors.length)], pts: pts });
                    for(let p of path) filled[p.y][p.x] = 1;
                    placedOne = true; break;
                }
            }
            if(!placedOne) { stuck = true; break; }
        }
        if(!stuck) {
            placedArrows.reverse();
            placedArrows.forEach((a, i) => a.id = i + 1);
            return placedArrows;
        }
    }
    // 실패 시 1cell 폴백 (mixed/squiggly가 밀도 높은 마스크에서 실패할 때)
    if (type !== '1cell') {
        generationNotice = getFallbackNotice(type);
        if (import.meta.env.DEV) console.info(generationNotice);
        return generateLevel('1cell', maskData);
    }
    generationNotice = '퍼즐 생성 실패: 마스크를 단순화하거나 Grid Max를 낮춰주세요.';
    if (import.meta.env.DEV) console.info(generationNotice);
    return [];
}

let particles = [];
let historyStack = [];
let arrows = [];

class Particle {
    constructor(x, y, color) {
        this.x = x; this.y = y;
        const angle = Math.random() * Math.PI * 2;
        const speed = Math.random() * 400 + 100;
        this.vx = Math.cos(angle) * speed; this.vy = Math.sin(angle) * speed;
        this.color = color; this.life = 1.0; this.decay = Math.random() * 1.5 + 0.8;
    }
    update(dt) { this.x += this.vx * dt; this.y += this.vy * dt; this.life -= this.decay * dt; }
    draw(ctx) {
        if (this.life <= 0) return;
        ctx.globalAlpha = this.life; ctx.fillStyle = this.color;
        ctx.beginPath(); ctx.arc(this.x, this.y, 4, 0, Math.PI * 2); ctx.fill();
        ctx.globalAlpha = 1.0;
    }
}

function loadLevel(type) {
    currentGenType = type;
    generationNotice = '';
    let data = generateLevel(type, currentMask);

    // 완전 실패 → 기본 하트 마스크로 자동 복귀
    if (data.length === 0 && currentMask !== maskHeart) {
        generationNotice = '이미지 마스크 생성 실패: 기본 하트 맵으로 복귀했습니다.';
        if (import.meta.env.DEV) console.info(generationNotice);
        currentMask = maskHeart;
        lastUploadedImg = null;
        data = generateLevel(type, currentMask);
    }

    arrows = data.map(d => new Arrow(d, { cellSize, lineWidth }));
    historyStack = []; particles = [];
    const statusEl = document.getElementById('status-text');
    statusEl.innerText = generationNotice;
    if (data.length === 0) {
        statusEl.innerText = '⚠️ 생성 실패';
    }
}
loadLevel('mixed');

function drawBackground() {
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    ctx.strokeStyle = '#2a2a3a'; ctx.lineWidth = 1;
    ctx.beginPath();
    for (let i = 0; i <= canvas.width; i += cellSize) { ctx.moveTo(i, 0); ctx.lineTo(i, canvas.height); }
    for (let i = 0; i <= canvas.height; i += cellSize) { ctx.moveTo(0, i); ctx.lineTo(canvas.width, i); }
    ctx.stroke();
}

let lastTime = performance.now();
function gameLoop(now) {
    const dt = Math.min((now - lastTime) / 1000, 0.1);
    lastTime = now;
    drawBackground();

    for (let i = arrows.length - 1; i >= 0; i--) {
        const wasDone = arrows[i].done;
        arrows[i].update(dt);
        // 방금 탈출 완료 → 파티클 생성
        if (!wasDone && arrows[i].done) {
            let lastSeg = arrows[i].segments[arrows[i].segments.length - 1];
            for (let j = 0; j < 20; j++) particles.push(new Particle(lastSeg.p2.x, lastSeg.p2.y, arrows[i].color));
        }
        arrows[i].draw(ctx);
    }
    for (let i = particles.length - 1; i >= 0; i--) {
        particles[i].update(dt); particles[i].draw(ctx);
        if (particles[i].life <= 0) particles.splice(i, 1);
    }

    if (arrows.length > 0 && arrows.every(a => a.done)) {
        document.getElementById('status-text').innerText = 'CLEAR!';
    }
    requestAnimationFrame(gameLoop);
}

canvas.addEventListener('pointerdown', (e) => {
    const rect = canvas.getBoundingClientRect();
    const x = (e.clientX - rect.left) * (canvas.width / rect.width);
    const y = (e.clientY - rect.top) * (canvas.height / rect.height);
    for (let arrow of arrows) {
        if (!arrow.done && !arrow.isMoving && arrow.isPointInside(x, y)) {
            if (arrow.canEscape(arrows)) {
                arrow.isMoving = true; historyStack.push(arrow.id); playSound('pop');
            } else {
                arrow.isJiggling = true; arrow.jiggleTime = 0.25; playSound('bump');
            }
            break;
        }
    }
});

// UI Event Listeners
document.getElementById('btn-undo').addEventListener('pointerdown', (e) => {
    e.preventDefault();
    if (historyStack.length > 0) {
        const lastId = historyStack.pop();
        const arrow = arrows.find(a => a.id === lastId);
        if (arrow) { arrow.progress = 0; arrow.isMoving = false; arrow.done = false; arrow.isJiggling = false; }
        document.getElementById('status-text').innerText = '';
    }
});
document.getElementById('btn-gen-1').addEventListener('pointerdown', (e) => { e.preventDefault(); loadLevel('1cell'); });
document.getElementById('btn-gen-sq').addEventListener('pointerdown', (e) => { e.preventDefault(); loadLevel('squiggly'); });
document.getElementById('btn-gen-mx').addEventListener('pointerdown', (e) => { e.preventDefault(); loadLevel('mixed'); });

document.getElementById('slider-grid').addEventListener('input', (e) => {
    maxGridSize = parseInt(e.target.value);
    document.getElementById('val-grid').innerText = maxGridSize;
    // 이미지 있으면 새 그리드 사이즈로 마스크 재계산, 없으면 현재 마스크로 재생성
    if (lastUploadedImg) {
        currentMask = processImageToMask(lastUploadedImg, maxGridSize);
    }
    loadLevel(currentGenType);
});

document.getElementById('img-upload').addEventListener('change', (e) => {
    const file = e.target.files[0];
    if(!file) return;
    const reader = new FileReader();
    reader.onload = (event) => {
        const img = new Image();
        img.onload = () => {
            lastUploadedImg = img;
            currentMask = processImageToMask(img, maxGridSize);
            loadLevel(currentGenType);
        };
        img.src = event.target.result;
    };
    reader.readAsDataURL(file);
});

document.getElementById('btn-export').addEventListener('pointerdown', (e) => {
    e.preventDefault();
    const exportData = arrows.map(a => ({ id: a.id, color: a.color, pts: a.gridPts }));
    const blob = new Blob([JSON.stringify(exportData, null, 2)], {type: 'application/json'});
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url; a.download = 'arrow_level.json';
    a.click();
    URL.revokeObjectURL(url);
});

requestAnimationFrame(gameLoop);
