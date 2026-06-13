import { SilentMoonButton } from "@/components/SilentMoonButton";
import { useState } from "react";
import { motion } from "framer-motion";
import { ArrowLeft } from "lucide-react";

interface ReminderSetupProps {
  onBack: () => void;
}

export default function ReminderSetup({ onBack }: ReminderSetupProps) {
  const [selectedDays, setSelectedDays] = useState<string[]>([]);
  const [selectedTime, setSelectedTime] = useState("09:00");

  const days = ["SU", "M", "T", "W", "TH", "F", "S"];
  const dayNames = [
    "Sunday",
    "Monday",
    "Tuesday",
    "Wednesday",
    "Thursday",
    "Friday",
    "Saturday",
  ];

  const toggleDay = (day: string) => {
    setSelectedDays((prev) =>
      prev.includes(day) ? prev.filter((d) => d !== day) : [...prev, day]
    );
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
          <h1 className="text-2xl font-bold text-foreground">Set Reminders</h1>
        </div>
      </div>

      {/* Content */}
      <div className="max-w-md mx-auto p-6 space-y-10">
        
        {/* Time Selection */}
        <motion.div 
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="space-y-4"
        >
          <h2 className="text-2xl font-extrabold text-foreground">
            What time would you like to meditate?
          </h2>
          <p className="text-muted-foreground">
            Any time you can choose but We recommend first thing in the morning.
          </p>

          <div className="bg-card rounded-3xl p-8 border border-border/50 shadow-sm">
            <div className="flex flex-col items-center justify-center gap-6">
              <input
                type="time"
                value={selectedTime}
                onChange={(e) => setSelectedTime(e.target.value)}
                className="text-5xl font-black bg-transparent border-none focus:ring-0 text-center tracking-tighter"
              />
            </div>
          </div>
        </motion.div>

        {/* Day Selection */}
        <motion.div 
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.1 }}
          className="space-y-6"
        >
          <h2 className="text-2xl font-extrabold text-foreground">
            Which day would you like to meditate?
          </h2>
          <p className="text-muted-foreground">
            Everyday is best, but we recommend picking at least five.
          </p>

          <div className="flex gap-2 justify-center flex-wrap">
            {days.map((day) => (
              <motion.button
                key={day}
                whileHover={{ scale: 1.1 }}
                whileTap={{ scale: 0.9 }}
                onClick={() => toggleDay(day)}
                className={`w-12 h-12 rounded-full font-bold transition-colors duration-300 flex items-center justify-center ${
                  selectedDays.includes(day)
                    ? "bg-primary text-primary-foreground shadow-lg shadow-primary/30"
                    : "bg-muted text-muted-foreground hover:bg-muted/80 border border-border"
                }`}
              >
                {day}
              </motion.button>
            ))}
          </div>

          {/* Selected Days Display */}
          {selectedDays.length > 0 && (
            <motion.div 
              initial={{ opacity: 0, scale: 0.9 }}
              animate={{ opacity: 1, scale: 1 }}
              className="bg-primary/10 rounded-2xl p-4 border border-primary/20 text-center"
            >
              <p className="text-sm font-medium text-primary">
                Selected: {selectedDays.map((d) => dayNames[days.indexOf(d)]).join(", ")}
              </p>
            </motion.div>
          )}
        </motion.div>

        {/* Action Buttons */}
        <motion.div 
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.2 }}
          className="space-y-4 pt-6"
        >
          <SilentMoonButton
            onClick={onBack}
            fullWidth
            size="lg"
            variant="primary"
            disabled={selectedDays.length === 0}
          >
            Save Reminders
          </SilentMoonButton>
          <SilentMoonButton
            onClick={onBack}
            fullWidth
            size="lg"
            variant="ghost"
          >
            No Thanks
          </SilentMoonButton>
        </motion.div>
      </div>
    </div>
  );
}
