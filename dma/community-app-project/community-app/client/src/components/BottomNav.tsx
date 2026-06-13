import type { LucideIcon } from "lucide-react";
import { Home, User, Star, Search, Settings } from "lucide-react";

export type NavTabId = "home" | "profile" | "center" | "search" | "settings";

interface BottomNavProps {
  currentPage: NavTabId;
  onNavigate: (page: NavTabId) => void;
}

const navItems: { id: NavTabId; icon: LucideIcon; label: string }[] = [
  { id: "home", icon: Home, label: "Home" },
  { id: "profile", icon: User, label: "Profile" },
  { id: "center", icon: Star, label: "Center" },
  { id: "search", icon: Search, label: "Search" },
  { id: "settings", icon: Settings, label: "Settings" },
];

export default function BottomNav({ currentPage, onNavigate }: BottomNavProps) {

  return (
    <div className="fixed bottom-0 left-0 right-0 z-20 lg:hidden bg-white border-t border-gray-200 px-2 py-2 flex justify-around dark:bg-gray-950 dark:border-gray-800">
      {navItems.map((item) => {
        const Icon = item.icon;
        const isActive = currentPage === item.id;
        return (
          <button
            key={item.id}
            onClick={() => onNavigate(item.id)}
            className={`flex flex-col items-center gap-0.5 p-2 rounded-lg transition ${
              isActive
                ? "text-purple-600 dark:text-purple-400"
                : "text-gray-600 hover:text-gray-800 dark:text-gray-400 dark:hover:text-gray-200"
            }`}
          >
            <Icon size={24} />
            <span className="text-xs font-medium">{item.label}</span>
          </button>
        );
      })}
    </div>
  );
}
