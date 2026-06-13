import { useParams, useLocation } from "wouter";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { RotateCcw, Home, Trophy, Share2 } from "lucide-react";
import { motion } from "framer-motion";
import { MOCK_CATEGORIES } from "@/lib/mockData";

export default function QuizResult() {
  const { categoryId } = useParams<{ categoryId: string }>();
  const [, setLocation] = useLocation();

  const queryParams = new URLSearchParams(window.location.search);
  const score = parseFloat(queryParams.get("score") || "0");
  const correct = parseInt(queryParams.get("correct") || "0");
  const total = parseInt(queryParams.get("total") || "0");
  const time = parseInt(queryParams.get("time") || "0");

  const category = MOCK_CATEGORIES.find((c) => c.id === parseInt(categoryId || "0"));

  const getGrade = (score: number) => {
    if (score >= 90) return { grade: "A+", color: "text-emerald-500", bgColor: "bg-emerald-100 dark:bg-emerald-900/30", emoji: "🏆", message: "완벽해요! 천재시군요!" };
    if (score >= 80) return { grade: "A", color: "text-green-500", bgColor: "bg-green-100 dark:bg-green-900/30", emoji: "🎉", message: "훌륭한 성적입니다!" };
    if (score >= 70) return { grade: "B", color: "text-blue-500", bgColor: "bg-blue-100 dark:bg-blue-900/30", emoji: "👍", message: "좋은 결과에요!" };
    if (score >= 60) return { grade: "C", color: "text-yellow-500", bgColor: "bg-yellow-100 dark:bg-yellow-900/30", emoji: "💪", message: "조금 더 분발해봐요!" };
    return { grade: "F", color: "text-red-500", bgColor: "bg-red-100 dark:bg-red-900/30", emoji: "📚", message: "다시 한번 도전해보세요!" };
  };

  const gradeInfo = getGrade(score);
  const minutes = Math.floor(time / 60);
  const seconds = time % 60;

  // Confetti-like particles for high scores
  const showCelebration = score >= 80;

  return (
    <div className="min-h-screen bg-gradient-to-br from-purple-50 via-white to-blue-50 dark:from-slate-900 dark:via-slate-800 dark:to-slate-900 p-4 relative overflow-hidden">
      {/* Decorative blobs */}
      <div className="fixed top-[-15%] right-[-10%] w-[500px] h-[500px] rounded-full bg-purple-200/20 dark:bg-purple-900/10 blur-3xl pointer-events-none" />
      <div className="fixed bottom-[-15%] left-[-10%] w-[500px] h-[500px] rounded-full bg-blue-200/20 dark:bg-blue-900/10 blur-3xl pointer-events-none" />

      {/* Floating celebration particles */}
      {showCelebration && (
        <>
          {[...Array(12)].map((_, i) => (
            <motion.div
              key={i}
              className="fixed pointer-events-none text-2xl"
              initial={{
                x: `${Math.random() * 100}vw`,
                y: "-10vh",
                rotate: 0,
                opacity: 1,
              }}
              animate={{
                y: "110vh",
                rotate: 360 * (Math.random() > 0.5 ? 1 : -1),
                opacity: [1, 1, 0],
              }}
              transition={{
                duration: 3 + Math.random() * 3,
                delay: Math.random() * 2,
                ease: "easeIn",
                repeat: Infinity,
                repeatDelay: Math.random() * 2,
              }}
            >
              {["🎉", "⭐", "🎊", "✨", "🌟", "💫"][i % 6]}
            </motion.div>
          ))}
        </>
      )}

      <div className="max-w-md mx-auto relative z-10">
        {/* Header */}
        <motion.div
          initial={{ opacity: 0, y: -20 }}
          animate={{ opacity: 1, y: 0 }}
          className="text-center mb-6 pt-4"
        >
          <p className="text-sm text-gray-500 dark:text-gray-400 mb-1">{category?.icon} {category?.name}</p>
          <h1 className="text-2xl font-bold text-gray-900 dark:text-white">
            퀴즈 완료!
          </h1>
        </motion.div>

        {/* Score Card */}
        <motion.div
          initial={{ opacity: 0, scale: 0.9 }}
          animate={{ opacity: 1, scale: 1 }}
          transition={{ type: "spring", stiffness: 200, damping: 20, delay: 0.1 }}
        >
          <Card className="card-base p-8 mb-6 text-center bg-white/70 dark:bg-slate-800/70 backdrop-blur-sm overflow-hidden relative">
            {/* Background gradient accent */}
            <div className="absolute inset-0 bg-gradient-to-br from-primary/5 to-transparent pointer-events-none" />

            {/* Grade */}
            <motion.div
              initial={{ scale: 0, rotate: -180 }}
              animate={{ scale: 1, rotate: 0 }}
              transition={{ type: "spring", stiffness: 200, damping: 15, delay: 0.3 }}
              className="relative z-10"
            >
              <div className={`inline-flex items-center justify-center w-24 h-24 ${gradeInfo.bgColor} rounded-3xl mb-4`}>
                <span className={`text-5xl font-extrabold ${gradeInfo.color}`}>
                  {gradeInfo.grade}
                </span>
              </div>
            </motion.div>

            {/* Emoji */}
            <motion.div
              initial={{ opacity: 0, y: 10 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.5 }}
              className="text-4xl mb-2 relative z-10"
            >
              {gradeInfo.emoji}
            </motion.div>

            {/* Score */}
            <motion.div
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              transition={{ delay: 0.6 }}
              className="relative z-10"
            >
              <h2 className="text-4xl font-extrabold text-gradient mb-1">
                {score.toFixed(0)}점
              </h2>
              <p className="text-sm text-gray-500 dark:text-gray-400 mb-1">
                {gradeInfo.message}
              </p>
              <p className="text-xs text-gray-400 dark:text-gray-500">
                {correct}개 정답 / {total}개 문제
              </p>
            </motion.div>

            {/* Stats Grid */}
            <motion.div
              initial={{ opacity: 0, y: 10 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.7 }}
              className="grid grid-cols-3 gap-3 mt-6 relative z-10"
            >
              <div className="bg-green-50 dark:bg-green-900/20 rounded-2xl p-3">
                <p className="text-xs text-gray-500 dark:text-gray-400">정답</p>
                <p className="text-xl font-bold text-green-600 dark:text-green-400">{correct}</p>
              </div>
              <div className="bg-red-50 dark:bg-red-900/20 rounded-2xl p-3">
                <p className="text-xs text-gray-500 dark:text-gray-400">오답</p>
                <p className="text-xl font-bold text-red-600 dark:text-red-400">{total - correct}</p>
              </div>
              <div className="bg-blue-50 dark:bg-blue-900/20 rounded-2xl p-3">
                <p className="text-xs text-gray-500 dark:text-gray-400">시간</p>
                <p className="text-xl font-bold text-blue-600 dark:text-blue-400">
                  {minutes}:{seconds.toString().padStart(2, "0")}
                </p>
              </div>
            </motion.div>
          </Card>
        </motion.div>

        {/* Action Buttons */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.8 }}
          className="space-y-3"
        >
          <Button
            onClick={() => setLocation(`/quiz/${categoryId}`)}
            className="w-full rounded-2xl h-12 text-base bg-primary hover:bg-primary/90 text-white shadow-lg shadow-purple-300/50 dark:shadow-purple-900/50"
          >
            <RotateCcw className="w-4 h-4 mr-2" />
            다시 풀기
          </Button>

          <div className="flex gap-3">
            <Button
              onClick={() => setLocation("/home")}
              variant="outline"
              className="flex-1 rounded-2xl h-11 border-gray-200 dark:border-gray-600"
            >
              <Home className="w-4 h-4 mr-2" />
              홈으로
            </Button>
            <Button
              onClick={() => setLocation("/leaderboard")}
              variant="outline"
              className="flex-1 rounded-2xl h-11 border-gray-200 dark:border-gray-600"
            >
              <Trophy className="w-4 h-4 mr-2" />
              랭킹 보기
            </Button>
          </div>
        </motion.div>
      </div>
    </div>
  );
}
