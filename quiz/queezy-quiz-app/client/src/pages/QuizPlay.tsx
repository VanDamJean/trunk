import { useEffect, useState } from "react";
import { useParams, useLocation } from "wouter";
import { mockApi, MOCK_CATEGORIES } from "@/lib/mockData";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Clock, CheckCircle, XCircle, ArrowLeft } from "lucide-react";
import { motion, AnimatePresence } from "framer-motion";

interface QuizAnswer {
  quizId: number;
  selectedAnswer: number;
  isCorrect: boolean;
}

export default function QuizPlay() {
  const { categoryId } = useParams<{ categoryId: string }>();
  const [, setLocation] = useLocation();

  const [currentQuestionIndex, setCurrentQuestionIndex] = useState(0);
  const [selectedAnswers, setSelectedAnswers] = useState<QuizAnswer[]>([]);
  const [timeSpent, setTimeSpent] = useState(0);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [quizStartTime] = useState(Date.now());
  const [showFeedback, setShowFeedback] = useState(false);

  const parsedCategoryId = parseInt(categoryId || "0");
  const quizzes = mockApi.getQuizzesByCategory(parsedCategoryId);
  const category = MOCK_CATEGORIES.find((c) => c.id === parsedCategoryId);

  // Timer effect
  useEffect(() => {
    const timer = setInterval(() => {
      setTimeSpent(Math.floor((Date.now() - quizStartTime) / 1000));
    }, 1000);
    return () => clearInterval(timer);
  }, [quizStartTime]);

  if (!categoryId || quizzes.length === 0) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-purple-50 to-blue-50 dark:from-slate-900 dark:to-slate-800">
        <div className="text-center">
          <p className="text-gray-600 dark:text-gray-400 mb-4">퀴즈가 없습니다.</p>
          <Button onClick={() => setLocation("/home")} className="rounded-2xl">홈으로 돌아가기</Button>
        </div>
      </div>
    );
  }

  const currentQuiz = quizzes[currentQuestionIndex];
  const progress = ((currentQuestionIndex + 1) / quizzes.length) * 100;
  const isAnswered = selectedAnswers.some((a) => a.quizId === currentQuiz.id);
  const currentAnswer = selectedAnswers.find((a) => a.quizId === currentQuiz.id);

  const handleSelectAnswer = (optionIndex: number) => {
    if (isAnswered) return;

    const isCorrect = optionIndex === currentQuiz.correctAnswer;
    const newAnswer: QuizAnswer = {
      quizId: currentQuiz.id,
      selectedAnswer: optionIndex,
      isCorrect,
    };
    setSelectedAnswers([...selectedAnswers, newAnswer]);
    setShowFeedback(true);

    // Auto-advance after delay
    setTimeout(() => {
      setShowFeedback(false);
      if (currentQuestionIndex < quizzes.length - 1) {
        setCurrentQuestionIndex(currentQuestionIndex + 1);
      }
    }, 1500);
  };

  const handleSubmit = () => {
    if (selectedAnswers.length !== quizzes.length) return;
    setIsSubmitting(true);

    const correctCount = selectedAnswers.filter((a) => a.isCorrect).length;
    const score = ((correctCount / quizzes.length) * 100).toFixed(2);

    mockApi.saveResult({
      categoryId: parsedCategoryId,
      totalQuestions: quizzes.length,
      correctAnswers: correctCount,
      score,
      timeSpent,
      answers: selectedAnswers,
    });

    setLocation(
      `/result/${categoryId}?score=${score}&correct=${correctCount}&total=${quizzes.length}&time=${timeSpent}`
    );
  };

  const optionLabels = ["A", "B", "C", "D"];

  return (
    <div className="min-h-screen bg-gradient-to-br from-purple-50 via-white to-blue-50 dark:from-slate-900 dark:via-slate-800 dark:to-slate-900 p-4">
      {/* Decorative */}
      <div className="fixed top-[-15%] right-[-10%] w-[400px] h-[400px] rounded-full bg-purple-200/20 dark:bg-purple-900/10 blur-3xl pointer-events-none" />

      <div className="max-w-2xl mx-auto">
        {/* Header */}
        <motion.div
          initial={{ opacity: 0, y: -10 }}
          animate={{ opacity: 1, y: 0 }}
          className="mb-6"
        >
          <div className="flex justify-between items-center mb-4">
            <div className="flex items-center gap-3">
              <Button
                onClick={() => setLocation("/home")}
                variant="ghost"
                size="icon"
                className="rounded-full"
              >
                <ArrowLeft className="w-5 h-5" />
              </Button>
              <div>
                <p className="text-xs text-gray-500 dark:text-gray-400">{category?.name || "퀴즈"}</p>
                <h2 className="text-lg font-bold text-gray-900 dark:text-white">
                  문제 {currentQuestionIndex + 1}/{quizzes.length}
                </h2>
              </div>
            </div>
            <div className="flex items-center gap-2 px-3 py-1.5 rounded-full bg-white/60 dark:bg-slate-800/60 backdrop-blur-sm border border-gray-100 dark:border-slate-700/50">
              <Clock className="w-4 h-4 text-primary" />
              <span className="text-sm font-mono font-medium text-gray-700 dark:text-gray-300">
                {Math.floor(timeSpent / 60)}:{(timeSpent % 60).toString().padStart(2, "0")}
              </span>
            </div>
          </div>

          {/* Progress Bar */}
          <div className="w-full bg-gray-100 dark:bg-gray-800 rounded-full h-2 overflow-hidden">
            <motion.div
              className="bg-gradient-to-r from-primary to-purple-400 h-2 rounded-full"
              initial={{ width: 0 }}
              animate={{ width: `${progress}%` }}
              transition={{ type: "spring", stiffness: 100, damping: 15 }}
            />
          </div>
        </motion.div>

        {/* Question Card */}
        <AnimatePresence mode="wait">
          <motion.div
            key={currentQuestionIndex}
            initial={{ opacity: 0, x: 30 }}
            animate={{ opacity: 1, x: 0 }}
            exit={{ opacity: 0, x: -30 }}
            transition={{ type: "spring", stiffness: 300, damping: 30 }}
          >
            <Card className="card-base p-6 sm:p-8 mb-6 bg-white/70 dark:bg-slate-800/70 backdrop-blur-sm">
              {/* Difficulty Badge */}
              <div className="mb-4">
                <span className={`px-2 py-0.5 text-xs font-medium rounded-full ${
                  currentQuiz.difficulty === "easy"
                    ? "bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400"
                    : currentQuiz.difficulty === "medium"
                    ? "bg-yellow-100 text-yellow-700 dark:bg-yellow-900/30 dark:text-yellow-400"
                    : "bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400"
                }`}>
                  {currentQuiz.difficulty === "easy" ? "쉬움" : currentQuiz.difficulty === "medium" ? "보통" : "어려움"}
                </span>
              </div>

              {/* Question */}
              <h3 className="text-xl font-bold mb-6 text-gray-900 dark:text-white leading-relaxed">
                {currentQuiz.question}
              </h3>

              {/* Options */}
              <div className="space-y-3">
                {currentQuiz.options.map((option, index) => {
                  const isSelected = currentAnswer?.selectedAnswer === index;
                  const isCorrectOption = index === currentQuiz.correctAnswer;
                  const showResult = isAnswered;

                  return (
                    <motion.button
                      key={index}
                      onClick={() => handleSelectAnswer(index)}
                      disabled={isAnswered}
                      className={`w-full p-4 rounded-2xl text-left font-medium transition-all border-2 ${
                        showResult
                          ? isCorrectOption
                            ? "bg-green-50 dark:bg-green-900/30 text-green-800 dark:text-green-200 border-green-400 dark:border-green-600"
                            : isSelected
                            ? "bg-red-50 dark:bg-red-900/30 text-red-800 dark:text-red-200 border-red-400 dark:border-red-600"
                            : "bg-gray-50 dark:bg-gray-800 text-gray-400 dark:text-gray-500 border-transparent"
                          : "bg-gray-50 dark:bg-slate-700/50 text-gray-800 dark:text-gray-200 border-transparent hover:border-primary/50 hover:bg-purple-50 dark:hover:bg-purple-900/20"
                      }`}
                      whileHover={!isAnswered ? { scale: 1.01 } : {}}
                      whileTap={!isAnswered ? { scale: 0.99 } : {}}
                    >
                      <div className="flex items-center gap-3">
                        <span className={`w-8 h-8 rounded-lg flex items-center justify-center text-sm font-bold flex-shrink-0 ${
                          showResult
                            ? isCorrectOption
                              ? "bg-green-200 dark:bg-green-800 text-green-800 dark:text-green-200"
                              : isSelected
                              ? "bg-red-200 dark:bg-red-800 text-red-800 dark:text-red-200"
                              : "bg-gray-200 dark:bg-gray-700 text-gray-400"
                            : "bg-primary/10 text-primary"
                        }`}>
                          {optionLabels[index]}
                        </span>
                        <span className="flex-1">{option}</span>
                        {showResult && isCorrectOption && (
                          <motion.div initial={{ scale: 0 }} animate={{ scale: 1 }} transition={{ type: "spring" }}>
                            <CheckCircle className="w-5 h-5 text-green-500" />
                          </motion.div>
                        )}
                        {showResult && isSelected && !isCorrectOption && (
                          <motion.div initial={{ scale: 0 }} animate={{ scale: 1 }} transition={{ type: "spring" }}>
                            <XCircle className="w-5 h-5 text-red-500" />
                          </motion.div>
                        )}
                      </div>
                    </motion.button>
                  );
                })}
              </div>

              {/* Explanation */}
              <AnimatePresence>
                {isAnswered && currentQuiz.explanation && (
                  <motion.div
                    initial={{ opacity: 0, height: 0 }}
                    animate={{ opacity: 1, height: "auto" }}
                    exit={{ opacity: 0, height: 0 }}
                    className="mt-5 overflow-hidden"
                  >
                    <div className="bg-blue-50 dark:bg-blue-900/20 border border-blue-200 dark:border-blue-800/50 p-4 rounded-2xl">
                      <p className="text-sm text-blue-800 dark:text-blue-200">
                        <strong>💡 설명:</strong> {currentQuiz.explanation}
                      </p>
                    </div>
                  </motion.div>
                )}
              </AnimatePresence>
            </Card>
          </motion.div>
        </AnimatePresence>

        {/* Navigation */}
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ delay: 0.2 }}
          className="flex gap-3"
        >
          {currentQuestionIndex === quizzes.length - 1 && isAnswered ? (
            <Button
              onClick={handleSubmit}
              disabled={selectedAnswers.length !== quizzes.length || isSubmitting}
              className="flex-1 rounded-2xl h-12 text-base bg-gradient-to-r from-primary to-purple-500 hover:from-primary/90 hover:to-purple-500/90 text-white shadow-lg shadow-purple-300/50 dark:shadow-purple-900/50"
            >
              {isSubmitting ? "제출 중..." : "결과 확인하기 🎉"}
            </Button>
          ) : (
            <div className="flex-1 flex gap-3">
              <Button
                onClick={() => setLocation("/home")}
                variant="outline"
                className="rounded-2xl h-12 border-gray-200 dark:border-gray-600"
              >
                나가기
              </Button>
              <Button
                disabled={!isAnswered}
                className="flex-1 rounded-2xl h-12 text-base bg-primary hover:bg-primary/90 text-white disabled:opacity-30"
                onClick={() => {
                  if (currentQuestionIndex < quizzes.length - 1) {
                    setCurrentQuestionIndex(currentQuestionIndex + 1);
                  }
                }}
              >
                다음 문제
              </Button>
            </div>
          )}
        </motion.div>
      </div>
    </div>
  );
}
