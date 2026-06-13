import { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { ChunkyBadge, DamagePop, Dice, ForestMonster, LayeredArena, PrimaryButton, ProgressBar, RoundAnimalHero } from '../components/ui';
import { ATTACK_CATEGORIES, COMBAT_TIMING } from '../game/constants';
import { calculateEnemyDamage, calculateHeroAutoDamage, calculateUltimateCategoryDamage, clampHp, createEnemy } from '../game/combat';
import { getHeroStats } from '../game/equipment';
import { evaluateHand, getValidAttackCategories, rollDice } from '../game/yahtzee';
import type { CombatOutcome, DieValue, EquipmentSet, RunState, YahtzeeAttackCategory } from '../game/types';

type TurnPhase = 'choosing' | 'enemyTurn' | 'over';
type RollPhase = 'idle' | 'rolling' | 'settled';
type StagePhase = 'approach' | 'fight' | 'victory' | 'advance';

// In Vitest, animation is instant so state updates are synchronous and testable.
const ROLL_ANIM_MS: number = import.meta.env.VITEST ? 0 : 620;
const ROLL_TICK_MS = 55;

interface CombatScreenProps {
  stage: number;
  equipment: EquipmentSet;
  run?: RunState;
  onFinish: (outcome: CombatOutcome, handUsed?: YahtzeeAttackCategory) => void;
  enemyTurnDelayMs?: number;
}

function randomDie(): DieValue {
  return (Math.floor(Math.random() * 6) + 1) as DieValue;
}

export function CombatScreen({
  stage,
  equipment,
  run,
  onFinish,
  enemyTurnDelayMs = COMBAT_TIMING.enemyTurnDelayMs
}: CombatScreenProps) {
  const diceCount = run?.diceCount ?? COMBAT_TIMING.diceCount;
  const maxRolls = run?.maxRolls ?? COMBAT_TIMING.maxRolls;

  const enemy = useMemo(() => createEnemy(stage), [stage]);
  const hero = useMemo(() => getHeroStats(equipment), [equipment]);
  const autoDamage = useMemo(() => calculateHeroAutoDamage(equipment), [equipment]);
  const enemyStrikeDamage = useMemo(() => calculateEnemyDamage(enemy, equipment), [enemy, equipment]);

  const [heroHp, setHeroHp] = useState(run?.hp ?? hero.maxHp);
  const [enemyHp, setEnemyHp] = useState(enemy.maxHp);

  // settled = final dice values for game logic
  const [dice, setDice] = useState<DieValue[]>([]);
  // displayDice = values shown on screen (animates during rolling)
  const [displayDice, setDisplayDice] = useState<DieValue[]>(Array(diceCount).fill(1) as DieValue[]);

  const [held, setHeld] = useState<boolean[]>(Array(diceCount).fill(false));
  const [rollsLeft, setRollsLeft] = useState(maxRolls);
  const [phase, setPhase] = useState<TurnPhase>('choosing');
  const [rollPhase, setRollPhase] = useState<RollPhase>('idle');
  const [settling, setSettling] = useState(false);
  const [stagePhase, setStagePhase] = useState<StagePhase>('approach');
  const [lastHand, setLastHand] = useState<YahtzeeAttackCategory | undefined>();
  const [feedback, setFeedback] = useState('');
  const [feedbackTone, setFeedbackTone] = useState<'crit' | 'hit'>('crit');
  const [turnCount, setTurnCount] = useState(1);

  const onFinishRef = useRef(onFinish);
  onFinishRef.current = onFinish;

  const rollIntervalRef = useRef<ReturnType<typeof setInterval> | null>(null);
  const rollSettleRef = useRef<ReturnType<typeof setTimeout> | null>(null);

  const validCategories = useMemo(
    () => (rollPhase === 'settled' && dice.length === diceCount ? getValidAttackCategories(dice) : []),
    [dice, diceCount, rollPhase]
  );

  const clearRollTimers = useCallback(() => {
    if (rollIntervalRef.current) { clearInterval(rollIntervalRef.current); rollIntervalRef.current = null; }
    if (rollSettleRef.current) { clearTimeout(rollSettleRef.current); rollSettleRef.current = null; }
  }, []);

  useEffect(() => () => clearRollTimers(), [clearRollTimers]);

  const startTurn = useCallback(() => {
    clearRollTimers();
    setDice([]);
    setDisplayDice(Array(diceCount).fill(1) as DieValue[]);
    setHeld(Array(diceCount).fill(false));
    setRollsLeft(maxRolls);
    setPhase('choosing');
    setRollPhase('idle');
    setFeedback('');
  }, [diceCount, maxRolls, clearRollTimers]);

  useEffect(() => {
    // monster approach animation on first load
    setStagePhase('approach');
    const t = window.setTimeout(() => setStagePhase('fight'), 400);
    startTurn();
    return () => window.clearTimeout(t);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [stage]);

  function startRolling(previousDice: DieValue[], heldMask: boolean[]) {
    clearRollTimers();
    setRollPhase('rolling');

    // Instant settle in test environment — no async timers needed
    if (ROLL_ANIM_MS === 0) {
      const newDice = rollDice(previousDice, heldMask);
      setDice(newDice);
      setDisplayDice(newDice);
      setRollPhase('settled');
      return;
    }

    // Animate: held dice stay fixed, others show random values
    rollIntervalRef.current = setInterval(() => {
      setDisplayDice(
        Array.from({ length: diceCount }, (_, i) =>
          heldMask[i] ? previousDice[i] : randomDie()
        ) as DieValue[]
      );
    }, ROLL_TICK_MS);

    rollSettleRef.current = setTimeout(() => {
      clearRollTimers();
      const newDice = rollDice(previousDice, heldMask);
      setDice(newDice);
      setDisplayDice(newDice);
      setRollPhase('settled');
      // Trigger settle-bounce animation briefly
      setSettling(true);
      setTimeout(() => setSettling(false), 320);
    }, ROLL_ANIM_MS);
  }

  function handleInitialRoll() {
    if (rollPhase !== 'idle' || phase !== 'choosing') return;
    setRollsLeft((n) => n - 1);
    startRolling([], Array(diceCount).fill(false));
  }

  function reroll() {
    if (rollPhase !== 'settled' || rollsLeft <= 0 || phase !== 'choosing') return;
    setRollsLeft((n) => n - 1);
    startRolling(dice, held);
  }

  function toggleHeld(index: number) {
    if (rollPhase !== 'settled' || phase !== 'choosing') return;
    setHeld((cur) => cur.map((v, i) => (i === index ? !v : v)));
  }

  function resolveAttack(damage: number, hand?: YahtzeeAttackCategory) {
    const next = clampHp(enemyHp - damage);
    setEnemyHp(next);
    if (hand) setLastHand(hand);

    if (next <= 0) {
      setPhase('over');
      setStagePhase('victory');
      const label = hand ? (ATTACK_CATEGORIES.find(c => c.category === hand)?.label ?? hand) : '기본 공격';
      setFeedback(`${label} ${damage}! 승리!`);
      setFeedbackTone('crit');
      // brief victory anim before finishing
      const finishDelay = ROLL_ANIM_MS === 0 ? 0 : 600;
      const finTmr = window.setTimeout(() => {
        setStagePhase('advance');
        onFinishRef.current('win', hand ?? lastHand);
      }, finishDelay);
      // store timer for cleanup
      rollSettleRef.current = finTmr;
      return;
    }

    setFeedback(`${damage} 피해!`);
    setFeedbackTone('crit');
    setPhase('enemyTurn');
  }

  function diceAttack(category: YahtzeeAttackCategory) {
    if (phase !== 'choosing' || rollPhase !== 'settled' || !validCategories.includes(category)) return;
    resolveAttack(calculateUltimateCategoryDamage({ dice, category, equipment, run }), category);
  }

  function basicAttack() {
    if (phase !== 'choosing' || rollPhase === 'rolling') return;
    resolveAttack(autoDamage);
  }

  // Enemy turn: roll dice internally → evaluate hand → deal damage
  useEffect(() => {
    if (phase !== 'enemyTurn') return;
    const timer = window.setTimeout(() => {
      const enemyDice = rollDice([], []);
      const enemyHand = evaluateHand(enemyDice);
      const nextHeroHp = clampHp(heroHp - enemyStrikeDamage);
      setHeroHp(nextHeroHp);

      if (nextHeroHp <= 0) {
        setPhase('over');
        setFeedback(`${enemy.name} [${enemyHand.label}] ${enemyStrikeDamage} 피해. 패배!`);
        setFeedbackTone('hit');
        onFinishRef.current('loss');
        return;
      }

      setFeedback(`${enemy.name} [${enemyHand.label}] ${enemyStrikeDamage} 피해.`);
      setFeedbackTone('hit');
      setTurnCount((n) => n + 1);
      startTurn();
    }, enemyTurnDelayMs);
    return () => window.clearTimeout(timer);
  }, [phase, heroHp, enemyStrikeDamage, enemyTurnDelayMs, startTurn, enemy.name]);

  function forceFixture(diceFixture: DieValue[]) {
    if (phase === 'over') return;
    clearRollTimers();
    setDice(diceFixture);
    setDisplayDice(diceFixture);
    setHeld(Array(diceCount).fill(false));
    setRollsLeft(maxRolls - 1);
    setPhase('choosing');
    setRollPhase('settled');
    setFeedback('');
  }

  const isChoosing = phase === 'choosing';
  const isEnemyTurn = phase === 'enemyTurn';
  const isRolling = rollPhase === 'rolling';
  const isIdle = rollPhase === 'idle';
  const isSettled = rollPhase === 'settled';
  const canAttack = isChoosing && isSettled;
  const heroClass = stagePhase === 'advance' ? 'hero-advancing' : '';
  const monsterClass = stagePhase === 'approach' ? 'monster-entering' : stagePhase === 'victory' ? 'monster-defeated' : '';

  return (
    <div className="combat-screen">

      {/* ── HUD row ─────────────────────────────── */}
      <div className="combat-hud">
        <div className="combat-hud-left">
          <h1 className="combat-title">전투</h1>
          <span className="combat-stage-tag">Stage {stage}</span>
        </div>
        <ChunkyBadge>{isEnemyTurn ? '적 턴' : `턴 ${turnCount}`}</ChunkyBadge>
      </div>

      {/* ── 전투 영역 1/3 ───────────────────────── */}
      <div className="combat-battle-section">
        <LayeredArena className="combat-arena-compact">
          <div className="battle-scene battle-scene-compact">
            <div className={`combatant combatant-sm ${heroClass}`.trim()}>
              <RoundAnimalHero size="sm" title="Dice Cub" />
              <span>Cub</span>
            </div>
            <div className="battle-center-feedback battle-feedback-compact">
              {feedback && (
                <DamagePop value={feedback} tone={feedbackTone} key={`${feedback}-${turnCount}`} />
              )}
            </div>
            <div className={`combatant combatant-sm enemy-combatant ${monsterClass}`.trim()}>
              <ForestMonster size="sm" title={enemy.name} />
              <span>{enemy.name.replace(/^.*\s/, '')}</span>
            </div>
          </div>
        </LayeredArena>

        <div className="battle-hp-row">
          <ProgressBar label="내 HP" value={heroHp} max={hero.maxHp} />
          <ProgressBar label={enemy.name.replace(/^.*\s/, '')} value={enemyHp} max={enemy.maxHp} />
        </div>
      </div>

      {/* ── 주사위 영역 2/3 ─────────────────────── */}
      <div className="combat-dice-section">

        {/* 주사위 */}
        <div className="dice-grid dice-grid-compact">
          {Array.from({ length: diceCount }, (_, i) => (
            <Dice
              key={i}
              value={isIdle ? undefined : displayDice[i]}
              held={held[i]}
              rolling={isRolling && !held[i]}
              settling={settling && !held[i]}
              onClick={() => toggleHeld(i)}
              disabled={!canAttack || !dice[i]}
            />
          ))}
        </div>

        {/* 굴리기 / 리롤 버튼 */}
        {isIdle && (
          <PrimaryButton disabled={!isChoosing} onClick={handleInitialRoll}>
            🎲 주사위 굴리기
          </PrimaryButton>
        )}
        {isRolling && (
          <PrimaryButton disabled>굴리는 중…</PrimaryButton>
        )}
        {isSettled && (
          <PrimaryButton
            className="secondary"
            disabled={!isChoosing || rollsLeft <= 0}
            onClick={reroll}
          >
            리롤 ({rollsLeft}회 남음)
          </PrimaryButton>
        )}

        {/* 족보 공격 카테고리 */}
        <div className="attack-category-list attack-category-compact" aria-label="Attack hand categories">
          {ATTACK_CATEGORIES.map(({ category, label, multiplier }) => {
            const isValid = validCategories.includes(category);
            const previewDmg = canAttack && isValid
              ? calculateUltimateCategoryDamage({ dice, category, equipment, run })
              : null;
            const disabledHint = isValid ? undefined : (isIdle || isRolling ? '굴리기 필요' : 'Not matched');

            return (
              <PrimaryButton
                key={category}
                className="secondary category-button"
                disabled={!canAttack || !isValid}
                onClick={() => diceAttack(category)}
                aria-label={`${label} attack x${multiplier}${previewDmg !== null ? ` → ${previewDmg}` : disabledHint ? ` - ${disabledHint}` : ''}`}
              >
                <span className="cat-label">{label}</span>
                <span className="cat-meta">
                  x{multiplier}{previewDmg !== null ? ` → ${previewDmg}` : ''}
                </span>
              </PrimaryButton>
            );
          })}
        </div>

        {/* 기본 공격 */}
        <PrimaryButton
          className="secondary"
          disabled={!isChoosing || isRolling}
          onClick={basicAttack}
          aria-label={`Basic Attack ${autoDamage}`}
        >
          기본 공격 ({autoDamage})
        </PrimaryButton>

        {/* DEV 패널 — 다이스 섹션 맨 아래, 작게 */}
        {import.meta.env.DEV && (
          <div className="combat-dev-strip">
            <button type="button" className="dev-chip" aria-label="Force Pair Fixture" onClick={() => forceFixture([2, 2, 2, 2, 5])}>Pair</button>
            <button type="button" className="dev-chip" aria-label="Force Full House Fixture" onClick={() => forceFixture([2, 2, 2, 3, 3])}>FH</button>
            <button type="button" className="dev-chip dev-chip--win" aria-label="Force Win" onClick={() => onFinish('win', 'yahtzee')}>Win</button>
            <button type="button" className="dev-chip dev-chip--loss" aria-label="Force Loss" onClick={() => onFinish('loss')}>Loss</button>
          </div>
        )}
      </div>
    </div>
  );
}
