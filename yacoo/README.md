# Yacoo

한국식 야추(Yacht Dice) 웹 게임입니다.一个人 vs 로컬 봇.

## 실행 방법

### 필수 요구사항

- Node.js 18 이상
- pnpm 9 이상

```bash
pnpm install
```

### 개발 서버

```bash
pnpm dev
```

브라우저에서 http://localhost:5173 을 열면 게임이 시작됩니다.

### 게임 방법

1. **굴리기** 버튼으로 주사위 5개를 굴립니다 (최대 3번).
2. 굴린 후 주사위를 클릭해 **고정**할 수 있습니다.
3. 고정하지 않은 주사위만 다시 굴립니다.
4. 점수판에서 기록할 칸을 클릭하면 점수가 등록되고 봇 차례로 넘어갑니다.
5. 13개 칸을 전부 채우면 게임 종료.

### 검증

```bash
pnpm test -- --run                    # 단위 테스트 (30개)
pnpm build                            # 프로덕션 빌드
pnpm exec playwright install chromium # E2E 테스트용 브라우저 설치
pnpm exec playwright test             # E2E 테스트 (6개)
```

### 프로덕션 빌드

```bash
pnpm build
pnpm preview
```

## Rules

- Five six-sided dice.
- Each turn allows up to three rolls.
- Held dice stay fixed across rerolls.
- A score category can be used once per player.
- There is no upper-section bonus in v1.

Categories:

- 에이스, 듀스, 트레이, 포스, 파이브, 식스: sum dice matching that face.
- 초이스: sum all dice.
- 포카인드: sum all dice when any face appears at least four times; otherwise 0.
- 풀하우스: sum all dice only for exact 3+2 counts. Yacht does not count as full house.
- 스몰 스트레이트: 15 points for 1-2-3-4, 2-3-4-5, or 3-4-5-6.
- 라지 스트레이트: 30 points for 1-2-3-4-5 or 2-3-4-5-6.
- 요트: 50 points for five matching dice.

## Bot

The bot is local and deterministic. It does not call any LLM or network API.

Hold priority on roll 1-2:

1. Hold all dice for Large Straight or Yacht.
2. Hold a Yacht/four-kind group, higher face wins ties.
3. Hold the first available straight candidate in this order: 2-3-4-5, 1-2-3-4, 3-4-5-6.
4. Hold full-house candidates: exact full house, then highest triple, then two pairs, then highest pair.
5. Hold the highest repeated face.
6. Hold the lowest-index die among the highest face.

On scoring, the bot picks the unused category with the highest immediate score. Equal scores use this tie-break order: 요트, 라지 스트레이트, 포카인드, 풀하우스, 초이스, 스몰 스트레이트, 식스, 파이브, 포스, 트레이, 듀스, 에이스.

## Non-goals

- No backend, auth, database, account system, persistence, localStorage, multiplayer networking, leaderboard, analytics, sounds, or AI API calls.
- No minimax, expected-value solver, ML, or advanced bot difficulty.
- No manual model-routing changes are required to play the game.
