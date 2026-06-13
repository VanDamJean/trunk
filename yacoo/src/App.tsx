import { useEffect, useRef, useState } from 'react';
import { DiceRow } from './components/DiceRow';
import { GameLog } from './components/GameLog';
import { GameOver } from './components/GameOver';
import { ScoreCard } from './components/ScoreCard';
import { TurnPanel } from './components/TurnPanel';
import { playBotTurn } from './bot/playBotTurn';
import { CATEGORY_INFO } from './game/categories';
import { rollAllDice } from './game/dice';
import { totalScore } from './game/scoring';
import { createInitialGame, getWinner, rollDice, scoreTurn, toggleHold } from './game/state';
import type { CategoryId, Dice } from './game/categories';
import type { GameResult, GameState } from './game/types';

declare global {
  interface Window {
    __YACOO_PRELOADED_STATE__?: GameState;
    __YACOO_TEST_ROLLS__?: Dice[];
  }
}

export function App() {
  const [game, setGame] = useState(() => window.__YACOO_PRELOADED_STATE__ ?? createInitialGame());
  const [error, setError] = useState<string | null>(null);
  const [isRolling, setIsRolling] = useState(false);
  const prevRollCount = useRef(game.rollCount);
  const activePlayer = game.players[game.activePlayer];
  const canRoll = game.phase !== 'gameOver' && game.rollCount < 3;
  const canToggleDice = game.phase === 'selectingCategory' && game.rollCount > 0;
  const canScore = game.phase === 'selectingCategory' && game.rollCount > 0 && game.dice !== null;
  const humanScore = totalScore(game.players.human.scorecard);
  const botScore = totalScore(game.players.bot.scorecard);
  const winner = getWinner(game);

  useEffect(() => {
    if (game.rollCount !== prevRollCount.current) {
      prevRollCount.current = game.rollCount;
      if (game.rollCount > 0) {
        setIsRolling(true);
        const timer = setTimeout(() => setIsRolling(false), 650);
        return () => clearTimeout(timer);
      }
    }
  }, [game.rollCount]);

  function applyResult(result: GameResult) {
    setGame(result.state);
    setError(result.ok ? null : result.error ?? '알 수 없는 오류입니다.');
  }

  function handleReset() {
    setGame(createInitialGame());
    setError(null);
  }

  function nextRoll(): Dice {
    return window.__YACOO_TEST_ROLLS__?.shift() ?? rollAllDice();
  }

  function handleScore(category: CategoryId) {
    const humanResult = scoreTurn(game, category);
    if (!humanResult.ok || humanResult.state.activePlayer !== 'bot' || humanResult.state.phase === 'gameOver') {
      applyResult(humanResult);
      return;
    }

    applyResult(playBotTurn(humanResult.state, nextRoll));
  }

  return (
    <main className="app-shell">
      <section className="hero-card" aria-labelledby="app-title">
        <p className="eyebrow">Human vs Bot</p>
        <h1 id="app-title">야추</h1>
        <p className="intro">주사위를 굴리고, 필요한 눈을 고정한 뒤, 점수 칸을 골라 기록하세요.</p>
      </section>

      <div className="game-grid">
        <div className="play-column">
          <TurnPanel
            activePlayerName={activePlayer.name}
            rollCount={game.rollCount}
            canRoll={canRoll}
            error={error}
            onRoll={() => applyResult(rollDice(game, nextRoll))}
            onReset={handleReset}
          />
          <DiceRow dice={game.dice} held={game.held} canToggle={canToggleDice} isRolling={isRolling} onToggle={(index) => applyResult(toggleHold(game, index))} />
          <GameOver winner={winner} humanScore={humanScore} botScore={botScore} onReset={handleReset} />
          <GameLog entries={game.log} />
        </div>

        <ScoreCard
          players={game.players}
          activePlayer={game.activePlayer}
          dice={game.dice}
          rollCount={game.rollCount}
          canScore={canScore}
          categoryInfo={CATEGORY_INFO}
          onScore={handleScore}
        />
      </div>
    </main>
  );
}
