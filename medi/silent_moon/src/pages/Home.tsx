import { MeditationCard } from "@/components/MeditationCard";
import { BottomNav } from "@/components/BottomNav";
import { SilentMoonButton } from "@/components/SilentMoonButton";
import { useState, useMemo } from "react";
import { motion, AnimatePresence } from "framer-motion";
import { Search, Bell, Home as HomeIcon, Moon, User } from "lucide-react";

import Onboarding from "./Onboarding";
import TopicSelection from "./TopicSelection";
import ReminderSetup from "./ReminderSetup";
import MeditationPlayer from "./MeditationPlayer";
import SleepSection from "./SleepSection";

type Page = "home" | "onboarding" | "topics" | "reminders" | "player" | "sleep";

const ALL_SESSIONS = [
  { id: 1, title: "Reduce Anxiety", description: "Calm your mind and body", duration: "10 MIN", color: "orange" as const, icon: "😌", category: "morning", audioUrl: "./waves.wav" },
  { id: 2, title: "Improve Happiness", description: "Boost your mood", duration: "15 MIN", color: "blue" as const, icon: "😊", category: "afternoon", audioUrl: "./bowl.wav" },
  { id: 3, title: "Personal Growth", description: "Develop yourself", duration: "20 MIN", color: "green" as const, icon: "🌱", category: "morning", audioUrl: "./bowl.wav" },
  { id: 4, title: "Better Sleep", description: "Rest well tonight", duration: "30 MIN", color: "purple" as const, icon: "😴", category: "evening", audioUrl: "./sleep.wav" },
  { id: 5, title: "Deep Focus", description: "Get into the zone", duration: "25 MIN", color: "pink" as const, icon: "🎯", category: "afternoon", audioUrl: "./waves.wav" },
];

export default function Home() {
  const [currentPage, setCurrentPage] = useState<Page>("onboarding");
  const [selectedSession, setSelectedSession] = useState<any>(null);
  const [searchQuery, setSearchQuery] = useState("");

  const filteredSessions = useMemo(() => {
    if (!searchQuery) return ALL_SESSIONS.slice(0, 4);
    return ALL_SESSIONS.filter(s => 
      s.title.toLowerCase().includes(searchQuery.toLowerCase()) || 
      s.description.toLowerCase().includes(searchQuery.toLowerCase())
    );
  }, [searchQuery]);

  const navItems = [
    { id: "home", label: "Home", icon: <HomeIcon /> },
    { id: "sleep", label: "Sleep", icon: <Moon /> },
    { id: "profile", label: "Profile", icon: <User /> },
  ];

  const handleSessionClick = (session: any) => {
    setSelectedSession(session);
    setCurrentPage("player");
  };

  const handleNavigate = (id: string) => {
    if (id === "home") setCurrentPage("home");
    else if (id === "sleep") setCurrentPage("sleep");
  };

  if (currentPage === "onboarding") return <Onboarding onComplete={() => setCurrentPage("home")} />;
  if (currentPage === "topics") return <TopicSelection onBack={() => setCurrentPage("home")} />;
  if (currentPage === "reminders") return <ReminderSetup onBack={() => setCurrentPage("home")} />;
  if (currentPage === "player") return <MeditationPlayer session={selectedSession} onBack={() => setCurrentPage("home")} />;
  if (currentPage === "sleep") return <SleepSection onNavigate={handleNavigate} onSessionSelect={handleSessionClick} />;

  return (
    <div className="min-h-screen bg-background pb-28">
      {/* Header */}
      <div className="sticky top-0 z-40 bg-background/80 backdrop-blur-xl border-b border-border/50 p-6 pt-10">
        <div className="max-w-md mx-auto">
          <div className="flex items-center justify-between mb-4">
            <h1 className="text-3xl font-extrabold text-foreground tracking-tight">Silent Moon</h1>
            <button className="w-10 h-10 rounded-full bg-secondary/10 flex items-center justify-center text-foreground hover:bg-secondary/20 transition-colors">
              <Bell className="w-5 h-5" />
            </button>
          </div>
          <p className="text-lg font-medium text-muted-foreground mb-6">
            Good Morning, Afsar
          </p>

          {/* Search Bar */}
          <div className="relative">
            <Search className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-muted-foreground" />
            <input 
              type="text" 
              placeholder="Search meditations..." 
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-full bg-card border-2 border-border/50 rounded-2xl py-4 pl-12 pr-4 outline-none focus:border-primary/50 focus:ring-4 focus:ring-primary/10 transition-all text-foreground font-medium"
            />
          </div>
        </div>
      </div>

      {/* Content */}
      <div className="max-w-md mx-auto p-6 space-y-10">
        <AnimatePresence mode="popLayout">
          {/* Featured Section */}
          <motion.div 
            key="featured"
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, scale: 0.95 }}
            className="space-y-4"
          >
            <h2 className="text-2xl font-bold text-foreground">
              {searchQuery ? "Search Results" : "What brings you to Silent Moon?"}
            </h2>
            
            {filteredSessions.length === 0 ? (
              <div className="py-10 text-center text-muted-foreground bg-card rounded-2xl border border-dashed border-border">
                No sessions found for "{searchQuery}"
              </div>
            ) : (
              <div className="grid grid-cols-2 gap-4">
                {filteredSessions.map((session) => (
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
            )}
          </motion.div>

          {/* Recommended Section (Hide when searching) */}
          {!searchQuery && (
            <motion.div 
              key="recommended"
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              className="space-y-4"
            >
              <h2 className="text-2xl font-bold text-foreground">
                Recommended for you
              </h2>
              <div className="space-y-3">
                {[
                  { title: "Focus Attention", duration: "3-10 MIN", icon: "🎯" },
                  { title: "Happiness", duration: "5-15 MIN", icon: "😊" },
                  { title: "Body Scan", duration: "10-20 MIN", icon: "🧘" },
                ].map((item, idx) => (
                  <motion.div
                    key={idx}
                    whileHover={{ scale: 1.02, x: 5 }}
                    className="bg-card rounded-2xl p-4 border border-border shadow-sm hover:shadow-md transition-all cursor-pointer flex items-center gap-4"
                  >
                    <div className="text-4xl bg-primary/10 p-2 rounded-xl">{item.icon}</div>
                    <div className="flex-1">
                      <h3 className="font-bold text-foreground">
                        {item.title}
                      </h3>
                      <p className="text-xs font-bold text-primary mt-1 tracking-wider">
                        {item.duration}
                      </p>
                    </div>
                    <div className="w-8 h-8 rounded-full border-2 border-primary/20 flex items-center justify-center text-primary">
                      <span className="ml-1 text-xs">▶</span>
                    </div>
                  </motion.div>
                ))}
              </div>
            </motion.div>
          )}

          {/* Action Buttons */}
          <motion.div 
            key="actions"
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            className="space-y-3 pt-4"
          >
            <SilentMoonButton onClick={() => setCurrentPage("topics")} fullWidth variant="secondary" size="lg">
              Choose Topic
            </SilentMoonButton>
            <SilentMoonButton onClick={() => setCurrentPage("reminders")} fullWidth variant="outline" size="lg">
              Set Reminders
            </SilentMoonButton>
          </motion.div>
        </AnimatePresence>
      </div>

      <BottomNav
        items={navItems}
        activeId="home"
        onNavigate={handleNavigate}
      />
    </div>
  );
}
