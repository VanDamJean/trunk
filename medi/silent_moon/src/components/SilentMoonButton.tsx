import { Button } from "@/components/ui/button";
import { cn } from "@/lib/utils";
import { ReactNode } from "react";
import { motion } from "framer-motion";

interface SilentMoonButtonProps {
  children: ReactNode;
  variant?: "primary" | "secondary" | "outline" | "ghost";
  size?: "sm" | "md" | "lg";
  fullWidth?: boolean;
  className?: string;
  [key: string]: any;
}

export function SilentMoonButton({
  children,
  variant = "primary",
  size = "md",
  fullWidth = false,
  className,
  ...props
}: SilentMoonButtonProps) {
  const baseStyles = "font-medium rounded-full transition-all duration-300";

  const variantStyles = {
    primary:
      "bg-gradient-to-r from-primary to-accent text-primary-foreground shadow-md hover:shadow-xl hover:shadow-primary/30",
    secondary:
      "bg-secondary text-secondary-foreground hover:bg-secondary/90 shadow-md",
    outline:
      "border-2 border-primary text-primary hover:bg-primary/5",
    ghost: "text-primary hover:bg-primary/10",
  };

  const sizeStyles = {
    sm: "px-4 py-2 text-sm",
    md: "px-6 py-3 text-base",
    lg: "px-8 py-4 text-lg",
  };

  return (
    <motion.div
      whileHover={{ scale: 1.02 }}
      whileTap={{ scale: 0.98 }}
      className={cn(fullWidth && "w-full", className)}
    >
      <Button
        className={cn(
          baseStyles,
          variantStyles[variant],
          sizeStyles[size],
          fullWidth && "w-full",
          "h-auto"
        )}
        {...props}
      >
        {children}
      </Button>
    </motion.div>
  );
}
