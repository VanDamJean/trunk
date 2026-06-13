import { ChunkyBadge, EquipmentBadge, HeroPaperdoll, LayeredArena, PrimaryButton, RedDotMarker, RewardBadge, RoundAnimalHero } from '../components/ui';
import { canUpgrade, getEquipmentItems, getHeroStats } from '../game/equipment';
import type { GameSave, Screen } from '../game/types';

interface HomeScreenProps {
  save: GameSave;
  onStart: () => void;
  onNavigate: (screen: Screen) => void;
  onReset: () => void;
}

export function HomeScreen({ save, onStart, onNavigate, onReset }: HomeScreenProps) {
  const stats = getHeroStats(save.equipment);
  const equipmentItems = getEquipmentItems(save.equipment);
  const hasUpgradeReady = equipmentItems.some((item) => canUpgrade(item, save.coins));

  return (
    <div className="home-screen">

      {/* ── Hero arena (fills flexible top space) ── */}
      <LayeredArena className="home-arena-fill">
        <div className="home-arena-layout">
          <div className="home-hero-label">
            <h1 className="screen-title home-title">Home</h1>
            <RewardBadge>Stage {save.stage}</RewardBadge>
          </div>
          <HeroPaperdoll equipment={save.equipment} size="md" />
          <div className="power-number" aria-label={`Power ${stats.power}`}>
            <span>Power</span>
            <strong>{stats.power}</strong>
          </div>
        </div>
      </LayeredArena>

      {/* ── Compact action panel ── */}
      <div className="home-action-section">

        {/* Stage route */}
        <div className="stage-route home-stage-route" aria-label={`Stage ${save.stage} route`}>
          <span className="stage-node active">{save.stage}</span>
          <span className="stage-path-line" aria-hidden="true" />
          <span className="stage-node">{save.stage + 1}</span>
          <span className="stage-path-line" aria-hidden="true" />
          <span className="stage-node boss">Boss</span>
        </div>

        {/* Primary CTA */}
        <PrimaryButton className="start-combat-button" onClick={onStart}>Start Combat</PrimaryButton>

        {hasUpgradeReady && (
          <div className="upgrade-prompt" role="status">
            <RedDotMarker label="Upgrade ready" />
            <span>Gear upgrade ready!</span>
          </div>
        )}

        {/* Secondary actions */}
        <div className="home-nav-row">
          <PrimaryButton className="secondary" onClick={() => onNavigate('equipment')}>Equipment</PrimaryButton>
          <PrimaryButton className="secondary upgrade-nav-button" onClick={() => onNavigate('upgrade')}>
            Upgrade
            {hasUpgradeReady && <RedDotMarker label="Upgrade ready" />}
          </PrimaryButton>
        </div>

        {/* Gear strip + coins */}
        <div className="home-gear-row">
          {equipmentItems.map((item) => (
            <div className="gear-chip home-gear-chip" key={item.id}>
              <EquipmentBadge kind={item.slot} />
              <span className="muted">Lv {item.level}</span>
            </div>
          ))}
          <ChunkyBadge className="home-coins-badge">{save.coins} Coins</ChunkyBadge>
        </div>

        {/* Reset */}
        <button className="home-reset-link" type="button" onClick={onReset}>Reset</button>
      </div>

    </div>
  );
}
