import { useState } from "react";
import { ChevronRight, Sparkles, Brain, Trophy } from "lucide-react";
import { Button } from "@/components/ui/button";
import { useLocation } from "wouter";
import { motion, AnimatePresence } from "framer-motion";

interface OnboardingSlide {
  id: number;
  title: string;
  description: string;
  icon: React.ReactNode;
  color: string;
}

const slides: OnboardingSlide[] = [
  {
    id: 1,
    title: "Queezy에 오신 것을\n환영합니다!",
    description: "재미있는 퀴즈로 지식을 테스트하고\n친구들과 경쟁하세요.",
    icon: <Sparkles className="w-16 h-16" />,
    color: "from-violet-500 to-purple-600",
  },
  {
    id: 2,
    title: "다양한 카테고리",
    description: "수학, 과학, 역사, 프로그래밍 등\n다양한 주제의 퀴즈를 즐겨보세요.",
    icon: <Brain className="w-16 h-16" />,
    color: "from-blue-500 to-purple-600",
  },
  {
    id: 3,
    title: "랭킹 & 배지",
    description: "점수를 획득하고 글로벌 랭킹에서\n당신의 순위를 확인하세요.",
    icon: <Trophy className="w-16 h-16" />,
    color: "from-amber-500 to-orange-600",
  },
];

const slideVariants = {
  enter: (direction: number) => ({
    x: direction > 0 ? 200 : -200,
    opacity: 0,
    scale: 0.9,
  }),
  center: {
    zIndex: 1,
    x: 0,
    opacity: 1,
    scale: 1,
  },
  exit: (direction: number) => ({
    zIndex: 0,
    x: direction < 0 ? 200 : -200,
    opacity: 0,
    scale: 0.9,
  }),
};

export default function Onboarding() {
  const [currentSlide, setCurrentSlide] = useState(0);
  const [direction, setDirection] = useState(0);
  const [, setLocation] = useLocation();

  const handleNext = () => {
    if (currentSlide < slides.length - 1) {
      setDirection(1);
      setCurrentSlide(currentSlide + 1);
    }
  };

  const handlePrev = () => {
    if (currentSlide > 0) {
      setDirection(-1);
      setCurrentSlide(currentSlide - 1);
    }
  };

  const handleStart = () => {
    setLocation("/home");
  };

  const slide = slides[currentSlide];

  return (
    <div className="min-h-screen bg-gradient-to-br from-purple-50 via-white to-blue-50 dark:from-slate-900 dark:via-slate-800 dark:to-slate-900 flex items-center justify-center p-4">
      {/* Decorative blobs */}
      <div className="fixed top-[-10%] right-[-10%] w-[400px] h-[400px] rounded-full bg-purple-200/30 dark:bg-purple-900/20 blur-3xl" />
      <div className="fixed bottom-[-10%] left-[-10%] w-[400px] h-[400px] rounded-full bg-blue-200/30 dark:bg-blue-900/20 blur-3xl" />

      <div className="w-full max-w-md relative z-10">
        {/* Logo */}
        <motion.div
          initial={{ opacity: 0, y: -20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6, ease: "easeOut" }}
          className="text-center mb-8"
        >
          <h2 className="text-4xl font-extrabold text-gradient tracking-tight">Queezy</h2>
        </motion.div>

        {/* Slide Container */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, delay: 0.15 }}
          className="card-base p-8 mb-8 overflow-hidden backdrop-blur-sm bg-white/80 dark:bg-slate-800/80"
        >
          <AnimatePresence mode="wait" custom={direction}>
            <motion.div
              key={currentSlide}
              custom={direction}
              variants={slideVariants}
              initial="enter"
              animate="center"
              exit="exit"
              transition={{
                x: { type: "spring", stiffness: 300, damping: 30 },
                opacity: { duration: 0.2 },
                scale: { duration: 0.2 },
              }}
            >
              {/* Icon */}
              <div className="flex justify-center mb-6">
                <motion.div
                  className={`p-5 rounded-3xl bg-gradient-to-br ${slide.color} text-white shadow-lg`}
                  whileHover={{ scale: 1.05, rotate: 2 }}
                  whileTap={{ scale: 0.95 }}
                >
                  {slide.icon}
                </motion.div>
              </div>

              {/* Title */}
              <h1 className="text-2xl font-bold text-center mb-3 text-gray-900 dark:text-white whitespace-pre-line leading-tight">
                {slide.title}
              </h1>

              {/* Description */}
              <p className="text-base text-center text-gray-500 dark:text-gray-400 whitespace-pre-line leading-relaxed">
                {slide.description}
              </p>
            </motion.div>
          </AnimatePresence>

          {/* Slide Indicators */}
          <div className="flex justify-center gap-2 mt-8">
            {slides.map((_, index) => (
              <motion.div
                key={index}
                className={`h-2 rounded-full transition-colors duration-300 ${
                  index === currentSlide
                    ? "bg-primary"
                    : "bg-gray-200 dark:bg-gray-600"
                }`}
                animate={{
                  width: index === currentSlide ? 32 : 8,
                }}
                transition={{ type: "spring", stiffness: 300, damping: 25 }}
              />
            ))}
          </div>
        </motion.div>

        {/* Navigation Buttons */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, delay: 0.3 }}
          className="flex gap-3 mb-4"
        >
          <Button
            onClick={handlePrev}
            disabled={currentSlide === 0}
            variant="outline"
            className="flex-1 rounded-2xl h-12 text-base disabled:opacity-30 border-gray-200 dark:border-gray-600"
          >
            이전
          </Button>

          {currentSlide < slides.length - 1 ? (
            <Button
              onClick={handleNext}
              className="flex-1 rounded-2xl h-12 text-base bg-primary hover:bg-primary/90 text-white shadow-lg shadow-purple-300/50 dark:shadow-purple-900/50"
            >
              다음
              <ChevronRight className="w-4 h-4 ml-1" />
            </Button>
          ) : (
            <Button
              onClick={handleStart}
              className="flex-1 rounded-2xl h-12 text-base bg-primary hover:bg-primary/90 text-white shadow-lg shadow-purple-300/50 dark:shadow-purple-900/50 animate-pulse-glow"
            >
              시작하기
              <ChevronRight className="w-4 h-4 ml-1" />
            </Button>
          )}
        </motion.div>

        {/* Skip Button */}
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ delay: 0.5 }}
          className="text-center"
        >
          {currentSlide < slides.length - 1 && (
            <button
              onClick={handleStart}
              className="text-sm text-gray-400 hover:text-gray-600 dark:text-gray-500 dark:hover:text-gray-300 transition-colors"
            >
              건너뛰기
            </button>
          )}
        </motion.div>
      </div>
    </div>
  );
}
