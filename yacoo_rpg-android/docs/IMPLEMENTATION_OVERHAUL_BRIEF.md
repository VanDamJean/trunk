# Implementation Overhaul Brief: Compose Refactoring Instructions

본 문서는 `ART_DIRECTION_OVERHAUL_BRIEF.md`에서 정의한 카툰 하이브리드 캐주얼 아트 스타일을 Jetpack Compose 코드 상에서 실제로 구현하기 위한 기술 설계 및 리팩토링 지침서입니다.

---

## 1. 핵심 설계 패턴: 카툰 UI 커스텀 Modifier

하이브리드 캐주얼 비주얼의 핵심인 **굵은 테두리(Outline)**와 **입체 3D 버튼(Cartoon Shadow)**을 ad-hoc 방식으로 매번 그리지 않고, Compose 공통 Modifier로 구현하여 재사용합니다.

### 1.1 Cartoon Border Modifier
```kotlin
fun Modifier.cartoonBorder(
    strokeWidth: Dp = 3.dp,
    color: Color = Color(0xFF1C1A1F),
    shape: Shape = RoundedCornerShape(12.dp)
): Modifier = this.border(width = strokeWidth, color = color, shape = shape)
```

### 1.2 Cartoon Shadow Modifier (입체 단차 구현)
단순한 Blur 그림자가 아니라, 우측 하단으로 픽셀만큼 밀려난 **Solid Color 음영 단차**를 표현합니다.
```kotlin
fun Modifier.cartoonShadow(
    shadowOffset: Dp = 4.dp,
    color: Color = Color(0xFF1C1A1F),
    shape: Shape = RoundedCornerShape(12.dp)
): Modifier = this.drawBehind {
    // 3D 입체 단차를 표현하기 위해 지정된 offset 만큼 이동하여 배경을 그리고
    // 그 위에 본래의 콘텐츠와 테두리가 올라가도록 DrawScope를 커스텀 구현합니다.
    // (또는 Compose Offset & GraphicLayer를 중첩하여 레이아웃 구조로 처리 가능)
}
```

### 1.3 Cartoon Clickable Modifier (누를 때 물리적 눌림 효과)
버튼을 클릭하면 아래로 2~3dp 만큼 Offset이 이동하고, 음영 단차가 줄어드는 효과를 구현합니다.
```kotlin
@Composable
fun Modifier.cartoonClickable(
    onClick: () -> Unit,
    enabled: Boolean = true
): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val translationY by animateDpAsState(
        targetValue = if (isPressed) 3.dp else 0.dp,
        label = "PressedTranslation"
    )
    
    return this
        .offset(y = translationY)
        .clickable(
            interactionSource = interactionSource,
            indication = null, // Ripple을 끄거나 카툰풍 커스텀 인디케이션 제공
            enabled = enabled,
            onClick = onClick
        )
}
```

---

## 2. 파일별 작업 범위 & 디자인 토큰 설정

### 2.1 디자인 토큰 재정의
* **대상 파일**: [Color.kt](file:///Users/a1/Desktop/manus/yacoo_rpg-android/app/src/main/java/com/yacoo/rpg/ui/theme/Color.kt), [Theme.kt](file:///Users/a1/Desktop/manus/yacoo_rpg-android/app/src/main/java/com/yacoo/rpg/ui/theme/Theme.kt)
* **작업 내용**:
  - 기존 Material3 테마 색상을 하이브리드 캐주얼 전용 팔레트로 교체합니다.
  - **테두리 전용 색상**: `CharcoalBorder = Color(0xFF1C1A1F)`
  - **콘텐츠 배경색**: `WarmIvory = Color(0xFFF9F5EB)`, `CreamGray = Color(0xFFEFECE6)`
  - **등급별 색상**:
    - 일반(C/B): `GradeGreen = Color(0xFF8DE855)`
    - 희귀(A): `GradeBlue = Color(0xFF3BAFFC)`
    - 에픽(S): `GradePurple = Color(0xFFB16BFF)`
    - 전설(SS): `GradeOrange = Color(0xFFFF973B)`
  - **하이라이트 버튼 그라데이션**: 
    - `ButtonYellowGrad = Brush.verticalGradient(listOf(Color(0xFFFFDF6D), Color(0xFFFFB800)))`

### 2.2 공통 컴포넌트 리팩토링
* **대상 파일**: [Shell.kt](file:///Users/a1/Desktop/manus/yacoo_rpg-android/app/src/main/java/com/yacoo/rpg/ui/components/Shell.kt)
* **작업 내용**:
  - `TopStatsBar`: 하트(HP), 칼(공격력), 방패(방어력) 등 스탯 지표와 재화 바를 캡슐 모양(`CircleShape`) 안에 다크 그레이 배경으로 감쌉니다. 우측의 `+` 아이콘은 밝은 녹색의 입체 사각형 버튼으로 변경합니다.
  - `BottomNav`: 하단 네비게이션 탭바의 아이템들을 굵은 외곽선의 카툰 아이콘으로 교체하고, 선택된 탭은 위로 솟아오르는 인터랙션과 함께 골드/옐로우 테두리로 감싸 시각적 주목도를 높입니다.

---

## 3. 화면별 구현 상세 지침

### 3.1 HomeScreen 리팩토링
* **대상 파일**: [HomeScreen.kt](file:///Users/a1/Desktop/manus/yacoo_rpg-android/app/src/main/java/com/yacoo/rpg/ui/screens/HomeScreen.kt)
* **작업 내용**:
  - 중앙의 챕터 배너 영역을 크고 두꺼운 카드뷰로 감싸고, 만화 톤의 퍼플색 배경과 오리지널 보스 일러스트(공허의 눈)의 비주얼을 크게 살립니다.
  - "시작" 버튼을 하단 중앙에 크고 입체감 있는 옐로우 그라데이션 버튼으로 배치하여 손맛을 극대화합니다.
  - 랭킹 및 챕터 패키지 영역을 하이브리드 캐주얼풍의 단상 일러스트 디자인으로 개편합니다.

### 3.2 Gear / Equipment Screen 리팩토링
* **대상 파일**: [EquipmentScreen.kt](file:///Users/a1/Desktop/manus/yacoo_rpg-android/app/src/main/java/com/yacoo/rpg/ui/screens/EquipmentScreen.kt), [UpgradeScreen.kt](file:///Users/a1/Desktop/manus/yacoo_rpg-android/app/src/main/java/com/yacoo/rpg/ui/screens/UpgradeScreen.kt)
* **작업 내용**:
  - 캐릭터 쇼케이스 뷰어에 Canvas를 활용한 심플한 그리드 배경 및 캐릭터의 귀여운 곰 실루엣을 배치합니다.
  - 장비 인벤토리의 그리드 아이템 카드를 둥글고 두꺼운 테두리로 변경하고, 아이템 등급 색상을 배경에 얹어 화려하게 표현합니다.
  - 강화 가능 장착 슬롯에 깜빡이는 녹색 위쪽 화살표(▲) 배지를 부착합니다.

### 3.3 CombatScreen 리팩토링
* **대상 파일**: [CombatScreen.kt](file:///Users/a1/Desktop/manus/yacoo_rpg-android/app/src/main/java/com/yacoo/rpg/ui/screens/CombatScreen.kt)
* **작업 내용**:
  - 몬스터와 플레이어 간의 대치 영역을 굵은 외곽선 캐릭터 렌더링으로 개선합니다.
  - 주사위 보드 영역에 빈티지 가죽 매트 스타일의 브라운 톤을 주어 테이블탑 RPG의 느낌을 카툰풍으로 재해석합니다.
  - 킵(Keep)한 주사위 주변에 골드빛 번개 테두리를 그려 상태를 눈에 띄게 처리합니다.

### 3.4 Gacha / Store Screen 리팩토링
* **대상 파일**: (기존 Gacha Screen이 미포함되어 있다면 신규 구현하거나 상점 탭 내부 확장)
* **작업 내용**:
  - 만화풍의 패키지 상점 느낌을 살리기 위해, 패키지 카드 뒤쪽에 사선 빗금 패턴이나 번개 무늬 데코레이션을 동적으로 드로잉합니다.
  - 보석/골드로 상자를 여는 버튼들을 굵고 입체감 넘치게 배치합니다.

### 3.5 ResultScreen 리팩토링
* **대상 파일**: [ResultScreen.kt](file:///Users/a1/Desktop/manus/yacoo_rpg-android/app/src/main/java/com/yacoo/rpg/ui/screens/ResultScreen.kt)
* **작업 내용**:
  - 승리 시 골드 리본 배너 애니메이션과 함께 보상 아이템들이 카드가 뒤집히며 화려하게 스케일업되도록 연출합니다.
  - 패배 시 어두운 다크 그레이 톤으로 연출하고, 장비 성장을 권장하는 단추 버튼을 배치합니다.

### 3.6 RunMap 리팩토링 (또는 신규 확장)
* **대상 파일**: (현재 네비게이션 및 스크린 경로 상에 챕터 진행 경로 표현이 포함되어 있을 경우)
* **작업 내용**:
  - 로그라이크 형태의 지하철 노선도 또는 구불구불한 숲길 맵을 Compose Canvas Path로 굵고 귀엽게 그리고, 각 노드의 아이콘(칼, 물약, 상자 등)을 카툰풍 입체 구체로 처리합니다.

---

## 4. 빌드 & 검증 조건

### 4.1 빌드 검증
모든 비주얼 요소와 Modifier 개편 이후에도 다음 빌드 명령어가 에러 없이 완료되어야 합니다:
```bash
./gradlew assembleDebug
```

### 4.2 UI 단위 테스트 및 프리뷰 검증
* 각 Screen 파일 하단에 대표 상태별 `@Preview` 컴포넌트를 최소 2개 이상 추가하여, 에뮬레이터 없이 Android Studio의 Split/Design 뷰에서 즉시 UI 스타일을 확인할 수 있도록 합니다.

---

## 5. CRITICAL RULE: 게임 로직 변경 절대 금지

> [!IMPORTANT]
> 본 개편은 오직 **UI/UX 비주얼과 인터랙션의 고도화**만을 대상으로 합니다.
> `app/src/main/java/com/yacoo/rpg/game/` 폴더 하위에 위치한 순수 게임 비주얼 독립 로직(주사위 계산, 데미지 계산, 장비 강화 수식 등)을 담은 다음 파일들은 **단 한 줄의 코드 수정도 허용하지 않습니다.**
> - `AppState.kt`
> - `Combat.kt`
> - `Constants.kt`
> - `Equipment.kt`
> - `Rewards.kt`
> - `Run.kt`
> - `Storage.kt`
> - `Types.kt`
> - `Yahtzee.kt`
>
> 리팩토링 완료 후 반드시 다음의 로직 단위 테스트가 **전부 통과(Success)**되는지 확인하십시오.
> ```bash
> ./gradlew testDebugUnitTest
> ```
