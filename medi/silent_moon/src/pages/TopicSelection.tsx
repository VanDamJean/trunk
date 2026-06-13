import { SilentMoonButton } from "@/components/SilentMoonButton";
import { useState } from "react";
import { motion } from "framer-motion";
import { ArrowLeft, Check } from "lucide-react";

interface TopicSelectionProps {
  onBack: () => void;
}

export default function TopicSelection({ onBack }: TopicSelectionProps) {
  const [selectedTopics, setSelectedTopics] = useState<string[]>([]);

  const topics = [
    { id: "stress", title: "Reduce Stress", icon: "😌", color: "from-orange-400/20 to-orange-400/5" },
    { id: "anxiety", title: "Reduce Anxiety", icon: "🧘", color: "from-purple-400/20 to-purple-400/5" },
    { id: "sleep", title: "Better Sleep", icon: "😴", color: "from-blue-400/20 to-blue-400/5" },
    { id: "focus", title: "Improve Focus", icon: "🎯", color: "from-green-400/20 to-green-400/5" },
    { id: "happiness", title: "Increase Happiness", icon: "😊", color: "from-pink-400/20 to-pink-400/5" },
    { id: "growth", title: "Personal Growth", icon: "🌱", color: "from-emerald-400/20 to-emerald-400/5" },
  ];

  const toggleTopic = (id: string) => {
    setSelectedTopics((prev) =>
      prev.includes(id) ? prev.filter((t) => t !== id) : [...prev, id]
    );
  };

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
    hidden: { opacity: 0, scale: 0.8 },
    show: { opacity: 1, scale: 1 }
  };

  return (
    <div className="min-h-screen bg-background pb-20">
      {/* Header */}
      <div className="sticky top-0 z-40 bg-background/80 backdrop-blur-xl border-b border-border/50 p-6">
        <div className="max-w-md mx-auto flex items-center gap-4">
          <button
            onClick={onBack}
            className="p-2 rounded-full hover:bg-secondary/20 transition-colors"
          >
            <ArrowLeft className="w-6 h-6" />
          </button>
          <h1 className="text-2xl font-bold text-foreground">Choose Topic</h1>
        </div>
      </div>

      {/* Content */}
      <div className="max-w-md mx-auto p-6 space-y-8">
        <div className="space-y-2">
          <h2 className="text-3xl font-extrabold tracking-tight">What to focus on?</h2>
          <p className="text-muted-foreground text-lg">
            Select the topics that interest you. You can choose multiple.
          </p>
        </div>

        {/* Topic Grid */}
        <motion.div 
          className="grid grid-cols-2 gap-4"
          variants={container}
          initial="hidden"
          animate="show"
        >
          {topics.map((topic) => {
            const isSelected = selectedTopics.includes(topic.id);
            return (
              <motion.button
                key={topic.id}
                variants={item}
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
                onClick={() => toggleTopic(topic.id)}
                className={`relative p-6 rounded-3xl border-2 transition-all duration-300 text-center space-y-4 flex flex-col items-center justify-center min-h-[160px] overflow-hidden ${
                  isSelected
                    ? `bg-gradient-to-br ${topic.color} border-primary shadow-lg`
                    : "bg-card border-border hover:border-primary/30"
                }`}
              >
                {/* Checkmark overlay */}
                {isSelected && (
                  <motion.div 
                    initial={{ scale: 0, opacity: 0 }}
                    animate={{ scale: 1, opacity: 1 }}
                    className="absolute top-3 right-3 w-6 h-6 bg-primary rounded-full flex items-center justify-center"
                  >
                    <Check className="w-4 h-4 text-white" />
                  </motion.div>
                )}
                
                <div className="text-5xl drop-shadow-md">{topic.icon}</div>
                <h3 className="font-semibold text-foreground text-sm">
                  {topic.title}
                </h3>
              </motion.button>
            );
          })}
        </motion.div>

        {/* Save Button */}
        <motion.div 
          initial={{ y: 50, opacity: 0 }}
          animate={{ y: 0, opacity: 1 }}
          transition={{ delay: 0.5 }}
          className="pt-6"
        >
          <SilentMoonButton
            onClick={onBack}
            fullWidth
            size="lg"
            variant="primary"
            disabled={selectedTopics.length === 0}
          >
            Save Selection {selectedTopics.length > 0 && `(${selectedTopics.length})`}
          </SilentMoonButton>
        </motion.div>
      </div>
    </div>
  );
}
