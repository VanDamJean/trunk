import { useState } from "react";
import { Menu, Settings, Home as HomeIcon, User, Search, Clock } from "lucide-react";
import NewsCard from "@/components/NewsCard";
import DailyTaskCard from "@/components/DailyTaskCard";
import ProgressSection from "@/components/ProgressSection";
import ListItem from "@/components/ListItem";
import BottomNav from "@/components/BottomNav";
import ProfilePage from "./Profile";

export default function Home() {
  const [currentPage, setCurrentPage] = useState<string>("home");

  if (currentPage === "profile") {
    return <ProfilePage onNavigate={setCurrentPage} />;
  }

  return (
    <div className="min-h-screen bg-white flex flex-col lg:flex-row">
      {/* Left Column - Home Page */}
      <div className="flex-1 flex flex-col w-full lg:max-w-md lg:border-r lg:border-gray-200">
        {/* Header */}
        <div className="bg-white border-b border-gray-200 px-4 py-3 flex items-center justify-between sticky top-0 z-10">
          <button className="p-2 hover:bg-gray-100 rounded-lg transition">
            <Menu size={24} className="text-gray-800" />
          </button>
          <h1 className="text-lg font-semibold text-gray-800">Home</h1>
          <button className="p-2 hover:bg-gray-100 rounded-lg transition">
            <Settings size={24} className="text-gray-800" />
          </button>
        </div>

        {/* Status Bar */}
        <div className="bg-white px-4 py-2 flex items-center justify-between text-xs text-gray-600 border-b border-gray-100">
          <span className="font-medium">16:05</span>
          <div className="flex gap-1">
            <span>📶</span>
            <span>🔋</span>
          </div>
        </div>

        {/* Main Content */}
        <div className="flex-1 overflow-y-auto pb-24 md:pb-8">
          <div className="px-4 py-6 space-y-6">
            {/* News Section */}
            <div>
              <h2 className="text-gray-500 text-sm font-medium mb-3">News</h2>
              <div className="flex gap-3 overflow-x-auto pb-2">
                <NewsCard
                  title="Short news title will be here"
                  gradient="from-purple-600 to-pink-500"
                />
                <NewsCard
                  title="Short news title will be here"
                  gradient="from-teal-400 to-cyan-500"
                />
                <NewsCard
                  title="Short news title will be here"
                  gradient="from-purple-500 to-indigo-600"
                />
              </div>
            </div>

            {/* Daily Tasks Section */}
            <div>
              <h2 className="text-gray-500 text-sm font-medium mb-3">Daily Tasks:</h2>
              <div className="grid grid-cols-3 gap-3">
                <DailyTaskCard label="Daily" count={3} color="#FF6B6B" />
                <DailyTaskCard label="Daily deep" count={1} color="#FF6B6B" />
                <DailyTaskCard label="Daily mantra" count={2} color="#51CF66" />
              </div>
            </div>

            {/* Progress Section */}
            <ProgressSection progress={60} />

            {/* List Items */}
            <div className="space-y-3">
              <ListItem
                icon={<Clock size={20} className="text-gray-400" />}
                title="How was your day?"
                description="Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut sed odio in urna ultrices."
              />
              <ListItem
                icon={<Clock size={20} className="text-red-500" />}
                title="Current Transit 3rd House"
                description="This is demonstrate siblings, hobbies, efforts, confidence, friends and short tr..."
              />
            </div>
          </div>
        </div>

        {/* Bottom Navigation - Mobile Only */}
        <BottomNav currentPage={currentPage} onNavigate={setCurrentPage} />
      </div>

      {/* Right Column - Profile Page (Desktop Only) */}
      <div className="hidden lg:flex flex-1 flex-col w-full lg:max-w-md">
        <ProfilePage onNavigate={(page: string) => setCurrentPage(page)} />
      </div>
    </div>
  );
}
