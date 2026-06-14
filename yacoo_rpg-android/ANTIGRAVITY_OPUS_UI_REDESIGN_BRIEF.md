# Yacoo RPG — Opus UI Redesign Brief for Antigravity

이 문서는 Antigravity에서 Opus 모델에게 그대로 전달하기 위한 작업 지시서입니다.

> ⚠️ **이 문서는 `ART_DIRECTION_OVERHAUL_BRIEF.md`와 `IMPLEMENTATION_OVERHAUL_BRIEF.md`를 대체(supersede)합니다.**
> 기존 두 문서의 밝은 캐주얼 Idle RPG 방향(Legend of Slime/Mushroom Brave 스타일)은 폐기합니다.
> 새 방향은 **다크 픽셀 판타지**입니다.

목표는 현재 Yacoo RPG Android UI를 “조금 예쁘게” 다듬는 것이 아니라, 사용자가 제공한 5장 레퍼런스 스크린샷의 **위치, 배치, 화면 밀도, 레이어 구조를 거의 그대로 이식**하는 것입니다. 단, 게임 장르와 캐릭터/전투 시스템은 Yacoo RPG에 맞게 치환합니다.

---

## 0. 한 줄 목표

현재 밝은 카툰 RPG UI를 버리고, 레퍼런스처럼 **픽셀풍 다크 판타지 모바일 RPG 로비 + 오른쪽 메뉴 패널 + 패키지 팝업 + 로딩 화면** 구조로 재배치한다.

---

## 1. 사용자가 원하는 해석

- 레퍼런스와 “톤만 비슷하게”가 아니라 **위치와 배치를 거의 똑같이** 맞춘다.
- 외부 게임 에셋은 복사하지 않는다. 레이아웃/밀도/구성만 참고한다.
- Yacoo는 야츠제 주사위 전투 RPG이므로 기능명과 보상은 Yacoo에 맞게 치환한다.
- 현재 UI의 초원 배경, 큰 곰, 큰 노란 START 버튼 중심 구성은 버린다.

핵심 구성:
- 상단 좌측 프로필 카드
- 상단 우측 에너지/재화 바
- 중앙 상단 스테이지/지역명
- 중앙 캐릭터 + pedestal
- 좌우 세로형 이벤트/메뉴 버튼
- 중앙 하단 작은 기능 버튼 2개
- 중앙 하단 사다리꼴 START CTA
- 최하단 5개 탭 바, 가운데 Home 강조
- Options는 오른쪽 슬라이드/드롭다운 패널
- Shop/Gacha는 dim overlay + 중앙 패키지 팝업
- Loading은 풀스크린 일러스트 + progress bar

---

## 2. 프로젝트 컨텍스트

- 경로: `/Users/a1/Desktop/manus/yacoo_rpg-android`
- 스택: Kotlin + Jetpack Compose
- 패키지: `com.yacoo.rpg`
- 빌드:

```bash
JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" ./gradlew assembleDebug
```

- Kotlin LSP가 없을 수 있으므로 Gradle build를 최종 검증으로 사용한다.

---

## 3. 먼저 읽을 파일

| 파일 | 역할 |
|---|---|
| `app/src/main/java/com/yacoo/rpg/navigation/NavGraph.kt` | route wiring, shell 적용 |
| `app/src/main/java/com/yacoo/rpg/ui/components/Shell.kt` | top HUD, bottom nav, Options overlay |
| `app/src/main/java/com/yacoo/rpg/ui/screens/HomeScreen.kt` | 홈/로비. 이번 작업의 핵심 |
| `app/src/main/java/com/yacoo/rpg/ui/screens/CombatScreen.kt` | 전투. bottom nav hidden 유지 |
| `app/src/main/java/com/yacoo/rpg/ui/screens/GachaScreen.kt` | 상점/패키지 팝업 후보 |
| `app/src/main/java/com/yacoo/rpg/ui/screens/EquipmentScreen.kt` | 장비/인벤토리 |
| `app/src/main/java/com/yacoo/rpg/ui/theme/GameIcons.kt` | 아이콘 role/drawable mapping |
| `app/src/main/java/com/yacoo/rpg/ui/components/GameComponents.kt` | 버튼/카드/칩/보드 |
| `app/src/main/java/com/yacoo/rpg/ui/components/GameArt.kt` | 배경(`AdventureBackground`), 히어로 paperdoll, 몬스터 렌더링. 배경 변경의 핵심 파일 |
| `app/src/main/java/com/yacoo/rpg/ui/theme/Color.kt` | 전체 색상 토큰. §11 팔레트로 교체할 대상 |
| `app/src/main/java/com/yacoo/rpg/ui/theme/Type.kt` | 타이포그래피 토큰 |
| `app/src/main/res/drawable/` | 기존 PNG 에셋 |

---

## 3.5 현재 Composable → Brief 섹션 매핑

Opus는 아래 테이블을 참고하여 각 Composable을 어떤 섹션의 요구사항으로 변경해야 하는지 확인한다.

| Composable | 파일 | Brief 섹션 | 변경 방향 |
|---|---|---|---|
| `TopStatsBar()` | Shell.kt | §5-1 | 다크 반투명 pill로 재디자인 |
| `PlayerPill()` | Shell.kt | §5-1 | 프로필 카드 재디자인 |
| `StatPill()` | Shell.kt | §5-1 | 다크 톤 유지, 아이콘 업데이트 |
| `AdventureTicket()` | HomeScreen.kt | §5-2 | 큰 배너 → 작은 스테이지 태그/칩으로 축소 |
| `HorizontalStatsBar()` | HomeScreen.kt | 삭제 | 홈에서 제거. HP/ATK/DEF는 Gear 화면에서만 노출 |
| `HeroLobbyStage()` | HomeScreen.kt | §5-4 | 초원 → 다크 pedestal + 보라/남색 그림자 |
| `FloatingSideButton()` | HomeScreen.kt | §5-5,6 | 밝은 원형 → 다크 픽셀풍 사각 아이콘 |
| `HomeSideButton()` | HomeScreen.kt | - | 현재 미사용. 연결하거나 삭제 |
| `ModeShortcutRow()` | HomeScreen.kt | - | 레퍼런스에 없음. 삭제 또는 dead code 유지 |
| `RankingPreview()` | HomeScreen.kt | - | 레퍼런스에 없음. 삭제 또는 dead code 유지 |
| `GameButton(SECONDARY)` bottom | HomeScreen.kt | §5-8 | 사다리꼴 START 입장판으로 교체 |
| `ShellOptionsDialog()` | Shell.kt | §6 | `AlertDialog` → 우측 슬라이드 패널 |
| `YacooBottomNav()` | Shell.kt | §5-9 | 픽셀풍 슬롯형 두꺼운 테두리 바 |
| `AdventureBackground()` | GameArt.kt | §5-3 | 밝은 초원 → 어두운 숲/던전 배경 |
| `GachaScreen()` | GachaScreen.kt | §8 | 전체화면 → dim overlay + 중앙 팝업 |
| `DrawPanel()` | GachaScreen.kt | §8 | 팝업 내부 패널로 재배치 |

---

## 3.6 에셋 생성 지시 (Gemini Image Generation)

Opus에게 전달하기 전에, 아래 에셋을 **Gemini Image Generation**으로 미리 생성하여 `app/src/main/res/drawable/`에 배치한다.
생성하지 못한 에셋은 Canvas gradient + shape fallback으로 대체한다.

| 에셋 파일명 | 사이즈 | 설명 | Canvas 대체 가능 |
|---|---|---|---|
| `bg_home_dark_forest.png` | 1080x1920 | 어두운 보라/남색 숲 배경, 달빛, 실루엣 나무, 픽셀풍 | ✅ gradient fallback |
| `bg_loading_title.png` | 1080x1920 | 어두운 보라 배경, 타이틀 공간, 캐릭터/몬스터/주사위 실루엣 콜라주 | ✅ gradient fallback |
| `bg_start_trapezoid.9.png` | 선택 | 사다리꼴 입장판 텍스처 (금/주황 그라디언트) | ✅ Canvas Path 대체 가능 |
| `panel_options_bg.9.png` | 선택 | 오른쪽 패널용 다크 보라 배경 텍스처 | ✅ Brush.verticalGradient 대체 |

---

## 4. 현재 최신 상태

이미 적용된 상태를 유지한다.

- bottom nav 순서: `Options / Gear / Home / Upgrade / Draw`
- 가운데 돌출 탭은 `Home`
- `Options`는 현재 modal dialog를 열고 언어 변경 가능
- `Screen.COMBAT`에서는 bottom nav를 숨김
- `CombatScreen`에는 `Exit/나가기` 버튼이 있음
- `bottomNavContentClearance()` helper가 `Shell.kt`에 있음

이번 redesign 중에도 **전투 중 bottom nav가 다시 보이면 안 됨**.

---

## 5. Reference 1 — 메인 로비 화면

`HomeScreen.kt`를 이 레이아웃으로 완전히 재구성한다.

### 실제 레퍼런스 분석 (텝빨용사 메인 로비)

```
┌─────────────────────────────────────────────┐
│ [👤용사835832] [390]     [⚡35/35] [💎0]   │ ← 상단 HUD
│                                             │
│              최고 기록 0/12                  │ ← 작은 진행도 배지
│         2. 니못잎이 시드는 곳                │ ← 스테이지명 (큰 흰 텍스트)
│              ⚔️ 267                         │ ← 전투력 배지
│                                             │
│ [특건]     ┌──────────────┐   [러시이벤트]   │ ← 좌/우 세로 버튼
│ [23시간]   │              │   [LIMITED]      │
│            │   🗡️ 🛡️      │   [초대]         │
│            │  PixelKnight │                  │
│            │   on stone   │                  │
│            │  pedestal    │                  │
│            └──────────────┘                  │
│           ───── dark forest bg ─────         │
│                                             │
│           [챔터 돌파]  [원정]                 │ ← 2개 소형 기능 버튼
│                                             │
│           ┌───────────────┐                  │
│           │    시 작       │                  │ ← 사다리꼴 START
│           │    ⚡ 5        │                  │
│           └───────────────┘                  │
│                                             │
│ [서버 공지 스크롤 배너 ...]                   │ ← 하단 공지 배너
│                                             │
│ ┌──┐ ┌──┐ ┌════┐ ┌──┐ ┌──┐                 │ ← Bottom Nav 5탭
│ │⚙ │ │🛡│ │ 모험 │ │⬆│ │🎲│                 │    가운데 '모험' 강조
│ └──┘ └──┘ └════┘ └──┘ └──┘                 │
└─────────────────────────────────────────────┘
```

### 375x812 기준 상세 구조

1. **최상단 HUD (status bar 아래, Y≈50~80px)**
   - **좌측 프로필 pill**: 다크 보라 배경 `#1A1025` + 2px 밝은 테두리
     - 픽셀 캐릭터 아이콘 (28dp, 원형, 다크 배경)
     - 유저명 `용사835832` → Yacoo: `Yacoo` 또는 `meta.playerName`
     - 전투력 `390` → Yacoo: `meta.coins` 또는 `heroStats.power`
   - **우측 재화 row**: 에너지 `⚡35/35`, 젬 `💎0`
     - 각각 작은 pill (다크 chrome 배경 + 굵은 테두리)
     - 에너지: Yacoo에 없으면 placeholder `⚡--`
     - 젬: `meta.gems` (현재 0)

2. **스테이지 정보 영역 (Y≈100~180px)**
   - 첫 줄: `최고 기록 0/12` → 작은 회색 배지 (가운데 정렬)
   - 둘째 줄: `2. 니못잎이 시드는 곳` → **큰 흰색 Bold 텍스트** (20sp+)
     - Yacoo 치환: `${meta.bestChapter}. 앰버베이 II` 또는 스테이지명
   - 셋째 줄: `⚔️ 267` → 전투력 배지 (보라/주황 pill)
     - Yacoo: `heroStats.power`

3. **배경 (전체화면)**
   - **어두운 숲**: 남색-보라 톤, 실루엣 나무와 풀, 안개/연무 효과
   - **색상**: 상단 `#0B0B18` ~ 중단 `#121025` ~ 하단 `#1C1635`
   - 나무 실루엣: 양쪽 가장자리에 어둡게
   - **레퍼런스에서 핵심**: 배경이 단순 gradient가 아니라 실루엣 나무가 보인다

4. **중앙 캐릭터 + pedestal (Y≈220~450px, 화면 중앙~중앙 아래)**
   - **캐릭터**: 픽셀 기사 (칼+방패), 약 120~160dp 크기
     - Yacoo: `HeroPaperdollCanvas(equipment = meta.equipment)`
   - **pedestal**: 회색-보라 톤 타원형 돌 플랫폼
     - 약 180dp 너비, 40dp 높이의 납작한 타원
     - 밝은 가장자리 하이라이트 + 어두운 그림자
   - **`HorizontalStatsBar` (HP/ATK/DEF)는 홈에서 완전 제거**

5. **좌측 세로 버튼 (X≈0~60dp, Y≈180~350dp)**
   - 화면 왼쪽 가장자리에 붙어있음 (edge-anchored)
   - 세로로 2~3개 쌓인 작은 사각 버튼 (40~50dp)
   - 레퍼런스 항목:
     - `특건` + 타이머 `23시간` (이벤트 아이콘 + 빨간 배지)
     - 곰 아이콘 (캐릭터 관련)
   - **Yacoo 치환**: 
     - `퀘스트` (GameIconRole.TREASURE) → `onNavigate(Screen.RUN_MAP)` 
     - `출석` (GameIconRole.REWARD) → placeholder/disabled
   - 스타일: 다크 배경 + 2px 밝은 테두리 + 작은 텍스트 라벨 아래

6. **우측 세로 버튼 (X≈315~375dp, Y≈180~350dp)**
   - 화면 오른쪽 가장자리에 붙어있음
   - 레퍼런스 항목:
     - `러시 이벤트` + `LIMITED` 빨간 배지
     - `초대`
   - **Yacoo 치환**:
     - `이벤트` (GameIconRole.STAR) → placeholder
     - `초대` → placeholder/disabled
   - 같은 스타일 (다크 사각 + 테두리)

7. **중앙 하단 기능 버튼 2개 (Y≈480~520dp)**
   - START 바로 위, 가로 배치 (간격 12dp)
   - 레퍼런스: `챔터 돌파`, `원정` (각각 아이콘 + 텍스트)
   - **Yacoo 치환**:
     - `챔터 돌파` → `RUN_MAP` (GameIconRole.TREASURE) 
     - `원정` → placeholder/gacha shortcut (GameIconRole.CHEST)
   - 스타일: 작은 직사각 버튼 (60x40dp), 다크 배경 + 아이콘

8. **START CTA (Y≈530~590dp)**
   - **형태**: 사다리꼴/입장판 (위가 좁고 아래가 넓은 trapezoid)
   - **크기**: 약 200dp 폭, 56dp 높이
   - **배경**: 짙은 다크 보라-남색 `#1A1025` + 밝은 보라 테두리 `#7A58C8`
   - **텍스트**: `시작` / `START` (굵은 흰색 14-16sp)
   - **에너지 비용**: 아래 작은 행에 `⚡5` (노란-주황색)
   - **press 피드백**: scale 0.95 + glow (Reference 3에서 확인)
   - 클릭은 기존 `onStartCombat()` 유지
   - ⚠️ **둥근 버튼 절대 금지. Canvas Path로 trapezoid 그리기**

9. **공지 스크롤 배너 (Y≈600~620dp)**
   - START와 bottom nav 사이에 한 줄 공지 텍스트 배너
   - `[서버] 극강: kr-185섭 1위 [대한민국]클랜에서...` 식의 가로 스크롤
   - Yacoo 치환: 간단한 placeholder 텍스트 또는 생략 가능

10. **Bottom Nav (Y≈640~712dp + safe area)**
    - **5개 탭**: 좌→우 순서
    - 레퍼런스 탭: 뭔가, 뭔가, `모험`(가운데·강조), 뭔가, 뭔가
    - **Yacoo 순서 유지**: `Options / Gear / Home / Upgrade / Draw`
    - 스타일:
      - 다크 navy 배경 `#0F0A1A`
      - 각 아이콘은 사각 슬롯 형태 (둥근 pill 아님)
      - **가운데 `Home/모험`만 위로 돌출** + 강조 색상
      - 비활성 탭: 어둡고 회색 (`#888899`)
      - 활성 탭: 밝은 보라/흰색
    - safe-area/home indicator 충돌 금지

### 성공 기준

- 레퍼런스 1과 같은 위치 구조로 읽힌다.
- 현재처럼 초원 위 큰 곰 + 큰 노란 버튼이면 **실패**.
- 좌우 세로 버튼, 중앙 캐릭터 on pedestal, 스테이지명, 작은 기능 버튼 2개, 사다리꼴 START, 공지 배너, bottom nav가 모두 있다.
- 배경이 어두운 숲이고, 전체 UI가 다크 톤이다.

---

## 6. Reference 2 — 오른쪽 Options/Menu 패널

현재 `ShellOptionsDialog`는 임시다. 레퍼런스처럼 오른쪽 패널로 바꾼다.

### 요구사항

- Options bottom tab 또는 우측 메뉴 버튼 클릭 시 오른쪽 메뉴 패널 표시
- 배경 dim 처리
- 패널은 화면 오른쪽 60~70% 폭
- 항목:
  - `공지` / Notice
  - `우편` / Mail
  - `인벤토리` / Inventory
  - `도감` / Collection
  - `설정` / Settings
- 실제 기능 없는 항목은 disabled 또는 “준비 중” 처리
- Settings 또는 하단 영역에서 언어 변경 가능
- 닫기 X 버튼 포함

### 구현 위치

- `Shell.kt`의 `ShellOptionsDialog`를 `ShellOptionsPanel`로 교체
- `AlertDialog` 대신 `Box` overlay + 오른쪽 anchored panel 사용

### 성공 기준

- 기본 Material AlertDialog처럼 보이면 실패
- 레퍼런스 2처럼 오른쪽에 붙은 보라/다크 메뉴판이어야 함

---

## 7. Reference 3 — START 터치/진입 상태

- START는 사다리꼴/입장판 형태
- press scale + glow 피드백
- 내부에 `시작/START`와 `⚡ 5`
- 클릭 동작은 기존 `onStartCombat()` 그대로

---

## 8. Reference 4 — 패키지/가방 팝업

`GachaScreen.kt` 또는 reusable popup으로 레퍼런스 4 구조를 구현한다.

### 레이아웃

- 전체 배경 dim
- 상단 빨강/주황 리본 타이틀
  - 예: `모험가 보급 상자` / `Adventure Supply Pack`
- 중앙 큰 chest/pack illustration
- 2개 소형 상품 카드 + 1개 대형 상품 카드
- 각 카드:
  - grid preview
  - 수량 badge
  - 가격 버튼
  - 실제 결제가 없으면 disabled/placeholder
- 하단 닫기 X

### 성공 기준

- 레퍼런스 4처럼 중앙 상점 팝업으로 읽힘
- 기존 GachaScreen의 카드 겹침/큰 크림 패널 느낌 제거

---

## 9. Reference 5 — 로딩 화면

새 component 또는 screen으로 만든다.

### 요구사항

- 파일: `ui/components/LoadingScreen.kt` (component로 만든다)
- **Screen enum에 추가하지 않는다.** NavGraph에 연결하지 않는다.
- 앱 시작 시 MainActivity에서 잠깐 보여주는 splash 컴포넌트로 사용하거나, Preview/Component만 만들어두고 실제 navigation에는 연결하지 않는다.
- 어두운 보라/남색 배경
- 중앙 Yacoo RPG title
- 주변 캐릭터/몬스터/주사위/장비 실루엣 collage
- 하단 progress bar
- 텍스트: `모험장에 접근하는 중` / `Entering adventure...`
- 실제 loading flow가 없으면 preview/component만 구현

---

## 10. 우선순위

### P0
- HomeScreen을 Reference 1/3 구조로 완전 재배치
- Options AlertDialog를 Reference 2 오른쪽 패널로 교체

### P1
- Gacha/Shop을 Reference 4 패키지 팝업으로 변경
- Loading component 추가

### P2
- Combat, Gear, Upgrade, Result, RunMap을 같은 다크 픽셀풍 토큰으로 맞춤
- CombatScreen의 DicePanel(`DiceView.kt`)을 다크 픽셀 스타일 floating board로 재디자인

---

## 11. 디자인 토큰 방향

### Palette

- Background: `#0B0B18`, `#121025`, `#1C1635`
- Panel: `#211A3A`, `#2D214A`, `#3A2A60`
- Border: `#090612`, `#4D3A78`, `#7A58C8`
- Accent purple: `#8E44FF`, `#B75CFF`
- Energy cyan: `#45E8FF`
- CTA yellow/orange: `#F6B43B`, `#FFCC4D`
- Ribbon red/orange: `#D7392F`, `#FF6533`

### Shape

- 픽셀풍 사각 panel 위주
- 너무 둥근 pill 남발 금지
- radius 4~10dp 중심
- 외곽선 두껍게

### Motion

- START: press scale + glow
- Options: right slide-in
- Popup: scale/bounce-in
- Bottom nav: selected slot glow

---

## 12. 데이터 치환표

| Reference 기능 | Yacoo 치환 |
|---|---|
| 프로필/용사 ID | Yacoo profile, best chapter |
| 에너지 35/35 | placeholder stamina/entry energy |
| 보석 | gems placeholder `0` |
| 최고 기록 0/12 | run/map progress |
| 지역명 | `Stage ${meta.bestChapter}` |
| 전투력 | `heroStats.power` |
| 던전 돌파 | `Screen.RUN_MAP` |
| 원정 | placeholder/reward/gacha shortcut |
| 시작 ⚡5 | `onStartCombat()` |
| 메뉴 항목 | Options panel entries |
| 워리어 가방 패키지 | Adventure Supply Pack |
| 로딩 | Yacoo title + current drawable collage |

---

## 13. Guardrails

1. `game/` 로직 수정 금지.
2. `Screen` enum route 추가는 가급적 금지. Options는 overlay panel 유지.
3. Combat에서 bottom nav를 다시 보이게 하지 말 것.
4. `onStartCombat`, `onNavigate`, `onLanguageChange`, `onReset` 동작 유지.
5. 한국어/영어 label 유지.
6. `WindowInsets.safeDrawing` 제거 금지.
7. 외부 게임 에셋 복사 금지.
8. `assembleDebug` 통과 필수.

---

## 14. 권장 구현 순서

0. **(최초 작업)** `Color.kt`의 기존 밝은 팔레트를 §11 다크 픽셀 팔레트로 교체.
   - `ColorSurfacePanel`, `ColorCard`, `ColorParchment` 등을 다크 계열로 전환
   - `ColorPrimaryTop`/`ColorPrimaryBottom`은 CTA 오렌지/옐로 유지
   - `AdventureBackground()`(GameArt.kt)의 밝은 초원을 다크 배경으로 변경
1. `HomeScreen.kt`를 section 단위로 재작성:
   - `HomeTopHud`
   - `HomeStageLabel`
   - `HomeSideButtons`
   - `HomeHeroPedestal`
   - `HomeQuickActions`
   - `HomeStartPanel`
   - `HorizontalStatsBar`, `ModeShortcutRow`, `RankingPreview` 삭제 또는 dead code 처리
2. `Shell.kt` bottom nav visual을 레퍼런스 bottom slot bar처럼 retune.
3. `ShellOptionsDialog`를 `ShellOptionsPanel`로 교체.
4. `GachaScreen.kt`를 supply pack popup style로 변경.
5. Loading screen component 추가 (`ui/components/LoadingScreen.kt`, Screen enum 추가 금지).
6. Build and screenshot review.

---

## 15. Acceptance Checklist

Home:
- [ ] 레퍼런스 1과 같은 위치 구조다.
- [ ] 상단 좌측 프로필, 상단 우측 재화 HUD가 있다.
- [ ] 중앙 stage/title이 있다.
- [ ] 중앙 캐릭터가 pedestal 위에 있다.
- [ ] 좌우 세로 버튼이 있다.
- [ ] START가 사다리꼴/입장판 형태다.
- [ ] bottom nav의 center가 Home이다.

Options:
- [ ] Options 클릭 가능.
- [ ] Material AlertDialog가 아니라 오른쪽 패널이다.
- [ ] 공지/우편/인벤토리/도감/설정 항목이 있다.
- [ ] 닫기 X가 있다.
- [ ] 언어 변경 가능.

Combat:
- [ ] 전투 중 bottom nav가 보이지 않는다.
- [ ] Exit/나가기 버튼이 있다.

Gacha/Shop:
- [ ] dim overlay + 중앙 pack popup 구조다.
- [ ] 카드/가격/닫기 버튼이 보인다.

Loading:
- [ ] full-screen title art + progress bar component가 있다.

Build:
- [ ] `assembleDebug` 성공.

---

## 16. Antigravity용 프롬프트

아래를 Opus 모델에게 그대로 전달하세요.

```text
You are working in /Users/a1/Desktop/manus/yacoo_rpg-android.

Implement the UI redesign described in ANTIGRAVITY_OPUS_UI_REDESIGN_BRIEF.md.

The user provided five reference screenshots. Match their layout and screen density very closely:
1. dark pixel-fantasy lobby with top profile/resources, center character pedestal, side buttons, two small actions, trapezoid START, bottom tab bar
2. right-side options/menu panel overlay
3. pressed START entrance panel state
4. dimmed supply-pack/shop popup
5. full-screen loading/title art with progress bar

Do not merely polish the current bright cartoon UI. Rebuild the Home screen composition to match the reference positions and hierarchy. Tone may differ to fit Yacoo RPG, but placement and layout should be very close.

Read these first:
- ANTIGRAVITY_OPUS_UI_REDESIGN_BRIEF.md
- app/src/main/java/com/yacoo/rpg/ui/screens/HomeScreen.kt
- app/src/main/java/com/yacoo/rpg/ui/components/Shell.kt
- app/src/main/java/com/yacoo/rpg/navigation/NavGraph.kt
- app/src/main/java/com/yacoo/rpg/ui/theme/GameIcons.kt
- app/src/main/java/com/yacoo/rpg/ui/components/GameComponents.kt

Guardrails:
- Do not change game logic in game/.
- Do not copy external copyrighted assets.
- Preserve Korean/English language support.
- Preserve safe-area handling.
- Keep bottom nav hidden during Combat.
- Keep Home as the center bottom tab.
- Options should be a right-side panel, not a basic AlertDialog.

Implementation priority:
P0: Rebuild HomeScreen to match reference 1 and 3.
P0: Replace Options AlertDialog with right-side menu panel matching reference 2.
P1: Redesign Gacha/Shop as dimmed supply-pack popup matching reference 4.
P1: Add loading screen component matching reference 5.
P2: Align remaining screens with the dark pixel-fantasy token system.

Validate with:
JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" ./gradlew assembleDebug

Return a short summary of changed files, what was implemented, and any screenshots/manual QA notes.
```
