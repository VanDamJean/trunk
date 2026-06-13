import type { Winner } from '../game/types';

type GameOverProps = {
  winner: Winner | null;
  humanScore: number;
  botScore: number;
  onReset: () => void;
};

export function GameOver({ winner, humanScore, botScore, onReset }: GameOverProps) {
  if (!winner) {
    return null;
  }

  const result = winner === 'tie' ? '무승부입니다' : `${winner === 'human' ? '나' : '봇'} 승리입니다`;

  return (
    <section className="panel game-over-panel" aria-labelledby="game-over-title">
      <p className="eyebrow">Game Over</p>
      <h2 id="game-over-title">{result}</h2>
      <p>
        최종 점수는 나 {humanScore}점, 봇 {botScore}점입니다.
      </p>
      <button className="primary-button" type="button" onClick={onReset}>
        새 게임
      </button>
    </section>
  );
}
