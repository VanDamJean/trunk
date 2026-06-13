import Confetti from "@/components/Confetti";
import { useCallback, useEffect, useMemo, useState } from "react";
import { X } from "lucide-react";

interface GameState {
  currentLevel: number;
  score: number;
  gems: number;
  showHelpModal: boolean;
  showGemModal: boolean;
  showSuccessModal: boolean;
  starsEarned: number;
}

const RIDDLES = [
  {
    question: "Тиши бар бирок тиштебейт",
    answer: "БУЛАК",
    letters: ["Б", "У", "Л", "А", "К", "Ш", "И", "Т"],
  },
  {
    question: "Асыл таш",
    answer: "АЛМАЗ",
    letters: ["А", "Л", "М", "А", "З", "Ш", "И", "Т"],
  },
  {
    question: "Жардам",
    answer: "ОТ",
    letters: ["О", "Т", "Ш", "И", "Л", "А", "К", "Б"],
  },
] as const;

function shuffleInPlace(ids: number[]): void {
  for (let i = ids.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    const a = ids[i]!;
    const b = ids[j]!;
    ids[i] = b;
    ids[j] = a;
  }
}

function initialSlots(len: number): (number | null)[] {
  return Array.from({ length: len }, () => null);
}

function initialGridPerm(): number[] {
  return [0, 1, 2, 3, 4, 5, 6, 7];
}

export default function Home() {
  const [gameState, setGameState] = useState<GameState>({
    currentLevel: 0,
    score: 0,
    gems: 50,
    showHelpModal: false,
    showGemModal: false,
    showSuccessModal: false,
    starsEarned: 0,
  });

  const [slotTileId, setSlotTileId] = useState<(number | null)[]>(() =>
    initialSlots(RIDDLES[0].answer.length),
  );
  const [gridPerm, setGridPerm] = useState<number[]>(initialGridPerm);

  const [showConfetti, setShowConfetti] = useState(false);
  const [wrongHint, setWrongHint] = useState(false);
  const [adLoading, setAdLoading] = useState(false);

  const riddle = RIDDLES[gameState.currentLevel % RIDDLES.length];
  const answerLength = riddle.answer.length;

  const tiles = useMemo(
    () => riddle.letters.map((char, id) => ({ id, char })),
    [riddle.letters],
  );

  useEffect(() => {
    setSlotTileId(initialSlots(riddle.answer.length));
    setGridPerm(initialGridPerm());
  }, [gameState.currentLevel, riddle.answer.length]);

  const usedTileIds = useMemo(
    () => new Set(slotTileId.filter((x): x is number => x !== null)),
    [slotTileId],
  );

  const currentWord = useMemo(
    () =>
      slotTileId
        .map((tid) => (tid === null ? "" : tiles[tid]?.char ?? ""))
        .join(""),
    [slotTileId, tiles],
  );

  const isAnswerComplete = slotTileId.every((x) => x !== null);
  const isAnswerCorrect = currentWord === riddle.answer;

  const handleConfettiDone = useCallback(() => {
    setShowConfetti(false);
  }, []);

  const handleCellClick = (cellIndex: number) => {
    const tileId = gridPerm[cellIndex];
    if (tileId === undefined || usedTileIds.has(tileId)) return;
    const slotIdx = slotTileId.findIndex((s) => s === null);
    if (slotIdx === -1) return;
    setSlotTileId((prev) => {
      const next = [...prev];
      next[slotIdx] = tileId;
      return next;
    });
  };

  const handleRemoveLetter = () => {
    setSlotTileId((prev) => {
      const last = [...prev].map((v, i) => (v !== null ? i : -1)).filter((i) => i >= 0).pop();
      if (last === undefined) return prev;
      const next = [...prev];
      next[last] = null;
      return next;
    });
  };

  const handleSubmitAnswer = () => {
    if (!isAnswerCorrect) {
      setWrongHint(true);
      window.setTimeout(() => setWrongHint(false), 1600);
      return;
    }
    const starsEarned = Math.max(1, 4 - Math.floor(answerLength / 2));
    setShowConfetti(true);
    setGameState((prev) => ({
      ...prev,
      showSuccessModal: true,
      score: prev.score + starsEarned * 10,
      gems: prev.gems + 2,
      starsEarned,
    }));
  };

  const handleNextLevel = () => {
    setGameState((prev) => ({
      ...prev,
      currentLevel: prev.currentLevel + 1,
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
    setAdLoading(false);
  };

  const handleShuffleGrid = () => {
    setGridPerm((prev) => {
      const next = [...prev];
      const used = new Set(slotTileId.filter((x): x is number => x !== null));
      const freeCells = next
        .map((tid, cell) => (!used.has(tid) ? cell : -1))
        .filter((c) => c >= 0);
      const ids = freeCells.map((c) => next[c]!);
      shuffleInPlace(ids);
      freeCells.forEach((cell, i) => {
        next[cell] = ids[i]!;
      });
      return next;
    });
  };

  const handleUseHelp = (type: "reveal" | "shuffle") => {
    if (type === "reveal" && gameState.gems >= 5) {
      const emptySlots = slotTileId
        .map((v, i) => (v === null ? i : -1))
        .filter((i) => i >= 0);
      if (emptySlots.length === 0) {
        handleCloseModal();
        return;
      }
      const pos = emptySlots[Math.floor(Math.random() * emptySlots.length)]!;
      const need = riddle.answer[pos]!;
      const used = new Set(slotTileId.filter((x): x is number => x !== null));
      const candidates = gridPerm
        .map((tid, cell) => ({ tid, cell }))
        .filter(({ tid }) => !used.has(tid) && tiles[tid]?.char === need);
      if (candidates.length === 0) {
        handleCloseModal();
        return;
      }
      const pick = candidates[Math.floor(Math.random() * candidates.length)]!;
      setSlotTileId((prev) => {
        const next = [...prev];
        next[pos] = pick.tid;
        return next;
      });
      setGameState((prev) => ({ ...prev, gems: prev.gems - 5 }));
      handleCloseModal();
    } else if (type === "shuffle" && gameState.gems >= 3) {
      handleShuffleGrid();
      setGameState((prev) => ({ ...prev, gems: prev.gems - 3 }));
      handleCloseModal();
    }
  };

  const simulateWatchAd = () => {
    setAdLoading(true);
    window.setTimeout(() => {
      setGameState((prev) => ({ ...prev, gems: prev.gems + 5, showGemModal: false }));
      setAdLoading(false);
    }, 1600);
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-gradient-to-b from-sky-300 via-sky-200 to-green-300 p-4 md:p-6 lg:p-8">
      <Confetti isActive={showConfetti} onComplete={handleConfettiDone} />

      <div className="w-full max-w-md overflow-hidden rounded-3xl bg-gradient-to-b from-sky-100 to-sky-50 shadow-2xl">
        <div className="flex items-center justify-between bg-gradient-to-r from-blue-500 to-blue-600 px-4 py-4 text-white sm:px-6">
          <div className="flex items-center gap-2">
            <span className="text-2xl">⭐</span>
            <span className="text-lg font-bold">{gameState.score}</span>
          </div>
          <div className="text-center text-lg font-bold">Level {gameState.currentLevel + 1}</div>
          <button
            type="button"
            onClick={() => setGameState((p) => ({ ...p, showGemModal: true }))}
            className="flex items-center gap-2 rounded-lg px-1 py-1 transition hover:bg-white/10"
            aria-label="Асыл таш"
          >
            <span className="text-2xl">💎</span>
            <span className="text-lg font-bold">{gameState.gems}</span>
          </button>
        </div>

        <div className="space-y-6 p-4 sm:p-6">
          <div className="rounded-2xl bg-white p-4 text-center shadow-md sm:p-6">
            <p className="text-lg font-semibold text-gray-700">{riddle.question}</p>
          </div>

          <div className="flex flex-wrap justify-center gap-2 sm:gap-3">
            {Array.from({ length: answerLength }).map((_, idx) => (
              <div
                key={idx}
                className={`flex h-12 w-12 items-center justify-center rounded-lg border-2 border-blue-400 bg-white text-lg font-bold text-blue-600 shadow-md transition ${
                  wrongHint ? "animate-pulse border-red-400" : ""
                }`}
              >
                {slotTileId[idx] !== null ? tiles[slotTileId[idx]!]?.char : ""}
              </div>
            ))}
          </div>

          {wrongHint && (
            <p className="text-center text-sm font-semibold text-red-600">Туура эмес — кайра аракет кылыңыз</p>
          )}

          <div className="flex flex-wrap justify-center gap-3">
            <button
              type="button"
              onClick={() => setGameState((prev) => ({ ...prev, showHelpModal: true }))}
              className="rounded-full bg-blue-500 px-6 py-2 font-bold text-white shadow-md transition hover:bg-blue-600 active:scale-95"
            >
              💡 Жардам
            </button>
            {slotTileId.some((s) => s !== null) && (
              <button
                type="button"
                onClick={handleRemoveLetter}
                className="rounded-full bg-red-500 px-6 py-2 font-bold text-white shadow-md transition hover:bg-red-600 active:scale-95"
              >
                ✕ Өчүрүү
              </button>
            )}
          </div>

          <div className="rounded-2xl bg-gradient-to-b from-blue-400 to-blue-600 p-4 shadow-lg">
            <div className="grid grid-cols-4 gap-2">
              {gridPerm.map((tileId, cellIndex) => {
                const ch = tiles[tileId]?.char ?? "";
                const used = usedTileIds.has(tileId);
                return (
                  <button
                    key={cellIndex}
                    type="button"
                    onClick={() => !used && handleCellClick(cellIndex)}
                    disabled={used}
                    className={`h-12 rounded-lg text-lg font-bold text-white transition ${
                      used
                        ? "cursor-not-allowed bg-gray-400 opacity-50"
                        : "bg-blue-300 shadow-md hover:bg-blue-200 active:scale-95"
                    }`}
                  >
                    {ch}
                  </button>
                );
              })}
            </div>
          </div>

          {isAnswerComplete && (
            <button
              type="button"
              onClick={handleSubmitAnswer}
              className="w-full rounded-full bg-green-500 py-3 text-lg font-bold text-white shadow-lg transition hover:bg-green-600 active:scale-95"
            >
              ✓ Жибергүү
            </button>
          )}
        </div>
      </div>

      {gameState.showHelpModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
          <div className="max-w-sm rounded-2xl bg-white p-6 shadow-2xl">
            <div className="mb-4 flex items-center justify-between">
              <h2 className="text-xl font-bold text-gray-800">Жардам</h2>
              <button
                type="button"
                onClick={handleCloseModal}
                className="text-gray-500 hover:text-gray-700"
                aria-label="Жабуу"
              >
                <X size={24} />
              </button>
            </div>
            <p className="mb-4 text-sm text-gray-600">Асыл таш табышмалары</p>
            <div className="space-y-3">
              <button
                type="button"
                onClick={() => handleUseHelp("reveal")}
                disabled={gameState.gems < 5}
                className={`w-full rounded-lg py-2 font-bold transition ${
                  gameState.gems >= 5
                    ? "bg-blue-500 text-white hover:bg-blue-600"
                    : "cursor-not-allowed bg-gray-300 text-gray-500"
                }`}
              >
                💡 Бир тамгасын ачуу (5 💎)
              </button>
              <button
                type="button"
                onClick={() => handleUseHelp("shuffle")}
                disabled={gameState.gems < 3}
                className={`w-full rounded-lg py-2 font-bold transition ${
                  gameState.gems >= 3
                    ? "bg-blue-500 text-white hover:bg-blue-600"
                    : "cursor-not-allowed bg-gray-300 text-gray-500"
                }`}
              >
                🔄 Тамгаларды аралаштыруу (3 💎)
              </button>
            </div>
          </div>
        </div>
      )}

      {gameState.showGemModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
          <div className="max-w-sm rounded-2xl bg-white p-6 text-center shadow-2xl">
            <div className="mb-4 flex items-center justify-between text-left">
              <h2 className="text-xl font-bold text-gray-800">Асыл таш</h2>
              <button
                type="button"
                onClick={handleCloseModal}
                className="text-gray-500 hover:text-gray-700"
                aria-label="Жабуу"
              >
                <X size={24} />
              </button>
            </div>
            <p className="mb-4 text-4xl">💎</p>
            <p className="mb-6 text-gray-600">Видео көрүп, 5 асыл таш алыңыз!</p>
            <div className="space-y-3">
              <button
                type="button"
                disabled={adLoading}
                onClick={simulateWatchAd}
                className="w-full rounded-lg bg-blue-500 py-2 font-bold text-white transition hover:bg-blue-600 disabled:opacity-60"
              >
                {adLoading ? "Күтө туруңуз…" : "▶ Асыл змес"}
              </button>
              <button
                type="button"
                onClick={handleCloseModal}
                className="w-full rounded-lg bg-gray-200 py-2 font-bold text-gray-800 transition hover:bg-gray-300"
              >
                Мекул
              </button>
            </div>
          </div>
        </div>
      )}

      {gameState.showSuccessModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
          <div className="max-w-sm rounded-2xl border-4 border-blue-500 bg-white p-8 text-center shadow-2xl">
            <div className="mb-4 animate-pulse text-5xl">🎉</div>
            <div className="mb-4 flex justify-center gap-2">
              {Array.from({ length: 3 }).map((_, idx) => (
                <div
                  key={idx}
                  className={`text-3xl transition-all ${
                    idx < gameState.starsEarned ? "animate-bounce" : "opacity-30"
                  }`}
                >
                  ⭐
                </div>
              ))}
            </div>
            <h2 className="mb-2 text-2xl font-bold text-gray-800">БУРКУТ</h2>
            <p className="mb-2 text-gray-600">Туура жооп!</p>
            <p className="mb-6 font-bold text-blue-600">
              +{gameState.starsEarned * 10} ⭐ +2 💎
            </p>
            <button
              type="button"
              onClick={handleNextLevel}
              className="w-full rounded-lg bg-blue-500 py-3 text-lg font-bold text-white transition hover:bg-blue-600 active:scale-95"
            >
              Улантуу →
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
