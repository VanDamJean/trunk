import type { CSSProperties, PropsWithChildren, ReactNode } from 'react';
import type { EquipmentSet, EquipmentSlot } from '../game/types';

type ArtSize = 'sm' | 'md' | 'lg';
type BadgeKind = 'coin' | 'weapon' | 'armor' | 'charm' | 'boots';

interface ArtProps {
  className?: string;
  size?: ArtSize;
  title?: string;
}

interface BadgeProps {
  kind?: BadgeKind;
  label?: ReactNode;
  className?: string;
}

function artClass(base: string, size: ArtSize = 'md', className = '') {
  return `${base} ${base}--${size} ${className}`.trim();
}

function artA11y(title?: string) {
  return title ? { role: 'img', 'aria-label': title } : { 'aria-hidden': true };
}

export function RoundAnimalHero({ className = '', size = 'md', title = 'Round animal hero' }: ArtProps) {
  return (
    <svg className={artClass('game-art hero-art', size, className)} viewBox="0 0 120 120" {...artA11y(title)}>
      <defs>
        <linearGradient id="heroBody" x1="22" x2="98" y1="18" y2="108" gradientUnits="userSpaceOnUse">
          <stop stopColor="var(--art-hero-light)" />
          <stop offset="1" stopColor="var(--art-hero-deep)" />
        </linearGradient>
        <linearGradient id="heroCap" x1="36" x2="92" y1="14" y2="62" gradientUnits="userSpaceOnUse">
          <stop stopColor="var(--art-leaf-light)" />
          <stop offset="1" stopColor="var(--art-leaf-deep)" />
        </linearGradient>
      </defs>
      <ellipse className="art-shadow" cx="60" cy="105" rx="40" ry="10" />
      <circle className="art-stroke art-fill-hero" cx="60" cy="63" r="42" fill="url(#heroBody)" />
      <circle className="art-stroke art-fill-hero" cx="30" cy="54" r="16" fill="url(#heroBody)" />
      <circle className="art-stroke art-fill-hero" cx="90" cy="54" r="16" fill="url(#heroBody)" />
      <path className="art-stroke" d="M38 43C48 22 75 20 88 39C77 35 58 36 38 43Z" fill="url(#heroCap)" />
      <circle className="art-face" cx="47" cy="61" r="5" />
      <circle className="art-face" cx="74" cy="61" r="5" />
      <path className="art-line" d="M53 75C58 80 66 80 72 75" />
      <path className="art-stroke art-fill-cream" d="M41 84C48 93 73 94 81 84C78 99 46 101 41 84Z" />
      <path className="art-line" d="M31 38C37 35 43 36 47 42" />
      <path className="art-line" d="M89 38C83 35 77 36 73 42" />
    </svg>
  );
}

export function ForestMonster({ className = '', size = 'md', title = 'Forest monster' }: ArtProps) {
  return (
    <svg className={artClass('game-art monster-art', size, className)} viewBox="0 0 120 120" {...artA11y(title)}>
      <defs>
        <linearGradient id="monsterBody" x1="27" x2="94" y1="18" y2="106" gradientUnits="userSpaceOnUse">
          <stop stopColor="var(--art-moss-light)" />
          <stop offset="1" stopColor="var(--art-moss-deep)" />
        </linearGradient>
      </defs>
      <ellipse className="art-shadow" cx="60" cy="106" rx="38" ry="10" />
      <path className="art-stroke" d="M22 72C22 43 40 26 61 26C84 26 100 44 100 72C100 95 83 106 60 106C38 106 22 95 22 72Z" fill="url(#monsterBody)" />
      <path className="art-stroke art-fill-leaf" d="M37 32L28 16L48 25Z" />
      <path className="art-stroke art-fill-leaf" d="M78 28L91 13L92 38Z" />
      <path className="art-stroke art-fill-bark" d="M25 71L10 64L25 57Z" />
      <path className="art-stroke art-fill-bark" d="M95 71L111 62L96 56Z" />
      <circle className="art-stroke art-fill-cream" cx="46" cy="62" r="10" />
      <circle className="art-stroke art-fill-cream" cx="75" cy="62" r="10" />
      <circle className="art-face" cx="49" cy="63" r="4" />
      <circle className="art-face" cx="72" cy="63" r="4" />
      <path className="art-line" d="M47 82C57 88 70 88 80 82" />
      <path className="art-line" d="M38 50C44 46 51 46 56 51" />
      <path className="art-line" d="M68 51C75 45 83 47 87 52" />
    </svg>
  );
}

export function LayeredArena({ children, className = '' }: PropsWithChildren<{ className?: string }>) {
  return (
    <div className={`layered-arena ${className}`.trim()}>
      <svg className="arena-art" viewBox="0 0 360 210" aria-hidden="true">
        <path className="arena-sky" d="M0 0H360V210H0Z" />
        <circle className="arena-sun" cx="294" cy="45" r="28" />
        <path className="arena-hill arena-hill-back" d="M0 115C43 75 88 86 123 112C165 61 228 68 262 112C296 89 329 92 360 121V210H0Z" />
        <path className="arena-hill arena-hill-front" d="M0 150C35 119 78 127 112 150C154 111 210 120 242 150C284 125 324 130 360 154V210H0Z" />
        <path className="arena-ground" d="M0 160C72 149 125 168 180 160C238 150 288 156 360 164V210H0Z" />
        <path className="arena-path" d="M139 210C150 182 169 164 187 161C210 174 226 191 238 210Z" />
      </svg>
      <div className="arena-content">{children}</div>
    </div>
  );
}

export function CoinBadge({ label, className = '' }: Omit<BadgeProps, 'kind'>) {
  return <EquipmentBadge kind="coin" label={label} className={className} />;
}

export function EquipmentBadge({ kind = 'weapon', label, className = '' }: BadgeProps) {
  return (
    <span className={`art-badge art-badge--${kind} ${className}`.trim()}>
      <BadgeGlyph kind={kind} />
      {label && <span className="art-badge-label">{label}</span>}
    </span>
  );
}

function BadgeGlyph({ kind }: { kind: BadgeKind }) {
  if (kind === 'coin') {
    return (
      <svg className="badge-glyph" viewBox="0 0 32 32" aria-hidden="true">
        <circle className="badge-coin" cx="16" cy="16" r="12" />
        <path className="badge-mark" d="M11 17C13 22 21 22 23 16C21 19 14 19 11 17Z" />
      </svg>
    );
  }

  if (kind === 'armor') {
    return (
      <svg className="badge-glyph" viewBox="0 0 32 32" aria-hidden="true">
        <path className="badge-shape" d="M16 4L26 8V15C26 22 21 27 16 29C11 27 6 22 6 15V8Z" />
        <path className="badge-mark" d="M16 8V25" />
      </svg>
    );
  }

  if (kind === 'charm') {
    return (
      <svg className="badge-glyph" viewBox="0 0 32 32" aria-hidden="true">
        <path className="badge-shape" d="M16 5L20 12L28 13L22 19L24 27L16 23L8 27L10 19L4 13L12 12Z" />
        <circle className="badge-mark-fill" cx="16" cy="16" r="4" />
      </svg>
    );
  }

  if (kind === 'boots') {
    return (
      <svg className="badge-glyph" viewBox="0 0 32 32" aria-hidden="true">
        <path className="badge-shape" d="M9 7H19V17L27 21V26H7V21L10 18Z" />
        <path className="badge-mark" d="M9 21H26" />
      </svg>
    );
  }

  return (
    <svg className="badge-glyph" viewBox="0 0 32 32" aria-hidden="true">
      <path className="badge-shape" d="M22 4L28 10L12 26H6V20Z" />
      <path className="badge-mark" d="M19 7L25 13" />
    </svg>
  );
}

export function RedDotMarker({ label = 'New', className = '' }: { label?: string; className?: string }) {
  return <span className={`red-dot-marker ${className}`.trim()} aria-label={label} />;
}

export function DamagePop({ value, tone = 'hit', className = '' }: { value: ReactNode; tone?: 'hit' | 'heal' | 'crit'; className?: string }) {
  return <span className={`damage-pop damage-pop--${tone} ${className}`.trim()}>{value}</span>;
}

export function RewardBurst({ children, className = '' }: PropsWithChildren<{ className?: string }>) {
  return (
    <div className={`reward-burst ${className}`.trim()}>
      <span className="reward-burst-rays" aria-hidden="true" />
      <span className="reward-burst-core">{children}</span>
    </div>
  );
}

export function ArtStack({ children, className = '', offset = 0 }: PropsWithChildren<{ className?: string; offset?: number }>) {
  return (
    <div className={`art-stack ${className}`.trim()} style={{ '--art-stack-offset': `${offset}px` } as CSSProperties}>
      {children}
    </div>
  );
}

/* ── Hero Paperdoll ─────────────────────────────────────────────────── */

function equipTier(level: number): 1 | 2 | 3 {
  if (level >= 7) return 3;
  if (level >= 4) return 2;
  return 1;
}

const TIER_FILL: Record<1 | 2 | 3, string> = {
  1: '#c8d4dc',
  2: '#89d56f',
  3: '#ffe66d',
};

const TIER_STROKE: Record<1 | 2 | 3, string> = {
  1: '#7a9aaa',
  2: '#3d9c45',
  3: '#bd7025',
};

interface PaperdollProps {
  equipment: EquipmentSet;
  highlightSlot?: EquipmentSlot | null;
  size?: 'sm' | 'md';
  className?: string;
}

export function HeroPaperdoll({ equipment, highlightSlot, size = 'md', className = '' }: PaperdollProps) {
  const sizeClass = size === 'sm' ? 'hero-paperdoll--sm' : 'hero-paperdoll--md';
  const w = equipment.weapon;
  const a = equipment.armor;
  const c = equipment.charm;
  const b = equipment.boots;

  const wTier = equipTier(w.level);
  const aTier = equipTier(a.level);
  const cTier = equipTier(c.level);
  const bTier = equipTier(b.level);

  function slotStyle(slot: EquipmentSlot) {
    const active = highlightSlot === slot;
    return {
      filter: active ? 'brightness(1.25) drop-shadow(0 0 6px rgba(238,147,40,0.9))' : undefined,
      transition: 'filter 180ms ease',
    };
  }

  return (
    <svg
      className={`hero-paperdoll ${sizeClass} ${className}`.trim()}
      viewBox="0 0 120 120"
      role="img"
      aria-label="Hero with equipment"
    >
      {/* ── Hero base (same as RoundAnimalHero) ── */}
      <defs>
        <linearGradient id="pdHeroBody" x1="22" x2="98" y1="18" y2="108" gradientUnits="userSpaceOnUse">
          <stop stopColor="var(--art-hero-light)" />
          <stop offset="1" stopColor="var(--art-hero-deep)" />
        </linearGradient>
        <linearGradient id="pdHeroCap" x1="36" x2="92" y1="14" y2="62" gradientUnits="userSpaceOnUse">
          <stop stopColor="var(--art-leaf-light)" />
          <stop offset="1" stopColor="var(--art-leaf-deep)" />
        </linearGradient>
      </defs>
      <ellipse className="art-shadow" cx="60" cy="105" rx="40" ry="10" />
      <circle className="art-stroke art-fill-hero" cx="60" cy="63" r="42" fill="url(#pdHeroBody)" />
      <circle className="art-stroke art-fill-hero" cx="30" cy="54" r="16" fill="url(#pdHeroBody)" />
      <circle className="art-stroke art-fill-hero" cx="90" cy="54" r="16" fill="url(#pdHeroBody)" />
      <path className="art-stroke" d="M38 43C48 22 75 20 88 39C77 35 58 36 38 43Z" fill="url(#pdHeroCap)" />
      <circle className="art-face" cx="47" cy="61" r="5" />
      <circle className="art-face" cx="74" cy="61" r="5" />
      <path className="art-line" d="M53 75C58 80 66 80 72 75" />
      <path className="art-stroke art-fill-cream" d="M41 84C48 93 73 94 81 84C78 99 46 101 41 84Z" />
      <path className="art-line" d="M31 38C37 35 43 36 47 42" />
      <path className="art-line" d="M89 38C83 35 77 36 73 42" />

      {/* ── Boots ── */}
      <g style={slotStyle('boots')}>
        <path
          d="M38 100C37 96 39 92 44 91L52 91L54 107L36 107Z"
          fill={TIER_FILL[bTier]} stroke={TIER_STROKE[bTier]} strokeWidth="2.5" strokeLinejoin="round"
        />
        <path
          d="M82 100C83 96 81 92 76 91L68 91L66 107L84 107Z"
          fill={TIER_FILL[bTier]} stroke={TIER_STROKE[bTier]} strokeWidth="2.5" strokeLinejoin="round"
        />
        {bTier >= 2 && (
          <>
            <path d="M37 104L53 104" stroke={TIER_STROKE[bTier]} strokeWidth="1.5" strokeLinecap="round" />
            <path d="M83 104L67 104" stroke={TIER_STROKE[bTier]} strokeWidth="1.5" strokeLinecap="round" />
          </>
        )}
        {bTier === 3 && (
          <>
            <circle cx="45" cy="93" r="2" fill={TIER_STROKE[bTier]} />
            <circle cx="75" cy="93" r="2" fill={TIER_STROKE[bTier]} />
          </>
        )}
      </g>

      {/* ── Armor (chest plate) ── */}
      <g style={slotStyle('armor')}>
        <path
          d="M42 72C44 62 50 56 60 54C70 56 76 62 78 72C76 80 70 86 60 88C50 86 44 80 42 72Z"
          fill={TIER_FILL[aTier]} stroke={TIER_STROKE[aTier]} strokeWidth="2.5" strokeLinejoin="round" fillOpacity="0.85"
        />
        {aTier >= 2 && (
          <path d="M60 56L60 86" stroke={TIER_STROKE[aTier]} strokeWidth="1.5" strokeLinecap="round" opacity="0.6" />
        )}
        {aTier === 3 && (
          <>
            <circle cx="60" cy="70" r="3" fill={TIER_STROKE[aTier]} />
            <path d="M48 67L72 67" stroke={TIER_STROKE[aTier]} strokeWidth="1.5" strokeLinecap="round" opacity="0.7" />
          </>
        )}
      </g>

      {/* ── Weapon (sword right side) ── */}
      <g style={slotStyle('weapon')}>
        {/* Blade */}
        <path
          d="M104 24L100 72L108 72Z"
          fill={TIER_FILL[wTier]} stroke={TIER_STROKE[wTier]} strokeWidth="2" strokeLinejoin="round"
        />
        {/* Crossguard */}
        <rect x="96" y="70" width="16" height="5" rx="2.5"
          fill={TIER_FILL[wTier]} stroke={TIER_STROKE[wTier]} strokeWidth="2" />
        {/* Hilt */}
        <rect x="101" y="75" width="6" height="14" rx="3"
          fill={TIER_STROKE[wTier]} stroke={TIER_STROKE[wTier]} strokeWidth="1.5" />
        {wTier === 3 && (
          <circle cx="104" cy="24" r="4" fill={TIER_FILL[wTier]} stroke={TIER_STROKE[wTier]} strokeWidth="2" />
        )}
      </g>

      {/* ── Charm (gem above head) ── */}
      <g style={slotStyle('charm')}>
        <polygon
          points="60,10 66,18 60,26 54,18"
          fill={TIER_FILL[cTier]} stroke={TIER_STROKE[cTier]} strokeWidth="2.5" strokeLinejoin="round"
        />
        {cTier >= 2 && (
          <line x1="60" y1="10" x2="60" y2="26" stroke={TIER_STROKE[cTier]} strokeWidth="1" opacity="0.5" />
        )}
        {cTier === 3 && (
          <>
            <circle cx="54" cy="12" r="2.5" fill={TIER_FILL[cTier]} stroke={TIER_STROKE[cTier]} strokeWidth="1.5" />
            <circle cx="66" cy="12" r="2.5" fill={TIER_FILL[cTier]} stroke={TIER_STROKE[cTier]} strokeWidth="1.5" />
          </>
        )}
      </g>
    </svg>
  );
}
