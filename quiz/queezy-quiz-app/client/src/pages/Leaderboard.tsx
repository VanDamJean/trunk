import { mockApi } from "@/lib/mockData";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { ArrowLeft, Crown, Medal, Award } from "lucide-react";
import { useLocation } from "wouter";
import { motion } from "framer-motion";

const container = {
  hidden: { opacity: 0 },
  show: { opacity: 1, transition: { staggerChildren: 0.06, delayChildren: 0.2 } },
};
const item = {
  hidden: { opacity: 0, x: -20 },
  show: { opacity: 1, x: 0 },
};

const getRankIcon = (rank: number) => {
  switch (rank) {
    case 1: return <Crown className="w-5 h-5 text-yellow-500" />;
    case 2: return <Medal className="w-5 h-5 text-gray-400" />;
    case 3: return <Award className="w-5 h-5 text-amber-600" />;
    default: return <span className="text-sm font-bold text-gray-400 dark:text-gray-500 w-5 text-center">{rank}</span>;
  }
};

const getRankBg = (rank: number) => {
  switch (rank) {
    case 1: return "bg-gradient-to-r from-yellow-50 to-amber-50 dark:from-yellow-900/20 dark:to-amber-900/20 border-yellow-200 dark:border-yellow-800/50";
    case 2: return "bg-gradient-to-r from-gray-50 to-slate-50 dark:from-gray-800/50 dark:to-slate-800/50 border-gray-200 dark:border-gray-700/50";
    case 3: return "bg-gradient-to-r from-orange-50 to-amber-50 dark:from-orange-900/20 dark:to-amber-900/20 border-orange-200 dark:border-orange-800/50";
    default: return "bg-white/70 dark:bg-slate-800/70 border-gray-100 dark:border-slate-700/50";
  }
};

export default function Leaderboard() {
  const [, setLocation] = useLocation();
  const leaderboard = mockApi.getLeaderboard();
  const myRank = mockApi.getUserRank();

  return (
    <div className="min-h-screen bg-gradient-to-br from-purple-50 via-white to-blue-50 dark:from-slate-900 dark:via-slate-800 dark:to-slate-900 pb-8">
      {/* Decorative */}
      <div className="fixed top-[-15%] left-[-10%] w-[500px] h-[500px] rounded-full bg-amber-200/15 dark:bg-amber-900/10 blur-3xl pointer-events-none" />

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
          <h1 className="text-lg font-bold text-gray-900 dark:text-white">🏆 리더보드</h1>
        </div>
      </motion.div>

      <div className="container mx-auto px-4 py-6 max-w-lg">
        {/* Top 3 Podium */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.1 }}
          className="flex items-end justify-center gap-3 mb-8 px-4"
        >
          {/* 2nd Place */}
          {leaderboard[1] && (
            <motion.div
              initial={{ opacity: 0, y: 30 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.3, type: "spring" }}
              className="flex flex-col items-center"
            >
              <div className="w-14 h-14 rounded-2xl flex items-center justify-center text-2xl mb-2 shadow-sm"
                style={{ backgroundColor: leaderboard[1].avatarColor + "20" }}>
                🥈
              </div>
              <p className="text-sm font-bold text-gray-700 dark:text-gray-300 truncate max-w-[80px] text-center">{leaderboard[1].userName}</p>
              <p className="text-xs text-gray-400">{parseFloat(leaderboard[1].totalScore).toFixed(0)}점</p>
              <div className="w-20 h-16 bg-gray-200 dark:bg-gray-700 rounded-t-xl mt-2 flex items-center justify-center">
                <span className="text-lg font-bold text-gray-500 dark:text-gray-400">2</span>
              </div>
            </motion.div>
          )}

          {/* 1st Place */}
          {leaderboard[0] && (
            <motion.div
              initial={{ opacity: 0, y: 30 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.2, type: "spring" }}
              className="flex flex-col items-center"
            >
              <motion.div
                animate={{ y: [0, -5, 0] }}
                transition={{ duration: 2, repeat: Infinity, ease: "easeInOut" }}
                className="text-3xl mb-1"
              >
                👑
              </motion.div>
              <div className="w-16 h-16 rounded-2xl flex items-center justify-center text-3xl mb-2 shadow-lg ring-2 ring-yellow-300 dark:ring-yellow-600"
                style={{ backgroundColor: leaderboard[0].avatarColor + "20" }}>
                🥇
              </div>
              <p className="text-sm font-bold text-gray-900 dark:text-white truncate max-w-[90px] text-center">{leaderboard[0].userName}</p>
              <p className="text-xs text-primary font-medium">{parseFloat(leaderboard[0].totalScore).toFixed(0)}점</p>
              <div className="w-24 h-24 bg-gradient-to-t from-yellow-300 to-yellow-200 dark:from-yellow-700 dark:to-yellow-600 rounded-t-xl mt-2 flex items-center justify-center">
                <span className="text-2xl font-bold text-yellow-800 dark:text-yellow-200">1</span>
              </div>
            </motion.div>
          )}

          {/* 3rd Place */}
          {leaderboard[2] && (
            <motion.div
              initial={{ opacity: 0, y: 30 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.4, type: "spring" }}
              className="flex flex-col items-center"
            >
              <div className="w-14 h-14 rounded-2xl flex items-center justify-center text-2xl mb-2 shadow-sm"
                style={{ backgroundColor: leaderboard[2].avatarColor + "20" }}>
                🥉
              </div>
              <p className="text-sm font-bold text-gray-700 dark:text-gray-300 truncate max-w-[80px] text-center">{leaderboard[2].userName}</p>
              <p className="text-xs text-gray-400">{parseFloat(leaderboard[2].totalScore).toFixed(0)}점</p>
              <div className="w-20 h-12 bg-amber-200 dark:bg-amber-800 rounded-t-xl mt-2 flex items-center justify-center">
                <span className="text-lg font-bold text-amber-700 dark:text-amber-300">3</span>
              </div>
            </motion.div>
          )}
        </motion.div>

        {/* My Rank Highlight */}
        {myRank && (
          <motion.div
            initial={{ opacity: 0, scale: 0.95 }}
            animate={{ opacity: 1, scale: 1 }}
            transition={{ delay: 0.3 }}
            className="mb-4"
          >
            <Card className="card-base p-4 bg-gradient-to-r from-primary/10 to-purple-100/50 dark:from-primary/20 dark:to-purple-900/30 border-primary/20">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 rounded-xl bg-primary/20 flex items-center justify-center text-lg font-bold text-primary">
                  {myRank.rank}
                </div>
                <div className="flex-1">
                  <p className="text-sm font-bold text-gray-900 dark:text-white">내 순위</p>
                  <p className="text-xs text-gray-500 dark:text-gray-400">{parseFloat(myRank.totalScore).toFixed(0)}점 · {myRank.quizCount}회 플레이</p>
                </div>
                <span className="text-sm font-medium text-primary">#{myRank.rank}</span>
              </div>
            </Card>
          </motion.div>
        )}

        {/* Full Rankings */}
        <motion.div
          variants={container}
          initial="hidden"
          animate="show"
          className="space-y-2"
        >
          {leaderboard.slice(3).map((entry) => (
            <motion.div key={entry.userId} variants={item}>
              <Card className={`p-3 rounded-2xl border backdrop-blur-sm transition-all hover:shadow-md ${getRankBg(entry.rank)} ${entry.userId === 999 ? 'ring-2 ring-primary/30' : ''}`}>
                <div className="flex items-center gap-3">
                  {getRankIcon(entry.rank)}
                  <div className="w-9 h-9 rounded-xl flex items-center justify-center text-lg flex-shrink-0"
                    style={{ backgroundColor: entry.avatarColor + "20" }}>
                    {entry.userName.charAt(0)}
                  </div>
                  <div className="flex-1 min-w-0">
                    <p className={`text-sm font-bold truncate ${entry.userId === 999 ? 'text-primary' : 'text-gray-800 dark:text-gray-200'}`}>
                      {entry.userName} {entry.userId === 999 && '(나)'}
                    </p>
                    <p className="text-xs text-gray-400 dark:text-gray-500">{entry.quizCount}회 플레이</p>
                  </div>
                  <p className="text-sm font-bold text-gray-700 dark:text-gray-300">
                    {parseFloat(entry.totalScore).toFixed(0)}점
                  </p>
                </div>
              </Card>
            </motion.div>
          ))}
        </motion.div>
      </div>
    </div>
  );
}
