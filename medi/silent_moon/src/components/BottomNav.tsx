import { cn } from "@/lib/utils";
import { ReactNode } from "react";
import { motion } from "framer-motion";

interface NavItem {
  id: string;
  label: string;
  icon: ReactNode;
}

interface BottomNavProps {
  items: NavItem[];
  activeId: string;
  onNavigate: (id: string) => void;
}

export function BottomNav({ items, activeId, onNavigate }: BottomNavProps) {
  return (
    <nav className="fixed bottom-0 left-0 right-0 z-50">
      <div className="absolute inset-0 bg-background/80 backdrop-blur-xl border-t border-border shadow-[0_-10px_40px_-15px_rgba(0,0,0,0.1)]"></div>
      <div className="relative flex justify-around items-center h-20 max-w-md mx-auto px-4 pb-safe">
        {items.map((item) => {
          const isActive = activeId === item.id;
          return (
            <button
              key={item.id}
              onClick={() => onNavigate(item.id)}
              className="relative flex flex-col items-center justify-center w-16 h-full gap-1 group"
            >
              {isActive && (
                <motion.div
                  layoutId="bottomNavIndicator"
                  className="absolute top-0 w-8 h-1 bg-primary rounded-b-full"
                  initial={false}
                  transition={{ type: "spring", stiffness: 500, damping: 30 }}
                />
              )}
              <motion.div
                animate={{
                  y: isActive ? -4 : 0,
                  scale: isActive ? 1.1 : 1,
                  color: isActive ? "var(--color-primary)" : "var(--color-muted-foreground)",
                }}
                className="text-2xl transition-colors"
              >
                {item.icon}
              </motion.div>
              <motion.span
                animate={{
                  opacity: isActive ? 1 : 0.7,
                  color: isActive ? "var(--color-primary)" : "var(--color-muted-foreground)",
                }}
                className="text-[10px] font-semibold"
              >
                {item.label}
              </motion.span>
            </button>
          );
        })}
      </div>
    </nav>
  );
}
