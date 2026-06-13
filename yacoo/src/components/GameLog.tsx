type GameLogProps = {
  entries: readonly string[];
};

export function GameLog({ entries }: GameLogProps) {
  return (
    <section className="panel log-panel" aria-labelledby="log-title">
      <div className="panel-heading">
        <p className="eyebrow">Log</p>
        <h2 id="log-title">기록</h2>
      </div>
      <ol className="game-log" aria-live="polite">
        {entries.map((entry, index) => (
          <li key={`${index}-${entry}`}>{entry}</li>
        ))}
      </ol>
    </section>
  );
}
