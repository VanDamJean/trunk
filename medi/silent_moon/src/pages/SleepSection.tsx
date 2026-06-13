import { MeditationCard } from "@/components/MeditationCard";
import { BottomNav } from "@/components/BottomNav";
import { Home as HomeIcon, Moon, User } from "lucide-react";
import { motion } from "framer-motion";
import { useMemo } from "react";

interface SleepSectionProps {
  onNavigate: (page: string) => void;
  onSessionSelect: (session: any) => void;
}

export default function SleepSection({ onNavigate, onSessionSelect }: SleepSectionProps) {
  const stars = useMemo(() =>
    Array.from({ length: 20 }, (_, i) => ({
      id: i,
      width: Math.random() * 3 + 1,
      top: Math.random() * 100,
      left: Math.random() * 100,
      duration: Math.random() * 3 + 2,
    })),
    []
  );

  const sleepContent = [
    { id: 1, title: "Night Island", description: "A peaceful journey to sleep", duration: "45 MIN", icon: "🏝️", audioUrl: "./waves.wav", category: "evening" },
    { id: 2, title: "Sweet Dreams", description: "Drift into restful sleep", duration: "30 MIN", icon: "☁️", audioUrl: "./sleep.wav", category: "evening" },
    { id: 3, title: "Moonlight Stories", description: "Bedtime tales for adults", duration: "20 MIN", icon: "📖", audioUrl: "./bowl.wav", category: "evening" },
    { id: 4, title: "Sleep Music", description: "Ambient sounds for rest", duration: "60 MIN", icon: "🎵", audioUrl: "./sleep.wav", category: "evening" },
  ];

  const navItems = [
    { id: "home", label: "Home", icon: <HomeIcon /> },
    { id: "sleep", label: "Sleep", icon: <Moon /> },
    { id: "profile", label: "Profile", icon: <User /> },
  ];

  const container = {
    hidden: { opacity: 0 },
    show: {
      opacity: 1,
      transition: {
        staggerChildren: 0.1
      }
    }
  };

  const item = {
    hidden: { opacity: 0, y: 20 },
    show: { opacity: 1, y: 0 }
  };

  return (
    <div className="min-h-screen dark bg-slate-950 text-slate-50 pb-28 relative overflow-hidden">
      {/* Starry Background */}
      <div className="absolute inset-0 overflow-hidden pointer-events-none opacity-50">
        {stars.map((star) => (
          <motion.div
            key={star.id}
            animate={{ opacity: [0.2, 0.8, 0.2] }}
            transition={{ duration: star.duration, repeat: Infinity }}
            className="absolute bg-white rounded-full"
            style={{
              width: star.width + "px",
              height: star.width + "px",
              top: star.top + "%",
              left: star.left + "%",
            }}
          />
        ))}
      </div>

      {/* Header */}
      <div className="sticky top-0 z-40 bg-slate-950/80 backdrop-blur-xl border-b border-white/10 p-6">
        <div className="max-w-md mx-auto">
          <div className="flex items-center justify-between mb-2">
            <h1 className="text-3xl font-extrabold text-white tracking-tight">Sleep Music</h1>
          </div>
          <p className="text-sm text-indigo-200">
            Drift into peaceful sleep
          </p>
        </div>
      </div>

      {/* Content */}
      <div className="relative z-10 max-w-md mx-auto p-6 space-y-10">
        
        {/* Featured Sleep Content */}
        <motion.div 
          variants={container}
          initial="hidden"
          animate="show"
          className="space-y-4"
        >
          <h2 className="text-2xl font-bold text-white">
            Sleep Stories
          </h2>
          <div className="grid grid-cols-2 gap-4">
            {sleepContent.slice(0, 2).map((content) => (
              <motion.div key={content.id} variants={item}>
                <MeditationCard
                  title={content.title}
                  description={content.description}
                  duration={content.duration}
                  icon={content.icon}
                  color="blue"
                  onClick={() => onSessionSelect(content)}
                  className="bg-indigo-900/40 border-indigo-500/30 text-white hover:border-indigo-400"
                />
              </motion.div>
            ))}
          </div>
        </motion.div>

        {/* Sleep Music Library */}
        <motion.div 
          variants={container}
          initial="hidden"
          animate="show"
          className="space-y-4"
        >
          <h2 className="text-2xl font-bold text-white">
            Sleep Music Library
          </h2>
          <div className="space-y-4">
            {sleepContent.slice(2).map((content) => (
              <motion.div
                key={content.id}
                variants={item}
                onClick={() => onSessionSelect(content)}
                whileHover={{ scale: 1.02, x: 5 }}
                className="bg-white/5 backdrop-blur-lg rounded-2xl p-5 border border-white/10 hover:border-indigo-500/50 transition-colors cursor-pointer flex items-center gap-5"
              >
                <div className="text-4xl bg-indigo-500/20 p-3 rounded-2xl">{content.icon}</div>
                <div className="flex-1">
                  <h3 className="font-bold text-lg text-white">
                    {content.title}
                  </h3>
                  <p className="text-sm text-indigo-200/70">
                    {content.description}
                  </p>
                  <p className="text-xs font-bold text-indigo-400 mt-2 uppercase tracking-wider">
                    {content.duration}
                  </p>
                </div>
                <div className="w-10 h-10 rounded-full border border-indigo-500/30 flex items-center justify-center text-indigo-400">
                  <span className="ml-1">▶</span>
                </div>
              </motion.div>
            ))}
          </div>
        </motion.div>

        {/* Sleep Tips */}
        <motion.div 
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.5 }}
          className="bg-indigo-900/20 rounded-3xl p-8 border border-indigo-500/20 space-y-4 backdrop-blur-md"
        >
          <h3 className="font-bold text-lg text-white flex items-center gap-2">
            💡 Sleep Tips
          </h3>
          <ul className="text-sm text-indigo-100/80 space-y-3 font-medium">
            <li className="flex items-center gap-2"><span>•</span> Keep your bedroom cool and dark</li>
            <li className="flex items-center gap-2"><span>•</span> Avoid screens 30 minutes before bed</li>
            <li className="flex items-center gap-2"><span>•</span> Try meditation 10 minutes before sleep</li>
            <li className="flex items-center gap-2"><span>•</span> Maintain a consistent sleep schedule</li>
          </ul>
        </motion.div>
      </div>

      <BottomNav
        items={navItems}
        activeId="sleep"
        onNavigate={onNavigate}
      />
    </div>
  );
}
