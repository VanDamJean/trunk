import { useState } from 'react';
import { ArtCard, Card, ChunkyBadge, EquipmentBadge, PrimaryButton, RedDotMarker } from '../components/ui';
import { canUpgrade, equipmentBonusLabel, getEquipmentItems, upgradeCost } from '../game/equipment';
import type { EquipmentSet, EquipmentSlot } from '../game/types';

export function UpgradeScreen({ equipment, coins, onUpgrade }: { equipment: EquipmentSet; coins: number; onUpgrade: (slot: EquipmentSlot) => void }) {
  const [selectedSlot, setSelectedSlot] = useState<EquipmentSlot>('weapon');
  const equipmentItems = getEquipmentItems(equipment);
  const selected = equipment[selectedSlot];
  const cost = upgradeCost(selected);
  const blocked = !canUpgrade(selected, coins);
  const hasUpgradeReady = equipmentItems.some((item) => canUpgrade(item, coins));
  const message = selected.level >= 10 ? 'Max Level' : coins < cost ? 'Insufficient coins' : 'Ready to upgrade';

  return (
    <>
      <ArtCard className="upgrade-header-card">
        <div>
          <h1 className="screen-title">Upgrade</h1>
          <p className="muted">Coins: <strong>{coins}</strong></p>
        </div>
        {hasUpgradeReady && (
          <div className="upgrade-prompt" role="status">
            <RedDotMarker label="Upgrade ready" />
            <span>Upgrade ready</span>
          </div>
        )}
      </ArtCard>

      <Card className="upgrade-select-card">
        <div className="section-heading-row">
          <h2>Select Gear</h2>
          <ChunkyBadge>{message}</ChunkyBadge>
        </div>
        <div className="upgrade-grid">
          {equipmentItems.map((item) => {
            const itemCanUpgrade = canUpgrade(item, coins);
            return (
              <PrimaryButton
                key={item.id}
                className={item.slot === selectedSlot ? 'upgrade-choice selected' : 'secondary upgrade-choice'}
                onClick={() => setSelectedSlot(item.slot)}
              >
                <EquipmentBadge kind={item.slot} className="gear-icon" />
                <span>{item.name}</span>
                {itemCanUpgrade && <RedDotMarker label={`${item.name} upgrade ready`} />}
              </PrimaryButton>
            );
          })}
        </div>
      </Card>

      <Card className="upgrade-detail-card">
        <div className="upgrade-detail-top">
          <EquipmentBadge kind={selected.slot} className="gear-icon" />
          <div>
            <h2>{selected.name}</h2>
            <p className="muted">Current level: <strong>{selected.level}</strong></p>
          </div>
        </div>
        <div className="upgrade-stat-panel">
          <span>{equipmentBonusLabel(selected)}</span>
          <strong>Cost: {cost} coins</strong>
        </div>
        <p className="message">{message}</p>
        <PrimaryButton disabled={blocked} onClick={() => onUpgrade(selectedSlot)}>Upgrade</PrimaryButton>
      </Card>
    </>
  );
}
