# Yacoo RPG — Game Plan v1

목표: 지금의 “초기 세팅/프로토타입”을 **스토어에 올렸을 때 10~20분은 재미를 느낄 수 있는 캐주얼 로그라이트 RPG**로 만든다.

이 문서는 바로 구현할 기능 목록이 아니라, 앞으로 구현이 산으로 가지 않게 잡아두는 **게임 방향/우선순위/완성 기준**이다.

---

## 1. 게임 한 줄 정의

**Yacoo RPG는 주사위 야츠제 족보로 공격하는 캐주얼 로그라이트 RPG다.**

플레이어는 짧은 런을 반복하며 전투, 보상 선택, 장비 강화로 점점 강해진다. 한 판은 가볍지만, 매 턴 주사위 선택과 족보 공격에서 작은 판단이 생겨야 한다.

---

## 2. 플레이어에게 주는 약속

스토어 유저가 설치 후 1분 안에 이해해야 하는 약속:

1. “주사위를 굴려 족보를 만들면 공격한다.”
2. “좋은 족보일수록 더 세게 때린다.”
3. “전투 후 보상을 골라 이번 런을 강화한다.”
4. “런이 끝나도 일부 재화/장비 성장은 남는다.”
5. “다음 판은 조금 더 강하게 시작한다.”

이 5개가 앱 안에서 명확히 느껴지면 게임답다.

---

## 3. 장르 기준

Yacoo는 아래 장르 조합으로 간다.

- **Core genre:** 캐주얼 RPG
- **Combat hook:** Yahtzee dice hand attack
- **Run structure:** Slay the Spire식 노드 진행을 아주 단순화
- **Meta progression:** 장비 강화 + 재화 축적
- **Session length:** 3~7분짜리 짧은 런
- **Target feel:** Archero/Habby류 짧은 반복 재미 + 주사위 판단

하면 안 되는 방향:
- 방치형 RPG처럼 전투가 자동으로만 흘러가는 것
- 퍼즐 게임처럼 전투 외 메타가 약한 것
- 복잡한 TRPG처럼 설명이 긴 것
- UI만 화려하고 실제 선택지가 없는 것

---

## 4. 핵심 재미 기둥

### 4.1 Dice Decision

주사위는 단순 랜덤 숫자가 아니라 “이번 턴에 뭘 노릴까?”를 만들기 위한 장치다.

필수 요소:
- 5개 주사위 굴림
- 원하는 주사위 hold
- 제한된 reroll 횟수
- 현재 가능한 족보 강조
- 더 좋은 족보를 노릴지, 지금 공격할지 선택

재미 포인트:
- Pair는 안전하지만 약하다.
- Full House/Small Straight는 욕심낼 가치가 있다.
- Yahtzee는 희귀하고 강해야 한다.
- reroll을 아껴야 다음 턴이 편해지는 느낌도 가능하다.

### 4.2 Fast Combat Feedback

전투는 느리면 안 된다. 매 턴 5~10초 안에 판단/결과가 나와야 한다.

필수 피드백:
- 주사위 굴림 소리/흔들림
- hold 시 클릭감
- 족보 공격 버튼 glow
- 공격 시 캐릭터 살짝 전진
- 데미지 숫자 크게 튐
- 적 피격 흔들림
- 적 반격 명확히 표시
- 승리/패배 전환 짧고 강하게

### 4.3 Reward Choice

전투 후 보상은 “그냥 받기”가 아니라 “이번 런 방향 선택”이어야 한다.

예시 보상:
- HP 회복
- 공격력 증가
- 방어력 증가
- 주사위 개수 +1
- reroll +1
- 특정 족보 강화
- 스크랩/코인 획득

초기 MVP에서는 3택 보상만 제대로 작동해도 충분하다.

### 4.4 Meta Growth

런이 끝나면 아무것도 안 남으면 허무하다.

남겨야 할 것:
- 코인
- 스크랩
- 장비 레벨
- 최고 챕터/스테이지
- 해금된 기능 placeholder

초기에는 장비 4개만 있어도 된다:
- Weapon: 공격력
- Armor: HP/방어
- Charm: 족보/치명 보너스 후보
- Boots: reroll/속도/회피 후보

---

## 5. 핵심 게임 루프

### 5.1 30초 루프

```text
주사위 굴림 → hold 선택 → reroll/공격 선택 → 데미지 → 적 반격 → 다음 턴
```

이 루프가 전투의 기본이다.

### 5.2 3분 루프

```text
RunMap 노드 선택 → Combat → RewardPick → 다음 노드 → 보스/런 종료
```

### 5.3 10분 루프

```text
런 여러 번 반복 → 코인/스크랩 축적 → 장비 강화 → 더 높은 챕터 도달
```

---

## 6. MVP 화면별 역할

### 6.1 Home

역할: 게임의 로비이자 플레이어 목표판.

반드시 보여줄 것:
- 현재 스테이지/최고 진행도
- 전투력
- 코인/젬/에너지
- 캐릭터
- START
- 장비/강화/상점/옵션 진입

Home에서 유저가 느껴야 하는 것:
- “내 캐릭터가 있다.”
- “강해질 수 있다.”
- “바로 시작할 수 있다.”
- “다음 목표가 보인다.”

### 6.2 Combat

역할: 핵심 재미 화면.

반드시 보여줄 것:
- 내 HP / 적 HP
- 턴 표시
- 주사위 5개
- hold 상태
- reroll 남은 횟수
- 가능한 족보 공격
- 기본 공격
- 나가기 버튼

전투 중 bottom nav는 숨김 유지.

### 6.3 RunMap

역할: 다음 선택지와 진행도 제공.

초기 MVP에서는 복잡한 맵보다 단순한 세로 노드 리스트로 충분하다.

노드 종류:
- Battle
- Elite
- Treasure
- Rest
- Boss

### 6.4 RewardPick

역할: 런 중 성장 선택.

3장 카드 선택 구조:
- 카드명
- 아이콘
- 효과 설명
- 즉시 적용

### 6.5 Equipment

역할: 영구 성장 확인.

장착 슬롯 4개 + 능력치 + 인벤토리 placeholder.

### 6.6 Upgrade

역할: 코인/스크랩 소비처.

장비 레벨업, 비용, 강화 결과 피드백.

### 6.7 Gacha/Shop

역할: 나중 과금/상점 자리.

현재는 placeholder지만, UI는 패키지 팝업처럼 게임답게 보여야 한다.

---

## 7. Combat Design v1

### 7.1 턴 시작

- 주사위는 비어 있거나 1로 초기화.
- `Roll Dice` 버튼 강조.
- 적은 idle 애니메이션.

### 7.2 Roll

- 모든 unheld dice가 빠르게 변함.
- 결과는 내부적으로 먼저 정해도 된다.
- 애니메이션 마지막 0.15초에 목표 눈으로 settle.

### 7.3 Hold

- 주사위 탭하면 hold.
- held dice는 위로 살짝 올라가거나 잠금 테두리.
- hold된 dice는 reroll에서 유지.

### 7.4 Attack Choice

가능한 족보만 활성화.

족보 티어 예시:

| Hand | Role |
|---|---|
| Chance | 안전한 약공격 |
| Pair | 기본 공격 |
| Two Pair | 안정 중간 공격 |
| Three of a Kind | 강한 단일 공격 |
| Small Straight | 중상급 공격 |
| Full House | 강한 공격 |
| Four of a Kind | 매우 강한 공격 |
| Large Straight | 매우 강한 공격 |
| Yahtzee | 궁극기 |

### 7.5 Damage Formula

초기에는 단순해도 된다.

```text
damage = hero.attack * handMultiplier + diceSumBonus
```

중요한 건 수식보다 체감이다.

- Chance도 쓸 수 있어야 함.
- Yahtzee는 명확히 강해야 함.
- 적 HP bar가 눈에 띄게 줄어야 함.

### 7.6 Enemy Turn

- 적이 0.3초 앞으로 튐.
- 내 HP 감소.
- 데미지 숫자 표시.
- 다시 내 턴.

### 7.7 Win/Loss

Win:
- Victory banner
- 보상 화면으로 이동

Loss:
- RunResult 또는 Home 결과
- 그래도 일부 코인/스크랩 지급 가능

---

## 8. Reward Design v1

### 8.1 Reward Card Types

초기 구현 우선순위:

1. Heal
   - 현재 HP 회복
2. Scrap
   - 강화 재료 획득
3. Dice
   - 이번 런 주사위 개수 +1
4. Reroll
   - 이번 런 reroll +1
5. Attack Up
   - 이번 런 공격력 +N
6. Defense Up
   - 이번 런 방어 +N
7. Hand Boost
   - 특정 족보 배율 +N%

### 8.2 Reward Choice Rules

- 항상 3장 제시.
- 같은 타입이 중복되어도 괜찮지만 수치는 다르게.
- 보상 선택 후 바로 RunMap으로 복귀.
- 보상 결과를 짧게 toast/banner로 표시.

### 8.3 Store-ready Fun Requirement

보상 카드가 “무의미한 숫자”로 보이면 안 된다.

각 카드는 다음 중 하나를 만족해야 한다:
- 다음 전투를 쉽게 만든다.
- 플레이 스타일을 바꾼다.
- 위험을 감수하게 만든다.
- 성장한 느낌을 준다.

---

## 9. Meta Progression v1

### 9.1 Permanent Currency

- Coin: 장비 강화
- Scrap: 고급 강화/제작 후보
- Gem: 현재 placeholder, 나중 상점/가챠

### 9.2 Equipment Upgrade

장비 강화는 가장 먼저 완성해야 할 메타 성장이다.

각 장비 역할:

| Slot | Primary effect |
|---|---|
| Weapon | attack 증가 |
| Armor | max HP / defense 증가 |
| Charm | hand multiplier / reward 보너스 후보 |
| Boots | reroll / initiative / dodge 후보 |

초기에는 Weapon/Armor만 명확해도 된다.

### 9.3 Unlock Roadmap

처음부터 다 열지 말고 진행도에 따라 잠금 해제한다.

| Unlock | 조건 |
|---|---|
| Upgrade | 기본 오픈 |
| Equipment | 기본 오픈 |
| RunMap | Stage 2 또는 즉시 오픈 |
| Gacha | Stage 5 이후 placeholder |
| Collection | 나중 |
| Daily/Quest | 나중 |

---

## 10. First-time User Experience

스토어 유저 첫 5분 기준.

### Minute 0
- 앱 실행
- 강한 로딩/타이틀 화면
- Home 진입
- START가 명확히 보임

### Minute 1
- 첫 전투
- 주사위 굴림
- 가능한 족보 highlight
- 공격 성공

### Minute 2
- 적 반격
- 두 번째 roll에서 hold/reroll 경험
- Victory

### Minute 3
- 보상 3택
- 선택 후 다음 노드

### Minute 5
- 런 종료 또는 2~3번째 전투
- 코인 획득
- 장비 강화 가능

첫 5분 안에 반드시 경험해야 할 것:
- 주사위 판단
- 보상 선택
- 성장 피드백
- 다음 판 이유

---

## 11. Content Plan v1

### 11.1 Enemies

초기 10개면 충분하다.

| Enemy | Behavior |
|---|---|
| Slime | 기본 약한 적 |
| Bat | 공격 약하지만 빠름 |
| Goblin | 평균형 |
| Shield Bug | 방어 높음 |
| Mage Blob | 공격 높음 |
| Elite Slime | HP 높음 |
| Elite Goblin | 공격 높음 |
| Beholder | 보스 |
| Dark Knight | 보스 후보 |
| Mimic | 보상/상점 연계 후보 |

### 11.2 Chapters

초기 3개 챕터.

| Chapter | Theme | Boss |
|---|---|---|
| 1 | Forest Ruins | Big Slime |
| 2 | Moon Castle | Beholder |
| 3 | Dark Mine | Dark Knight |

### 11.3 Node Count

한 런은 너무 길면 안 된다.

초기 추천:
- 6 nodes per chapter
- 4 battle
- 1 reward/rest
- 1 boss

---

## 12. UI/UX Direction

현재 사용자가 원하는 레퍼런스 방향:

- 다크 픽셀풍 판타지
- 높은 UI 밀도
- 상단/좌우/하단이 꽉 찬 모바일 RPG 로비
- 중앙 캐릭터 집중
- START CTA 명확
- 메뉴는 오른쪽 패널
- 패키지 팝업은 dim overlay 중앙 카드

UI 원칙:
- 유저가 “누를 곳”을 바로 알아야 한다.
- 전투 중에는 전투 UI만 보여야 한다.
- Home은 목표판이어야 한다.
- 보상/강화는 숫자만이 아니라 애니메이션과 시각적 피드백이 있어야 한다.

---

## 13. Sound/Haptic Plan

스토어 느낌을 위해 소리/햅틱은 필수다.

### Required SFX
- button click
- dice roll
- dice hold
- attack hit
- enemy hit
- victory
- defeat
- reward select
- upgrade success
- error/disabled

### Haptic
- dice hold: light tick
- roll: medium thump
- attack: impact
- victory: success pattern
- upgrade: success pattern

---

## 14. Implementation Roadmap

### Phase 1 — Product Shell and Home

목표: 앱 첫인상을 게임답게 만든다.

작업:
- HomeScreen 레퍼런스 구조로 재작성
- Options 오른쪽 패널화
- bottom nav 픽셀 슬롯 스타일
- LoadingScreen component 추가

완료 기준:
- 앱 켜자마자 “모바일 RPG 로비”로 보임.
- START 위치/기능 명확.

### Phase 2 — Combat Fun Pass

목표: 주사위 전투가 재미있게 느껴지게 만든다.

작업:
- dice roll animation 강화
- hold visual 강화
- 가능한 족보 glow
- 공격/피격 연출
- enemy turn 연출
- victory/defeat transition

완료 기준:
- 첫 전투만 해도 게임의 핵심 재미가 이해됨.

### Phase 3 — Reward and Run Loop

목표: 한 판 더 하고 싶게 만든다.

작업:
- RewardPick 3택 polish
- 보상 타입 확장
- RunMap 노드 시각화 개선
- 보상 적용 banner

완료 기준:
- 전투 후 보상 선택이 의미 있게 느껴짐.

### Phase 4 — Meta Progression

목표: 런이 끝나도 남는 성장.

작업:
- Equipment screen 정리
- Upgrade cost/feedback 개선
- 장비 강화 체감 강화
- Home 전투력/진행도 반영

완료 기준:
- 유저가 강화 후 더 강해졌다고 느낀다.

### Phase 5 — Store MVP Polish

목표: 스토어에 올릴 수 있는 최소 품질.

작업:
- onboarding hint
- settings/options 정리
- sound/haptic pass
- crash-free build
- responsive layout check
- placeholder가 너무 티나는 부분 제거

완료 기준:
- 10분 플레이 가능.
- 주요 화면에서 깨진 UI 없음.
- 빌드 성공.

---

## 15. One-week Build Plan

### Day 1
- Home redesign 완성
- Options panel 완성
- build 검증

### Day 2
- Combat animation/feedback pass
- dice hold/attack highlight 개선

### Day 3
- RewardPick 3택 재미 개선
- reward 타입 확장

### Day 4
- RunMap 흐름 정리
- chapter/node progression 정리

### Day 5
- Equipment/Upgrade 성장 체감 개선
- 강화 연출 추가

### Day 6
- Gacha/Shop placeholder를 패키지 팝업으로 교체
- Loading screen 추가

### Day 7
- QA
- 375x812 screenshot pass
- build pass
- store MVP checklist 점검

---

## 16. Store MVP Acceptance Criteria

스토어에 올릴 최소 기준:

### Game Loop
- [x] Home에서 START 가능
- [x] Combat에서 주사위 굴림/hold/reroll 가능
- [x] 족보 공격 가능
- [x] 적 반격 가능
- [x] 승리/패배 결과 가능
- [x] 보상 선택 가능
- [x] RunMap으로 진행 가능
- [x] 장비 강화 가능

### Fun
- [x] 첫 전투에서 뭘 해야 하는지 10초 안에 이해됨
- [x] 좋은 족보가 강하다는 게 체감됨
- [x] 보상 선택이 다음 전투에 영향 줌
- [x] 강화 후 수치/전투력이 오른 게 보임

### UI
- [x] Home이 게임 로비처럼 보임
- [x] Combat 중 bottom nav 없음
- [x] Options 클릭 가능
- [x] 모든 CTA가 safe-area와 충돌하지 않음
- [x] 한국어/영어 기본 동작 유지

### Technical
- [x] `assembleDebug` 성공
- [x] 앱 시작 crash 없음
- [x] 주요 route 이동 crash 없음
- [x] placeholder는 있어도 dead button은 최소화

---

## 17. 지금 당장 다음 액션

1. Antigravity/Opus로 `ANTIGRAVITY_OPUS_UI_REDESIGN_BRIEF.md` 기준 Home/Options/Gacha/Loading UI를 갈아엎는다.
2. 그 결과가 들어오면 이 문서 기준으로 Combat Fun Pass를 시작한다.
3. Combat Fun Pass 후 Reward/Run loop를 확장한다.

즉, 순서는:

```text
UI 로비 정체성 확립 → 전투 재미 강화 → 보상/런 루프 강화 → 메타 성장 강화 → 스토어 MVP polish
```

---

## 18. 구현 중 판단 기준

무언가 애매하면 아래 질문으로 결정한다.

1. 이 변경이 첫 5분 재미를 높이나?
2. 유저가 다음 판을 하고 싶게 만드나?
3. 주사위 전투의 판단을 더 명확하게 하나?
4. 성장 체감을 주나?
5. UI가 누를 곳을 더 명확하게 하나?

5개 중 하나도 해당 안 되면 지금 하지 않는다.
