# 07 — 비주얼 & UX 폴리시 명세 (껍데기/필드/홈/장비 그래픽)

> 이 문서는 **외부 AI(Sonnet / GPT 등)에게 "겉모습·연출" 작업을 맡기기 위한 핸드오프 명세**다.
> 게임 로직(데미지 공식, 족보 판정, 저장)은 이미 동작한다. 여기서 다루는 건 **"요즘 모바일 게임처럼 보이고 만지게" 만드는 일**이다.
> 작업 전 반드시 `05-CODING-GUIDELINES.md`를 먼저 읽을 것. 특히 `game/` 폴더는 건드리지 않는다(순수 로직). 이 작업은 거의 전부 `src/components/`, `src/screens/`, `src/styles.css`에서 일어난다.

## 0. 작업 범위 한눈에

사용자가 지적한 4가지를 그대로 영역으로 나눴다.

| 영역 | 한 줄 요약 | 주 파일 |
|---|---|---|
| **A. 껍데기(Shell)** | 프레임/상단바/하단탭이 요즘 모바일 게임 같지 않다 → 현대화 | `components/ui.tsx`, `styles.css` |
| **B. 필드 진행 + 주사위 던지기 연출** | 앞으로 전진하며 몬스터 처치/이동하는 그래픽이 없음, 주사위가 "실제로 던져지는" 느낌 없음 | `components/GameArt.tsx`, `screens/CombatScreen.tsx`, `styles.css` |
| **C. 홈 한 화면** | 홈 UI가 스크롤됨 → 한 화면에 압축 | `screens/HomeScreen.tsx`, `styles.css` |
| **D. 장비 페이퍼돌** | 장비가 캐릭터에 파츠별로 그래픽으로 안 붙음 | `components/GameArt.tsx`, `screens/EquipmentScreen.tsx`, `styles.css` |

> **권장 진행 순서: C → A → D → B.** C/A는 레이아웃 정리라 빠르게 체감되고, B(연출)는 가장 무겁고 재미 비중이 크니 마지막에 충분히 시간 들인다.

## 0.1 절대 규칙 (어기면 반려)

- **`game/` 폴더 수정 금지.** 데미지/족보/저장 로직은 그대로 둔다. 시각 작업만 한다.
- **기존 테스트(`*.test.tsx`, `*.test.ts`)와 `scripts/qa.mjs`를 깨지 말 것.** 이들은 `aria-label`, `role`, 텍스트(예: `전투`, `주사위 굴리기`, `기본 공격`, `N 피해`)로 요소를 찾는다. **접근성 라벨/텍스트를 바꾸려면 테스트도 같이 갱신**하고 이유를 남긴다. (현재 라벨 목록은 §6 참고)
- **외부 게임(딸깍 다이스 등)의 이미지/사운드/코드를 복사 금지.** 메커니즘과 "느낌"만 참고. 그림은 전부 자체 인라인 SVG / CSS로 만든다(`GameArt.tsx` 패턴).
- **디자인 토큰을 쓸 것.** 색/간격/반경은 `styles.css` `:root`의 `--color-*`, `--space-*`, `--radius-*`, `--shadow-*`를 사용한다. 하드코딩 hex/px 신규 추가 최소화.
- **기준 화면 = 세로 375×812, 한 손.** 모든 화면은 이 안에서 스크롤 없이(또는 최소로) 들어와야 한다. 터치 타겟 ≥ 44px(`--touch-target: 48px`).
- **완료 기준 3종:** `npm run build`, `npm test -- --run`, `npm run qa` 모두 통과.

## 0.2 현재 상태 스냅샷 (작업 시작점)

- 셸 구조: `Shell`(`components/ui.tsx`) = `TopStatsBar`(상단 Stage/Coins/Power) + `main.screen-area`(스크롤 영역) + `BottomNav`(Home/Combat/Equipment/Upgrade/Result 5탭).
- 아트: `components/GameArt.tsx`에 인라인 SVG 컴포넌트가 이미 있다 — `RoundAnimalHero`, `ForestMonster`, `LayeredArena`(하늘/언덕/길 배경), `EquipmentBadge`(weapon/armor/charm/boots 글리프), `DamagePop`, `RewardBurst`. **이걸 확장해서 쓴다. 새 라이브러리 추가 금지.**
- 전투: `screens/CombatScreen.tsx`가 §B의 주사위 굴림 애니메이션(`rollPhase: idle|rolling|settled`, `displayDice`, `.die.rolling` keyframe)을 이미 일부 갖고 있다. 이걸 "던지는 느낌"으로 끌어올리는 게 B의 절반이다.
- 디자인 톤: 둥근 동물 캐릭터 + 크림/연두/주황 파스텔, 두꺼운 외곽선, 입체 그림자(`--shadow-card-chunky`). 워크스페이스 `gemsin`/Habby 스타일과 결이 같다. **이 톤을 유지·강화**한다(완전히 새 아트 스타일 금지).

---

# A. 껍데기(Shell) 현대화

**문제:** 상단 스탯바 + 본문 + 하단 5탭이 "웹페이지" 같다. 요즘 모바일 게임 셸은 (1) 재화 HUD가 알약형 칩으로 우상단에 모이고, (2) 하단 탭이 아이콘+라벨의 도드라진 독(dock)이며, (3) 화면 전체가 기기 프레임 안에서 한 덩어리로 느껴진다.

**목표:** "야추 RPG 게임 화면을 열었다"는 첫인상. 로직·라우팅은 그대로, 보이는 셸만 바꾼다.

### A-1. 상단 HUD (`TopStatsBar` / `StatPill`)
- [ ] 재화 칩을 아이콘 동반 알약형으로: 코인은 `EquipmentBadge kind="coin"`(이미 있음) 글리프 + 숫자, 파워는 ⚡/검 글리프 + 숫자. Stage는 좌측에 "스테이지 뱃지"로 분리.
- [ ] 숫자 증감 시 살짝 튀는(scale pop) 트랜지션. 값 변경 키로 `key`를 줘서 CSS 애니메이션 재생(`DamagePop` 패턴 참고).
- [ ] 상단바를 `position: sticky; top: 0`로 고정하고 반투명 배경(`--color-stat-bg`) + 블러(`backdrop-filter: blur(...)`)로 떠 있는 느낌.
- [ ] 좌측에 작은 햄버거/설정 자리(아이콘만, 동작은 비워둬도 됨)를 둘지 검토 — 선택.

### A-2. 하단 내비 (`BottomNav`)
- [ ] 5탭 → 텍스트만에서 **아이콘 + 라벨** 2단 구성으로. 각 탭 아이콘은 인라인 SVG로 `GameArt.tsx`에 `NavIcon({name})` 추가(home/combat/equipment/upgrade/result). 외부 아이콘 폰트 금지.
- [ ] active 탭: 알약형 배경 강조 + 아이콘 약간 위로 떠오르는(translateY) 마이크로 인터랙션, `--color-primary-*` 사용.
- [ ] 독을 화면 하단에서 살짝 띄우고(부유형) `--shadow-nav`로 입체감. 세이프에어리어(`env(safe-area-inset-bottom)`) 패딩 고려.
- [ ] **주의:** `aria-current="page"`, 버튼 `name`(Home/Combat/...)은 유지. `qa.mjs`/테스트가 라벨로 탭을 누른다.

### A-3. 프레임 / 배경 (`app-shell`, `screen-area`)
- [ ] 데스크톱에서 볼 때 기기 목업(둥근 프레임 `--radius-shell`, `--shadow-shell`)을 더 명확히. 모바일 폭에서는 풀블리드.
- [ ] `screen-area`의 스크롤바를 숨기되 스크롤은 유지(`scrollbar-width: none` + `::-webkit-scrollbar { display:none }`).
- [ ] 화면 전환 시 가벼운 페이드/슬라이드 전환(150~200ms). 라우팅은 그대로, `screen-area` 자식에 `key={screen}` + CSS만으로.

### A-4. (선택) 토스트/피드백 레이어
- [ ] 코인 획득·업그레이드 완료 등 단발 피드백을 띄울 공용 토스트 슬롯을 셸 상단에 마련(M2/M4에서 재사용).

**A 완료 기준:** 라우팅/접근성 라벨 그대로, 상단 HUD·하단 독·프레임이 "모바일 게임 셸"로 보이고 탭 전환에 마이크로 인터랙션이 있다. 3종 통과.

---

# B. 필드 진행 + 주사위 "던지기" 연출 (★ 핵심)

이 영역이 "게임 같다"를 가장 크게 좌우한다. 두 덩어리로 나눈다: **B1 필드 진행 그래픽**, **B2 주사위 던지기**.

## B1. 필드 진행 (전진/처치/이동)

**문제:** 지금은 같은 자리에서 영웅 vs 몬스터가 정면으로 붙어 있을 뿐, "길을 따라 앞으로 나아가며 적을 처치한다"는 진행감이 전혀 없다.

**목표(연출 우선, 데이터는 가짜여도 됨):** 횡스크롤 러너 느낌의 "전진 → 적 조우 → 전투 → 처치 → 다시 전진"의 시각적 리듬. 실제 노드/맵 데이터 연동은 M2 몫이니, **여기서는 연출 컴포넌트와 상태 훅만 만들고 더미 진행으로 보여줘도 된다.**

- [ ] `GameArt.tsx`에 **스크롤 배경** 추가: `LayeredArena`를 확장하거나 `ScrollingField` 신규. 언덕/나무/길 레이어를 패럴랙스(앞 레이어 빠르게, 뒤 레이어 느리게)로 좌→우 이동. CSS `@keyframes` + `transform: translateX`.
- [ ] **영웅 전진 연출:** 적 처치 시 영웅이 다음 조우 지점까지 걸어가는(좌→우 이동 + 위아래 bob) 짧은 트랜지션(0.6~1.0s). `RoundAnimalHero`에 `walking` 상태 prop(다리/그림자 흔들림) 추가.
- [ ] **몬스터 처치 연출:** HP 0 시 몬스터가 납작해지거나(squash) 펑 터지며(scale↑→fade) 코인/조각이 튀는 `RewardBurst` 재사용. 처치 후 다음 몬스터가 우측에서 등장.
- [ ] **진행 인디케이터:** 화면 상단/하단에 "이번 챕터 N/총 M" 또는 작은 길 지도(노드 점들). `HomeScreen`의 `.stage-route`(노드 점+연결선) 스타일을 전투 화면용으로 재활용.
- [ ] **상태 훅:** `CombatScreen`에 연출 단계용 `stagePhase`(`approach` → `fight` → `victory` → `advance`) 같은 시각 전용 상태를 두되, **전투 로직(`phase`, `rollPhase`)과 분리**한다. 처치(`onFinish('win')`) 직후 `advance` 연출 → 다음 전투로.
- [ ] `prefers-reduced-motion` 존중: 모션 줄임이면 이동/패럴랙스를 즉시 컷으로.

> **데이터 경계 주의:** 실제로 "여러 적을 한 화면에서 연속" 도는 건 M2(런 루프)에서 챕터/노드가 들어와야 자연스럽다. B1은 **연출 레이어를 미리 만들어** M2가 데이터만 꽂으면 되게 한다. 무리하게 게임 로직을 바꾸지 말 것.

## B2. 주사위 "실제로 던지는" 연출

**현재:** `CombatScreen`에 `rollPhase` 상태머신과 `.die.rolling`(`die-tumble` keyframe: scale+rotate 흔들기)이 이미 있다. 굴리는 동안 `displayDice`가 랜덤 눈으로 깜빡인 뒤 최종값으로 정착한다. **느낌이 "흔들"에 가깝고 "던져서 굴러와 멈춘다"가 아니다.**

**목표:** 버튼을 누르면 주사위가 **던져져 화면으로 굴러 들어와(throw-in) → 통통 튀다 → 자리에 멈추는(settle)** 3D스러운 연출. 홀드한 주사위는 안 던져지고 그대로.

- [ ] **던지기 진입 연출:** `rolling` 시작 시 각 주사위가 하단/측면에서 `translate` + `rotate`로 굴러 들어오는 enter 애니메이션. 주사위마다 약간의 `animation-delay` 스태거(0/40/80…ms)로 흩뿌려지는 느낌.
- [ ] **굴림 → 정착:** `settled` 전환 시 마지막에 1회 바운스(`scale` overshoot 후 복귀)로 "탁" 멈추는 느낌. 현재 `die-tumble`을 enter/tumble/settle 3단계로 분리.
- [ ] **3D 주사위(권장):** 단일 숫자 `?`/`1` 대신, 핍(pip, 점) 배치로 주사위 눈을 그린 SVG 면(`DieFace`)을 `GameArt.tsx`에 추가. 가능하면 CSS 3D(`transform-style: preserve-3d`, 6면 큐브)로 굴림 중 면이 도는 느낌. 과하면 2D 핍 + 흔들림으로 대체 가능.
- [ ] **착지 효과:** 정착 순간 주사위 아래 먼지/충격 링(작은 scale-out ring), 가벼운 화면 흔들림(전투 영역 한정 미세 shake). 과하지 않게.
- [ ] **홀드 피드백:** 홀드된 주사위는 던져지지 않고 살짝 떠서 빛나는(glow, `--color-held*`) 상태 유지. 이미 `held` 클래스 있음 → 강화.
- [ ] **타이밍 상수 유지:** `ROLL_ANIM_MS`(현재 620), `ROLL_TICK_MS`(55)는 그대로 쓰거나 살짝 조정. **`import.meta.env.VITEST`일 때 `ROLL_ANIM_MS=0`(즉시 정착) 로직은 반드시 유지** — 안 그러면 테스트가 비동기 타이머로 깨진다.
- [ ] **사운드/햅틱 훅(선택):** 던질 때/멈출 때 훅 포인트만 남겨둔다(웹 Vibration API). 실제 사운드는 M4.
- [ ] `prefers-reduced-motion`이면 던지기 생략하고 바로 결과 표시.

**B 완료 기준:** (B2) 주사위 굴리기 버튼을 누르면 주사위가 던져져 굴러와 멈추는 게 분명히 보이고 홀드/리롤이 자연스럽다. (B1) 적 처치 시 전진·처치 연출이 재생된다(더미 진행 허용). 테스트의 즉시정착 경로 유지, 3종 통과.

---

# C. 홈 한 화면 압축

**문제:** `HomeScreen`이 카드 4개(`home-hero-card`, `stage-card`, `hub-actions-card`, `gear-summary-card`)를 세로로 쌓아 375×812에서 스크롤된다.

**목표:** 전투 화면처럼 **스크롤 없이 한 화면**. 영웅 히어로가 주인공으로 크게 보이고, 핵심 CTA(전투 시작)가 엄지존(하단)에 온다.

**참고:** §A 작업 후 셸이 바뀌면 본문 높이가 달라진다. **C는 A 이후에 하는 게 낫다.** 전투 화면의 `flex: 1; min-height: 0` + 섹션 분할 패턴(`styles.css`의 `.combat-screen`/`.combat-dice-section`)을 그대로 차용하면 됨.

- [ ] `HomeScreen` 루트를 `home-screen` 플렉스 컬럼(`flex:1; min-height:0`)으로. 전투 화면처럼 상단 비주얼 영역 / 하단 액션 영역으로 분할.
- [ ] **상단(주역):** `LayeredArena` + `RoundAnimalHero size="lg"` + Power. 여기에 **장비 페이퍼돌(§D)**을 얹어 영웅이 장비를 착용한 모습이 보이게(D와 연계).
- [ ] **스테이지 경로(`stage-route`)** 는 한 줄로 컴팩트하게 유지.
- [ ] **CTA:** `Start Combat`(=전투 시작)를 가장 크고 눈에 띄게 하단에. 업그레이드 가능 시 `RedDotMarker` 유지.
- [ ] **Camp Prep / Gear Summary** 는 카드를 합치거나 칩 스트립으로 납작하게. `Equipment`/`Upgrade` 진입은 아이콘 버튼 2개로 축소(하단 독과 중복되면 과감히 칩화). `Reset`은 작은 보조 버튼/설정 안으로.
- [ ] 코인/기어 요약은 한 줄 칩 스트립(`gear-strip` 재활용, 가로 스크롤 허용)으로.
- [ ] **접근성/테스트 주의:** `Start Combat`, `Equipment`, `Upgrade`, `Reset` 버튼의 접근성 이름을 유지(테스트/`qa.mjs`가 누름). 줄이더라도 `aria-label`로 이름 보존.

**C 완료 기준:** 375×812에서 홈이 세로 스크롤 없이 한 화면에 들어오고, 전투 시작 CTA가 명확하며 영웅이 주인공으로 보인다. 3종 통과.

---

# D. 장비 페이퍼돌 (캐릭터에 파츠 장착)

**문제:** 장비가 리스트/뱃지로만 표시된다(`EquipmentScreen`). "캐릭터에 무기·갑옷·장신구·신발이 그래픽으로 붙는" 맛이 없다.

**목표:** 영웅 SVG에 슬롯별 파츠가 **실제로 겹쳐 그려지는** 페이퍼돌. 레벨/종류에 따라 파츠가 바뀌거나 빛나면 성장 체감이 커진다.

**데이터:** 슬롯은 `weapon | armor | charm | boots` 4종(`types.ts` `EquipmentSlot`). 각 아이템은 `slot`, `name`, `level`, 선택적 `specialtyHand`을 가진다(`getEquipmentItems(equipment)`로 배열 획득).

- [ ] `GameArt.tsx`에 **`HeroPaperdoll`** 컴포넌트 신규: `RoundAnimalHero` 위에 슬롯별 파츠 SVG를 절대배치(`<g>` 레이어)로 겹친다. 좌표는 `viewBox="0 0 120 120"` 기준(영웅과 동일 좌표계).
  - weapon: 손/옆에 무기(검·지팡이 등) 글리프.
  - armor: 몸통에 갑옷/가슴판 오버레이.
  - charm: 목/머리 위 장신구(별·보석).
  - boots: 하단 발 부분 신발.
- [ ] **파츠 표시 규칙:** props로 장착 세트를 받아 슬롯별로 렌더. 레벨대(예: 1–3 / 4–6 / 7+)별로 색/디테일이 달라지는 간단한 티어 매핑. 비어있는 슬롯은 미표시.
- [ ] `EquipmentScreen` 상단을 리스트 대신 **페이퍼돌 프리뷰**로 교체/추가: 가운데 영웅+장착 파츠, 그 주위 4슬롯 칩(`EquipmentBadge`)에서 선택 시 해당 파츠가 강조(glow/pulse)되게.
- [ ] **홈 연계(C와 함께):** 홈 히어로에도 `HeroPaperdoll`을 써서 현재 장착이 항상 보이게.
- [ ] **업그레이드 피드백(선택):** `UpgradeScreen`에서 레벨업 시 해당 파츠가 반짝(`RewardBurst`/glow)하는 연출.
- [ ] 파츠는 전부 자체 인라인 SVG. 외부 에셋 금지. 기존 `--art-*` 토큰 색을 재사용.
- [ ] **접근성:** 페이퍼돌 컨테이너에 `role="img"` + `aria-label`(예: "장비 착용 영웅"). 슬롯 칩의 기존 라벨(`Lv N`, 슬롯명)·테스트 의존 텍스트 유지.

**D 완료 기준:** 장비/홈 화면에서 영웅에 무기·갑옷·장신구·신발이 그래픽으로 붙어 보이고, 슬롯 선택/레벨에 따라 시각 변화가 있다. 3종 통과.

---

# 5. 파일별 산출물 요약 (작업자 체크용)

| 파일 | 추가/수정할 것 | 관련 영역 |
|---|---|---|
| `src/components/GameArt.tsx` | `NavIcon`, `ScrollingField`(또는 `LayeredArena` 확장), `DieFace`, `HeroPaperdoll`, 처치/착지 이펙트 | A, B, D |
| `src/components/ui.tsx` | `Shell`/`TopStatsBar`/`StatPill`/`BottomNav` 마크업·클래스 보강, `Dice`에 3D/핍 표시 옵션 | A, B |
| `src/screens/HomeScreen.tsx` | 한 화면 플렉스 레이아웃, 페이퍼돌 연계, CTA 강조 | C, D |
| `src/screens/CombatScreen.tsx` | 던지기 연출 단계 강화, 필드 진행(`stagePhase`) 연출 훅 — **전투 로직은 건드리지 말 것** | B |
| `src/screens/EquipmentScreen.tsx` | 페이퍼돌 프리뷰 + 슬롯 선택 강조 | D |
| `src/styles.css` | 위 전부의 클래스/`@keyframes`. `:root` 토큰 재사용, 필요한 신규 토큰만 추가 | A,B,C,D |
| `src/App.tsx` | (필요 시) 화면 전환 `key`/래퍼 정도만. 라우팅 분기 변경 금지 | A |

> 새 파일이 정말 필요하면 만들어도 되지만(예: `components/FieldScene.tsx`), **기존 컴포넌트 확장이 우선**이다.

# 6. 건드리면 안 되는 접근성 라벨 / 텍스트 (테스트·QA 의존)

아래는 `App.test.tsx` / `scripts/qa.mjs`가 의존하는 식별 문자열이다. **시각만 바꾸고 이 이름들은 유지**한다. 부득이 바꾸면 해당 테스트/`qa.mjs`도 같은 커밋에서 갱신하고 사유를 남긴다.

- 전투 제목 heading: `전투`
- 주사위 초기 버튼: `🎲 주사위 굴리기`(텍스트에 `주사위 굴리기` 포함)
- 리롤 버튼: `리롤 (N회 남음)`
- 기본 공격 버튼 `aria-label`: `Basic Attack {damage}`
- 족보 버튼 `aria-label`: `{label} attack x{multiplier} ...`
- 피드백 텍스트: `{N} 피해` / `... 승리!` / `... 패배!`
- DEV 칩 `aria-label`: `Force Pair Fixture`, `Force Full House Fixture`, `Force Win`, `Force Loss`
- 하단 내비 버튼 이름: `Home`, `Combat`, `Equipment`, `Upgrade`, `Result` + `aria-current="page"`
- 홈/장비/업그레이드의 버튼 이름: `Start Combat`, `Equipment`, `Upgrade`, `Reset` 등

> 작업 전 `App.test.tsx`와 `scripts/qa.mjs`를 한 번 읽고, 본인이 바꾸려는 요소가 거기서 어떻게 선택되는지 확인할 것.

# 7. 마무리 체크리스트 (제출 전)

- [ ] `npm run build` 통과 (tsc 포함)
- [ ] `npm test -- --run` 통과 (라벨/텍스트 변경 시 테스트 동반 갱신)
- [ ] `npm run qa` 통과 (콘솔 에러 없음)
- [ ] 375×812에서 홈·전투가 스크롤 없이 들어옴
- [ ] `prefers-reduced-motion`에서 과한 애니메이션이 꺼짐
- [ ] `game/` 폴더 무수정 확인
- [ ] 새 색/간격은 가능한 한 기존 토큰 사용
- [ ] 외부 게임 에셋 미사용(전부 자체 SVG/CSS)
