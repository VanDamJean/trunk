import { Toaster } from "@/components/ui/sonner";
import { TooltipProvider } from "@/components/ui/tooltip";
import NotFound from "@/pages/NotFound";
import { Route, Switch, useLocation } from "wouter";
import ErrorBoundary from "./components/ErrorBoundary";
import { ThemeProvider } from "./contexts/ThemeContext";
import { AnimatePresence, motion } from "framer-motion";

// Pages
import Onboarding from "./pages/Onboarding";
import Home from "./pages/Home";
import QuizPlay from "./pages/QuizPlay";
import QuizResult from "./pages/QuizResult";
import Profile from "./pages/Profile";
import Leaderboard from "./pages/Leaderboard";
import History from "./pages/History";
import Badges from "./pages/Badges";

// Page transition variants
const pageVariants = {
  initial: {
    opacity: 0,
    y: 12,
    scale: 0.99,
  },
  in: {
    opacity: 1,
    y: 0,
    scale: 1,
  },
  out: {
    opacity: 0,
    y: -8,
    scale: 0.99,
  },
};

const pageTransition = {
  type: "tween",
  ease: [0.25, 0.46, 0.45, 0.94],
  duration: 0.3,
};

function AnimatedPage({ children }: { children: React.ReactNode }) {
  return (
    <motion.div
      initial="initial"
      animate="in"
      exit="out"
      variants={pageVariants}
      transition={pageTransition}
      style={{ width: "100%" }}
    >
      {children}
    </motion.div>
  );
}

function Router() {
  const [location] = useLocation();

  return (
    <AnimatePresence mode="wait">
      <Switch location={location} key={location}>
        {/* Public Routes */}
        <Route path="/">
          <AnimatedPage><Onboarding /></AnimatedPage>
        </Route>

        {/* Main Routes */}
        <Route path="/home">
          <AnimatedPage><Home /></AnimatedPage>
        </Route>
        <Route path="/quiz/:categoryId">
          {(params) => <AnimatedPage><QuizPlay /></AnimatedPage>}
        </Route>
        <Route path="/result/:categoryId">
          {(params) => <AnimatedPage><QuizResult /></AnimatedPage>}
        </Route>

        {/* New Pages */}
        <Route path="/profile">
          <AnimatedPage><Profile /></AnimatedPage>
        </Route>
        <Route path="/leaderboard">
          <AnimatedPage><Leaderboard /></AnimatedPage>
        </Route>
        <Route path="/history">
          <AnimatedPage><History /></AnimatedPage>
        </Route>
        <Route path="/badges">
          <AnimatedPage><Badges /></AnimatedPage>
        </Route>

        {/* Fallback */}
        <Route path="/404">
          <AnimatedPage><NotFound /></AnimatedPage>
        </Route>
        <Route>
          <AnimatedPage><NotFound /></AnimatedPage>
        </Route>
      </Switch>
    </AnimatePresence>
  );
}

function App() {
  return (
    <ErrorBoundary>
      <ThemeProvider defaultTheme="light">
        <TooltipProvider>
          <Toaster richColors position="top-center" />
          <Router />
        </TooltipProvider>
      </ThemeProvider>
    </ErrorBoundary>
  );
}

export default App;
