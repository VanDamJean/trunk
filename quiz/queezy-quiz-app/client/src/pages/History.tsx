import { mockApi, MOCK_CATEGORIES } from "@/lib/mockData";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { ArrowLeft, Clock, CheckCircle, XCircle, RotateCcw } from "lucide-react";
import { useLocation } from "wouter";
import { motion } from "framer-motion";

const container = {
  hidden: { opacity: 0 },
  show: { opacity: 1, transition: { staggerChildren: 0.06, delayChildren: 0.15 } },
};
const item = {
  hidden: { opacity: 0, y: 15 },
  show: { opacity: 1, y: 0 },
};

const getGradeInfo = (score: number) => {
  if (score >= 90) return { grade: "A+", color: "text-emerald-500", bg: "bg-emerald-100 dark:bg-emerald-900/30" };
  if (score >= 80) return { grade: "A", color: "text-green-500", bg: "bg-green-100 dark:bg-green-900/30" };
  if (score >= 70) return { grade: "B", color: "text-blue-500", bg: "bg-blue-100 dark:bg-blue-900/30" };
  if (score >= 60) return { grade: "C", color: "text-yellow-500", bg: "bg-yellow-100 dark:bg-yellow-900/30" };
  return { grade: "F", color: "text-red-500", bg: "bg-red-100 dark:bg-red-900/30" };
};

function formatTimeAgo(dateString: string) {
  const now = new Date();
  const date = new Date(dateString);
  const diff = Math.floor((now.getTime() - date.getTime()) / 1000);

  if (diff < 60) return "방금 전";
  if (diff < 3600) return `${Math.floor(diff / 60)}분 전`;
  if (diff < 86400) return `${Math.floor(diff / 3600)}시간 전`;
  if (diff < 604800) return `${Math.floor(diff / 86400)}일 전`;
  return date.toLocaleDateString("ko-KR", { month: "short", day: "numeric" });
}

export default function History() {
  const [, setLocation] = useLocation();
  const results = mockApi.getResultHistory();

  return (
    <div className="min-h-screen bg-gradient-to-br from-purple-50 via-white to-blue-50 dark:from-slate-900 dark:via-slate-800 dark:to-slate-900 pb-8">
      {/* Decorative */}
      <div className="fixed bottom-[-15%] right-[-10%] w-[500px] h-[500px] rounded-full bg-blue-200/20 dark:bg-blue-900/10 blur-3xl pointer-events-none" />

      {/* Header */}
      <motion.div
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
        className="bg-white/70 dark:bg-slate-800/70 backdrop-blur-xl border-b border-gray-100 dark:border-slate-700/50 sticky top-0 z-20"
      >
        <div className="container mx-auto px-4 py-4 flex items-center gap-3">
          <Button onClick={() => setLocation("/home")} variant="ghost" size="icon" className="rounded-full">
            <ArrowLeft className="w-5 h-5" />
          </Button>
          <h1 className="text-lg font-bold text-gray-900 dark:text-white">📋 플레이 기록</h1>
        </div>
      </motion.div>

      <div className="container mx-auto px-4 py-6 max-w-lg">
        {/* Summary Stats */}
        <motion.div
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.1 }}
          className="grid grid-cols-2 gap-3 mb-6"
        >
          <Card className="card-base p-3 bg-white/70 dark:bg-slate-800/70 backdrop-blur-sm text-center">
            <p className="text-2xl font-bold text-primary">{results.length}</p>
            <p className="text-xs text-gray-500 dark:text-gray-400">총 플레이</p>
          </Card>
          <Card className="card-base p-3 bg-white/70 dark:bg-slate-800/70 backdrop-blur-sm text-center">
            <p className="text-2xl font-bold text-emerald-500">
              {results.length > 0
                ? (results.reduce((acc, r) => acc + parseFloat(r.score), 0) / results.length).toFixed(0)
                : 0}%
            </p>
            <p className="text-xs text-gray-500 dark:text-gray-400">평균 점수</p>
          </Card>
        </motion.div>

        {/* Results List */}
        {results.length > 0 ? (
          <motion.div
            variants={container}
            initial="hidden"
            animate="show"
            className="space-y-3"
          >
            {results.map((result) => {
              const category = MOCK_CATEGORIES.find((c) => c.id === result.categoryId);
              const score = parseFloat(result.score);
              const gradeInfo = getGradeInfo(score);
              const minutes = Math.floor(result.timeSpent / 60);
              const seconds = result.timeSpent % 60;

              return (
                <motion.div key={result.id} variants={item}>
                  <Card className="card-base p-4 bg-white/70 dark:bg-slate-800/70 backdrop-blur-sm hover:shadow-lg transition-all">
                    <div className="flex items-center gap-3">
                      {/* Category Icon */}
                      <div
                        className="w-12 h-12 rounded-2xl flex items-center justify-center text-xl flex-shrink-0"
                        style={{ backgroundColor: (category?.color || "#8b5cf6") + "15" }}
                      >
                        {category?.icon || "📚"}
                      </div>

                      {/* Info */}
                      <div className="flex-1 min-w-0">
                        <div className="flex items-center gap-2 mb-0.5">
                          <p className="text-sm font-bold text-gray-900 dark:text-white truncate">
                            {result.categoryName || category?.name || "퀴즈"}
                          </p>
                          <span className="text-[10px] text-gray-400 dark:text-gray-500 flex-shrink-0">
                            {formatTimeAgo(result.completedAt)}
                          </span>
                        </div>
                        <div className="flex items-center gap-3 text-xs text-gray-500 dark:text-gray-400">
                          <span className="flex items-center gap-1">
                            <CheckCircle className="w-3 h-3 text-green-400" />
                            {result.correctAnswers}/{result.totalQuestions}
                          </span>
                          <span className="flex items-center gap-1">
                            <Clock className="w-3 h-3 text-blue-400" />
                            {minutes}:{seconds.toString().padStart(2, "0")}
                          </span>
                        </div>
                      </div>

                      {/* Grade */}
                      <div className={`w-10 h-10 rounded-xl ${gradeInfo.bg} flex items-center justify-center flex-shrink-0`}>
                        <span className={`text-sm font-bold ${gradeInfo.color}`}>{gradeInfo.grade}</span>
                      </div>
                    </div>

                    {/* Score Bar */}
                    <div className="mt-3 flex items-center gap-2">
                      <div className="flex-1 bg-gray-100 dark:bg-gray-800 rounded-full h-1.5 overflow-hidden">
                        <motion.div
                          className={`h-1.5 rounded-full ${
                            score >= 80 ? 'bg-gradient-to-r from-emerald-400 to-green-400' :
                            score >= 60 ? 'bg-gradient-to-r from-yellow-400 to-amber-400' :
                            'bg-gradient-to-r from-red-400 to-orange-400'
                          }`}
                          initial={{ width: 0 }}
                          animate={{ width: `${score}%` }}
                          transition={{ duration: 0.8, delay: 0.3 }}
                        />
                      </div>
                      <span className="text-xs font-medium text-gray-600 dark:text-gray-400 w-10 text-right">{score.toFixed(0)}%</span>
                    </div>
                  </Card>
                </motion.div>
              );
            })}
          </motion.div>
        ) : (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            className="text-center py-20"
          >
            <p className="text-4xl mb-4">📝</p>
            <p className="text-gray-500 dark:text-gray-400 mb-2">아직 플레이 기록이 없어요</p>
            <p className="text-sm text-gray-400 dark:text-gray-500 mb-6">첫 번째 퀴즈에 도전해보세요!</p>
            <Button
              onClick={() => setLocation("/home")}
              className="rounded-2xl bg-primary text-white"
            >
              퀴즈 풀러 가기
            </Button>
          </motion.div>
        )}
      </div>
    </div>
  );
}
