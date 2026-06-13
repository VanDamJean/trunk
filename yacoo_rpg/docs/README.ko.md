# Yacoo RPG 실행 가이드

## 만든 것

React + Vite + TypeScript로 만든 웹 RPG 프로토타입입니다.

- 귀여운 캐주얼 RPG 느낌의 모바일 우선 화면 (375×812 기준)
- 화면: Home, Combat, Equipment, Upgrade, Result
- **수동 턴제 전투**: 매 턴 주사위를 굴려 족보로 공격
- Yahtzee 주사위: 5개, 최대 3번 굴림, 홀드 가능, 핍(pip) 그래픽 + 던지기 연출
- 장비 페이퍼돌: 무기/방어구/부적/신발이 캐릭터에 그래픽으로 표시
- 장비 성장: 코인으로 업그레이드
- 진행 저장: `localStorage` (`yacoo-rpg-save-v2`)
- 외부 게임 자산 복사 없음 — CSS/SVG 컴포넌트만 사용

## 실행 방법

프로젝트 폴더로 이동:

```bash
cd yacoo_rpg
```

패키지 설치 (최초 1회):

```bash
npm install
```

개발 서버 실행:

```bash
npm run dev -- --host 127.0.0.1
```

브라우저에서 열기:

```text
http://127.0.0.1:5173
```

모바일 비율로 보려면 DevTools 기기 모드 **375×812** 권장.

### 빌드 미리보기 (배포물 확인)

```bash
npm run build
npm run preview -- --host 127.0.0.1
```

기본 포트는 `4173`입니다. `http://127.0.0.1:4173`

## 테스트 / QA

단위 테스트:

```bash
npm test -- --run
```

프로덕션 빌드:

```bash
npm run build
```

브라우저 QA (dev 서버가 켜져 있어야 함):

```bash
# 터미널 1
npm run dev -- --host 127.0.0.1

# 터미널 2
npm run qa
```

Playwright 브라우저가 없으면:

```bash
npx playwright install chromium
```

## 플레이 방법

1. Home에서 `Start Combat` 클릭
2. Combat에서 `🎲 주사위 굴리기` 클릭
3. 원하는 주사위를 탭해서 홀드 (리롤 시 유지)
4. `리롤 (N회 남음)`으로 나머지 주사위 다시 굴리기 (최대 3회)
5. 활성화된 족보 버튼(Pair, Full House 등)을 눌러 공격 — 예상 피해가 `→ N`으로 표시됨
6. 주사위 없이 `기본 공격`도 선택 가능
7. 적 턴 후 다시 1번부터
8. 승리 → Result에서 `Claim Reward` → Upgrade에서 장비 강화

## 확인된 상태

- `npm run build`: 통과
- `npm test -- --run`: 통과
- `npm run qa`: 통과 (dev 서버 실행 중일 때)
