# Gemsin Android 앱 전환 조사 보고서

## 1. 목적

현재 `gemsin` 웹 목업을 기반으로, 추후 Android 앱으로 전환할 때 필요한 기술 선택, 화면 범위, 백엔드/API, 데이터 모델, 상태관리, 에셋 전략, 공수와 리스크를 정리한다.

현재 목업은 백엔드 없이 사용자 테스트 가능한 수준의 프론트 목업이며, 다음 화면/기능을 포함한다.

- 온보딩 3장
- 홈
- 게임 카드/상세 모달
- Leaderboard
- Credit & Ticket
- Teman(Friends)
- History
- Profile
- Daily Checkin
- Edit Profile

## 2. 현재 목업 상태 요약

### 구현 완료

- 화면 전환/탭 네비게이션
- mock 데이터 분리 (`mock-data.js`)
- localStorage 상태 유지
- 리더보드 검색/정렬/필터
- 친구 Follow/Following 토글
- 히스토리 필터
- 크레딧 claim/top-up mock
- 게임 상세 모달
- 이미지 슬롯 및 fallback 구조

### 남은 UI 정밀 작업

- Figma 실제 텍스트/이미지/수치 반영
- 버튼/폰트/간격/반경/그림자 1:1 보정
- 프로필/체크인/편집 화면의 Figma 기준 재보정

## 3. 추천 앱 스택

### 1안: Kotlin + Jetpack Compose

권장도: 높음

적합한 이유:

- Android 단독 출시 목적에 가장 적합
- Compose가 현재 목업처럼 카드/탭/리스트/모달 중심 UI 구현에 적합
- Android 플랫폼 기능(FCM, Play Billing, DataStore, Room, 권한, 딥링크)과 결합이 자연스러움
- 장기 유지보수와 Play Store 배포 안정성이 좋음

단점:

- iOS까지 확장하려면 별도 구현 필요
- 웹 목업 코드를 직접 재사용하기는 어려움

추천 구조:

- UI: Jetpack Compose
- 아키텍처: MVVM
- DI: Hilt
- 네트워크: Retrofit + OkHttp
- 이미지: Coil
- 로컬 저장: DataStore + Room
- 비동기: Kotlin Coroutines / Flow

### 2안: React Native

권장도: 중간

적합한 이유:

- 현재 웹 목업의 사고방식(컴포넌트/상태/JS 데이터)을 비교적 쉽게 이전 가능
- Android/iOS 동시 확장 가능

단점:

- Android 네이티브 기능, 성능, 빌드 이슈에서 관리 포인트가 늘어날 수 있음
- Play Billing, FCM, 이미지 캐싱 등에서 네이티브 브릿지 의존 증가

### 3안: Flutter

권장도: 중간

적합한 이유:

- 픽셀 단위 UI 재현에 강함
- Android/iOS 동시 구현 가능

단점:

- Dart/Flutter 학습 및 별도 생태계 진입 필요
- 기존 웹 목업 자산/구조의 직접 재사용성은 낮음

## 4. MVP 범위 제안

### MVP에 포함

- 온보딩
- 로그인/게스트 진입
- 홈
- 게임 리스트/게임 상세
- 리더보드
- 친구 목록
- 히스토리
- 프로필 조회/수정
- 데일리 체크인
- 크레딧 표시 및 mock top-up

### MVP에서 제외 또는 후순위

- 실제 결제
- 실시간 멀티플레이
- 채팅
- 푸시 알림 고도화
- 커뮤니티 피드
- 관리자 페이지
- 전체 게임 엔진 구현

## 5. 화면별 Android 전환 항목

| 화면 | Android UI 구성 | 필요한 데이터 | 백엔드 필요도 |
|---|---|---|---|
| Onboarding | Compose Pager / Navigation | 온보딩 문구, 이미지 | 낮음 |
| Home | LazyVerticalGrid, Card, BottomBar | 유저 크레딧, 게임 목록, 체크인 상태 | 중간 |
| Game Detail | BottomSheet/Dialog 또는 Screen | 게임 상세, 난이도, 플레이 상태 | 중간 |
| Leaderboard | Top3 Podium + LazyColumn | 랭킹, 지역/게임 필터, 아바타 | 높음 |
| Credit & Ticket | Wallet Card + Pack List | 크레딧, 패키지, 결제 상태 | 높음 |
| Teman | LazyColumn + Follow Button | 친구 목록, 팔로우 상태 | 중간 |
| History | Filter Chips + LazyColumn | 게임 이력, 점수, 날짜 | 중간 |
| Profile | Profile Header + Settings List | 사용자 정보, 아바타, 연락처 | 높음 |
| Daily Checkin | Calendar Grid | 출석 일자, 보상, claim 가능 여부 | 중간 |
| Edit Profile | Form + Validation | 이름, 전화, 주소, 이미지 | 높음 |

## 6. 백엔드/API 설계 초안

### Auth

- `POST /auth/google`
- `POST /auth/guest`
- `POST /auth/refresh`
- `POST /auth/logout`

필요 데이터:

- access token
- refresh token
- user id
- login provider

### User/Profile

- `GET /me`
- `PATCH /me/profile`
- `POST /me/avatar`
- `GET /me/settings`
- `PATCH /me/settings`

필요 데이터:

- name
- username
- phone
- address
- avatar url
- credit
- diamond

### Games

- `GET /games`
- `GET /games/{gameId}`
- `POST /games/{gameId}/start`
- `POST /games/{gameId}/result`

필요 데이터:

- game id
- title
- mode
- difficulty
- thumbnail
- current user best score

### Leaderboard

- `GET /leaderboard?scope=national|province|city&gameId=all&period=weekly|monthly`

필요 데이터:

- rank
- user id
- username
- avatar url
- score/ticket
- region

### Friends

- `GET /friends`
- `GET /friends/search?q=`
- `POST /friends/{userId}/follow`
- `DELETE /friends/{userId}/follow`

필요 데이터:

- friend user id
- username
- avatar url
- status
- following flag

### History

- `GET /me/history?range=today|yesterday|week`

필요 데이터:

- game name
- game thumbnail
- score
- played at
- reward/credit change

### Daily Checkin

- `GET /me/checkin`
- `POST /me/checkin/claim`

필요 데이터:

- current streak
- claimed days
- today claim state
- reward amount

### Credit/Ticket

- `GET /me/wallet`
- `GET /packs`
- `POST /packs/{packId}/purchase`

MVP에서는 mock 또는 sandbox 처리 가능.

## 7. DB 모델 초안

### users

- id
- provider
- provider_user_id
- name
- username
- phone
- address
- avatar_url
- credit
- diamond
- created_at
- updated_at

### games

- id
- title
- mode
- difficulty
- thumbnail_url
- is_active

### game_results

- id
- user_id
- game_id
- score
- reward_credit
- played_at

### leaderboard_entries

- id
- user_id
- game_id
- score
- scope
- period
- rank_snapshot
- updated_at

### follows

- id
- follower_id
- following_id
- created_at

### checkins

- id
- user_id
- claim_date
- reward_credit

### purchases

- id
- user_id
- pack_id
- provider
- status
- amount
- created_at

## 8. Android 상태관리 설계

### 앱 전역 상태

- auth session
- current user
- wallet/credit
- selected bottom tab

### 화면별 상태

- Leaderboard
  - selected region
  - selected game
  - selected period
  - search query
  - ranking list
- Friends
  - search query
  - follow state
- History
  - range filter
  - history list
- Daily Checkin
  - claimed days
  - reward state
- Edit Profile
  - form input
  - validation error
  - save loading state

### 권장 저장 위치

- DataStore
  - auth token
  - selected language
  - onboarding seen
- Room
  - cached games
  - cached profile
  - cached leaderboard snapshot
  - cached history

## 9. 이미지/에셋 전략

현재 목업에는 `avatar`, `thumb` 슬롯과 fallback 구조가 있다. Android 전환 시 이 구조를 그대로 가져가는 것이 좋다.

### 필요한 에셋 유형

- app icon
- splash logo
- onboarding pixel art
- game thumbnails
- user avatars
- leaderboard badge/crown
- bottom tab icons
- checkin calendar icons
- fallback avatar/icon

### Android 구현 권장

- 원격 이미지: Coil
- placeholder: 로컬 vector 또는 initials composable
- 실패 처리: fallback initials/icon
- 캐시: Coil disk/memory cache
- 형식: WebP 우선, 투명 아이콘은 VectorDrawable/SVG 변환

## 10. 플랫폼 기능

### MVP 우선순위 높음

- Google Sign-In
- Local session persistence
- Network error handling
- Offline fallback cache
- Play Store release signing

### 후순위

- FCM push notification
- Play Billing
- Deep links
- Analytics
- Crash reporting

## 11. 예상 공수

전제:

- Android 단독
- Kotlin + Jetpack Compose
- 백엔드 기본 API 포함
- 게임 자체는 웹뷰/미니게임 mock 또는 별도 구현 전

### 1단계: Android UI 이식

- 기간: 1~2주
- 내용:
  - 화면 구조
  - 네비게이션
  - mock 데이터
  - 디자인 1차 반영

### 2단계: 백엔드/API 연결

- 기간: 2~4주
- 내용:
  - auth
  - profile
  - leaderboard
  - friends
  - history
  - checkin

### 3단계: 품질/배포 준비

- 기간: 1~2주
- 내용:
  - 에러 처리
  - 로딩/empty state
  - QA
  - Play Console 내부 테스트

### 총 예상

- 빠른 MVP: 4~6주
- 디자인 정밀도 높은 MVP: 6~8주
- 결제/푸시/게임 로직 포함: 8~12주 이상

## 12. 주요 리스크

- Figma 정밀 수치 미확보
  - 영향: UI 1:1 재현 지연
  - 대응: 스크린샷 기반 1차 구현 후 Inspect/MCP로 핵심 수치만 보정
- 백엔드 스펙 불명확
  - 영향: 프론트-백 왕복 증가
  - 대응: API 계약서 먼저 작성
- 리더보드 정책
  - 영향: 점수 산정/동점/기간별 랭킹 처리 복잡
  - 대응: MVP에서는 weekly + all games부터 시작
- 크레딧/결제
  - 영향: 정책/보안/스토어 심사 이슈
  - 대응: MVP에서는 claim/top-up mock 또는 sandbox
- 게임 자체 구현
  - 영향: 공수 급증
  - 대응: 앱 MVP에서는 게임 실행 mock 또는 WebView/PWA 방식 검토

## 13. 추천 진행 순서

1. Figma 화면 스크린샷 전체 확보
2. Android 스택 확정
3. Android 프로젝트 생성
4. Design token 초안 작성
5. Compose 컴포넌트 분리
6. mock 데이터 기반 화면 이식
7. API 계약서 작성
8. 백엔드 MVP 구현
9. API 연결
10. QA/배포 준비

## 14. 결론

현재 `gemsin` 웹 목업은 Android 앱 전환을 위한 기능 흐름, 데이터 모델, 상태관리 방향을 잡기에 충분한 상태다.

다음 의사결정은 다음 3개가 핵심이다.

- Android 단독이면 Kotlin + Jetpack Compose 권장
- 백엔드는 auth/profile/leaderboard/checkin부터 수직 슬라이스로 붙이는 것이 효율적
- Figma는 전체 스크린샷 기반으로 먼저 맞추고, 수치 보정은 MCP/Inspect로 핵심 컴포넌트만 추출하는 방식이 비용 대비 가장 좋다
