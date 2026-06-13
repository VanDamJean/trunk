import { SilentMoonButton } from "@/components/SilentMoonButton";
import { useState } from "react";
import { motion, AnimatePresence } from "framer-motion";

interface OnboardingProps {
  onComplete: () => void;
}

export default function Onboarding({ onComplete }: OnboardingProps) {
  const [step, setStep] = useState(1);

  const steps = [
    {
      title: "Hi Afsar, Welcome to Silent Moon",
      subtitle: "Explore the power of meditation",
      icon: "🧘",
      description: "Your personal meditation companion",
    },
    {
      title: "What brings you to Silent Moon?",
      subtitle: "Choose your meditation goal",
      icon: "🎯",
      description: "We'll personalize your experience",
    },
    {
      title: "Ready to begin?",
      subtitle: "Start your journey to inner peace",
      icon: "✨",
      description: "Your first session awaits",
    },
  ];

  const currentStep = steps[step - 1];

  const handleNext = () => {
    if (step < steps.length) {
      setStep(step + 1);
    } else {
      onComplete();
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-background via-background to-accent/5 flex flex-col items-center justify-center p-6 overflow-hidden">
      {/* Decorative Background */}
      <div className="absolute inset-0 overflow-hidden pointer-events-none">
        <motion.div
          animate={{
            scale: [1, 1.2, 1],
            rotate: [0, 90, 0],
            opacity: [0.5, 0.3, 0.5],
          }}
          transition={{ duration: 10, repeat: Infinity, ease: "linear" }}
          className="absolute -top-40 -right-40 w-96 h-96 bg-primary/20 rounded-full blur-3xl mix-blend-multiply"
        />
        <motion.div
          animate={{
            scale: [1, 1.5, 1],
            rotate: [0, -90, 0],
            opacity: [0.5, 0.3, 0.5],
          }}
          transition={{ duration: 15, repeat: Infinity, ease: "linear" }}
          className="absolute -bottom-40 -left-40 w-96 h-96 bg-accent/20 rounded-full blur-3xl mix-blend-multiply"
        />
      </div>

      {/* Content */}
      <div className="relative z-10 w-full max-w-md text-center space-y-12">
        <AnimatePresence mode="wait">
          <motion.div
            key={step}
            initial={{ opacity: 0, x: 20 }}
            animate={{ opacity: 1, x: 0 }}
            exit={{ opacity: 0, x: -20 }}
            transition={{ duration: 0.3 }}
            className="space-y-8"
          >
            {/* Icon */}
            <motion.div
              initial={{ scale: 0 }}
              animate={{ scale: 1 }}
              transition={{ type: "spring", damping: 12, delay: 0.1 }}
              className="text-8xl flex justify-center"
            >
              <div className="w-40 h-40 bg-white/50 backdrop-blur-lg rounded-full flex items-center justify-center shadow-xl border border-white/20">
                {currentStep.icon}
              </div>
            </motion.div>

            {/* Text Content */}
            <div className="space-y-4">
              <h1 className="text-4xl font-extrabold text-foreground tracking-tight">
                {currentStep.title}
              </h1>
              <p className="text-lg text-muted-foreground">{currentStep.subtitle}</p>
              <p className="text-sm text-muted-foreground/70">{currentStep.description}</p>
            </div>
          </motion.div>
        </AnimatePresence>

        {/* Progress Indicator */}
        <div className="flex gap-2 justify-center">
          {steps.map((_, index) => (
            <motion.div
              key={index}
              className={`h-2 rounded-full ${
                index < step ? "bg-primary" : "bg-muted"
              }`}
              initial={false}
              animate={{ width: index < step ? 32 : 8 }}
              transition={{ duration: 0.3 }}
            />
          ))}
        </div>

        {/* Buttons */}
        <div className="space-y-4 pt-4">
          <SilentMoonButton onClick={handleNext} fullWidth size="lg" variant="primary">
            {step === steps.length ? "Get Started" : "Next"}
          </SilentMoonButton>

          <AnimatePresence>
            {step > 1 && (
              <motion.div
                initial={{ opacity: 0, height: 0 }}
                animate={{ opacity: 1, height: "auto" }}
                exit={{ opacity: 0, height: 0 }}
              >
                <SilentMoonButton onClick={() => setStep(step - 1)} fullWidth size="lg" variant="ghost">
                  Back
                </SilentMoonButton>
              </motion.div>
            )}
          </AnimatePresence>
        </div>
      </div>
    </div>
  );
}
