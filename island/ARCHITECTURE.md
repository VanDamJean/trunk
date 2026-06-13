# Island — 아키텍처 설계

> 현재: HTML 목업 (디자인/플로우 검증용)  
> 목표: Android Native (Kotlin + Jetpack Compose)

---

## 설계 원칙

- **단일 책임 원칙** — 각 클래스/파일은 딱 한 가지 역할만
- **상태와 UI 분리** — ViewModel이 상태 소유, Composable은 렌더링만
- **단방향 데이터 흐름** — State → UI, Event → ViewModel
- **테스트 가능성** — MatchEngine, GravitySystem 등 순수 로직은 UI 의존성 없이 단독 테스트

---

## 전체 모듈 구조

```
island-android/
├── app/src/main/
│   ├── java/com/island/
│   │   ├── MainActivity.kt          ← 진입점, NavHost 설정
│   │   │
│   │   ├── navigation/
│   │   │   └── NavGraph.kt          ← 라우트 정의 (Home, Play, Game 등)
│   │   │
│   │   ├── game/
│   │   │   ├── engine/
│   │   │   │   ├── GameState.kt     ← 순수 데이터 (grid, score, moves, target)
│   │   │   │   ├── MatchEngine.kt   ← 매치 탐색 (findMatches, cascade)
│   │   │   │   ├── GravitySystem.kt ← 낙하 로직 (applyGravity, refill)
│   │   │   │   └── SwapValidator.kt ← 스왑 유효성 검사
│   │   │   │
│   │   │   ├── input/
│   │   │   │   └── DragController.kt← 터치/드래그 입력 처리
│   │   │   │
│   │   │   └── GameViewModel.kt     ← 상태 관리, engine 조합, UI 이벤트 처리
│   │   │
│   │   ├── ui/
│   │   │   ├── screen/
│   │   │   │   ├── HomeScreen.kt    ← 스플래시, Play 버튼
│   │   │   │   ├── PlayMapScreen.kt ← 섬 맵뷰, Level/Zone 버튼
│   │   │   │   ├── LevelScreen.kt   ← 레벨 선택 그리드
│   │   │   │   └── GameScreen.kt    ← 게임 플레이 (젬 그리드)
│   │   │   │
│   │   │   ├── component/
│   │   │   │   ├── GemGrid.kt       ← 6×6 젬 그리드 Composable
│   │   │   │   ├── GemCell.kt       ← 젬 하나 (색상, 애니메이션)
│   │   │   │   ├── ScoreBar.kt      ← Score/Target/Moves 상단 바
│   │   │   │   ├── ProgressBar.kt   ← 별 3개 진행 바
│   │   │   │   ├── TopBar.kt        ← 코인/젬/키 상단 UI
│   │   │   │   ├── BottomNav.kt     ← 하단 내비게이션 바
│   │   │   │   └── ToolBar.kt       ← 게임 중 아이템 툴바
│   │   │   │
│   │   │   └── modal/
│   │   │       ├── LevelSelectModal.kt
│   │   │       ├── BeginModal.kt
│   │   │       ├── YouWinModal.kt
│   │   │       ├── SettingsModal.kt
│   │   │       ├── EditProfileModal.kt
│   │   │       ├── TasksModal.kt
│   │   │       └── GoldenKeyModal.kt
│   │   │
│   │   ├── data/
│   │   │   ├── model/
│   │   │   │   ├── GemColor.kt      ← enum: BLUE, PINK, YELLOW, GREEN
│   │   │   │   ├── Level.kt         ← 레벨 데이터 (id, target, moves, theme)
│   │   │   │   └── PlayerProfile.kt ← 이름, 국기, 코인, 키
│   │   │   │
│   │   │   └── repository/
│   │   │       ├── LevelRepository.kt   ← 레벨 목록, 진행 상황
│   │   │       └── ProfileRepository.kt ← 유저 정보 (SharedPreferences)
│   │   │
│   │   └── ui/theme/
│   │       ├── Color.kt             ← 색상 팔레트
│   │       ├── Typography.kt
│   │       └── Theme.kt
│   │
│   └── res/
│       ├── drawable/                ← 배경 이미지, 아이콘
│       └── values/strings.xml
```

---

## 핵심 모듈 상세

### GameState.kt — 순수 데이터
```kotlin
data class GameState(
    val grid: Array<Array<GemColor?>>,  // 6×6, null = 빈 칸
    val score: Int = 0,
    val target: Int = 42,               // 남은 파란 젬 수집 수
    val moves: Int = 24,
    val isGameOver: Boolean = false
)
```

### MatchEngine.kt — 매치 탐색
```kotlin
object MatchEngine {
    fun findMatches(grid: Array<Array<GemColor?>>): Set<Pair<Int,Int>>
    fun hasAnyMatch(grid: Array<Array<GemColor?>>): Boolean
}
// UI 의존성 없음 → 단독 Unit 테스트 가능
```

### GravitySystem.kt — 낙하
```kotlin
object GravitySystem {
    fun apply(grid: Array<Array<GemColor?>>): Array<Array<GemColor?>>
    // 빈 칸을 아래로 채우고, 상단에 랜덤 젬 보충
    // 원본 불변, 새 grid 반환 → 테스트 용이
}
```

### DragController.kt — 드래그 입력
```kotlin
class DragController {
    // detectDragGestures (Jetpack Compose Modifier)
    // 드래그 방향 판별 → (fromRow, fromCol, toRow, toCol) 이벤트 방출
    // 최소 드래그 거리 threshold 설정
}
```

### GameViewModel.kt — 조합 및 상태 관리
```kotlin
class GameViewModel : ViewModel() {
    private val _state = MutableStateFlow(GameState(...))
    val state: StateFlow<GameState> = _state

    fun onSwap(r1, c1, r2, c2) {
        // SwapValidator → MatchEngine → GravitySystem → cascade 순서 처리
        // 코루틴으로 애니메이션 타이밍 제어 (delay)
    }
}
```

---

## 데이터 흐름

```
사용자 드래그
    ↓
DragController (Modifier.detectDragGestures)
    ↓ 스왑 이벤트
GameViewModel.onSwap()
    ↓
SwapValidator → MatchEngine → GravitySystem
    ↓ 새 GameState
StateFlow 업데이트
    ↓
GameScreen / GemGrid recompose
    ↓ (Compose 자동 갱신)
화면 반영
```

---

## 애니메이션 전략

| 애니메이션 | 구현 방법 |
|---|---|
| 젬 스왑 | `animateOffsetAsState` 또는 `Animatable` |
| 매치 플래시 | `animateFloatAsState` (scale + alpha) |
| 낙하 | `animateOffsetAsState` (y offset) |
| 새 젬 등장 | `scale(0→1)` + `fadeIn` |
| 연쇄 딜레이 | `viewModelScope.launch { delay(600) }` |

---

## HTML 목업 → Android 대응표

| HTML 목업 | Android |
|---|---|
| `screen-*` div | `NavHost` 라우트 Screen |
| `modal-*` div | `AnimatedVisibility` Dialog/Sheet |
| `.gem.matched` CSS | `Animatable` + `LaunchedEffect` |
| `.gem.new-drop` CSS | `animateFloatAsState` scale |
| `dragState` JS 변수 | `DragController` + ViewModel event |
| `grid[][]` JS 배열 | `GameState.grid` StateFlow |
| `findMatches()` | `MatchEngine.findMatches()` |
| `applyGravity()` | `GravitySystem.apply()` |
| `processMatches()` | `GameViewModel.onSwap()` 내부 코루틴 |
| `SceneManager` nav/modal | `NavController` + `remember { mutableStateOf }` |

---

## 구현 우선순위

```
1단계 — 게임 코어 (UI 없이 테스트)
  └─ GameState, GemColor, MatchEngine, GravitySystem, SwapValidator

2단계 — ViewModel + 상태 연결
  └─ GameViewModel (코루틴 애니 타이밍 포함)

3단계 — 게임 UI
  └─ GemGrid, GemCell, ScoreBar, ProgressBar, ToolBar

4단계 — 화면 연결
  └─ GameScreen, NavGraph

5단계 — 나머지 화면
  └─ HomeScreen, PlayMapScreen, LevelScreen + 모든 Modal

6단계 — 데이터 레이어
  └─ LevelRepository, ProfileRepository
```

---

## 참고

- 디자인 스펙: `island/FIGMA_NOTES.md`
- 목업 구현 상세: `island/mockup/SPECS.md`
- Figma 원본: `fileKey = 8vCqgMLyOEtUKLZ3LCSJf3`, 시작 노드 `135:23810`
