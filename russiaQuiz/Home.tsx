import { useState } from 'react';
import { X, Star } from 'lucide-react';
import Confetti from '@/components/Confetti';

interface GameState {
  currentLevel: number;
  score: number;
  gems: number;
  showHelpModal: boolean;
  showGemModal: boolean;
  showSuccessModal: boolean;
  selectedLetters: string[];
  usedLetters: string[];
  starsEarned: number;
}

const RIDDLES = [
  {
    question: 'It has teeth but cannot eat',
    answer: 'COMB',
    letters: ['C', 'O', 'M', 'B', 'S', 'T', 'A', 'X'],
  },
  {
    question: 'A shiny precious stone',
    answer: 'PEARL',
    letters: ['P', 'E', 'A', 'R', 'L', 'T', 'S', 'M'],
  },
  {
    question: 'Hot, bright, and flickering',
    answer: 'FLAME',
    letters: ['F', 'L', 'A', 'M', 'E', 'T', 'S', 'I'],
  },
];

export default function Home() {
  const [gameState, setGameState] = useState<GameState>({
    currentLevel: 0,
    score: 0,
    gems: 50,
    showHelpModal: false,
    showGemModal: false,
    showSuccessModal: false,
    selectedLetters: [],
    usedLetters: [],
    starsEarned: 0,
  });

  const [showConfetti, setShowConfetti] = useState(false);

  const currentRiddle = RIDDLES[gameState.currentLevel % RIDDLES.length];
  const answerLength = currentRiddle.answer.length;
  const isAnswerComplete = gameState.selectedLetters.length === answerLength;
  const isAnswerCorrect =
    gameState.selectedLetters.join('') === currentRiddle.answer;

  const handleLetterClick = (letter: string) => {
    if (gameState.selectedLetters.length < answerLength) {
      setGameState((prev) => ({
        ...prev,
        selectedLetters: [...prev.selectedLetters, letter],
        usedLetters: [...prev.usedLetters, letter],
      }));
    }
  };

  const handleRemoveLetter = () => {
    setGameState((prev) => ({
      ...prev,
      selectedLetters: prev.selectedLetters.slice(0, -1),
    }));
  };

  const handleSubmitAnswer = () => {
    if (isAnswerCorrect) {
      const starsEarned = Math.max(1, 4 - Math.floor(gameState.selectedLetters.length / 2));
      setShowConfetti(true);
      setGameState((prev) => ({
        ...prev,
        showSuccessModal: true,
        score: prev.score + starsEarned * 10,
        gems: prev.gems + 2,
        starsEarned,
      }));
    }
  };

  const handleNextLevel = () => {
    setGameState((prev) => ({
      ...prev,
      currentLevel: prev.currentLevel + 1,
      selectedLetters: [],
      usedLetters: [],
      showSuccessModal: false,
      starsEarned: 0,
    }));
  };

  const handleCloseModal = () => {
    setGameState((prev) => ({
      ...prev,
      showHelpModal: false,
      showGemModal: false,
    }));
  };

  const handleUseHelp = (type: 'reveal' | 'shuffle') => {
    if (type === 'reveal' && gameState.gems >= 5) {
      // 한 글자 공개
      const unrevealed = currentRiddle.answer
        .split('')
        .map((char, idx) => (!gameState.selectedLetters.includes(char) ? idx : -1))
        .filter((idx) => idx !== -1);

      if (unrevealed.length > 0) {
        const randomIdx = unrevealed[Math.floor(Math.random() * unrevealed.length)];
        const charToReveal = currentRiddle.answer[randomIdx];
        setGameState((prev) => ({
          ...prev,
          selectedLetters: [...prev.selectedLetters, charToReveal],
          usedLetters: [...prev.usedLetters, charToReveal],
          gems: prev.gems - 5,
        }));
      }
      handleCloseModal();
    } else if (type === 'shuffle' && gameState.gems >= 3) {
      // 문자 섞기 (UI만 - 실제로는 letters 배열을 섞음)
      setGameState((prev) => ({
        ...prev,
        gems: prev.gems - 3,
      }));
      handleCloseModal();
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-b from-sky-300 via-sky-200 to-green-300 flex items-center justify-center p-4">
      {/* 폭죽 애니메이션 */}
      <Confetti isActive={showConfetti} onComplete={() => setShowConfetti(false)} />

      {/* Game Container */}
      <div className="w-full max-w-md bg-gradient-to-b from-sky-100 to-sky-50 rounded-3xl shadow-2xl overflow-hidden">
        {/* Header */}
        <div className="bg-gradient-to-r from-blue-500 to-blue-600 text-white px-6 py-4 flex justify-between items-center">
          <div className="flex items-center gap-2">
            <span className="text-2xl">⭐</span>
            <span className="font-bold text-lg">{gameState.score}</span>
          </div>
          <div className="text-center font-bold text-lg">Level {gameState.currentLevel + 1}</div>
          <div className="flex items-center gap-2">
            <span className="text-2xl">💎</span>
            <span className="font-bold text-lg">{gameState.gems}</span>
          </div>
        </div>

        {/* Main Game Area */}
        <div className="p-6 space-y-6">
          {/* Riddle Question */}
          <div className="bg-white rounded-2xl p-6 shadow-md text-center">
            <p className="text-gray-700 font-semibold text-lg">{currentRiddle.question}</p>
          </div>

          {/* Answer Slots */}
          <div className="flex justify-center gap-3 flex-wrap">
            {Array.from({ length: answerLength }).map((_, idx) => (
              <div
                key={idx}
                className="w-12 h-12 bg-white border-2 border-blue-400 rounded-lg flex items-center justify-center font-bold text-lg text-blue-600 shadow-md"
              >
                {gameState.selectedLetters[idx] || ''}
              </div>
            ))}
          </div>

          {/* Action Buttons */}
          <div className="flex gap-3 justify-center">
            <button
              onClick={() => setGameState((prev) => ({ ...prev, showHelpModal: true }))}
              className="bg-blue-500 hover:bg-blue-600 text-white px-6 py-2 rounded-full font-bold shadow-md transition active:scale-95"
            >
              💡 Help
            </button>
            {gameState.selectedLetters.length > 0 && (
              <button
                onClick={handleRemoveLetter}
                className="bg-red-500 hover:bg-red-600 text-white px-6 py-2 rounded-full font-bold shadow-md transition active:scale-95"
              >
                ✕ Delete
              </button>
            )}
          </div>

          {/* Letter Grid */}
          <div className="bg-gradient-to-b from-blue-400 to-blue-600 rounded-2xl p-4 shadow-lg">
            <div className="grid grid-cols-4 gap-2">
              {currentRiddle.letters.map((letter, idx) => {
                const isUsed = gameState.selectedLetters.includes(letter);
                return (
                  <button
                    key={idx}
                    onClick={() => !isUsed && handleLetterClick(letter)}
                    disabled={isUsed}
                    className={`h-12 rounded-lg font-bold text-white text-lg transition ${
                      isUsed
                        ? 'bg-gray-400 opacity-50 cursor-not-allowed'
                        : 'bg-blue-300 hover:bg-blue-200 shadow-md active:scale-95'
                    }`}
                  >
                    {letter}
                  </button>
                );
              })}
            </div>
          </div>

          {/* Submit Button */}
          {isAnswerComplete && (
            <button
              onClick={handleSubmitAnswer}
              className="w-full bg-green-500 hover:bg-green-600 text-white py-3 rounded-full font-bold text-lg shadow-lg transition active:scale-95"
            >
              ✓ Submit
            </button>
          )}
        </div>
      </div>

      {/* Help Modal - 화면 2 */}
      {gameState.showHelpModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-2xl p-6 max-w-sm shadow-2xl">
            <div className="flex justify-between items-center mb-4">
              <h2 className="text-xl font-bold text-gray-800">Help</h2>
              <button
                onClick={handleCloseModal}
                className="text-gray-500 hover:text-gray-700"
              >
                <X size={24} />
              </button>
            </div>
            <p className="text-gray-600 text-sm mb-4">Gem Riddles</p>
            <div className="space-y-3">
              <button
                onClick={() => handleUseHelp('reveal')}
                disabled={gameState.gems < 5}
                className={`w-full py-2 rounded-lg font-bold transition ${
                  gameState.gems >= 5
                    ? 'bg-blue-500 hover:bg-blue-600 text-white'
                    : 'bg-gray-300 text-gray-500 cursor-not-allowed'
                }`}
              >
                💡 Reveal a Letter (5 💎)
              </button>
              <button
                onClick={() => handleUseHelp('shuffle')}
                disabled={gameState.gems < 3}
                className={`w-full py-2 rounded-lg font-bold transition ${
                  gameState.gems >= 3
                    ? 'bg-blue-500 hover:bg-blue-600 text-white'
                    : 'bg-gray-300 text-gray-500 cursor-not-allowed'
                }`}
              >
                🔄 Shuffle Letters (3 💎)
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Gem Modal - 화면 4 */}
      {gameState.showGemModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-2xl p-6 max-w-sm shadow-2xl text-center">
            <div className="flex justify-between items-center mb-4">
              <h2 className="text-xl font-bold text-gray-800">Gems</h2>
              <button
                onClick={handleCloseModal}
                className="text-gray-500 hover:text-gray-700"
              >
                <X size={24} />
              </button>
            </div>
            <p className="text-4xl mb-4">💎</p>
            <p className="text-gray-600 mb-6">Watch a video to earn 5 gems!</p>
            <div className="space-y-3">
              <button className="w-full bg-blue-500 hover:bg-blue-600 text-white py-2 rounded-lg font-bold transition">
                ▶ Watch Video
              </button>
              <button
                onClick={handleCloseModal}
                className="w-full bg-gray-200 hover:bg-gray-300 text-gray-800 py-2 rounded-lg font-bold transition"
              >
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Success Modal - 화면 5 */}
      {gameState.showSuccessModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-2xl p-8 max-w-sm shadow-2xl text-center animate-bounce border-4 border-blue-500">
            <div className="text-5xl mb-4 animate-pulse">🎉</div>
            
            {/* Stars Display */}
            <div className="flex justify-center gap-2 mb-4">
              {Array.from({ length: 3 }).map((_, idx) => (
                <div
                  key={idx}
                  className={`text-3xl transition-all ${
                    idx < gameState.starsEarned
                      ? 'animate-bounce'
                      : 'opacity-30'
                  }`}
                >
                  ⭐
                </div>
              ))}
            </div>

            <h2 className="text-2xl font-bold text-gray-800 mb-2">WELL DONE!</h2>
            <p className="text-gray-600 mb-2">Correct answer!</p>
            <p className="text-blue-600 font-bold mb-6">+{gameState.starsEarned * 10} ⭐ +2 💎</p>
            
            <button
              onClick={handleNextLevel}
              className="w-full bg-blue-500 hover:bg-blue-600 text-white py-3 rounded-lg font-bold text-lg transition active:scale-95"
            >
              Continue →
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
