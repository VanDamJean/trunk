type TurnPanelProps = {
  activePlayerName: string;
  rollCount: number;
  canRoll: boolean;
  error: string | null;
  onRoll: () => void;
  onReset: () => void;
};

export function TurnPanel({ activePlayerName, rollCount, canRoll, error, onRoll, onReset }: TurnPanelProps) {
  const rollsLeft = Math.max(0, 3 - rollCount);

  return (
    <section className="panel turn-panel" aria-labelledby="turn-title">
      <div className="panel-heading">
        <p className="eyebrow">Turn</p>
        <h2 id="turn-title">진행</h2>
        <p className="rolls-left" aria-label={`남은 굴림 ${rollsLeft}회`}>
          ⚂ {rollsLeft} left
        </p>
      </div>
      <dl className="turn-stats">
        <div>
          <dt>현재 차례</dt>
          <dd>{activePlayerName}</dd>
        </div>
        <div>
          <dt>굴림</dt>
          <dd>{rollCount} / 3</dd>
        </div>
      </dl>
      <div className="turn-actions">
        <button className="primary-button" disabled={!canRoll} type="button" onClick={onRoll}>
          굴리기
        </button>
        <button className="ghost-button" type="button" onClick={onReset}>
          새 게임
        </button>
      </div>
      {error ? <p className="error-message" role="alert">{error}</p> : null}
    </section>
  );
}
