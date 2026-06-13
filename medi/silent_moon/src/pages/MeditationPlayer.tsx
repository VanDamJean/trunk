import { SilentMoonButton } from "@/components/SilentMoonButton";
import { useState, useEffect, useRef } from "react";
import { motion } from "framer-motion";
import { ArrowLeft, MoreVertical, Heart, Play, Pause, Download } from "lucide-react";

interface MeditationPlayerProps {
  session: any;
  onBack: () => void;
}

export default function MeditationPlayer({ session, onBack }: MeditationPlayerProps) {
  const parsedDuration = session ? (parseInt(session.duration) || 10) * 60 : 600;
  const [isPlaying, setIsPlaying] = useState(false);
  const [progress, setProgress] = useState(0);
  const [isFavorite, setIsFavorite] = useState(false);
  const audioRef = useRef<HTMLAudioElement | null>(null);
  const timerRef = useRef<number | null>(null);

  useEffect(() => {
    // Initialize audio element for endless looping background sound
    if (session?.audioUrl && !audioRef.current) {
      audioRef.current = new Audio(session.audioUrl);
      audioRef.current.loop = true;
    }

    return () => {
      if (audioRef.current) {
        audioRef.current.pause();
        audioRef.current = null;
      }
    };
  }, [session]);

  useEffect(() => {
    if (isPlaying) {
      // Start background audio (play() returns a Promise — must catch autoplay block errors)
      if (audioRef.current) {
        audioRef.current.play().catch(() => {
          // Autoplay blocked or audio unavailable — continue timer without audio
        });
      }

      // Start session timer
      timerRef.current = window.setInterval(() => {
        setProgress((prev) => {
          if (prev >= parsedDuration) {
            setIsPlaying(false);
            if (audioRef.current) audioRef.current.pause();
            return parsedDuration; // Stop at max
          }
          return prev + 1;
        });
      }, 1000);
    } else {
      // Pause background audio
      if (audioRef.current) audioRef.current.pause();
      if (timerRef.current) clearInterval(timerRef.current);
    }

    return () => {
      if (timerRef.current) clearInterval(timerRef.current);
    };
  }, [isPlaying, parsedDuration]);

  const togglePlay = () => {
    if (progress >= parsedDuration) {
      setProgress(0); // Reset if it was finished
    }
    setIsPlaying(!isPlaying);
  };

  if (!session) {
    return null;
  }

  const formatTime = (seconds: number) => {
    if (!seconds || isNaN(seconds)) return "0:00";
    const m = Math.floor(seconds / 60);
    const s = Math.floor(seconds % 60);
    return `${m}:${s < 10 ? '0' : ''}${s}`;
  };

  const progressPercentage = parsedDuration > 0 ? (progress / parsedDuration) * 100 : 0;
  
  const isSleepTheme = session?.category === "evening";

  return (
    <div className={`h-screen overflow-y-auto ${isSleepTheme ? "dark bg-slate-950 text-slate-50" : "bg-gradient-to-b from-primary/20 via-background to-background"} flex flex-col`}>
      {/* Animated Background */}
      {isPlaying && (
        <motion.div
          animate={{
            scale: [1, 1.1, 1],
            opacity: [0.3, 0.5, 0.3],
          }}
          transition={{
            duration: 4,
            repeat: Infinity,
            ease: "easeInOut",
          }}
          className={`fixed inset-0 rounded-full blur-[100px] pointer-events-none ${isSleepTheme ? "bg-indigo-500/10" : "bg-primary/5"}`}
        />
      )}

      {/* Header — sticky so it stays visible when scrolling */}
      <div className={`sticky top-0 left-0 right-0 flex items-center justify-between p-5 z-10 ${isSleepTheme ? "bg-slate-950/70" : "bg-background/70"} backdrop-blur-md`}>
        <button
          onClick={onBack}
          className="p-3 rounded-full hover:bg-secondary/20 transition-colors bg-white/10"
        >
          <ArrowLeft className="w-6 h-6" />
        </button>
        <button className="p-3 rounded-full hover:bg-secondary/20 transition-colors bg-white/10">
          <MoreVertical className="w-6 h-6" />
        </button>
      </div>

      {/* Content — flex-start so overflow always scrolls down to controls */}
      <div className="relative z-10 w-full max-w-md mx-auto flex flex-col items-center space-y-8 px-6 py-8 pb-12">
        {/* Large Icon/Visualization */}
        <div className="flex justify-center">
          <motion.div
            animate={isPlaying ? { scale: [1, 1.05, 1], rotate: [0, 5, -5, 0] } : {}}
            transition={{ duration: 4, repeat: Infinity, ease: "easeInOut" }}
            className="w-44 h-44 sm:w-56 sm:h-56 rounded-full bg-gradient-to-br from-white/40 to-white/10 backdrop-blur-xl border border-white/20 shadow-2xl flex items-center justify-center text-7xl sm:text-8xl relative"
          >
            {/* Inner pulsating ring */}
            {isPlaying && (
              <motion.div
                animate={{ scale: [1, 1.2], opacity: [0.5, 0] }}
                transition={{ duration: 2, repeat: Infinity }}
                className="absolute inset-0 rounded-full border-4 border-primary/30"
              />
            )}
            {session.icon}
          </motion.div>
        </div>

        {/* Session Info */}
        <div className="text-center space-y-3">
          <motion.h1 
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            className="text-3xl font-extrabold text-foreground"
          >
            {session.title}
          </motion.h1>
          <motion.p 
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.1 }}
            className="text-muted-foreground text-lg"
          >
            {session.description}
          </motion.p>
          <motion.p 
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.2 }}
            className="text-sm text-primary font-bold tracking-widest uppercase"
          >
            {session.duration}
          </motion.p>
        </div>

        {/* Progress Bar */}
        <div className="space-y-3 px-4">
          <div className="w-full h-3 bg-muted rounded-full overflow-hidden shadow-inner">
            <motion.div
              className="h-full bg-gradient-to-r from-primary to-accent"
              initial={{ width: 0 }}
              animate={{ width: `${progressPercentage}%` }}
              transition={{ ease: "linear", duration: 0.8 }}
            />
          </div>
          <div className="flex justify-between text-sm font-medium text-muted-foreground">
            <span>{formatTime(progress)}</span>
            <span>{formatTime(parsedDuration)}</span>
          </div>
        </div>

        {/* Controls */}
        <div className="flex items-center justify-center gap-10">
          <motion.button
            whileHover={{ scale: 1.1 }}
            whileTap={{ scale: 0.9 }}
            onClick={() => setIsFavorite(!isFavorite)}
            className={`p-4 rounded-full transition-colors ${isFavorite ? 'text-pink-500 bg-pink-500/10' : 'text-muted-foreground hover:bg-secondary/20'}`}
          >
            <Heart className="w-8 h-8" fill={isFavorite ? "currentColor" : "none"} />
          </motion.button>

          <motion.button
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.95 }}
            onClick={togglePlay}
            className="w-24 h-24 rounded-full bg-primary text-white shadow-xl shadow-primary/40 flex items-center justify-center"
          >
            {isPlaying ? (
              <Pause className="w-10 h-10 fill-current" />
            ) : (
              <Play className="w-10 h-10 fill-current ml-2" />
            )}
          </motion.button>

          <motion.button 
            whileHover={{ scale: 1.1 }}
            whileTap={{ scale: 0.9 }}
            className="p-4 rounded-full text-muted-foreground hover:bg-secondary/20 transition-colors"
          >
            <Download className="w-8 h-8" />
          </motion.button>
        </div>
      </div>
    </div>
  );
}
