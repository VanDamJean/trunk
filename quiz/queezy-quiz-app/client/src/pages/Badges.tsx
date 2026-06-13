import { MOCK_BADGES } from "@/lib/mockData";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { ArrowLeft, Lock } from "lucide-react";
import { useLocation } from "wouter";
import { motion } from "framer-motion";

const container = {
  hidden: { opacity: 0 },
  show: { opacity: 1, transition: { staggerChildren: 0.08, delayChildren: 0.15 } },
};
const item = {
  hidden: { opacity: 0, scale: 0.8 },
  show: { opacity: 1, scale: 1 },
};

export default function Badges() {
  const [, setLocation] = useLocation();
  const badges = MOCK_BADGES;
  const unlockedCount = badges.filter((b) => b.unlocked).length;

  return (
    <div className="min-h-screen bg-gradient-to-br from-purple-50 via-white to-blue-50 dark:from-slate-900 dark:via-slate-800 dark:to-slate-900 pb-8">
      {/* Decorative */}
      <div className="fixed top-[-10%] left-[-10%] w-[400px] h-[400px] rounded-full bg-amber-200/15 dark:bg-amber-900/10 blur-3xl pointer-events-none" />

      {/* Header */}
      <motion.div
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
        className="bg-white/70 dark:bg-slate-800/70 backdrop-blur-xl border-b border-gray-100 dark:border-slate-700/50 sticky top-0 z-20"
      >
        <div className="container mx-auto px-4 py-4 flex items-center gap-3">
          <Button onClick={() => setLocation("/profile")} variant="ghost" size="icon" className="rounded-full">
            <ArrowLeft className="w-5 h-5" />
          </Button>
          <h1 className="text-lg font-bold text-gray-900 dark:text-white">🏅 배지 컬렉션</h1>
        </div>
      </motion.div>

      <div className="container mx-auto px-4 py-6 max-w-lg">
        {/* Progress */}
        <motion.div
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.1 }}
          className="mb-6"
        >
          <Card className="card-base p-5 bg-gradient-to-r from-primary/5 to-purple-100/50 dark:from-primary/10 dark:to-purple-900/20 border-primary/10">
            <div className="flex items-center justify-between mb-3">
              <p className="text-sm font-medium text-gray-700 dark:text-gray-300">수집 진행도</p>
              <p className="text-sm font-bold text-primary">{unlockedCount}/{badges.length}</p>
            </div>
            <div className="w-full bg-gray-100 dark:bg-gray-800 rounded-full h-2.5 overflow-hidden">
              <motion.div
                className="bg-gradient-to-r from-primary to-purple-400 h-2.5 rounded-full"
                initial={{ width: 0 }}
                animate={{ width: `${(unlockedCount / badges.length) * 100}%` }}
                transition={{ duration: 1, ease: "easeOut", delay: 0.3 }}
              />
            </div>
          </Card>
        </motion.div>

        {/* Badges Grid */}
        <motion.div
          variants={container}
          initial="hidden"
          animate="show"
          className="grid grid-cols-2 gap-3"
        >
          {badges.map((badge) => (
            <motion.div key={badge.id} variants={item}>
              <Card
                className={`card-base p-5 text-center transition-all ${
                  badge.unlocked
                    ? "bg-white/70 dark:bg-slate-800/70 backdrop-blur-sm hover:shadow-lg"
                    : "bg-gray-50/70 dark:bg-slate-800/40 opacity-60"
                }`}
              >
                {/* Badge Icon */}
                <motion.div
                  className={`w-16 h-16 rounded-2xl mx-auto mb-3 flex items-center justify-center text-3xl ${
                    badge.unlocked
                      ? "bg-gradient-to-br from-primary/10 to-purple-100 dark:from-primary/20 dark:to-purple-900/30 shadow-sm"
                      : "bg-gray-100 dark:bg-gray-800"
                  }`}
                  whileHover={badge.unlocked ? { scale: 1.1, rotate: 5 } : {}}
                >
                  {badge.unlocked ? (
                    badge.icon
                  ) : (
                    <Lock className="w-6 h-6 text-gray-300 dark:text-gray-600" />
                  )}
                </motion.div>

                {/* Badge Name */}
                <h3 className={`text-sm font-bold mb-1 ${
                  badge.unlocked ? "text-gray-900 dark:text-white" : "text-gray-400 dark:text-gray-600"
                }`}>
                  {badge.name}
                </h3>

                {/* Badge Description */}
                <p className="text-[11px] text-gray-500 dark:text-gray-400 leading-relaxed mb-2">
                  {badge.description}
                </p>

                {/* Status */}
                {badge.unlocked ? (
                  <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded-full bg-green-100 dark:bg-green-900/30 text-green-600 dark:text-green-400 text-[10px] font-medium">
                    ✓ 획득 완료
                  </span>
                ) : (
                  <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded-full bg-gray-100 dark:bg-gray-800 text-gray-400 dark:text-gray-500 text-[10px] font-medium">
                    <Lock className="w-2.5 h-2.5" /> 미획득
                  </span>
                )}
              </Card>
            </motion.div>
          ))}
        </motion.div>
      </div>
    </div>
  );
}
