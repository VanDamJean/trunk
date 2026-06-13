import { Card } from "@/components/ui/card";
import { cn } from "@/lib/utils";
import { ReactNode } from "react";
import { motion } from "framer-motion";

interface MeditationCardProps {
  title: string;
  description?: string;
  duration?: string;
  icon?: ReactNode;
  color?: "purple" | "orange" | "green" | "blue" | "pink";
  onClick?: () => void;
  className?: string;
}

export function MeditationCard({
  title,
  description,
  duration,
  icon,
  color = "purple",
  onClick,
  className,
}: MeditationCardProps) {
  const colorMap = {
    purple: "bg-gradient-to-br from-primary/20 to-accent/20 border-primary/30 text-primary-foreground",
    orange: "bg-gradient-to-br from-orange-100 to-orange-50 border-orange-200 text-orange-900",
    green: "bg-gradient-to-br from-green-100 to-green-50 border-green-200 text-green-900",
    blue: "bg-gradient-to-br from-blue-100 to-blue-50 border-blue-200 text-blue-900",
    pink: "bg-gradient-to-br from-pink-100 to-pink-50 border-pink-200 text-pink-900",
  };

  return (
    <motion.div
      whileHover={{ y: -5, scale: 1.02 }}
      whileTap={{ scale: 0.95 }}
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.3 }}
    >
      <Card
        className={cn(
          "p-6 cursor-pointer rounded-3xl border-2 transition-shadow duration-300",
          "hover:shadow-xl backdrop-blur-md",
          colorMap[color],
          className
        )}
        onClick={onClick}
      >
        <div className="flex flex-col h-full">
          {icon && <div className="mb-4 text-4xl drop-shadow-sm">{icon}</div>}
          <h3 className="font-bold text-lg text-foreground mb-2 leading-tight">{title}</h3>
          {description && (
            <p className="text-sm text-foreground/70 mb-4 flex-grow">{description}</p>
          )}
          {duration && (
            <div className="mt-auto flex items-center justify-between">
              <p className="text-xs font-bold uppercase tracking-wider text-foreground/60">
                {duration}
              </p>
              <div className="w-8 h-8 rounded-full bg-white/30 flex items-center justify-center">
                <span className="text-xs">▶</span>
              </div>
            </div>
          )}
        </div>
      </Card>
    </motion.div>
  );
}
