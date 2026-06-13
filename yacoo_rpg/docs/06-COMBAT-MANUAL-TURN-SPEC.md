# 06 — 전투: 수동 턴제 설계서

> **이 문서가 전투 모델의 최종 사양이다.** `02-GAME-DESIGN-SPEC.md` §1(하이브리드 전투)는 폐기되었고 이 문서로 대체된다.
> 데이터 타입은 `03-DATA-MODEL.md`, 코딩 규칙은 `05-CODING-GUIDELINES.md` 참고.

## 0. 결정 사항 (확정)

1. **전투 모델 = 순수 수동 턴제** (딸깍 다이스 원작 방식). 시간 기반 오토배틀 폐기.
2. **평타는 자동이 아니라 수동 선택지로 유지** — 매 턴 플레이어가 "주사위 공격" vs "평타" 중 택1.
3. **기존 오토배틀 전투는 완전히 교체** (토글로 남기지 않음). 수동 턴제만 남긴다.

## 1. 방향 전환 요약 (기존 결정 번복)

| 항목 | 기존(하이브리드, 폐기) | 신규(수동 턴제) |
|---|---|---|
| 진행 방식 | 시간 타이머로 자동 진행 | 플레이어 입력으로만 진행 |
| 평타 | 매 초 자동 | 매 턴 선택지 중 하나 |
| 주사위 | 가끔 충전되는 필살기 | 매 턴의 주 행동, 항상 사용 가능 |
| 적 공격 | 타이머가 자동(1.2초마다) | 내가 공격한 직후 1회 반격 |
| 타이머 | 3개(`heroAttackMs`/`enemyAttackMs`/`diceChargeMs`) | 0개(연출용 setTimeout 1개만 선택적) |

## 2. 턴 흐름

```
전투 시작 (영웅 HP = run.hp, 적 = createEnemy(chapter/node))
반복 {
  [내 턴]
    1. 주사위 자동 굴림 (턴 시작 시 1회)
    2. 홀드 선택 → 리롤 (rollsLeft 만큼; 0이면 리롤 불가)
    3. 행동 택1:
        (A) 주사위 공격 — 유효 족보 중 하나 선택 → 큰 데미지
        (B) 평타        — 주사위 무시, 고정 데미지(attack + autoBonus)
    4. 적 HP 감소 → 0이면 WIN, 전투 종료
  [적 턴]
    5. 적이 영웅 공격(enemyDamage) → 영웅 HP 감소
    6. 영웅 HP 0이면 LOSS, 전투 종료
}
```

- 평타(B)의 존재 이유: 족보가 안 풀렸을 때 안전하게 한 대 치거나 리롤을 아낄 때.
- **주의**: `chance` 족보가 항상 유효하므로 평타와 역할이 겹친다. §6 밸런스에서 차별화할 것(평타 = 리롤 소모 없이 즉시 / `chance` = 굴린 눈값 합 기반, 또는 평타를 "방어(받는 피해 감소)"로 대체 검토).

## 3. 상태머신 (`CombatScreen.tsx` 재작성)

```ts
type TurnPhase = 'choosing' | 'enemyTurn' | 'over';

// 컴포넌트 상태
heroHp: number
enemyHp: number
dice: DieValue[]        // 항상 표시 (기존 diceOpen 개념 제거)
held: boolean[]
rollsLeft: number       // 턴 시작 시 maxRolls - 1 (첫 굴림은 자동)
phase: TurnPhase
log: string[]           // 전투 로그 (선택)
lastHand?: YahtzeeAttackCategory  // 승리 시 onFinish로 전달
```

함수:

| 함수 | 동작 |
|---|---|
| `startTurn()` | `dice = rollDice(diceCount)`, `held=[false…]`, `rollsLeft = maxRolls - 1`, `phase='choosing'` |
| `reroll()` | `rollsLeft > 0`일 때만, 홀드 안 한 주사위만 재굴림, `rollsLeft--` |
| `toggleHeld(i)` | 홀드 토글 (기존 그대로) |
| `diceAttack(cat)` | 유효 족보만 허용 → `calculateUltimateCategoryDamage` → 적 HP↓ → 승패 체크 → `enemyTurn()` |
| `basicAttack()` | `calculateHeroAutoDamage` 고정 데미지 → 적 HP↓ → 승패 체크 → `enemyTurn()` |
| `enemyTurn()` | (연출용 0.4~0.6초 지연 후) 영웅 HP↓ → 승패 체크 → 살아있으면 `startTurn()` |

- `phase='enemyTurn'` 동안에는 모든 입력 버튼 비활성화(중복 입력 방지).
- 연출 지연은 `setTimeout` 1개로 충분하며, 없애고 즉시 처리해도 로직상 무방하다. 다만 테스트에서는 즉시 처리가 더 안정적이니 지연 시간을 상수로 빼서 테스트에서 0으로 둘 수 있게 한다.

## 4. 재사용 / 제거 / 추가 목록

**그대로 재사용 (`game/` 순수 로직 — 손대지 않음):**
- `yahtzee.ts`: `rollDice`, `getValidAttackCategories`, `evaluateHand`
- `combat.ts`: `calculateUltimateCategoryDamage`, `calculateHeroAutoDamage`, `calculateEnemyDamage`, `clampHp`, `createEnemy`

**제거 (`CombatScreen.tsx` 내부):**
- 타이머 3개 `useEffect`(영웅 평타 / 적 공격 / 주사위 충전)
- `diceReady`, `diceOpen` 상태와 `openDice()`, `closeDice()`
- "Open Dice" 버튼, "Dice charged / Auto battle" 배지

**제거 (`constants.ts`):**
- `COMBAT_TIMING.heroAttackMs`, `enemyAttackMs`, `diceChargeMs`, `maxDiceCharges` → 폐기
- `maxRolls`, `diceCount`는 유지 (단, M3에서 `run.maxRolls`/`run.diceCount`로 이전 예정 — `02` §1.5 참고)
- 연출 지연 상수 신설 권장: `enemyTurnDelayMs`(기본 500, 테스트 시 0)

**추가 (`CombatScreen.tsx`):**
- `phase` 턴 상태머신과 `startTurn` / `basicAttack` / `enemyTurn`
- UI: 리롤 버튼(+남은 횟수), 유효 족보 버튼 목록, 평타 버튼, 현재 턴/페이즈 표시

## 5. UI 레이아웃 (세로 375, 한 손)

```
┌─ 상단: 적 (HP바, 이름, 아트)
├─ 중앙: 데미지 팝업 / 턴·페이즈 표시 / 영웅 HP바
├─ 하단(엄지존):
│   주사위 5개 (탭 = 홀드)
│   [ Reroll   남은: 2 ]
│   [ 족보 버튼들 — 유효한 것만 활성, 예상 데미지 표기 ]
│   [ 평타 ]
└─
```

- 기존 컴포넌트 재사용: `Dice`, `ProgressBar`, `DamagePop`, `PrimaryButton`, `ForestMonster`, `RoundAnimalHero`, `Card`, `ChunkyBadge`.
- 족보 버튼에 **예상 데미지**를 함께 보여주면(예: `Full House x2.5 → 65`) 수동 선택의 의사결정이 명확해진다.
- 터치 타겟 ≥ 44px, 핵심 조작은 전부 하단.

## 6. 밸런스 메모

- 적 공격이 "1.2초마다"에서 **"턴당 1회"** 로 바뀌므로 `createEnemy`의 `attack` 곡선을 **반드시 재조정**해야 한다(안 하면 너무 쉬움). 한 전투가 몇 턴 만에 끝나는지를 기준으로 적 HP/공격을 튜닝.
- 평타 vs `chance` 차별화(§2 주의 참고): 둘 중 하나는 분명히 다른 가치를 줘야 함.
- 모든 적 수치/데미지 상수는 `constants.ts`에 모아 튜닝.
- 리롤 횟수(`maxRolls`)가 전투 체감 난이도를 크게 좌우한다 — 기본 3 유지하되 특성으로 증가(M3).

## 7. 테스트 / QA 영향

- `App.test.tsx`: "Open Dice" / "Force Dice Charge" 흐름이 사라지므로 **전투 관련 테스트 재작성** 필요. 새 전제: 콤뱃 진입 시 주사위가 이미 굴려져 있고 족보/평타 버튼이 보인다.
- `scripts/qa.mjs`: 동일하게 "Open Dice"/"Force Dice Charge" 단계 제거, 턴제 흐름(리롤 → 족보 선택 → 적 턴 → 반복 → 승리)으로 갱신.
- `combat.ts` / `yahtzee.ts` 순수 함수 단위 테스트는 **영향 없음**(데미지 공식·족보 판정 그대로).
- DEV QA 패널의 fixture 버튼들(`Force Pair/Full House Fixture`)은 턴제에서도 유용하니 유지하되, "주사위가 항상 열림" 전제에 맞게 동작 조정.

## 8. 구현 순서 (이 문서 기반 작업 시)

1. `constants.ts`에서 폐기 타이밍 상수 제거 + `enemyTurnDelayMs` 추가.
2. `CombatScreen.tsx`를 §3 상태머신으로 재작성(타이머/Open Dice 제거).
3. `basicAttack` 데미지원(`calculateHeroAutoDamage`) 연결.
4. `App.test.tsx` 전투 테스트 재작성.
5. `scripts/qa.mjs` 턴제 흐름으로 갱신.
6. 적 밸런스 1차 튜닝(§6).
7. `npm run build` / `npm test -- --run` / `npm run qa` 3종 통과.
