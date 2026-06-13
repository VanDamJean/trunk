# Community Mobile App - 디자인 분석

## 화면 구성

### 1. Home 화면 (좌측)
- **헤더**: 시간(16:05), 햄버거 메뉴, 설정 아이콘
- **News 섹션**: 
  - 두 개의 그래디언트 카드 (보라색/분홍색, 청록색)
  - "Short news title will be here" 텍스트
  - 부분적으로 세 번째 카드 보임
- **Daily Tasks 섹션**:
  - 3개 항목: Daily (3), Daily deep (1), Daily mantra (2)
  - 각 항목에 숫자와 아이콘
- **Progress 섹션**:
  - "Your overall progress is 60%" 텍스트
  - 진행률 바
- **하단 항목들**:
  - "How was your day?" - 캘린더 아이콘 + 설명 텍스트
  - "Current Transit 3rd House" - 알림 아이콘 + 설명 텍스트
- **하단 네비게이션**: 5개 탭 (홈, 프로필, 중앙, 검색, 설정)

### 2. Profile 화면 (우측)
- **헤더**: 시간(16:05), 햄버거 메뉴, 설정 아이콘
- **프로필 섹션**:
  - 프로필 이미지 (파란색 얼굴)
  - 이름: "Angelica Jackson"
  - 직책: "Analyzer"
  - "Change profile" 링크
- **Strong side 섹션**:
  - 3개 태그: Analytics, Perfectionism, Analytics
- **Weak side 섹션**:
  - 2개 태그: Perfectionism, Analytics
- **My Reports 섹션**:
  - 6개 보고서 카드 (아이콘 + 제목)
  - Astro psychological report, Monthly prediction report, Daily Prediction, Love report

## 색상 팔레트
- 보라색: #5B3FA0 (News 카드)
- 분홍색: #D97B9E (News 카드)
- 청록색: #4ECDC4 (News 카드)
- 빨강: #FF6B6B (Daily deep, 알림 아이콘)
- 초록: #51CF66 (Daily mantra)
- 회색: #999999 (텍스트)
- 흰색: #FFFFFF (배경)

## 폰트
- 헤더: 중간 굵기 (Medium/SemiBold)
- 본문: Regular
- 숫자: Bold

## 레이아웃
- 모바일 중심 (약 375px 너비)
- 상단 고정 헤더
- 하단 고정 네비게이션
- 스크롤 가능한 중앙 콘텐츠

## 주요 컴포넌트
1. News Card - 그래디언트 배경, 텍스트
2. Daily Task Item - 숫자, 제목, 아이콘
3. Progress Bar - 선형 진행률 표시
4. List Item - 아이콘, 제목, 설명, 화살표
5. Tag - 작은 배지 스타일
6. Report Card - 아이콘, 제목, 설명
7. Bottom Navigation - 5개 탭
