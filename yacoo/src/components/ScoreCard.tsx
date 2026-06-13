import { CATEGORY_ORDER, type CategoryId, type CategoryInfo, type Dice } from '../game/categories';
import { scoreCategory, totalScore } from '../game/scoring';
import type { GameState, PlayerId } from '../game/types';

type ScoreCardProps = {
  players: GameState['players'];
  activePlayer: PlayerId;
  dice: Dice | null;
  rollCount: number;
  canScore: boolean;
  categoryInfo: Record<CategoryId, CategoryInfo>;
  onScore: (category: CategoryId) => void;
};

export function ScoreCard({ players, activePlayer, dice, rollCount, canScore, categoryInfo, onScore }: ScoreCardProps) {
  return (
    <section className="panel score-panel" aria-labelledby="score-title">
      <div className="panel-heading score-heading">
        <div>
          <p className="eyebrow">Score</p>
          <h2 id="score-title">점수판</h2>
        </div>
        <div className="totals" aria-label="현재 총점">
          <span>나 {totalScore(players.human.scorecard)}점</span>
          <span>봇 {totalScore(players.bot.scorecard)}점</span>
        </div>
      </div>

      <div className="score-table" role="table" aria-label="야추 점수판">
        <div className="score-row score-row-head" role="row">
          <span role="columnheader">칸</span>
          <span role="columnheader">나</span>
          <span role="columnheader">봇</span>
          <span role="columnheader">선택</span>
        </div>
        {CATEGORY_ORDER.map((category) => {
          const humanScore = players.human.scorecard[category];
          const botScore = players.bot.scorecard[category];
          const activeScore = players[activePlayer].scorecard[category];
          const previewScore = dice ? scoreCategory(category, dice) : null;
          const disabled = !canScore || rollCount === 0 || activeScore !== undefined;
          const info = categoryInfo[category];

          return (
            <div className="score-row" role="row" key={category}>
              <div className="category-copy" role="cell">
                <strong>{info.label}</strong>
                <span>{info.description}</span>
              </div>
              <span role="cell">{humanScore ?? '-'}</span>
              <span role="cell">{botScore ?? '-'}</span>
              <button
                aria-label={`${info.label} ${activeScore !== undefined ? '사용함' : previewScore === null ? '대기' : `${previewScore}점 기록`}`}
                className="score-button"
                disabled={disabled}
                type="button"
                onClick={() => onScore(category)}
              >
                {activeScore !== undefined ? '사용함' : previewScore === null ? '대기' : `${previewScore}점 기록`}
              </button>
            </div>
          );
        })}
      </div>
    </section>
  );
}
