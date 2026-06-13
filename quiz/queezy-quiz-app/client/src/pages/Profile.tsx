import { mockApi, MOCK_BADGES } from "@/lib/mockData";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { ArrowLeft, Trophy, Clock, Target, Award, ChevronRight } from "lucide-react";
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

export default function Profile() {
  const [, setLocation] = useLocation();
  const stats = mockApi.getUserStats();
  const badges = MOCK_BADGES;
  const unlockedBadges = badges.filter((b) => b.unlocked);
  const totalMinutes = Math.floor(stats.totalTimeSpent / 60);

  return (
    <div className="min-h-screen bg-gradient-to-br from-purple-50 via-white to-blue-50 dark:from-slate-900 dark:via-slate-800 dark:to-slate-900 pb-8">
      {/* Decorative */}
      <div className="fixed top-[-15%] right-[-10%] w-[500px] h-[500px] rounded-full bg-purple-200/20 dark:bg-purple-900/10 blur-3xl pointer-events-none" />

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
          <h1 className="text-lg font-bold text-gray-900 dark:text-white">프로필</h1>
        </div>
      </motion.div>

      <div className="container mx-auto px-4 py-6 max-w-lg">
        {/* Profile Card */}
        <motion.div
          initial={{ opacity: 0, scale: 0.95 }}
          animate={{ opacity: 1, scale: 1 }}
          transition={{ type: "spring", stiffness: 200, damping: 20 }}
        >
          <Card className="card-base p-6 mb-6 bg-gradient-to-br from-primary/5 to-purple-100/50 dark:from-primary/10 dark:to-purple-900/20 border-primary/10 overflow-hidden relative">
            <div className="absolute top-0 right-0 w-40 h-40 bg-primary/5 rounded-full -translate-y-1/2 translate-x-1/2" />
            <div className="relative z-10 flex items-center gap-4">
              {/* Avatar */}
              <motion.div
                className="w-16 h-16 rounded-2xl bg-gradient-to-br from-primary to-purple-500 flex items-center justify-center text-3xl text-white shadow-lg"
                whileHover={{ scale: 1.05, rotate: 3 }}
              >
                😊
              </motion.div>
              <div>
                <h2 className="text-xl font-bold text-gray-900 dark:text-white">테스트 사용자</h2>
                <p className="text-sm text-gray-500 dark:text-gray-400">퀴즈 마스터 Lv.{Math.floor(stats.totalQuizzes / 3) + 1}</p>
              </div>
            </div>
          </Card>
        </motion.div>

        {/* Stats Grid */}
        <motion.div
          variants={container}
          initial="hidden"
          animate="show"
          className="grid grid-cols-2 gap-3 mb-6"
        >
          <motion.div variants={item}>
            <Card className="card-base p-4 bg-white/70 dark:bg-slate-800/70 backdrop-blur-sm">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 rounded-xl bg-primary/10 flex items-center justify-center">
                  <Target className="w-5 h-5 text-primary" />
                </div>
                <div>
                  <p className="text-2xl font-bold text-gray-900 dark:text-white">{stats.totalQuizzes}</p>
                  <p className="text-xs text-gray-500 dark:text-gray-400">총 퀴즈 수</p>
                </div>
              </div>
            </Card>
          </motion.div>

          <motion.div variants={item}>
            <Card className="card-base p-4 bg-white/70 dark:bg-slate-800/70 backdrop-blur-sm">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 rounded-xl bg-emerald-100 dark:bg-emerald-900/30 flex items-center justify-center">
                  <Trophy className="w-5 h-5 text-emerald-500" />
                </div>
                <div>
                  <p className="text-2xl font-bold text-gray-900 dark:text-white">{parseFloat(stats.averageScore).toFixed(0)}%</p>
                  <p className="text-xs text-gray-500 dark:text-gray-400">평균 점수</p>
                </div>
              </div>
            </Card>
          </motion.div>

          <motion.div variants={item}>
            <Card className="card-base p-4 bg-white/70 dark:bg-slate-800/70 backdrop-blur-sm">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 rounded-xl bg-blue-100 dark:bg-blue-900/30 flex items-center justify-center">
                  <Clock className="w-5 h-5 text-blue-500" />
                </div>
                <div>
                  <p className="text-2xl font-bold text-gray-900 dark:text-white">{totalMinutes}분</p>
                  <p className="text-xs text-gray-500 dark:text-gray-400">총 학습 시간</p>
                </div>
              </div>
            </Card>
          </motion.div>

          <motion.div variants={item}>
            <Card className="card-base p-4 bg-white/70 dark:bg-slate-800/70 backdrop-blur-sm">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 rounded-xl bg-amber-100 dark:bg-amber-900/30 flex items-center justify-center">
                  <Award className="w-5 h-5 text-amber-500" />
                </div>
                <div>
                  <p className="text-2xl font-bold text-gray-900 dark:text-white">{unlockedBadges.length}/{badges.length}</p>
                  <p className="text-xs text-gray-500 dark:text-gray-400">배지 수</p>
                </div>
              </div>
            </Card>
          </motion.div>
        </motion.div>

        {/* Badges Preview */}
        <motion.div
          initial={{ opacity: 0, y: 15 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.3 }}
        >
          <div className="flex justify-between items-center mb-3">
            <h3 className="text-lg font-bold text-gray-900 dark:text-white">획득한 배지</h3>
            <Button
              onClick={() => setLocation("/badges")}
              variant="ghost"
              size="sm"
              className="text-primary text-sm"
            >
              전체 보기 <ChevronRight className="w-4 h-4 ml-1" />
            </Button>
          </div>

          <Card className="card-base p-4 bg-white/70 dark:bg-slate-800/70 backdrop-blur-sm">
            <div className="flex gap-3 overflow-x-auto scrollbar-hide pb-1">
              {unlockedBadges.map((badge) => (
                <motion.div
                  key={badge.id}
                  className="flex flex-col items-center gap-1 flex-shrink-0"
                  whileHover={{ scale: 1.1, y: -4 }}
                >
                  <div className="w-14 h-14 rounded-2xl bg-gradient-to-br from-primary/10 to-purple-100 dark:from-primary/20 dark:to-purple-900/30 flex items-center justify-center text-2xl shadow-sm">
                    {badge.icon}
                  </div>
                  <span className="text-[10px] text-gray-500 dark:text-gray-400 text-center w-14 truncate">{badge.name}</span>
                </motion.div>
              ))}
              {unlockedBadges.length === 0 && (
                <p className="text-sm text-gray-400 dark:text-gray-500 py-4 text-center w-full">아직 획득한 배지가 없어요</p>
              )}
            </div>
          </Card>
        </motion.div>

        {/* Quick Actions */}
        <motion.div
          initial={{ opacity: 0, y: 15 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.4 }}
          className="mt-6 space-y-3"
        >
          <Button
            onClick={() => setLocation("/history")}
            variant="outline"
            className="w-full rounded-2xl h-12 justify-between border-gray-200 dark:border-gray-600"
          >
            <span className="flex items-center gap-2">
              <Clock className="w-4 h-4" /> 플레이 기록
            </span>
            <ChevronRight className="w-4 h-4" />
          </Button>
          <Button
            onClick={() => setLocation("/leaderboard")}
            variant="outline"
            className="w-full rounded-2xl h-12 justify-between border-gray-200 dark:border-gray-600"
          >
            <span className="flex items-center gap-2">
              <Trophy className="w-4 h-4" /> 랭킹 보기
            </span>
            <ChevronRight className="w-4 h-4" />
          </Button>
        </motion.div>
      </div>
    </div>
  );
}
