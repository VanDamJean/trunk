# 08 — Yacoo RPG Android 포팅 가이드

## 프로젝트 개요

`yacoo_rpg-android/`는 웹(`yacoo_rpg/`) M1+M1.5+M1.6를 Kotlin/Jetpack Compose로 포팅한 Android 앱입니다.
패키지명: `com.yacoo.rpg`

---

## 빌드 & 실행 방법

### 전제 조건

| 도구 | 버전 |
|---|---|
| Android Studio | 최신 Stable (Meerkat 이상) |
| JDK | Android Studio 내장 JBR (21) |
| Android SDK | compileSdk 35, minSdk 26 |
| Gradle | 9.4.1 (wrapper 자동 다운로드) |

### JAVA_HOME 설정 (터미널 빌드 시)

```bash
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
export PATH="$JAVA_HOME/bin:$PATH"
```

### JVM 단위 테스트

```bash
cd yacoo_rpg-android
./gradlew testDebugUnitTest
# → 40개 테스트, 0 failures
```

### Debug APK 빌드

```bash
./gradlew assembleDebug
# → app/build/outputs/apk/debug/app-debug.apk
```

### 에뮬레이터/실기기 설치

```bash
./gradlew installDebug
```

---

## 프로젝트 구조

```
yacoo_rpg-android/
  app/src/main/java/com/yacoo/rpg/
    game/              ← Phase 1: 웹 game/*.ts → Kotlin 1:1 포팅
      Types.kt
      Constants.kt
      Yahtzee.kt       ← rollDice, evaluateHand, getValidAttackCategories
      Equipment.kt     ← getHeroStats, upgradeEquipment, canUpgrade
      Combat.kt        ← createEnemy, calculateUltimateCategoryDamage
      Rewards.kt       ← createWinReward, createLossReward
      Run.kt           ← createRun, advanceNode, settleRun
      AppState.kt      ← finishCombatMeta, claimRewardMeta
      Storage.kt       ← MetaStorage 인터페이스, migrateV1Chapter
    data/              ← Phase 2: DataStore 저장
      MetaRepository.kt
    viewmodel/         ← Phase 2: 앱 전역 상태
      GameViewModel.kt
    ui/
      theme/           ← YacooTheme, Color.kt (웹 CSS 토큰 매핑), Type.kt
      components/      ← Shell.kt (TopStatsBar, BottomNav, Buttons, HpBar)
      screens/         ← Phase 3+4
        HomeScreen.kt
        CombatScreen.kt
        EquipmentScreen.kt
        UpgradeScreen.kt
        ResultScreen.kt
    navigation/
      NavGraph.kt      ← 5화면 NavHost + YacooShell 래핑
    MainActivity.kt
  app/src/test/        ← Phase 1 JVM 테스트
    YahtzeeTest.kt     (16 tests)
    CombatTest.kt      (8 tests)
    EquipmentTest.kt   (9 tests)
    AppStateTest.kt    (7 tests)
```

---

## 각 단계 완료 기준

| Phase | 완료 기준 | 상태 |
|---|---|---|
| 0 골격 | `assembleDebug` 성공 + 5탭 전환 | ✅ |
| 1 로직 | `testDebugUnitTest` 40개 통과 | ✅ |
| 2 저장/VM | 앱 재시작 후 코인/장비 레벨 유지 | ✅ |
| 3 Shell+Home | Home ↔ Equipment/Upgrade 왕복 | ✅ |
| 4 Combat | Home → Combat → Result → Home 전체 루프 | ✅ |
| 5 QA | `assembleDebug` + 테스트 전체 통과 | ✅ |
| 6 연출 | (선택) Paperdoll/주사위/필드 Canvas 애니 | 미구현 |
| 7 런 루프 | (선택) 웹 M2 완료 후 | 미구현 |

---

## 주요 아키텍처 결정

| 결정 | 이유 |
|---|---|
| `game/` 폴더 UI 미포함 | 웹과 동일: 순수 비즈니스 로직, RNG 주입으로 단위 테스트 가능 |
| `GameViewModel` 단일 진입점 | 웹 `useGameState` 패턴 대응; Compose StateFlow 수집 |
| DataStore + JSON 수동 직렬화 | `kotlinx-serialization-json` 사용, v1→v2 마이그레이션 포함 |
| 로컬 `Screen` 없음 | NavGraph에서 `game.Screen` enum의 `.name.lowercase()`를 route로 사용해 타입 중복 제거 |
| Combat 상태머신 Composable 내부 | 한 전투 세션 내 임시 상태이므로 ViewModel로 올리지 않음 |

---

## 다음 단계 (Phase 6 — 선택)

웹 `07-VISUAL-AND-UX-POLISH.md` 수준의 그래픽/연출을 Android에 구현:

1. `HeroPaperdoll` — Compose `Canvas`로 장비 레이어 드로잉
2. 주사위 pip + throw-in/settle 애니메이션
3. 몬스터 등장/처치 애니메이션 (`Animatable`, `animateFloatAsState`)
4. SoundPool + `VibrationEffect` (촉각 피드백)
