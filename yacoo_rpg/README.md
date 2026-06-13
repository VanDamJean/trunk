# Yacoo RPG

야추(Yahtzee) 주사위 룰 기반 모바일 RPG 웹 프로토타입.  
React 19 + Vite 7 + TypeScript.

## 빠른 실행

```bash
cd yacoo_rpg
npm install          # 최초 1회
npm run dev -- --host 127.0.0.1
```

브라우저에서 열기:

**http://127.0.0.1:5173**

> 모바일 크기로 보려면 DevTools → 기기 툴바 → **375×812** (세로) 권장.

## 다른 실행 방법

| 목적 | 명령 |
|---|---|
| 개발 서버 | `npm run dev -- --host 127.0.0.1` |
| 프로덕션 빌드 | `npm run build` |
| 빌드 결과 미리보기 | `npm run build && npm run preview -- --host 127.0.0.1` |
| 단위 테스트 | `npm test -- --run` |
| 브라우저 QA | dev 서버 켠 뒤 다른 터미널에서 `npm run qa` |

Playwright 브라우저가 없으면:

```bash
npx playwright install chromium
```

## 플레이 요약

1. **Home** → `Start Combat`
2. **Combat** → `🎲 주사위 굴리기` → 원하는 주사위 홀드 → `리롤` (최대 3회)
3. 족보 버튼(Pair, Full House 등) 또는 `기본 공격`으로 턴 공격
4. 적 턴 후 다시 1번부터 반복
5. 승리 → **Result**에서 보상 → **Upgrade**에서 장비 강화

## 문서

상세 기획·작업 명세는 `docs/` 폴더:

- `docs/README.ko.md` — 한글 실행/플레이 가이드 (이 파일과 동일 내용 + 상세)
- `docs/00-INDEX.md` — 전체 문서 목차

## 저장

진행 데이터는 브라우저 `localStorage` (`yacoo-rpg-save-v2`)에 저장됩니다.
