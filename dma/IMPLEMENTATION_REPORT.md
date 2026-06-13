# Community Mobile App - 구현 리포트

## 📋 프로젝트 개요

피그마 디자인(Mobile application Community)을 완벽하게 구현한 반응형 웹 애플리케이션입니다. React 19 + Tailwind CSS 4를 기반으로 모바일 우선 설계 원칙을 따르며, 데스크톱 환경에서도 2컬럼 레이아웃으로 동시에 두 페이지를 표시합니다.

---

## 🎨 디자인 철학 및 방법론

### 1. 색상 시스템 (Color Palette)

피그마 디자인에서 추출한 정확한 색상값을 CSS 변수로 정의하여 일관성을 유지했습니다.

| 색상명 | HEX 값 | 용도 |
|--------|--------|------|
| Primary Purple | #5B3FA0 | 주요 버튼, 액센트, 헤더 |
| Pink | #D97B9E | News 카드, 진행률 바 |
| Teal/Cyan | #4ECDC4 | News 카드, 태그 |
| Red | #FF6B6B | Daily deep 카운트, 경고 아이콘 |
| Green | #51CF66 | Daily mantra 카운트 |
| White | #FFFFFF | 배경, 텍스트 대비 |
| Light Gray | #F8F9FA, #E9ECEF | 카드 배경, 테두리 |

**구현 위치**: `client/src/index.css` (`:root` CSS 변수)

### 2. 타이포그래피 (Typography System)

두 가지 폰트를 전략적으로 사용하여 시각적 계층구조를 강화했습니다.

```css
/* Google Fonts에서 import */
@import url('https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&family=Inter:wght@400;500;600;700&display=swap');

/* 폰트 할당 */
body { font-family: 'Inter', sans-serif; }        /* 본문, 설명 텍스트 */
h1, h2, h3, h4, h5, h6 { font-family: 'Poppins', sans-serif; font-weight: 600; } /* 제목 */
```

**선택 이유**:
- **Poppins**: 현대적이고 친근한 느낌의 디스플레이 폰트 (제목, 헤더)
- **Inter**: 가독성이 우수한 본문 폰트 (설명, 본문)

### 3. 레이아웃 전략 (Layout Architecture)

#### 모바일 레이아웃 (< 1024px)
- **단일 컬럼**: Home 페이지만 표시
- **고정 헤더**: 상단에 고정된 헤더 (sticky)
- **고정 하단 네비게이션**: 5개 탭 (Home, Profile, Center, Search, Settings)
- **스크롤 가능한 콘텐츠**: 중앙 영역만 스크롤

```
┌─────────────────────┐
│  Header (sticky)    │
├─────────────────────┤
│                     │
│  Scrollable Content │
│                     │
├─────────────────────┤
│ Bottom Nav (fixed)  │
└─────────────────────┘
```

#### 데스크톱 레이아웃 (≥ 1024px)
- **2컬럼 레이아웃**: Home (좌측) + Profile (우측) 동시 표시
- **경계선**: 두 컬럼 사이에 회색 경계선
- **독립적 스크롤**: 각 컬럼이 독립적으로 스크롤

```
┌────────────────────┬────────────────────┐
│  Home Header       │  Profile Header    │
├────────────────────┼────────────────────┤
│                    │                    │
│  Home Content      │  Profile Content   │
│  (scrollable)      │  (scrollable)      │
│                    │                    │
└────────────────────┴────────────────────┘
```

**구현 방식**: Tailwind의 `lg:` 반응형 클래스 사용
```jsx
<div className="flex flex-col lg:flex-row">
  {/* 모바일: 세로 배치, 데스크톱: 가로 배치 */}
  <div className="flex-1 w-full lg:max-w-md lg:border-r">
    {/* Home */}
  </div>
  <div className="hidden lg:flex flex-1 w-full lg:max-w-md">
    {/* Profile - 데스크톱에서만 표시 */}
  </div>
</div>
```

### 4. 컴포넌트 설계 (Component Architecture)

#### 원자적 컴포넌트 (Atomic Components)

**NewsCard** - 그래디언트 배경 카드
```jsx
// 특징: 동적 그래디언트, 호버 효과, 그림자
<div className={`bg-gradient-to-br ${gradient} hover:scale-105 shadow-lg`}>
```

**DailyTaskCard** - 숫자 표시 카드
```jsx
// 특징: 동적 색상, 중앙 정렬, 작은 점 인디케이터
<div className="text-center">
  <p className="text-3xl font-bold" style={{ color }}>
```

**ProgressSection** - 진행률 표시
```jsx
// 특징: 그래디언트 진행률 바, 백분율 텍스트
<div className="bg-gradient-to-r from-pink-500 to-purple-600" 
     style={{ width: `${progress}%` }}
```

**ListItem** - 아이콘 + 텍스트 리스트 항목
```jsx
// 특징: 아이콘, 제목, 설명, 우측 화살표, 호버 효과
<div className="flex gap-3 hover:bg-gray-100">
```

**ReportCard** - 리포트 타일
```jsx
// 특징: 중앙 정렬, 아이콘 배지, 2줄 텍스트 제한
<div className="text-center hover:shadow-md">
```

**TagBadge** - 색상 배지
```jsx
// 특징: 제한된 색상 옵션 (teal, pink, purple), 타입 안전성
<span className={`px-3 py-1 rounded-full ${colorClasses[color]}`}>
```

**BottomNav** - 하단 네비게이션
```jsx
// 특징: 모바일 전용 (md:hidden), 5개 탭, 활성 상태 표시
<div className="fixed bottom-0 md:hidden">
```

#### 페이지 컴포넌트 (Page Components)

**Home.tsx** - 메인 페이지
- 상태 관리: `currentPage` (home/profile)
- 반응형 레이아웃 제어
- 모바일에서 페이지 전환, 데스크톱에서 동시 표시

**Profile.tsx** - 프로필 페이지
- 프로필 정보 표시
- Strong/Weak side 태그
- 4개 리포트 카드 그리드

---

## 🛠️ 기술 스택

| 항목 | 버전 | 용도 |
|------|------|------|
| React | 19.2.1 | UI 라이브러리 |
| TypeScript | 5.6.3 | 타입 안전성 |
| Tailwind CSS | 4.1.14 | 유틸리티 CSS |
| Vite | 7.1.7 | 빌드 도구 |
| Wouter | 3.3.5 | 클라이언트 라우팅 |
| lucide-react | 0.453.0 | 아이콘 라이브러리 |
| shadcn/ui | - | UI 컴포넌트 (기본 제공) |

---

## 📁 프로젝트 구조

```
community-app/
├── client/
│   ├── src/
│   │   ├── pages/
│   │   │   ├── Home.tsx              # 메인 페이지
│   │   │   ├── Profile.tsx           # 프로필 페이지
│   │   │   └── NotFound.tsx          # 404 페이지
│   │   ├── components/
│   │   │   ├── NewsCard.tsx          # 뉴스 카드
│   │   │   ├── DailyTaskCard.tsx     # 일일 과제 카드
│   │   │   ├── ProgressSection.tsx   # 진행률 섹션
│   │   │   ├── ListItem.tsx          # 리스트 항목
│   │   │   ├── ReportCard.tsx        # 리포트 카드
│   │   │   ├── TagBadge.tsx          # 태그 배지
│   │   │   ├── BottomNav.tsx         # 하단 네비게이션
│   │   │   ├── ui/                   # shadcn/ui 컴포넌트
│   │   │   ├── ErrorBoundary.tsx     # 에러 처리
│   │   │   └── ManusDialog.tsx       # 다이얼로그
│   │   ├── contexts/
│   │   │   └── ThemeContext.tsx      # 테마 컨텍스트
│   │   ├── App.tsx                   # 루트 컴포넌트
│   │   ├── main.tsx                  # 진입점
│   │   └── index.css                 # 글로벌 스타일
│   ├── index.html                    # HTML 템플릿
│   └── public/                       # 정적 파일
├── server/
│   └── index.ts                      # 프로덕션 서버
├── package.json                      # 의존성
└── IMPLEMENTATION_REPORT.md          # 이 파일
```

---

## 🎯 구현된 기능

### Home 페이지
- ✅ 헤더 (시간, 메뉴, 설정 아이콘)
- ✅ News 섹션 (3개 그래디언트 카드, 수평 스크롤)
- ✅ Daily Tasks 섹션 (3개 카운트 카드)
- ✅ Progress 섹션 (60% 진행률 바)
- ✅ List Items (2개 항목, 아이콘 + 설명)
- ✅ 하단 네비게이션 (5개 탭, 모바일 전용)

### Profile 페이지
- ✅ 프로필 헤더
- ✅ 프로필 이미지 (그래디언트 배경, 배지)
- ✅ 사용자 정보 (이름, 직책)
- ✅ Strong side 태그 (3개, 청록색)
- ✅ Weak side 태그 (2개, 분홍색)
- ✅ My Reports (4개 카드 그리드)

### 반응형 기능
- ✅ 모바일 (375px+): 단일 컬럼, 하단 네비게이션
- ✅ 태블릿 (768px+): 개선된 패딩
- ✅ 데스크톱 (1024px+): 2컬럼 레이아웃

---

## 🚀 개발 및 배포

### 로컬 개발
```bash
cd community-app
npm install
npm run dev
# http://localhost:3000에서 실행
```

### 빌드
```bash
npm run build
# dist/ 디렉토리에 프로덕션 빌드 생성
```

### 타입 체크
```bash
npm run check
# TypeScript 타입 검사
```

### 코드 포맷팅
```bash
npm run format
# Prettier로 코드 포맷
```

---

## 📐 디자인 결정 사항 (Design Decisions)

### 1. 왜 Poppins + Inter 조합인가?
- **Poppins**: 모던하고 친근한 느낌으로 제목의 시각적 임팩트 강화
- **Inter**: 작은 크기에서도 가독성이 우수하여 본문 텍스트에 최적
- **대비**: 두 폰트의 스타일 차이가 명확한 시각적 계층구조 생성

### 2. 왜 Tailwind CSS인가?
- **일관성**: CSS 변수로 색상/크기 통일
- **반응형**: `lg:`, `md:` 클래스로 간단한 반응형 처리
- **성능**: 프로덕션 빌드 시 사용하지 않는 스타일 제거
- **유지보수**: 클래스명이 의도를 명확히 표현

### 3. 왜 2컬럼 데스크톱 레이아웃인가?
- **피그마 디자인 충실**: 원본 디자인이 Home과 Profile을 나란히 표시
- **정보 밀도**: 데스크톱 화면을 효율적으로 활용
- **모바일 우선**: 모바일에서는 단순하게, 데스크톱에서는 풍부하게

### 4. 왜 고정 하단 네비게이션인가?
- **모바일 UX**: 엄지손가락으로 쉽게 도달 가능한 위치
- **피그마 충실**: 원본 디자인의 하단 탭 구현
- **공간 효율**: 콘텐츠 위에 겹치지 않도록 `pb-24` 패딩 추가

---

## 🎨 색상 적용 예시

### News 카드 그래디언트
```jsx
// 보라색 → 분홍색
gradient="from-purple-600 to-pink-500"

// 청록색 → 사이안
gradient="from-teal-400 to-cyan-500"

// 보라색 → 인디고
gradient="from-purple-500 to-indigo-600"
```

### 진행률 바
```jsx
// 분홍색 → 보라색 그래디언트
className="bg-gradient-to-r from-pink-500 to-purple-600"
```

### 태그 배지
```jsx
// Teal: 밝은 청록 배경 + 진한 청록 텍스트
"bg-teal-100 text-teal-700"

// Pink: 밝은 분홍 배경 + 진한 분홍 텍스트
"bg-pink-100 text-pink-700"
```

---

## ⚡ 성능 최적화

1. **컴포넌트 분리**: 각 UI 요소를 독립적인 컴포넌트로 분리하여 재사용성 및 유지보수성 향상
2. **Tailwind 유틸리티**: 커스텀 CSS 최소화로 번들 크기 감소
3. **반응형 클래스**: `hidden lg:flex` 등으로 불필요한 DOM 요소 숨김
4. **이미지 최적화**: 이모지 사용으로 이미지 로딩 제거

---

## 🔄 상태 관리

### Home.tsx의 페이지 전환
```jsx
const [currentPage, setCurrentPage] = useState<string>("home");

// 모바일에서 Profile 탭 클릭 시
if (currentPage === "profile") {
  return <ProfilePage onNavigate={setCurrentPage} />;
}

// 데스크톱에서는 항상 두 페이지 모두 표시
```

---

## 🎯 다음 단계 제안

1. **메뉴 기능**: 햄버거 메뉴 클릭 시 사이드바 또는 드롭다운 메뉴 구현
2. **탭 페이지**: Center, Search, Settings 탭에 각각의 페이지 콘텐츠 추가
3. **상호작용**: 뉴스 카드, 리포트 카드 클릭 시 상세 페이지로 이동
4. **데이터 연동**: API 연결로 동적 데이터 표시
5. **애니메이션**: Framer Motion으로 페이지 전환 애니메이션 추가

---

## 📝 파일별 코드 요약

### App.tsx - 루트 컴포넌트
```typescript
// 라우팅, 테마, 에러 처리 설정
<ThemeProvider defaultTheme="light">
  <Router />
</ThemeProvider>
```

### Home.tsx - 메인 페이지
```typescript
// 모바일: 단일 페이지, 데스크톱: 2컬럼
<div className="flex flex-col lg:flex-row">
  {/* Home 페이지 */}
  {/* Profile 페이지 (데스크톱만) */}
</div>
```

### 컴포넌트들 - 재사용 가능한 UI 블록
```typescript
// 각 컴포넌트는 Props 기반으로 설정 가능
<NewsCard title="..." gradient="..." />
<DailyTaskCard label="..." count={3} color="#..." />
<ProgressSection progress={60} />
```

---

## 🔗 참고 자료

- [Tailwind CSS 공식 문서](https://tailwindcss.com)
- [React 공식 문서](https://react.dev)
- [Lucide React 아이콘](https://lucide.dev)
- [Google Fonts](https://fonts.google.com)

---

**작성일**: 2026년 4월 21일  
**프로젝트**: Community Mobile App  
**버전**: 1.0.0
