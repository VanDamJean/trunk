# Silent Moon - 메디테이션 앱 소스코드

## 📁 프로젝트 구조

```
silent_moon/
├── client/
│   ├── src/
│   │   ├── pages/              # 페이지 컴포넌트
│   │   │   ├── Home.tsx        # 메인 홈 화면
│   │   │   ├── Onboarding.tsx  # 온보딩 화면
│   │   │   ├── TopicSelection.tsx
│   │   │   ├── ReminderSetup.tsx
│   │   │   ├── MeditationPlayer.tsx
│   │   │   └── SleepSection.tsx
│   │   ├── components/         # 재사용 컴포넌트
│   │   │   ├── SilentMoonButton.tsx
│   │   │   ├── MeditationCard.tsx
│   │   │   ├── BottomNav.tsx
│   │   │   └── ui/             # shadcn/ui 컴포넌트
│   │   ├── App.tsx
│   │   ├── index.css           # 디자인 시스템
│   │   └── main.tsx
│   └── index.html
├── server/
│   └── index.ts                # Express 서버 (정적 파일 제공)
└── package.json
```

---

## 🎨 주요 소스코드

### 1. Home.tsx - 메인 홈 화면

```typescript
import { MeditationCard } from "@/components/MeditationCard";
import { BottomNav } from "@/components/BottomNav";
import { SilentMoonButton } from "@/components/SilentMoonButton";
import { useState } from "react";
import { Onboarding } from "./Onboarding";
import { TopicSelection } from "./TopicSelection";
import { ReminderSetup } from "./ReminderSetup";
import { MeditationPlayer } from "./MeditationPlayer";
import { SleepSection } from "./SleepSection";

type Page = "home" | "onboarding" | "topics" | "reminders" | "player" | "sleep";

/**
 * Main Home Screen
 * 앱의 메인 화면 - 명상 세션 목록 및 추천
 * 
 * TODO:
 * - [ ] 실제 API에서 명상 세션 데이터 로드
 * - [ ] 사용자 선호도 기반 추천 알고리즘 구현
 * - [ ] 검색 기능 추가
 * - [ ] 즐겨찾기 기능 연동
 */
export default function Home() {
  const [currentPage, setCurrentPage] = useState<Page>("home");
  const [selectedSession, setSelectedSession] = useState<any>(null);

  const meditationSessions = [
    {
      id: 1,
      title: "Reduce Anxiety",
      description: "Calm your mind and body",
      duration: "10 MIN",
      color: "orange" as const,
      icon: "😌",
    },
    {
      id: 2,
      title: "Improve Happiness",
      description: "Boost your mood",
      duration: "15 MIN",
      color: "blue" as const,
      icon: "😊",
    },
    {
      id: 3,
      title: "Personal Growth",
      description: "Develop yourself",
      duration: "20 MIN",
      color: "green" as const,
      icon: "🌱",
    },
    {
      id: 4,
      title: "Better Sleep",
      description: "Rest well tonight",
      duration: "30 MIN",
      color: "purple" as const,
      icon: "😴",
    },
  ];

  const navItems = [
    { id: "home", label: "Home", icon: "🏠" },
    { id: "sleep", label: "Sleep", icon: "🌙" },
    { id: "profile", label: "Profile", icon: "👤" },
  ];

  const handleSessionClick = (session: any) => {
    setSelectedSession(session);
    setCurrentPage("player");
  };

  const handleNavigate = (id: string) => {
    if (id === "home") setCurrentPage("home");
    else if (id === "sleep") setCurrentPage("sleep");
  };

  // Page Routing
  if (currentPage === "onboarding") {
    return <Onboarding onComplete={() => setCurrentPage("home")} />;
  }

  if (currentPage === "topics") {
    return (
      <TopicSelection onBack={() => setCurrentPage("home")} />
    );
  }

  if (currentPage === "reminders") {
    return (
      <ReminderSetup onBack={() => setCurrentPage("home")} />
    );
  }

  if (currentPage === "player") {
    return (
      <MeditationPlayer
        session={selectedSession}
        onBack={() => setCurrentPage("home")}
      />
    );
  }

  if (currentPage === "sleep") {
    return (
      <SleepSection onNavigate={handleNavigate} />
    );
  }

  // Main Home Screen
  return (
    <div className="min-h-screen bg-background pb-24">
      {/* Header */}
      <div className="sticky top-0 z-40 bg-gradient-to-b from-background to-background/80 backdrop-blur-sm border-b border-border/50 p-6">
        <div className="max-w-md mx-auto">
          <div className="flex items-center justify-between mb-2">
            <h1 className="text-2xl font-bold text-foreground">Silent Moon</h1>
            <button className="text-2xl hover:scale-110 transition-transform">
              🔔
            </button>
          </div>
          <p className="text-sm text-muted-foreground">
            Good Morning, Afsar
          </p>
        </div>
      </div>

      {/* Content */}
      <div className="max-w-md mx-auto p-6 space-y-8">
        {/* Featured Section */}
        <div className="space-y-4">
          <h2 className="text-xl font-bold text-foreground">
            What brings you to Silent Moon?
          </h2>
          <div className="grid grid-cols-2 gap-4">
            {meditationSessions.map((session) => (
              <MeditationCard
                key={session.id}
                title={session.title}
                description={session.description}
                duration={session.duration}
                icon={session.icon}
                color={session.color}
                onClick={() => handleSessionClick(session)}
              />
            ))}
          </div>
        </div>

        {/* Recommended Section */}
        <div className="space-y-4">
          <h2 className="text-xl font-bold text-foreground">
            Recommended for you
          </h2>
          <div className="space-y-3">
            {[
              { title: "Focus Attention", duration: "3-10 MIN", icon: "🎯" },
              { title: "Happiness", duration: "5-15 MIN", icon: "😊" },
              { title: "Body Scan", duration: "10-20 MIN", icon: "🧘" },
            ].map((item, idx) => (
              <div
                key={idx}
                className="bg-card rounded-xl p-4 border border-border hover:shadow-md transition-all cursor-pointer"
              >
                <div className="flex items-center gap-4">
                  <div className="text-3xl">{item.icon}</div>
                  <div className="flex-1">
                    <h3 className="font-semibold text-foreground">
                      {item.title}
                    </h3>
                    <p className="text-xs text-muted-foreground">
                      {item.duration}
                    </p>
                  </div>
                  <button className="text-primary hover:scale-110 transition-transform">
                    ▶
                  </button>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Action Buttons */}
        <div className="space-y-3">
          <SilentMoonButton
            onClick={() => setCurrentPage("topics")}
            fullWidth
            variant="secondary"
          >
            Choose Topic
          </SilentMoonButton>
          <SilentMoonButton
            onClick={() => setCurrentPage("reminders")}
            fullWidth
            variant="outline"
          >
            Set Reminders
          </SilentMoonButton>
        </div>
      </div>

      {/* Bottom Navigation */}
      <BottomNav
        items={navItems}
        activeId="home"
        onNavigate={handleNavigate}
      />
    </div>
  );
}
```

---

### 2. SilentMoonButton.tsx - 커스텀 버튼 컴포넌트

```typescript
import { Button } from "@/components/ui/button";
import { cn } from "@/lib/utils";
import { ReactNode } from "react";

interface SilentMoonButtonProps {
  children: ReactNode;
  variant?: "primary" | "secondary" | "outline" | "ghost";
  size?: "sm" | "md" | "lg";
  fullWidth?: boolean;
  className?: string;
  [key: string]: any;
}

/**
 * Silent Moon Custom Button Component
 * 명상 앱의 감성에 맞춘 커스텀 버튼
 * - primary: 보라색 그라데이션 배경 (CTA)
 * - secondary: 네이비 배경 (보조 액션)
 * - outline: 테두리만 (3차 액션)
 * - ghost: 텍스트만 (최소 액션)
 * 
 * TODO:
 * - [ ] 로딩 상태 추가 (isLoading prop)
 * - [ ] 비활성화 상태 스타일 개선
 * - [ ] 아이콘 지원 추가
 */
export function SilentMoonButton({
  children,
  variant = "primary",
  size = "md",
  fullWidth = false,
  className,
  ...props
}: SilentMoonButtonProps) {
  const baseStyles = "font-medium rounded-full transition-all duration-300";

  const variantStyles = {
    primary:
      "bg-gradient-to-r from-primary to-accent text-primary-foreground hover:shadow-lg hover:shadow-primary/30 active:scale-95",
    secondary:
      "bg-secondary text-secondary-foreground hover:bg-secondary/90 active:scale-95",
    outline:
      "border-2 border-primary text-primary hover:bg-primary/5 active:scale-95",
    ghost: "text-primary hover:bg-primary/10 active:scale-95",
  };

  const sizeStyles = {
    sm: "px-4 py-2 text-sm",
    md: "px-6 py-3 text-base",
    lg: "px-8 py-4 text-lg",
  };

  return (
    <Button
      className={cn(
        baseStyles,
        variantStyles[variant],
        sizeStyles[size],
        fullWidth && "w-full",
        className
      )}
      {...props}
    >
      {children}
    </Button>
  );
}
```

---

### 3. MeditationCard.tsx - 명상 세션 카드

```typescript
import { Card } from "@/components/ui/card";
import { cn } from "@/lib/utils";
import { ReactNode } from "react";

interface MeditationCardProps {
  title: string;
  description?: string;
  duration?: string;
  icon?: ReactNode;
  color?: "purple" | "orange" | "green" | "blue" | "pink";
  onClick?: () => void;
  className?: string;
}

/**
 * Meditation Card Component
 * 명상 세션을 표시하는 카드 컴포넌트
 * 피그마의 카드 디자인을 반영하여 구현
 * 
 * TODO:
 * - [ ] 이미지 배경 지원 추가
 * - [ ] 별점 표시 추가
 * - [ ] 완료 상태 표시 (체크마크)
 * - [ ] 드래그 앤 드롭 지원
 */
export function MeditationCard({
  title,
  description,
  duration,
  icon,
  color = "purple",
  onClick,
  className,
}: MeditationCardProps) {
  const colorMap = {
    purple: "bg-gradient-to-br from-primary/20 to-accent/20 border-primary/30",
    orange: "bg-gradient-to-br from-orange-100 to-orange-50 border-orange-200",
    green: "bg-gradient-to-br from-green-100 to-green-50 border-green-200",
    blue: "bg-gradient-to-br from-blue-100 to-blue-50 border-blue-200",
    pink: "bg-gradient-to-br from-pink-100 to-pink-50 border-pink-200",
  };

  return (
    <Card
      className={cn(
        "p-6 cursor-pointer rounded-2xl border-2 transition-all duration-300",
        "hover:shadow-lg hover:scale-105 active:scale-95",
        colorMap[color],
        className
      )}
      onClick={onClick}
    >
      {icon && <div className="mb-4 text-3xl">{icon}</div>}
      <h3 className="font-bold text-lg text-foreground mb-2">{title}</h3>
      {description && (
        <p className="text-sm text-muted-foreground mb-3">{description}</p>
      )}
      {duration && (
        <p className="text-xs font-semibold text-primary uppercase tracking-wider">
          {duration}
        </p>
      )}
    </Card>
  );
}
```

---

### 4. BottomNav.tsx - 하단 탭 네비게이션

```typescript
import { cn } from "@/lib/utils";
import { ReactNode } from "react";

interface NavItem {
  id: string;
  label: string;
  icon: ReactNode;
}

interface BottomNavProps {
  items: NavItem[];
  activeId: string;
  onNavigate: (id: string) => void;
}

/**
 * Bottom Navigation Component
 * 모바일 앱의 하단 탭 네비게이션
 * 피그마 디자인의 하단 탭 바를 구현
 * 
 * TODO:
 * - [ ] 배지 지원 (알림 카운트 표시)
 * - [ ] 스와이프 제스처 지원
 * - [ ] 애니메이션 개선 (탭 전환 시)
 */
export function BottomNav({ items, activeId, onNavigate }: BottomNavProps) {
  return (
    <nav className="fixed bottom-0 left-0 right-0 bg-card border-t border-border shadow-lg">
      <div className="flex justify-around items-center h-20 max-w-md mx-auto">
        {items.map((item) => (
          <button
            key={item.id}
            onClick={() => onNavigate(item.id)}
            className={cn(
              "flex flex-col items-center justify-center w-full h-full gap-1 transition-all duration-200",
              activeId === item.id
                ? "text-primary"
                : "text-muted-foreground hover:text-foreground"
            )}
          >
            <div className="text-2xl">{item.icon}</div>
            <span className="text-xs font-medium">{item.label}</span>
          </button>
        ))}
      </div>
    </nav>
  );
}
```

---

### 5. Onboarding.tsx - 온보딩 화면

```typescript
import { SilentMoonButton } from "@/components/SilentMoonButton";
import { useState } from "react";

interface OnboardingProps {
  onComplete: () => void;
}

/**
 * Onboarding Screen
 * 사용자 첫 방문 시 보여지는 환영 화면
 * 
 * TODO:
 * - [ ] 사용자 이름 입력 기능 추가
 * - [ ] 소셜 로그인 통합 (Google, Apple)
 * - [ ] 이메일 가입 화면 추가
 * - [ ] 스킵 버튼 추가
 */
export function Onboarding({ onComplete }: OnboardingProps) {
  const [step, setStep] = useState(1);

  const steps = [
    {
      title: "Hi Afsar, Welcome to Silent Moon",
      subtitle: "Explore the power of meditation",
      icon: "🧘",
      description: "Your personal meditation companion",
    },
    {
      title: "What brings you to Silent Moon?",
      subtitle: "Choose your meditation goal",
      icon: "🎯",
      description: "We'll personalize your experience",
    },
    {
      title: "Ready to begin?",
      subtitle: "Start your journey to inner peace",
      icon: "✨",
      description: "Your first session awaits",
    },
  ];

  const currentStep = steps[step - 1];

  const handleNext = () => {
    if (step < steps.length) {
      setStep(step + 1);
    } else {
      onComplete();
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-background via-background to-accent/5 flex flex-col items-center justify-center p-6">
      {/* Decorative Background */}
      <div className="absolute inset-0 overflow-hidden pointer-events-none">
        <div className="absolute -top-40 -right-40 w-80 h-80 bg-primary/10 rounded-full blur-3xl"></div>
        <div className="absolute -bottom-40 -left-40 w-80 h-80 bg-accent/10 rounded-full blur-3xl"></div>
      </div>

      {/* Content */}
      <div className="relative z-10 w-full max-w-md text-center space-y-8">
        {/* Icon */}
        <div className="text-8xl animate-bounce">{currentStep.icon}</div>

        {/* Text Content */}
        <div className="space-y-4">
          <h1 className="text-4xl font-bold text-foreground">
            {currentStep.title}
          </h1>
          <p className="text-lg text-muted-foreground">{currentStep.subtitle}</p>
          <p className="text-sm text-muted-foreground/70">
            {currentStep.description}
          </p>
        </div>

        {/* Progress Indicator */}
        <div className="flex gap-2 justify-center">
          {steps.map((_, index) => (
            <div
              key={index}
              className={`h-2 rounded-full transition-all duration-300 ${
                index < step ? "bg-primary w-8" : "bg-muted w-2"
              }`}
            ></div>
          ))}
        </div>

        {/* Buttons */}
        <div className="space-y-3 pt-4">
          <SilentMoonButton
            onClick={handleNext}
            fullWidth
            size="lg"
            variant="primary"
          >
            {step === steps.length ? "Get Started" : "Next"}
          </SilentMoonButton>

          {step > 1 && (
            <SilentMoonButton
              onClick={() => setStep(step - 1)}
              fullWidth
              size="lg"
              variant="ghost"
            >
              Back
            </SilentMoonButton>
          )}
        </div>
      </div>
    </div>
  );
}
```

---

### 6. MeditationPlayer.tsx - 명상 플레이어

```typescript
import { SilentMoonButton } from "@/components/SilentMoonButton";
import { useState } from "react";

interface MeditationPlayerProps {
  session: any;
  onBack: () => void;
}

/**
 * Meditation Player Screen
 * 명상 세션을 재생하는 플레이어 화면
 * 
 * TODO:
 * - [ ] 실제 오디오 재생 기능 구현 (HTML5 Audio API)
 * - [ ] 재생 시간 동기화
 * - [ ] 음량 조절 기능
 * - [ ] 배속 조절 기능 (0.75x, 1x, 1.25x, 1.5x)
 * - [ ] 타이머 기능
 * - [ ] 자동 재생 다음 세션
 */
export function MeditationPlayer({ session, onBack }: MeditationPlayerProps) {
  const [isPlaying, setIsPlaying] = useState(false);
  const [progress, setProgress] = useState(30);
  const [isFavorite, setIsFavorite] = useState(false);

  if (!session) {
    return null;
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-primary/10 via-background to-accent/10 flex flex-col items-center justify-center p-6 pb-20">
      {/* Header */}
      <div className="absolute top-0 left-0 right-0 flex items-center justify-between p-6 z-10">
        <button
          onClick={onBack}
          className="text-2xl hover:scale-110 transition-transform"
        >
          ←
        </button>
        <button className="text-2xl hover:scale-110 transition-transform">
          ⋮
        </button>
      </div>

      {/* Content */}
      <div className="w-full max-w-md space-y-8 mt-16">
        {/* Large Icon/Visualization */}
        <div className="flex justify-center">
          <div className="w-48 h-48 rounded-full bg-gradient-to-br from-primary/30 to-accent/30 flex items-center justify-center text-8xl animate-pulse">
            {session.icon}
          </div>
        </div>

        {/* Session Info */}
        <div className="text-center space-y-2">
          <h1 className="text-3xl font-bold text-foreground">{session.title}</h1>
          <p className="text-muted-foreground">{session.description}</p>
          <p className="text-sm text-primary font-semibold">{session.duration}</p>
        </div>

        {/* Progress Bar */}
        <div className="space-y-2">
          <div className="w-full h-2 bg-muted rounded-full overflow-hidden">
            <div
              className="h-full bg-gradient-to-r from-primary to-accent transition-all duration-300"
              style={{ width: `${progress}%` }}
            ></div>
          </div>
          <div className="flex justify-between text-xs text-muted-foreground">
            <span>3:45</span>
            <span>12:30</span>
          </div>
        </div>

        {/* Controls */}
        <div className="flex items-center justify-center gap-8">
          {/* Favorite Button */}
          <button
            onClick={() => setIsFavorite(!isFavorite)}
            className="text-3xl hover:scale-110 transition-transform"
          >
            {isFavorite ? "❤️" : "🤍"}
          </button>

          {/* Play Button */}
          <button
            onClick={() => setIsPlaying(!isPlaying)}
            className="w-20 h-20 rounded-full bg-gradient-to-r from-primary to-accent flex items-center justify-center text-white text-3xl hover:shadow-lg hover:shadow-primary/50 active:scale-95 transition-all duration-300"
          >
            {isPlaying ? "⏸" : "▶"}
          </button>

          {/* Download Button */}
          <button className="text-3xl hover:scale-110 transition-transform">
            ⬇️
          </button>
        </div>

        {/* Action Buttons */}
        <div className="space-y-3 pt-6">
          <SilentMoonButton
            onClick={onBack}
            fullWidth
            size="lg"
            variant="primary"
          >
            Done
          </SilentMoonButton>
        </div>
      </div>
    </div>
  );
}
```

---

### 7. TopicSelection.tsx - 주제 선택 화면

```typescript
import { SilentMoonButton } from "@/components/SilentMoonButton";
import { useState } from "react";

interface TopicSelectionProps {
  onBack: () => void;
}

/**
 * Topic Selection Screen
 * 사용자가 명상 주제를 선택하는 화면
 * 
 * TODO:
 * - [ ] 선택 결과 저장 (localStorage 또는 API)
 * - [ ] 선택 결과 기반 추천 알고리즘 구현
 * - [ ] 토픽별 세션 개수 표시
 * - [ ] 토픽 검색 기능
 */
export function TopicSelection({ onBack }: TopicSelectionProps) {
  const [selectedTopics, setSelectedTopics] = useState<string[]>([]);

  const topics = [
    { id: "stress", title: "Reduce Stress", icon: "😌", color: "from-orange-100 to-orange-50" },
    { id: "anxiety", title: "Reduce Anxiety", icon: "🧘", color: "from-purple-100 to-purple-50" },
    { id: "sleep", title: "Better Sleep", icon: "😴", color: "from-blue-100 to-blue-50" },
    { id: "focus", title: "Improve Focus", icon: "🎯", color: "from-green-100 to-green-50" },
    { id: "happiness", title: "Increase Happiness", icon: "😊", color: "from-pink-100 to-pink-50" },
    { id: "growth", title: "Personal Growth", icon: "🌱", color: "from-emerald-100 to-emerald-50" },
  ];

  const toggleTopic = (id: string) => {
    setSelectedTopics((prev) =>
      prev.includes(id) ? prev.filter((t) => t !== id) : [...prev, id]
    );
  };

  return (
    <div className="min-h-screen bg-background pb-20">
      {/* Header */}
      <div className="sticky top-0 z-40 bg-gradient-to-b from-background to-background/80 backdrop-blur-sm border-b border-border/50 p-6">
        <div className="max-w-md mx-auto flex items-center gap-4">
          <button
            onClick={onBack}
            className="text-2xl hover:scale-110 transition-transform"
          >
            ←
          </button>
          <h1 className="text-2xl font-bold text-foreground">Choose Topic</h1>
        </div>
      </div>

      {/* Content */}
      <div className="max-w-md mx-auto p-6 space-y-6">
        <p className="text-muted-foreground">
          Select the topics that interest you. You can choose multiple.
        </p>

        {/* Topic Grid */}
        <div className="grid grid-cols-2 gap-4">
          {topics.map((topic) => (
            <button
              key={topic.id}
              onClick={() => toggleTopic(topic.id)}
              className={`p-6 rounded-2xl border-2 transition-all duration-300 text-center space-y-3 ${
                selectedTopics.includes(topic.id)
                  ? `bg-gradient-to-br ${topic.color} border-primary shadow-lg scale-105`
                  : "bg-card border-border hover:border-primary/50"
              }`}
            >
              <div className="text-4xl">{topic.icon}</div>
              <h3 className="font-semibold text-foreground text-sm">
                {topic.title}
              </h3>
              {selectedTopics.includes(topic.id) && (
                <div className="text-primary text-lg">✓</div>
              )}
            </button>
          ))}
        </div>

        {/* Save Button */}
        <div className="pt-6">
          <SilentMoonButton
            onClick={onBack}
            fullWidth
            size="lg"
            variant="primary"
            disabled={selectedTopics.length === 0}
          >
            Save Selection ({selectedTopics.length})
          </SilentMoonButton>
        </div>
      </div>
    </div>
  );
}
```

---

### 8. ReminderSetup.tsx - 리마인더 설정 화면

```typescript
import { SilentMoonButton } from "@/components/SilentMoonButton";
import { useState } from "react";

interface ReminderSetupProps {
  onBack: () => void;
}

/**
 * Reminder Setup Screen
 * 사용자가 명상 리마인더를 설정하는 화면
 * 
 * TODO:
 * - [ ] 리마인더 설정 저장 (localStorage 또는 API)
 * - [ ] 브라우저 알림 권한 요청
 * - [ ] 시간대 선택 기능
 * - [ ] 리마인더 반복 설정 (매일, 주 1회 등)
 * - [ ] 리마인더 수정/삭제 기능
 */
export function ReminderSetup({ onBack }: ReminderSetupProps) {
  const [selectedDays, setSelectedDays] = useState<string[]>([]);
  const [selectedTime, setSelectedTime] = useState("09:00");

  const days = ["SU", "M", "T", "W", "TH", "F", "S"];
  const dayNames = [
    "Sunday",
    "Monday",
    "Tuesday",
    "Wednesday",
    "Thursday",
    "Friday",
    "Saturday",
  ];

  const toggleDay = (day: string) => {
    setSelectedDays((prev) =>
      prev.includes(day) ? prev.filter((d) => d !== day) : [...prev, day]
    );
  };

  return (
    <div className="min-h-screen bg-background pb-20">
      {/* Header */}
      <div className="sticky top-0 z-40 bg-gradient-to-b from-background to-background/80 backdrop-blur-sm border-b border-border/50 p-6">
        <div className="max-w-md mx-auto flex items-center gap-4">
          <button
            onClick={onBack}
            className="text-2xl hover:scale-110 transition-transform"
          >
            ←
          </button>
          <h1 className="text-2xl font-bold text-foreground">Set Reminders</h1>
        </div>
      </div>

      {/* Content */}
      <div className="max-w-md mx-auto p-6 space-y-8">
        {/* Time Selection */}
        <div className="space-y-4">
          <h2 className="text-lg font-semibold text-foreground">
            What time would you like to meditate?
          </h2>
          <p className="text-sm text-muted-foreground">
            Everyday is best, but we recommend picking at least five.
          </p>

          <div className="bg-card rounded-2xl p-6 border border-border">
            <div className="flex items-center justify-between gap-4">
              <div className="flex gap-2">
                {["10", "29", "AM"].map((part, idx) => (
                  <input
                    key={idx}
                    type="text"
                    value={part}
                    readOnly
                    className="w-12 h-12 text-center font-bold text-lg bg-muted rounded-lg border border-border"
                  />
                ))}
              </div>
              <input
                type="time"
                value={selectedTime}
                onChange={(e) => setSelectedTime(e.target.value)}
                className="flex-1 px-4 py-2 rounded-lg border border-border bg-background text-foreground"
              />
            </div>
          </div>
        </div>

        {/* Day Selection */}
        <div className="space-y-4">
          <h2 className="text-lg font-semibold text-foreground">
            Which day would you like to meditate?
          </h2>
          <p className="text-sm text-muted-foreground">
            Everyday is best, but we recommend picking at least five.
          </p>

          <div className="flex gap-2 justify-center flex-wrap">
            {days.map((day, idx) => (
              <button
                key={day}
                onClick={() => toggleDay(day)}
                className={`w-12 h-12 rounded-full font-bold transition-all duration-300 ${
                  selectedDays.includes(day)
                    ? "bg-primary text-primary-foreground shadow-lg"
                    : "bg-muted text-muted-foreground hover:bg-muted/80"
                }`}
              >
                {day}
              </button>
            ))}
          </div>

          {/* Selected Days Display */}
          {selectedDays.length > 0 && (
            <div className="bg-accent/10 rounded-lg p-4 border border-accent/20">
              <p className="text-sm text-foreground">
                Selected: {selectedDays.map((d) => dayNames[days.indexOf(d)]).join(", ")}
              </p>
            </div>
          )}
        </div>

        {/* Action Buttons */}
        <div className="space-y-3 pt-6">
          <SilentMoonButton
            onClick={onBack}
            fullWidth
            size="lg"
            variant="primary"
            disabled={selectedDays.length === 0}
          >
            Save Reminders
          </SilentMoonButton>
          <SilentMoonButton
            onClick={onBack}
            fullWidth
            size="lg"
            variant="ghost"
          >
            No Thanks
          </SilentMoonButton>
        </div>
      </div>
    </div>
  );
}
```

---

### 9. SleepSection.tsx - 수면 섹션 (다크 테마)

```typescript
import { MeditationCard } from "@/components/MeditationCard";
import { BottomNav } from "@/components/BottomNav";

interface SleepSectionProps {
  onNavigate: (page: string) => void;
}

/**
 * Sleep Section Screen
 * 수면 유도 콘텐츠를 제공하는 다크 테마 섹션
 * 
 * TODO:
 * - [ ] 수면 추적 기능 (수면 시간, 품질 기록)
 * - [ ] 수면 통계 대시보드
 * - [ ] 스마트 알람 기능
 * - [ ] 수면 음악 플레이리스트 관리
 */
export function SleepSection({ onNavigate }: SleepSectionProps) {
  const sleepContent = [
    {
      id: 1,
      title: "Night Island",
      description: "A peaceful journey to sleep",
      duration: "45 MIN",
      icon: "🏝️",
    },
    {
      id: 2,
      title: "Sweet Dreams",
      description: "Drift into restful sleep",
      duration: "30 MIN",
      icon: "☁️",
    },
    {
      id: 3,
      title: "Moonlight Stories",
      description: "Bedtime tales for adults",
      duration: "20 MIN",
      icon: "📖",
    },
    {
      id: 4,
      title: "Sleep Music",
      description: "Ambient sounds for rest",
      duration: "60 MIN",
      icon: "🎵",
    },
  ];

  const navItems = [
    { id: "home", label: "Home", icon: "🏠" },
    { id: "sleep", label: "Sleep", icon: "🌙" },
    { id: "profile", label: "Profile", icon: "👤" },
  ];

  return (
    <div className="min-h-screen dark bg-background pb-24">
      {/* Header */}
      <div className="sticky top-0 z-40 bg-gradient-to-b from-background to-background/80 backdrop-blur-sm border-b border-border/50 p-6">
        <div className="max-w-md mx-auto">
          <div className="flex items-center justify-between mb-2">
            <h1 className="text-2xl font-bold text-foreground">Sleep Music</h1>
            <button className="text-2xl hover:scale-110 transition-transform">
              🔔
            </button>
          </div>
          <p className="text-sm text-muted-foreground">
            Drift into peaceful sleep
          </p>
        </div>
      </div>

      {/* Content */}
      <div className="max-w-md mx-auto p-6 space-y-8">
        {/* Featured Sleep Content */}
        <div className="space-y-4">
          <h2 className="text-xl font-bold text-foreground">
            Sleep Stories
          </h2>
          <div className="grid grid-cols-2 gap-4">
            {sleepContent.slice(0, 2).map((item) => (
              <MeditationCard
                key={item.id}
                title={item.title}
                description={item.description}
                duration={item.duration}
                icon={item.icon}
                color="blue"
              />
            ))}
          </div>
        </div>

        {/* Sleep Music Library */}
        <div className="space-y-4">
          <h2 className="text-xl font-bold text-foreground">
            Sleep Music Library
          </h2>
          <div className="space-y-3">
            {sleepContent.slice(2).map((item) => (
              <div
                key={item.id}
                className="bg-card rounded-xl p-4 border border-border hover:shadow-md transition-all cursor-pointer hover:border-primary/50"
              >
                <div className="flex items-center gap-4">
                  <div className="text-3xl">{item.icon}</div>
                  <div className="flex-1">
                    <h3 className="font-semibold text-foreground">
                      {item.title}
                    </h3>
                    <p className="text-xs text-muted-foreground">
                      {item.description}
                    </p>
                    <p className="text-xs font-semibold text-primary mt-1">
                      {item.duration}
                    </p>
                  </div>
                  <button className="text-primary hover:scale-110 transition-transform">
                    ▶
                  </button>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Sleep Tips */}
        <div className="bg-card rounded-2xl p-6 border border-border space-y-3">
          <h3 className="font-bold text-foreground">💡 Sleep Tips</h3>
          <ul className="text-sm text-muted-foreground space-y-2">
            <li>• Keep your bedroom cool and dark</li>
            <li>• Avoid screens 30 minutes before bed</li>
            <li>• Try meditation 10 minutes before sleep</li>
            <li>• Maintain a consistent sleep schedule</li>
          </ul>
        </div>
      </div>

      {/* Bottom Navigation */}
      <BottomNav
        items={navItems}
        activeId="sleep"
        onNavigate={onNavigate}
      />
    </div>
  );
}
```

---

### 10. App.tsx - 메인 앱 컴포넌트

```typescript
import { Toaster } from "@/components/ui/sonner";
import { TooltipProvider } from "@/components/ui/tooltip";
import NotFound from "@/pages/NotFound";
import { Route, Switch } from "wouter";
import ErrorBoundary from "./components/ErrorBoundary";
import { ThemeProvider } from "./contexts/ThemeContext";
import Home from "./pages/Home";

function Router() {
  return (
    <Switch>
      <Route path={"/"} component={Home} />
      <Route path={"/404"} component={NotFound} />
      {/* Final fallback route */}
      <Route component={NotFound} />
    </Switch>
  );
}

/**
 * Main App Component
 * 
 * TODO:
 * - [ ] 라우팅 개선 (wouter 대신 React Router 고려)
 * - [ ] 상태 관리 추가 (Zustand 또는 Context API)
 * - [ ] 에러 바운더리 개선
 * - [ ] 로딩 상태 관리
 * - [ ] 오프라인 지원 추가
 */
function App() {
  return (
    <ErrorBoundary>
      <ThemeProvider
        defaultTheme="light"
        switchable
      >
        <TooltipProvider>
          <Toaster />
          <Router />
        </TooltipProvider>
      </ThemeProvider>
    </ErrorBoundary>
  );
}

export default App;
```

---

### 11. index.css - 디자인 시스템

```css
@import "tailwindcss";
@import "tw-animate-css";

@custom-variant dark (&:is(.dark *));

@theme inline {
  --radius-sm: calc(var(--radius) - 4px);
  --radius-md: calc(var(--radius) - 2px);
  --radius-lg: var(--radius);
  --radius-xl: calc(var(--radius) + 4px);
  --color-background: var(--background);
  --color-foreground: var(--foreground);
  --color-card: var(--card);
  --color-card-foreground: var(--card-foreground);
  --color-popover: var(--popover);
  --color-popover-foreground: var(--popover-foreground);
  --color-primary: var(--primary);
  --color-primary-foreground: var(--primary-foreground);
  --color-secondary: var(--secondary);
  --color-secondary-foreground: var(--secondary-foreground);
  --color-muted: var(--muted);
  --color-muted-foreground: var(--muted-foreground);
  --color-accent: var(--accent);
  --color-accent-foreground: var(--accent-foreground);
  --color-destructive: var(--destructive);
  --color-destructive-foreground: var(--destructive-foreground);
  --color-border: var(--border);
  --color-input: var(--input);
  --color-ring: var(--ring);
  --color-chart-1: var(--chart-1);
  --color-chart-2: var(--chart-2);
  --color-chart-3: var(--chart-3);
  --color-chart-4: var(--chart-4);
  --color-chart-5: var(--chart-5);
  --color-sidebar: var(--sidebar);
  --color-sidebar-foreground: var(--sidebar-foreground);
  --color-sidebar-primary: var(--sidebar-primary);
  --color-sidebar-primary-foreground: var(--sidebar-primary-foreground);
  --color-sidebar-accent: var(--sidebar-accent);
  --color-sidebar-accent-foreground: var(--sidebar-accent-foreground);
  --color-sidebar-border: var(--sidebar-border);
  --color-sidebar-ring: var(--sidebar-ring);
}

:root {
  /* Silent Moon - Primary Brand Colors */
  --primary: oklch(0.55 0.25 270);     /* Vibrant Purple */
  --primary-foreground: oklch(0.98 0 0); /* White */
  --secondary: oklch(0.25 0.15 260);   /* Deep Navy */
  --secondary-foreground: oklch(0.95 0 0); /* Off-white */
  --accent: oklch(0.6 0.2 265);        /* Soft Purple */
  --accent-foreground: oklch(0.98 0 0);
  
  /* Neutral Palette */
  --background: oklch(0.98 0.001 0);   /* Warm White */
  --foreground: oklch(0.25 0.02 260);  /* Deep Navy Text */
  --card: oklch(0.99 0 0);             /* Pure White Cards */
  --card-foreground: oklch(0.25 0.02 260);
  --popover: oklch(0.99 0 0);
  --popover-foreground: oklch(0.25 0.02 260);
  --muted: oklch(0.92 0.005 260);      /* Light Gray */
  --muted-foreground: oklch(0.55 0.01 260); /* Medium Gray */
  --destructive: oklch(0.577 0.245 27.325);
  --destructive-foreground: oklch(0.985 0 0);
  --border: oklch(0.9 0.005 260);      /* Subtle Border */
  --input: oklch(0.95 0.002 260);
  --ring: oklch(0.55 0.25 270);        /* Purple Focus Ring */
  
  /* Sidebar (not used in this app, but keeping for consistency) */
  --sidebar: oklch(0.98 0.001 0);
  --sidebar-foreground: oklch(0.25 0.02 260);
  --sidebar-primary: oklch(0.55 0.25 270);
  --sidebar-primary-foreground: oklch(0.98 0 0);
  --sidebar-accent: oklch(0.6 0.2 265);
  --sidebar-accent-foreground: oklch(0.98 0 0);
  --sidebar-border: oklch(0.9 0.005 260);
  --sidebar-ring: oklch(0.55 0.25 270);
  
  /* Chart Colors (for future analytics) */
  --chart-1: oklch(0.55 0.25 270);     /* Purple */
  --chart-2: oklch(0.6 0.2 265);       /* Soft Purple */
  --chart-3: oklch(0.25 0.15 260);     /* Deep Navy */
  --chart-4: oklch(0.75 0.1 280);      /* Light Purple */
  --chart-5: oklch(0.45 0.2 275);      /* Medium Purple */
  
  --radius: 1rem;                      /* Generous Rounded Corners */
}

.dark {
  /* Dark Mode - Sleep Section Theme */
  --background: oklch(0.12 0.01 260);  /* Deep Navy Background */
  --foreground: oklch(0.95 0 0);       /* Off-white Text */
  --card: oklch(0.18 0.01 260);        /* Darker Navy Cards */
  --card-foreground: oklch(0.95 0 0);
  --popover: oklch(0.18 0.01 260);
  --popover-foreground: oklch(0.95 0 0);
  --secondary: oklch(0.2 0.01 260);    /* Very Dark Navy */
  --secondary-foreground: oklch(0.85 0 0);
  --muted: oklch(0.3 0.01 260);        /* Muted Dark */
  --muted-foreground: oklch(0.7 0.01 260);
  --accent: oklch(0.55 0.25 270);      /* Purple Accent (same as light) */
  --accent-foreground: oklch(0.98 0 0);
  --destructive: oklch(0.704 0.191 22.216);
  --destructive-foreground: oklch(0.985 0 0);
  --border: oklch(1 0 0 / 8%);
  --input: oklch(1 0 0 / 12%);
  --ring: oklch(0.55 0.25 270);
  --chart-1: oklch(0.55 0.25 270);
  --chart-2: oklch(0.6 0.2 265);
  --chart-3: oklch(0.75 0.1 280);
  --chart-4: oklch(0.45 0.2 275);
  --chart-5: oklch(0.35 0.15 270);
  --sidebar: oklch(0.18 0.01 260);
  --sidebar-foreground: oklch(0.95 0 0);
  --sidebar-primary: oklch(0.55 0.25 270);
  --sidebar-primary-foreground: oklch(0.98 0 0);
  --sidebar-accent: oklch(0.6 0.2 265);
  --sidebar-accent-foreground: oklch(0.98 0 0);
  --sidebar-border: oklch(1 0 0 / 8%);
  --sidebar-ring: oklch(0.55 0.25 270);
}

@layer base {
  * {
    @apply border-border outline-ring/50;
  }
  
  html {
    font-family: 'Inter', sans-serif;
  }
  
  body {
    @apply bg-background text-foreground;
    font-size: 1rem;
    line-height: 1.6;
    letter-spacing: -0.3px;
  }
  
  /* Display Headings - Poppins Bold */
  h1, h2, h3, h4, h5, h6 {
    font-family: 'Poppins', sans-serif;
    font-weight: 700;
    line-height: 1.2;
    letter-spacing: -0.5px;
  }
  
  h1 {
    font-size: 2.5rem;
    font-weight: 800;
  }
  
  h2 {
    font-size: 2rem;
    font-weight: 700;
  }
  
  h3 {
    font-size: 1.5rem;
    font-weight: 700;
  }
  
  h4 {
    font-size: 1.25rem;
    font-weight: 600;
  }
  
  p {
    font-size: 1rem;
    line-height: 1.6;
  }
  
  small {
    font-size: 0.875rem;
    line-height: 1.5;
  }
  button:not(:disabled),
  [role="button"]:not([aria-disabled="true"]),
  [type="button"]:not(:disabled),
  [type="submit"]:not(:disabled),
  [type="reset"]:not(:disabled),
  a[href],
  select:not(:disabled),
  input[type="checkbox"]:not(:disabled),
  input[type="radio"]:not(:disabled) {
    @apply cursor-pointer;
  }
  
  /* Smooth transitions for all interactive elements */
  button,
  a,
  input,
  select,
  textarea {
    @apply transition-all duration-200;
  }
}

@layer components {
  /**
   * Custom container utility that centers content and adds responsive padding.
   *
   * This overrides Tailwind's default container behavior to:
   * - Auto-center content (mx-auto)
   * - Add responsive horizontal padding
   * - Set max-width for large screens
   *
   * Usage: <div className="container">...</div>
   *
   * For custom widths, use max-w-* utilities directly:
   * <div className="max-w-6xl mx-auto px-4">...</div>
   */
  .container {
    width: 100%;
    margin-left: auto;
    margin-right: auto;
    padding-left: 1rem; /* 16px - mobile padding */
    padding-right: 1rem;
  }

  .flex {
    min-height: 0;
    min-width: 0;
  }

  @media (min-width: 640px) {
    .container {
      padding-left: 1.5rem; /* 24px - tablet padding */
      padding-right: 1.5rem;
    }
  }

  @media (min-width: 1024px) {
    .container {
      padding-left: 2rem; /* 32px - desktop padding */
      padding-right: 2rem;
      max-width: 1280px; /* Standard content width */
    }
  }
}
```

---

## 📋 전체 TODO 리스트

### 높은 우선순위 (필수)

- [ ] **오디오 재생 기능**: HTML5 Audio API를 활용한 실제 명상 음악 재생
- [ ] **사용자 인증**: Manus OAuth 또는 소셜 로그인 통합
- [ ] **데이터 저장**: 사용자 선호도, 진행 상황, 리마인더 설정 저장
- [ ] **API 연동**: 백엔드 API에서 명상 세션 데이터 동적 로드
- [ ] **프로필 페이지**: 사용자 정보, 진행 통계, 설정 관리

### 중간 우선순위 (권장)

- [ ] **수면 추적**: 수면 시간, 품질 기록 및 통계
- [ ] **즐겨찾기 기능**: 선호하는 명상 세션 저장
- [ ] **검색 기능**: 명상 세션 검색 및 필터링
- [ ] **알림 시스템**: 브라우저 푸시 알림 또는 이메일 알림
- [ ] **오프라인 지원**: 다운로드한 콘텐츠 오프라인 재생

### 낮은 우선순위 (선택)

- [ ] **소셜 공유**: 진행 상황 공유 기능
- [ ] **커뮤니티**: 사용자 간 경험 공유 포럼
- [ ] **고급 분석**: 명상 효과 분석 및 인사이트
- [ ] **AI 추천**: 머신러닝 기반 개인화 추천
- [ ] **다국어 지원**: 다양한 언어 지원

---

## 🚀 개발 시작 가이드

### 프로젝트 설정

```bash
# 프로젝트 디렉토리로 이동
cd /home/ubuntu/silent_moon

# 의존성 설치
pnpm install

# 개발 서버 시작
pnpm dev

# 프로덕션 빌드
pnpm build

# 프로덕션 서버 시작
pnpm start
```

### 파일 구조 설명

- **pages/**: 전체 화면을 담당하는 페이지 컴포넌트
- **components/**: 재사용 가능한 UI 컴포넌트
- **contexts/**: React Context를 활용한 전역 상태 관리
- **hooks/**: 커스텀 React 훅
- **lib/**: 유틸리티 함수 및 헬퍼
- **index.css**: 전역 스타일 및 디자인 시스템

### 주요 기술 스택

- **React 19**: UI 라이브러리
- **TypeScript**: 타입 안정성
- **Tailwind CSS 4**: 유틸리티 기반 스타일링
- **shadcn/ui**: 고품질 UI 컴포넌트 라이브러리
- **Vite**: 빠른 개발 서버 및 빌드 도구
- **Wouter**: 가벼운 라우팅 라이브러리

---

## 💡 주요 설계 결정

### 1. 상태 관리
현재는 React의 `useState`를 사용하여 간단하게 구현했습니다. 추후 복잡한 상태 관리가 필요하면 Zustand 또는 Redux를 도입하는 것을 권장합니다.

### 2. 라우팅
Wouter를 사용하여 가벼운 라우팅을 구현했습니다. 더 복잡한 라우팅이 필요하면 React Router로 마이그레이션할 수 있습니다.

### 3. 디자인 시스템
OKLCH 색상 공간을 사용하여 일관된 색상 팔레트를 유지하고 있습니다. 이는 더 정확한 색상 관리와 다크 모드 지원을 가능하게 합니다.

### 4. 컴포넌트 구조
shadcn/ui를 기반으로 하여 커스텀 컴포넌트(SilentMoonButton, MeditationCard 등)를 구현했습니다. 이는 일관된 디자인과 빠른 개발을 가능하게 합니다.

---

## 📝 라이선스

MIT License - 자유롭게 사용, 수정, 배포할 수 있습니다.
