import { mockApi, MOCK_CATEGORIES } from "@/lib/mockData";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { LogOut, Trophy, User, History, Award } from "lucide-react";
import { useLocation } from "wouter";
import { motion } from "framer-motion";

const container = {
  hidden: { opacity: 0 },
  show: {
    opacity: 1,
    transition: { staggerChildren: 0.08, delayChildren: 0.1 },
  },
};

const item = {
  hidden: { opacity: 0, y: 20, scale: 0.95 },
  show: { opacity: 1, y: 0, scale: 1 },
};

export default function Home() {
  const [, setLocation] = useLocation();
  const categories = MOCK_CATEGORIES;
  const stats = mockApi.getUserStats();

  return (
    <div className="min-h-screen bg-gradient-to-br from-purple-50 via-white to-blue-50 dark:from-slate-900 dark:via-slate-800 dark:to-slate-900">
      {/* Decorative */}
      <div className="fixed top-[-15%] right-[-10%] w-[500px] h-[500px] rounded-full bg-purple-200/20 dark:bg-purple-900/10 blur-3xl pointer-events-none" />
      <div className="fixed bottom-[-15%] left-[-10%] w-[500px] h-[500px] rounded-full bg-blue-200/20 dark:bg-blue-900/10 blur-3xl pointer-events-none" />

      {/* Header */}
      <motion.div
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
        className="bg-white/70 dark:bg-slate-800/70 backdrop-blur-xl border-b border-gray-100 dark:border-slate-700/50 sticky top-0 z-20"
      >
        <div className="container mx-auto px-4 py-4 flex justify-between items-center">
          <div>
            <h1 className="text-2xl font-extrabold text-gradient tracking-tight">Queezy</h1>
            <p className="text-sm text-gray-500 dark:text-gray-400">
              테스트 사용자님, 환영합니다!
            </p>
          </div>
          <div className="flex items-center gap-2">
            <Button
              onClick={() => setLocation("/profile")}
              variant="ghost"
              size="icon"
              className="rounded-full"
            >
              <User className="w-5 h-5" />
            </Button>
          </div>
        </div>
      </motion.div>

      {/* Main Content */}
      <div className="container mx-auto px-4 py-6 pb-28 relative z-10">
        {/* Quick Stats Bar */}
        <motion.div
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.1 }}
          className="grid grid-cols-3 gap-3 mb-8"
        >
          <div className="card-base p-3 text-center bg-white/60 dark:bg-slate-800/60 backdrop-blur-sm">
            <p className="text-2xl font-bold text-primary">{stats.totalQuizzes}</p>
            <p className="text-xs text-gray-500 dark:text-gray-400">풀이 수</p>
          </div>
          <div className="card-base p-3 text-center bg-white/60 dark:bg-slate-800/60 backdrop-blur-sm">
            <p className="text-2xl font-bold text-primary">{parseFloat(stats.averageScore).toFixed(0)}%</p>
            <p className="text-xs text-gray-500 dark:text-gray-400">평균 점수</p>
          </div>
          <div className="card-base p-3 text-center bg-white/60 dark:bg-slate-800/60 backdrop-blur-sm">
            <p className="text-2xl font-bold text-primary">{stats.badges.length}</p>
            <p className="text-xs text-gray-500 dark:text-gray-400">획득 배지</p>
          </div>
        </motion.div>

        {/* Section Title */}
        <motion.div
          initial={{ opacity: 0, x: -10 }}
          animate={{ opacity: 1, x: 0 }}
          transition={{ delay: 0.15 }}
          className="mb-6"
        >
          <h2 className="text-2xl font-bold text-gray-900 dark:text-white">
            어떤 주제를 공부할까요?
          </h2>
          <p className="text-gray-500 dark:text-gray-400 text-sm mt-1">
            카테고리를 선택해서 퀴즈를 시작하세요
          </p>
        </motion.div>

        {/* Categories Grid */}
        <motion.div
          variants={container}
          initial="hidden"
          animate="show"
          className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4"
        >
          {categories.map((category) => (
            <motion.div key={category.id} variants={item}>
              <Card
                onClick={() => setLocation(`/quiz/${category.id}`)}
                className="card-base p-5 cursor-pointer group hover:shadow-xl hover:shadow-purple-100/50 dark:hover:shadow-purple-900/20 transition-all duration-300 bg-white/70 dark:bg-slate-800/70 backdrop-blur-sm border-gray-100 dark:border-slate-700/50"
              >
                <div className="flex items-start gap-4">
                  {/* Icon */}
                  <motion.div
                    className="w-14 h-14 rounded-2xl flex items-center justify-center text-2xl flex-shrink-0 shadow-sm"
                    style={{ backgroundColor: category.color + "20" }}
                    whileHover={{ scale: 1.1, rotate: 5 }}
                    whileTap={{ scale: 0.95 }}
                  >
                    <span style={{ filter: "drop-shadow(0 1px 2px rgba(0,0,0,0.1))" }}>
                      {category.icon}
                    </span>
                  </motion.div>

                  <div className="flex-1 min-w-0">
                    {/* Title */}
                    <h3 className="text-lg font-bold text-gray-900 dark:text-white group-hover:text-primary transition-colors">
                      {category.name}
                    </h3>

                    {/* Description */}
                    <p className="text-sm text-gray-500 dark:text-gray-400 mt-0.5 line-clamp-2">
                      {category.description}
                    </p>

                    {/* Quiz Count Badge */}
                    <div className="mt-2 inline-flex items-center gap-1 px-2 py-0.5 rounded-full bg-primary/10 text-primary text-xs font-medium">
                      {category.quizCount}문제
                    </div>
                  </div>
                </div>
              </Card>
            </motion.div>
          ))}
        </motion.div>
      </div>

      {/* Bottom Navigation */}
      <motion.div
        initial={{ opacity: 0, y: 30 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.4, type: "spring", stiffness: 200 }}
        className="fixed bottom-0 left-0 right-0 z-30"
      >
        <div className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-xl border-t border-gray-100 dark:border-slate-700/50 px-4 py-2 pb-[env(safe-area-inset-bottom,8px)]">
          <div className="max-w-md mx-auto flex justify-around items-center">
            <NavButton icon={<Award className="w-5 h-5" />} label="홈" active onClick={() => setLocation("/home")} />
            <NavButton icon={<Trophy className="w-5 h-5" />} label="랭킹" onClick={() => setLocation("/leaderboard")} />
            <NavButton icon={<History className="w-5 h-5" />} label="기록" onClick={() => setLocation("/history")} />
            <NavButton icon={<User className="w-5 h-5" />} label="프로필" onClick={() => setLocation("/profile")} />
          </div>
        </div>
      </motion.div>
    </div>
  );
}

function NavButton({
  icon,
  label,
  active,
  onClick,
}: {
  icon: React.ReactNode;
  label: string;
  active?: boolean;
  onClick: () => void;
}) {
  return (
    <button
      onClick={onClick}
      className={`flex flex-col items-center gap-0.5 px-4 py-1.5 rounded-xl transition-all ${
        active
          ? "text-primary"
          : "text-gray-400 dark:text-gray-500 hover:text-gray-600 dark:hover:text-gray-300"
      }`}
    >
      {icon}
      <span className="text-[10px] font-medium">{label}</span>
    </button>
  );
}
