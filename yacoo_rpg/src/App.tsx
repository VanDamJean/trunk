import { Shell, StatPill } from './components/ui';
import { getHeroStats } from './game/equipment';
import { useGameState } from './hooks/useGameState';
import { CombatScreen } from './screens/CombatScreen';
import { EquipmentScreen } from './screens/EquipmentScreen';
import { HomeScreen } from './screens/HomeScreen';
import { ResultScreen } from './screens/ResultScreen';
import { UpgradeScreen } from './screens/UpgradeScreen';

export default function App() {
  const game = useGameState();
  const hero = getHeroStats(game.meta.equipment);

  const stats = (
    <>
      <StatPill label="Stage" value={game.run?.chapter ?? game.meta.bestChapter} className="stat-pill--primary" />
      <div className="top-stats-right">
        <StatPill label="Coins" value={game.meta.coins} />
        <StatPill label="Power" value={hero.power} />
      </div>
    </>
  );

  return (
    <Shell current={game.screen} onNavigate={game.navigate} stats={stats}>
      {game.screen === 'home' && (
        <>
          <HomeScreen save={game.save} onStart={game.startCombat} onNavigate={game.navigate} onReset={game.reset} />
          {import.meta.env.DEV && (
            <section className="card dev-panel">
              <h2>DEV QA</h2>
              <button className="primary-button secondary" type="button" onClick={() => game.grantCoins(100)}>Grant 100 Coins</button>
              <button className="primary-button secondary" type="button" onClick={() => game.forceResult('win')}>Force Win Result</button>
              <button className="primary-button danger" type="button" onClick={() => game.forceResult('loss')}>Force Loss Result</button>
            </section>
          )}
        </>
      )}
      {game.screen === 'combat' && (
        <CombatScreen
          stage={game.run?.chapter ?? game.save.stage}
          equipment={game.meta.equipment}
          run={game.run ?? undefined}
          onFinish={game.finishCombat}
        />
      )}
      {game.screen === 'equipment' && <EquipmentScreen equipment={game.meta.equipment} />}
      {game.screen === 'upgrade' && (
        <UpgradeScreen
          equipment={game.meta.equipment}
          coins={game.meta.coins}
          onUpgrade={game.upgrade}
        />
      )}
      {(game.screen === 'result' || game.screen === 'runResult') && (
        <ResultScreen result={game.meta.lastCombatResult} onClaim={game.claimReward} onHome={() => game.navigate('home')} />
      )}
      {/* runMap / rewardPick screens will be added in M2 */}
      {game.screen === 'runMap' && (
        <section className="card">
          <h1 className="screen-title">Chapter {game.run?.chapter}</h1>
          <p className="muted">Run map coming in M2. For now, go fight!</p>
          <button className="primary-button" type="button" onClick={() => game.navigate('combat')}>Enter Combat</button>
        </section>
      )}
    </Shell>
  );
}
