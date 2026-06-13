import { useState } from 'react';
import { ChunkyBadge, EquipmentBadge, HeroPaperdoll } from '../components/ui';
import { equipmentBonusLabel, getEquipmentItems } from '../game/equipment';
import type { EquipmentSet, EquipmentSlot } from '../game/types';

export function EquipmentScreen({ equipment }: { equipment: EquipmentSet }) {
  const equipmentItems = getEquipmentItems(equipment);
  const [selectedSlot, setSelectedSlot] = useState<EquipmentSlot | null>(null);

  const selectedItem = selectedSlot ? equipment[selectedSlot] : null;

  function toggleSlot(slot: EquipmentSlot) {
    setSelectedSlot((prev) => (prev === slot ? null : slot));
  }

  return (
    <>
      <h1 className="screen-title">Equipment</h1>

      {/* ── Paperdoll card ── */}
      <div className="equipment-paperdoll-card">
        <div className="paperdoll-left">
          <HeroPaperdoll equipment={equipment} highlightSlot={selectedSlot} size="md" />
        </div>

        <div className="paperdoll-slots">
          {equipmentItems.map((item) => (
            <button
              key={item.id}
              type="button"
              className={`paperdoll-slot${selectedSlot === item.slot ? ' selected' : ''}`}
              onClick={() => toggleSlot(item.slot)}
              aria-pressed={selectedSlot === item.slot}
            >
              <EquipmentBadge kind={item.slot} />
              <div className="paperdoll-slot-info">
                <span className="paperdoll-slot-name">{item.name}</span>
                <span className="paperdoll-slot-level">Lv {item.level}</span>
              </div>
            </button>
          ))}
        </div>
      </div>

      {/* ── Selected item detail ── */}
      {selectedItem && (
        <div className="equipment-detail-card">
          <div className="equipment-detail-header">
            <EquipmentBadge kind={selectedItem.slot} className="gear-icon" />
            <div>
              <h2 style={{ margin: 0 }}>{selectedItem.name}</h2>
              <p className="muted" style={{ margin: '2px 0 0' }}>Slot: {selectedItem.slot} · Level {selectedItem.level}</p>
            </div>
            <ChunkyBadge>Lv {selectedItem.level}</ChunkyBadge>
          </div>
          <strong>{equipmentBonusLabel(selectedItem)}</strong>
        </div>
      )}

      {/* ── Full gear list ── */}
      <div className="gear-list equipment-card-list">
        {equipmentItems.map((item) => (
          <button
            key={item.id}
            type="button"
            className={`card equipment-item-card${selectedSlot === item.slot ? ' selected' : ''}`}
            style={{ textAlign: 'left', width: '100%', cursor: 'pointer', background: selectedSlot === item.slot ? 'rgba(255,240,185,0.8)' : undefined }}
            onClick={() => toggleSlot(item.slot)}
          >
            <div className="gear-row">
              <EquipmentBadge kind={item.slot} className="gear-icon" />
              <div>
                <h2 style={{ margin: 0 }}>{item.name}</h2>
                <p className="muted" style={{ margin: '2px 0 0' }}>Slot: {item.slot} · Level {item.level}</p>
                <strong>{equipmentBonusLabel(item)}</strong>
              </div>
              <ChunkyBadge>Lv {item.level}</ChunkyBadge>
            </div>
          </button>
        ))}
      </div>
    </>
  );
}
