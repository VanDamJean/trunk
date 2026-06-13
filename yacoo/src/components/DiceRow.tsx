import { useEffect, useState } from 'react';
import type { Dice } from '../game/categories';
import type { DieValue } from '../game/categories';

type DiceRowProps = {
  dice: Dice | null;
  held: readonly [boolean, boolean, boolean, boolean, boolean];
  canToggle: boolean;
  isRolling: boolean;
  onToggle: (index: number) => void;
};

const EMPTY_DICE = ['?', '?', '?', '?', '?'] as const;
const ROLL_DURATION = 650;
const TOGGLE_INTERVAL = 60;
const FACE_VALUES = [1, 2, 3, 4, 5, 6] as const;
const PIP_POSITIONS: Record<DieValue, readonly string[]> = {
  1: ['center'],
  2: ['top-left', 'bottom-right'],
  3: ['top-left', 'center', 'bottom-right'],
  4: ['top-left', 'top-right', 'bottom-left', 'bottom-right'],
  5: ['top-left', 'top-right', 'center', 'bottom-left', 'bottom-right'],
  6: ['top-left', 'top-right', 'middle-left', 'middle-right', 'bottom-left', 'bottom-right']
};

function randomDie(): number {
  return Math.floor(Math.random() * 6) + 1;
}

function isDieValue(value: string | number): value is DieValue {
  return typeof value === 'number' && value >= 1 && value <= 6;
}

function DieFace({ value }: { value: DieValue }) {
  return (
    <span className="die-face" data-face={value}>
      {PIP_POSITIONS[value].map((position) => (
        <span className="die-pip" data-position={position} key={`${value}-${position}`} />
      ))}
    </span>
  );
}

export function DiceRow({ dice, held, canToggle, isRolling, onToggle }: DiceRowProps) {
  const realValues = dice ?? EMPTY_DICE;
  const [displayValues, setDisplayValues] = useState<readonly (string | number)[]>(realValues);

  useEffect(() => {
    if (!isRolling) {
      setDisplayValues(realValues);
      return;
    }

    const ids: ReturnType<typeof setTimeout>[] = [];
    for (let i = 0; i < realValues.length; i++) {
      if (held[i]) continue;
      const dieIndex = i;
      const id = setInterval(() => {
        setDisplayValues((prev) => {
          const next = [...prev];
          next[dieIndex] = randomDie();
          return next;
        });
      }, TOGGLE_INTERVAL);
      ids.push(id);
    }

    const stop = setTimeout(() => {
      ids.forEach((id) => clearInterval(id));
      setDisplayValues(realValues);
    }, ROLL_DURATION);
    ids.push(stop);

    return () => {
      ids.forEach((id) => clearTimeout(id));
      ids.forEach((id) => clearInterval(id));
    };
  }, [held, isRolling, realValues]);

  const values = isRolling ? displayValues : realValues;

  return (
    <section className="panel dice-panel" aria-labelledby="dice-title">
      <div className="panel-heading">
        <p className="eyebrow">Dice</p>
        <h2 id="dice-title">주사위</h2>
      </div>
      <div className="dice-row" role="group" aria-label="주사위 고정 선택">
        {values.map((value, index) => {
          const isThisDieRolling = isRolling && !held[index];
          const dieValue = isDieValue(value) ? value : null;
          return (
            <button
              className="die-button"
              data-held={held[index] ? 'true' : 'false'}
              data-rolling={isThisDieRolling ? 'true' : 'false'}
              data-value={dieValue ?? 'empty'}
              disabled={!canToggle}
              key={`die-${index}`}
              type="button"
              aria-label={`${index + 1}번 주사위 ${value}`}
              aria-pressed={held[index]}
              onClick={() => onToggle(index)}
            >
              <span className="die-stage" aria-hidden="true">
                {dieValue === null ? (
                  <span className="die-placeholder">?</span>
                ) : (
                  <span className={`die-cube die-cube-${dieValue}`} data-value={dieValue}>
                    {FACE_VALUES.map((faceValue) => (
                      <DieFace key={faceValue} value={faceValue} />
                    ))}
                  </span>
                )}
              </span>
              <span className="die-state">{held[index] ? '고정' : '선택'}</span>
            </button>
          );
        })}
      </div>
    </section>
  );
}
