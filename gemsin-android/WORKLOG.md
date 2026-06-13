# Gemsin Android 작업 내역

## 프로젝트 개요
`gemsin/` 웹 목업을 기반으로 Kotlin + Jetpack Compose Android 프로젝트 생성.

---

## 생성된 파일 구조

```
gemsin-android/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── gradle/
│   ├── libs.versions.toml
│   └── wrapper/
│       └── gradle-wrapper.properties
└── app/
    ├── build.gradle.kts
    └── src/main/
        ├── AndroidManifest.xml
        ├── res/values/themes.xml
        └── java/com/gemsin/app/
            ├── MainActivity.kt
            ├── data/
            │   └── MockData.kt
            ├── navigation/
            │   └── NavGraph.kt
            ├── ui/
            │   ├── theme/
            │   │   ├── Theme.kt
            │   │   └── Type.kt
            │   ├── components/
            │   │   ├── BottomBar.kt
            │   │   └── AvatarFallback.kt
            │   └── screens/
            │       ├── OnboardingScreens.kt
            │       ├── HomeScreen.kt
            │       ├── LeaderboardScreen.kt
            │       ├── TicketScreen.kt
            │       ├── FriendsScreen.kt
            │       ├── HistoryScreen.kt
            │       ├── ProfileScreen.kt
            │       ├── DailyCheckinScreen.kt
            │       └── EditProfileScreen.kt
```

---

## 작업 내역

### 1단계: 프로젝트 세팅
- `settings.gradle.kts`, `build.gradle.kts` 생성
- `libs.versions.toml` — AGP, Kotlin, Compose BOM, Navigation 버전 관리
- `gradle.properties` — JVM 힙 8192m으로 설정 (빌드 메모리 부족 이슈 대응)
- `AndroidManifest.xml`, `themes.xml` 생성

### 2단계: 공통 레이어
- **Theme.kt** — 다크 컬러스킴, Gemsin 브랜드 색상 정의
- **NavGraph.kt** — 11개 화면 Navigation Compose 라우팅
- **BottomBar.kt** — 하단 탭바 (Home / Rank / 🎟 / Teman / History)
- **AvatarFallback.kt** — 이미지 없을 때 이니셜 표시 컴포넌트

### 3단계: 데이터
- **MockData.kt** — `mock-data.js` → Kotlin data class로 이식
  - OnboardingItem, Game, LeaderboardEntry, Pack, Friend, HistoryEntry

### 4단계: 화면 구현 (10개)
| 화면 | 파일 | 주요 기능 |
|------|------|-----------|
| Onboarding 1~3 | OnboardingScreens.kt | 슬라이드 3장, Skip/Next/Done |
| Home | HomeScreen.kt | 게임 카드 그리드, 체크인 카드, 스탯, 게임 상세 Dialog |
| Leaderboard | LeaderboardScreen.kt | 포디움 Top3, 리스트, 검색/정렬/지역탭/범위토글 |
| Ticket | TicketScreen.kt | 크레딧 지갑, Daily Claim, 팩 구매 토스트 |
| Friends | FriendsScreen.kt | 친구 목록, 검색, Follow/Following 토글 |
| History | HistoryScreen.kt | All/Today/Yesterday 필터 |
| Profile | ProfileScreen.kt | 유저 정보, 체크인 카드, 설정 메뉴 |
| Daily Checkin | DailyCheckinScreen.kt | 28일 캘린더 그리드, 완료일 표시 |
| Edit Profile | EditProfileScreen.kt | 이름/유저명/전화/주소 폼 |

---

## 트러블슈팅 내역

| 문제 | 원인 | 해결 |
|------|------|------|
| `checkDebugAarMetadata` 빌드 실패 | composeBom / navigation-compose 버전 불일치 | BOM `2024.12.01`, navigation `2.8.5`로 업그레이드 |
| Gradle Daemon OOM | JVM 힙 기본 512m 부족 | `gradle.properties`에 `-Xmx8192m` 설정 |
| 홈 화면 게임 카드 너무 큼 | 갤탭 화면에서 `aspectRatio(1.4f)` 과대 | 고정 `height(140.dp)` + Row/chunked 그리드로 교체 |
| 홈 화면 하단 빈 공간 | `LazyVerticalGrid`를 `Column` 안에 중첩 | `verticalScroll` + 일반 Row 그리드로 교체 |

---

---

## 5단계: 미니게임 4개 구현

파일 구조 추가:
```
screens/
├── JumpManScreen.kt
├── FlappyBirdScreen.kt
├── UlarAngkaScreen.kt
└── TetrisScreen.kt
```

NavGraph에 4개 라우트 추가, HomeScreen Start Game 버튼에 게임별 라우팅 연결.

| 게임 | 조작 | 핵심 구현 | 속도 증가 |
|------|------|-----------|----------|
| Jump Man | 탭 | 중력 물리, 기둥 장애물, 충돌 감지 | ✅ frame 기반 |
| Flappy Bird | 탭 | 파이프 갭 통과, 천장/바닥 사망 | ✅ frame 기반 |
| Ular Angka | D-패드 ▲▼◀▶ | Grid 기반 스네이크, 벽/자기충돌 | ❌ 고정 (추후) |
| Tetris | ⟳ ◀ ▶ ▼▼ | 7종 테트로미노, 라인클리어, 고스트 피스, 벽킥 | ✅ 레벨업마다 |

공통 구조:
- `BoxWithConstraints`로 화면 크기 감지 → 셀/오브젝트 크기 동적 계산
- `LaunchedEffect` + `delay(16L)` 게임 루프 (~60fps)
- IDLE / RUNNING / DEAD 상태 머신
- 세션 내 베스트 점수 표시

---

## 트러블슈팅 내역

| 문제 | 원인 | 해결 |
|------|------|------|
| `checkDebugAarMetadata` 빌드 실패 | composeBom / navigation-compose 버전 불일치 | BOM `2024.12.01`, navigation `2.8.5`로 업그레이드 |
| Gradle Daemon OOM | JVM 힙 기본 512m 부족 | `gradle.properties`에 `-Xmx8192m` 설정 |
| 홈 화면 게임 카드 너무 큼 | 갤탭 화면에서 `aspectRatio(1.4f)` 과대 | 고정 `height(140.dp)` + Row/chunked 그리드로 교체 |
| 홈 화면 하단 빈 공간 | `LazyVerticalGrid`를 `Column` 안에 중첩 | `verticalScroll` + 일반 Row 그리드로 교체 |
| Jump Man 기둥이 점프보다 높음 | 기둥 높이 최대 `groundY * 56%`, 점프력 부족 | 기둥 높이 `10~28%`로 축소, JUMP_FORCE `-26f` / GRAVITY `0.7f`로 조정 |
| Ular Angka D-패드 화면 밖으로 밀림 | 보드 높이를 픽셀 고정값으로 계산해 화면 초과 | 보드를 `weight(1f)`로 변경, Canvas 내부에서 `minOf(w/COLS, h/ROWS)`로 셀 크기 동적 계산 |
| Tetris 가로모드 하단 잘림 | 셀 크기를 가로 기준으로만 계산 | `minOf(sw*0.62/BOARD_W, sh/BOARD_H)`로 세로도 고려 |
| TetrisScreen `verticalScroll` 미인식 | import 누락 | `rememberScrollState`, `verticalScroll` import 추가 |

---

## 현재 상태
- 갤탭 실기기 빌드/설치 확인 완료
- 앱 화면 10개 + 미니게임 4개 동작 확인
- 디자인은 테스트 수준 (Canvas 도형 기반)

## 미구현 / 후순위
- Ular Angka 시간 경과 속도 증가
- 실제 API 연결
- 게임 그래픽 에셋 교체 (스프라이트 PNG)
- 리더보드 지역/게임 필터 실제 데이터 분기
- 결제 (Play Billing)
- 푸시 알림 (FCM)
