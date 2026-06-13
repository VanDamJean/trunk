// ============================================================================
// Mock Data Layer — Replaces MySQL/tRPC for standalone local execution
// ============================================================================

export interface MockUser {
  id: number;
  openId: string;
  name: string;
  email: string;
  role: string;
}

export interface MockCategory {
  id: number;
  name: string;
  description: string;
  icon: string;
  color: string;
  order: number;
  quizCount?: number;
}

export interface MockQuiz {
  id: number;
  categoryId: number;
  question: string;
  options: string[];
  correctAnswer: number;
  explanation: string;
  difficulty: "easy" | "medium" | "hard";
  order: number;
}

export interface MockQuizResult {
  id: number;
  userId: number;
  categoryId: number;
  categoryName?: string;
  totalQuestions: number;
  correctAnswers: number;
  score: string;
  timeSpent: number;
  answers: Array<{ quizId: number; selectedAnswer: number; isCorrect: boolean }>;
  completedAt: string;
  createdAt: string;
}

export interface MockUserStats {
  userId: number;
  totalQuizzes: number;
  totalScore: string;
  averageScore: string;
  totalTimeSpent: number;
  badges: Array<{ id: string; name: string; icon: string; unlockedAt: string }>;
  lastPlayedAt: string | null;
}

export interface MockLeaderboardEntry {
  userId: number;
  userName: string;
  totalScore: string;
  rank: number;
  avatarColor: string;
  quizCount: number;
}

export interface MockBadge {
  id: string;
  name: string;
  description: string;
  icon: string;
  criteria: { type: "score" | "streak" | "category" | "time"; value: number };
  unlocked: boolean;
  unlockedAt?: string;
}

// ── Sample Categories ──────────────────────────────────────────────────────

export const MOCK_CATEGORIES: MockCategory[] = [
  {
    id: 1,
    name: "수학",
    description: "기초 수학부터 고급 수학까지 도전해보세요",
    icon: "🔢",
    color: "#8b5cf6",
    order: 0,
    quizCount: 5,
  },
  {
    id: 2,
    name: "과학",
    description: "물리, 화학, 생물학의 세계를 탐험하세요",
    icon: "🔬",
    color: "#06b6d4",
    order: 1,
    quizCount: 5,
  },
  {
    id: 3,
    name: "역사",
    description: "세계 역사와 문화를 알아보세요",
    icon: "📜",
    color: "#f59e0b",
    order: 2,
    quizCount: 5,
  },
  {
    id: 4,
    name: "프로그래밍",
    description: "코딩과 컴퓨터 과학 지식을 테스트하세요",
    icon: "💻",
    color: "#10b981",
    order: 3,
    quizCount: 5,
  },
  {
    id: 5,
    name: "일반상식",
    description: "다양한 분야의 상식을 확인해보세요",
    icon: "🧠",
    color: "#ef4444",
    order: 4,
    quizCount: 5,
  },
];

// ── Sample Quizzes ─────────────────────────────────────────────────────────

export const MOCK_QUIZZES: MockQuiz[] = [
  // 수학 (categoryId: 1)
  { id: 1, categoryId: 1, question: "2 + 2 = ?", options: ["2", "3", "4", "5"], correctAnswer: 2, explanation: "2 더하기 2는 4입니다.", difficulty: "easy", order: 0 },
  { id: 2, categoryId: 1, question: "15 × 3 = ?", options: ["35", "45", "55", "65"], correctAnswer: 1, explanation: "15 곱하기 3은 45입니다.", difficulty: "easy", order: 1 },
  { id: 3, categoryId: 1, question: "√144 = ?", options: ["10", "11", "12", "13"], correctAnswer: 2, explanation: "144의 제곱근은 12입니다.", difficulty: "medium", order: 2 },
  { id: 4, categoryId: 1, question: "π(파이)의 소수점 둘째자리까지의 값은?", options: ["3.12", "3.14", "3.16", "3.18"], correctAnswer: 1, explanation: "π는 약 3.14159...입니다.", difficulty: "easy", order: 3 },
  { id: 5, categoryId: 1, question: "log₂(256) = ?", options: ["6", "7", "8", "9"], correctAnswer: 2, explanation: "2⁸ = 256이므로 log₂(256) = 8입니다.", difficulty: "hard", order: 4 },

  // 과학 (categoryId: 2)
  { id: 6, categoryId: 2, question: "물의 끓는점은 몇 도인가요?", options: ["50°C", "75°C", "100°C", "125°C"], correctAnswer: 2, explanation: "표준 대기압에서 물의 끓는점은 100°C입니다.", difficulty: "easy", order: 0 },
  { id: 7, categoryId: 2, question: "태양계의 행성은 몇 개인가요?", options: ["7개", "8개", "9개", "10개"], correctAnswer: 1, explanation: "태양계에는 8개의 행성이 있습니다.", difficulty: "easy", order: 1 },
  { id: 8, categoryId: 2, question: "빛의 속도는 약 몇 km/s인가요?", options: ["100,000", "200,000", "300,000", "400,000"], correctAnswer: 2, explanation: "빛의 속도는 약 300,000km/s입니다.", difficulty: "medium", order: 2 },
  { id: 9, categoryId: 2, question: "원소 기호 'Au'는 무엇을 나타내나요?", options: ["은", "금", "구리", "알루미늄"], correctAnswer: 1, explanation: "Au는 금(Gold)의 원소 기호입니다.", difficulty: "medium", order: 3 },
  { id: 10, categoryId: 2, question: "인간의 DNA 염기쌍 수는 약?", options: ["30억 개", "50억 개", "70억 개", "100억 개"], correctAnswer: 0, explanation: "인간의 DNA는 약 30억 개의 염기쌍으로 이루어져 있습니다.", difficulty: "hard", order: 4 },

  // 역사 (categoryId: 3)
  { id: 11, categoryId: 3, question: "한국 전쟁은 언제 일어났나요?", options: ["1945년", "1950년", "1960년", "1970년"], correctAnswer: 1, explanation: "한국 전쟁은 1950년 6월 25일에 시작되었습니다.", difficulty: "medium", order: 0 },
  { id: 12, categoryId: 3, question: "프랑스 혁명은 언제 시작되었나요?", options: ["1789년", "1799년", "1809년", "1819년"], correctAnswer: 0, explanation: "프랑스 혁명은 1789년에 시작되었습니다.", difficulty: "medium", order: 1 },
  { id: 13, categoryId: 3, question: "세계 2차 대전 종전 연도는?", options: ["1943년", "1944년", "1945년", "1946년"], correctAnswer: 2, explanation: "제2차 세계 대전은 1945년에 종료되었습니다.", difficulty: "easy", order: 2 },
  { id: 14, categoryId: 3, question: "대한민국 최초의 대통령은?", options: ["박정희", "이승만", "윤보선", "김구"], correctAnswer: 1, explanation: "이승만은 대한민국 초대 대통령입니다.", difficulty: "easy", order: 3 },
  { id: 15, categoryId: 3, question: "로마 제국이 멸망한 연도는?", options: ["376년", "410년", "453년", "476년"], correctAnswer: 3, explanation: "서로마 제국은 476년에 멸망했습니다.", difficulty: "hard", order: 4 },

  // 프로그래밍 (categoryId: 4)
  { id: 16, categoryId: 4, question: "HTML에서 가장 큰 제목 태그는?", options: ["<h6>", "<h3>", "<h1>", "<heading>"], correctAnswer: 2, explanation: "<h1>이 가장 큰 제목 태그입니다.", difficulty: "easy", order: 0 },
  { id: 17, categoryId: 4, question: "JavaScript에서 배열의 길이를 구하는 속성은?", options: [".size", ".length", ".count", ".total"], correctAnswer: 1, explanation: ".length 속성으로 배열의 길이를 구합니다.", difficulty: "easy", order: 1 },
  { id: 18, categoryId: 4, question: "'===' 연산자는 무엇을 비교하나요?", options: ["값만", "타입만", "값과 타입 모두", "참조"], correctAnswer: 2, explanation: "===는 값과 타입을 모두 비교하는 엄격 동등 연산자입니다.", difficulty: "medium", order: 2 },
  { id: 19, categoryId: 4, question: "Git에서 원격 저장소의 변경사항을 가져오는 명령어는?", options: ["git push", "git pull", "git commit", "git add"], correctAnswer: 1, explanation: "git pull은 원격 저장소의 변경사항을 로컬로 가져옵니다.", difficulty: "easy", order: 3 },
  { id: 20, categoryId: 4, question: "TCP/IP 모델의 계층 수는?", options: ["3개", "4개", "5개", "7개"], correctAnswer: 1, explanation: "TCP/IP 모델은 4개의 계층으로 구성됩니다.", difficulty: "hard", order: 4 },

  // 일반상식 (categoryId: 5)
  { id: 21, categoryId: 5, question: "세계에서 가장 긴 강은?", options: ["아마존강", "나일강", "양쯔강", "미시시피강"], correctAnswer: 1, explanation: "나일강은 약 6,650km로 세계에서 가장 긴 강입니다.", difficulty: "easy", order: 0 },
  { id: 22, categoryId: 5, question: "올림픽 오륜기의 색상이 아닌 것은?", options: ["파란색", "보라색", "빨간색", "검은색"], correctAnswer: 1, explanation: "올림픽 오륜기는 파랑, 노랑, 검정, 초록, 빨강입니다.", difficulty: "easy", order: 1 },
  { id: 23, categoryId: 5, question: "세계에서 가장 높은 산은?", options: ["K2", "에베레스트", "칸첸중가", "로체"], correctAnswer: 1, explanation: "에베레스트산은 해발 8,849m로 세계에서 가장 높습니다.", difficulty: "easy", order: 2 },
  { id: 24, categoryId: 5, question: "커피의 원산지로 알려진 나라는?", options: ["브라질", "콜롬비아", "에티오피아", "인도네시아"], correctAnswer: 2, explanation: "커피는 에티오피아에서 기원한 것으로 알려져 있습니다.", difficulty: "medium", order: 3 },
  { id: 25, categoryId: 5, question: "인체에서 가장 큰 장기는?", options: ["간", "폐", "피부", "대장"], correctAnswer: 2, explanation: "피부는 인체에서 가장 큰 장기입니다.", difficulty: "medium", order: 4 },
];

// ── Sample Badges ──────────────────────────────────────────────────────────

export const MOCK_BADGES: MockBadge[] = [
  { id: "first-quiz", name: "첫 발걸음", description: "첫 번째 퀴즈를 완료하세요", icon: "🎯", criteria: { type: "streak", value: 1 }, unlocked: true, unlockedAt: "2026-04-10T10:00:00Z" },
  { id: "perfect-score", name: "퍼펙트!", description: "퀴즈에서 100점을 받으세요", icon: "💯", criteria: { type: "score", value: 100 }, unlocked: true, unlockedAt: "2026-04-12T14:30:00Z" },
  { id: "five-quizzes", name: "퀴즈 마스터", description: "5개의 퀴즈를 완료하세요", icon: "🏅", criteria: { type: "streak", value: 5 }, unlocked: true, unlockedAt: "2026-04-15T09:00:00Z" },
  { id: "speed-demon", name: "스피드 러너", description: "60초 이내에 퀴즈를 완료하세요", icon: "⚡", criteria: { type: "time", value: 60 }, unlocked: false },
  { id: "all-categories", name: "박학다식", description: "모든 카테고리의 퀴즈를 풀어보세요", icon: "📚", criteria: { type: "category", value: 5 }, unlocked: false },
  { id: "ten-quizzes", name: "퀴즈 중독자", description: "10개의 퀴즈를 완료하세요", icon: "🔥", criteria: { type: "streak", value: 10 }, unlocked: false },
  { id: "high-scorer", name: "고득점자", description: "평균 점수 90점 이상을 달성하세요", icon: "⭐", criteria: { type: "score", value: 90 }, unlocked: false },
  { id: "marathon", name: "마라톤 러너", description: "총 플레이 시간 30분 이상", icon: "🏃", criteria: { type: "time", value: 1800 }, unlocked: false },
];

// ── Sample Leaderboard ─────────────────────────────────────────────────────

export const MOCK_LEADERBOARD: MockLeaderboardEntry[] = [
  { userId: 101, userName: "김퀴즈왕", totalScore: "4850.00", rank: 1, avatarColor: "#f59e0b", quizCount: 52 },
  { userId: 102, userName: "박천재", totalScore: "4620.50", rank: 2, avatarColor: "#8b5cf6", quizCount: 48 },
  { userId: 103, userName: "이학자", totalScore: "4380.00", rank: 3, avatarColor: "#06b6d4", quizCount: 45 },
  { userId: 104, userName: "최두뇌", totalScore: "4120.75", rank: 4, avatarColor: "#10b981", quizCount: 43 },
  { userId: 105, userName: "정박사", totalScore: "3890.00", rank: 5, avatarColor: "#ef4444", quizCount: 41 },
  { userId: 999, userName: "테스트 사용자", totalScore: "1520.00", rank: 6, avatarColor: "#8b5cf6", quizCount: 8 },
  { userId: 106, userName: "홍길동", totalScore: "1200.50", rank: 7, avatarColor: "#f97316", quizCount: 15 },
  { userId: 107, userName: "강멘사", totalScore: "980.00", rank: 8, avatarColor: "#ec4899", quizCount: 12 },
  { userId: 108, userName: "윤도전", totalScore: "750.25", rank: 9, avatarColor: "#14b8a6", quizCount: 9 },
  { userId: 109, userName: "한초보", totalScore: "320.00", rank: 10, avatarColor: "#a855f7", quizCount: 4 },
];

// ── Sample Results History ─────────────────────────────────────────────────

export const MOCK_RESULTS: MockQuizResult[] = [
  { id: 1, userId: 999, categoryId: 1, categoryName: "수학", totalQuestions: 5, correctAnswers: 5, score: "100.00", timeSpent: 45, answers: [], completedAt: "2026-04-17T14:30:00Z", createdAt: "2026-04-17T14:30:00Z" },
  { id: 2, userId: 999, categoryId: 2, categoryName: "과학", totalQuestions: 5, correctAnswers: 4, score: "80.00", timeSpent: 72, answers: [], completedAt: "2026-04-16T10:15:00Z", createdAt: "2026-04-16T10:15:00Z" },
  { id: 3, userId: 999, categoryId: 4, categoryName: "프로그래밍", totalQuestions: 5, correctAnswers: 3, score: "60.00", timeSpent: 95, answers: [], completedAt: "2026-04-15T18:00:00Z", createdAt: "2026-04-15T18:00:00Z" },
  { id: 4, userId: 999, categoryId: 3, categoryName: "역사", totalQuestions: 5, correctAnswers: 5, score: "100.00", timeSpent: 58, answers: [], completedAt: "2026-04-14T20:45:00Z", createdAt: "2026-04-14T20:45:00Z" },
  { id: 5, userId: 999, categoryId: 5, categoryName: "일반상식", totalQuestions: 5, correctAnswers: 2, score: "40.00", timeSpent: 120, answers: [], completedAt: "2026-04-13T12:00:00Z", createdAt: "2026-04-13T12:00:00Z" },
  { id: 6, userId: 999, categoryId: 1, categoryName: "수학", totalQuestions: 5, correctAnswers: 4, score: "80.00", timeSpent: 62, answers: [], completedAt: "2026-04-12T09:30:00Z", createdAt: "2026-04-12T09:30:00Z" },
  { id: 7, userId: 999, categoryId: 2, categoryName: "과학", totalQuestions: 5, correctAnswers: 3, score: "60.00", timeSpent: 88, answers: [], completedAt: "2026-04-11T15:20:00Z", createdAt: "2026-04-11T15:20:00Z" },
  { id: 8, userId: 999, categoryId: 3, categoryName: "역사", totalQuestions: 5, correctAnswers: 5, score: "100.00", timeSpent: 42, answers: [], completedAt: "2026-04-10T11:00:00Z", createdAt: "2026-04-10T11:00:00Z" },
];

// ── User Stats ─────────────────────────────────────────────────────────────

export const MOCK_USER_STATS: MockUserStats = {
  userId: 999,
  totalQuizzes: 8,
  totalScore: "1520.00",
  averageScore: "77.50",
  totalTimeSpent: 582,
  badges: MOCK_BADGES.filter((b) => b.unlocked).map((b) => ({
    id: b.id,
    name: b.name,
    icon: b.icon,
    unlockedAt: b.unlockedAt!,
  })),
  lastPlayedAt: "2026-04-17T14:30:00Z",
};

// ── Mock API Functions (replaces tRPC calls) ───────────────────────────────

const resultStore = [...MOCK_RESULTS];
let resultIdCounter = resultStore.length + 1;

export const mockApi = {
  getCategories: () => MOCK_CATEGORIES,

  getQuizzesByCategory: (categoryId: number) =>
    MOCK_QUIZZES.filter((q) => q.categoryId === categoryId),

  saveResult: (result: {
    categoryId: number;
    totalQuestions: number;
    correctAnswers: number;
    score: string;
    timeSpent: number;
    answers: Array<{ quizId: number; selectedAnswer: number; isCorrect: boolean }>;
  }) => {
    const category = MOCK_CATEGORIES.find((c) => c.id === result.categoryId);
    const newResult: MockQuizResult = {
      id: resultIdCounter++,
      userId: 999,
      ...result,
      categoryName: category?.name,
      completedAt: new Date().toISOString(),
      createdAt: new Date().toISOString(),
    };
    resultStore.unshift(newResult);

    // Update stats
    MOCK_USER_STATS.totalQuizzes += 1;
    MOCK_USER_STATS.totalScore = (
      parseFloat(MOCK_USER_STATS.totalScore) + parseFloat(result.score)
    ).toFixed(2);
    MOCK_USER_STATS.averageScore = (
      parseFloat(MOCK_USER_STATS.totalScore) / MOCK_USER_STATS.totalQuizzes
    ).toFixed(2);
    MOCK_USER_STATS.totalTimeSpent += result.timeSpent;
    MOCK_USER_STATS.lastPlayedAt = new Date().toISOString();

    return newResult;
  },

  getUserStats: () => MOCK_USER_STATS,

  getLeaderboard: () => MOCK_LEADERBOARD,

  getUserRank: () =>
    MOCK_LEADERBOARD.find((e) => e.userId === 999) ?? null,

  getResultHistory: () => resultStore,

  getBadges: () => MOCK_BADGES,
};
