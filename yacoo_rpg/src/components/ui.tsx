import type { ButtonHTMLAttributes, PropsWithChildren, ReactNode } from 'react';
import type { DieValue, Screen } from '../game/types';
export { ArtStack, CoinBadge, DamagePop, EquipmentBadge, ForestMonster, HeroPaperdoll, LayeredArena, RedDotMarker, RewardBurst, RoundAnimalHero } from './GameArt';

interface ShellProps extends PropsWithChildren {
  current: Screen;
  onNavigate: (screen: Screen) => void;
  stats: ReactNode;
}

const navItems: Array<{ screen: Screen; label: string }> = [
  { screen: 'home', label: 'Home' },
  { screen: 'combat', label: 'Combat' },
  { screen: 'equipment', label: 'Equipment' },
  { screen: 'upgrade', label: 'Upgrade' },
  { screen: 'result', label: 'Result' }
];

function NavIcon({ name }: { name: Screen }) {
  return (
    <svg viewBox="0 0 24 24" className="nav-icon" aria-hidden="true" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      {name === 'home' && (
        <>
          <path d="M3 11L12 4l9 7" />
          <path d="M5 9v10a1 1 0 001 1h4v-5h4v5h4a1 1 0 001-1V9" />
        </>
      )}
      {name === 'combat' && (
        <>
          <rect x="3" y="3" width="18" height="18" rx="3" />
          <circle cx="8.5" cy="8.5" r="1.5" fill="currentColor" stroke="none" />
          <circle cx="15.5" cy="8.5" r="1.5" fill="currentColor" stroke="none" />
          <circle cx="12" cy="12" r="1.5" fill="currentColor" stroke="none" />
          <circle cx="8.5" cy="15.5" r="1.5" fill="currentColor" stroke="none" />
          <circle cx="15.5" cy="15.5" r="1.5" fill="currentColor" stroke="none" />
        </>
      )}
      {name === 'equipment' && (
        <path d="M12 3L3 7v5c0 5.5 3.8 10.7 9 12 5.2-1.3 9-6.5 9-12V7L12 3z" />
      )}
      {name === 'upgrade' && (
        <>
          <polyline points="12 19 12 5" />
          <polyline points="6 11 12 5 18 11" />
        </>
      )}
      {name === 'result' && (
        <>
          <path d="M8 21h8M12 17v4" />
          <path d="M7 4h10l-1 8a4 4 0 01-8 0L7 4z" />
          <path d="M7 6H4l1 5a2 2 0 002 2M17 6h3l-1 5a2 2 0 01-2 2" />
        </>
      )}
    </svg>
  );
}

export function Shell({ current, onNavigate, stats, children }: ShellProps) {
  return (
    <div className="app-shell">
      <TopStatsBar>{stats}</TopStatsBar>
      <main className="screen-area" key={current}>
        <div className="screen-enter">{children}</div>
      </main>
      <BottomNav current={current} onNavigate={onNavigate} />
    </div>
  );
}

export function TopStatsBar({ children }: PropsWithChildren) {
  return <header className="top-stats">{children}</header>;
}

export function BottomNav({ current, onNavigate }: { current: Screen; onNavigate: (screen: Screen) => void }) {
  return (
    <nav className="bottom-nav" aria-label="Main navigation">
      {navItems.map((item) => (
        <button
          key={item.screen}
          type="button"
          className={item.screen === current ? 'nav-button active' : 'nav-button'}
          onClick={() => onNavigate(item.screen)}
          aria-current={item.screen === current ? 'page' : undefined}
        >
          <NavIcon name={item.screen} />
          <span className="nav-label">{item.label}</span>
        </button>
      ))}
    </nav>
  );
}

export function Card({ children, className = '' }: PropsWithChildren<{ className?: string }>) {
  return <section className={`card ${className}`.trim()}>{children}</section>;
}

export function ArtCard({ children, className = '' }: PropsWithChildren<{ className?: string }>) {
  return <section className={`card art-card ${className}`.trim()}>{children}</section>;
}

export function PrimaryButton({ className = '', children, ...props }: ButtonHTMLAttributes<HTMLButtonElement>) {
  return (
    <button className={`primary-button ${className}`.trim()} type="button" {...props}>
      {children}
    </button>
  );
}

export function StatPill({ label, value, className = '' }: { label: string; value: ReactNode; className?: string }) {
  return (
    <div className={`stat-pill ${className}`.trim()} aria-label={`${label} ${value}`}>
      <span>{label}</span>
      <strong>{value}</strong>
    </div>
  );
}

export function ProgressBar({ label, value, max }: { label: string; value: number; max: number }) {
  const percent = max <= 0 ? 0 : Math.max(0, Math.min(100, (value / max) * 100));
  return (
    <div className="progress-group">
      <div className="progress-label">
        <span>{label}</span>
        <strong>
          {Math.max(0, value)} / {max}
        </strong>
      </div>
      <div className="progress-track" role="progressbar" aria-label={label} aria-valuemin={0} aria-valuemax={max} aria-valuenow={value}>
        <div className="progress-fill" style={{ width: `${percent}%` }} />
      </div>
    </div>
  );
}

const PIP_POSITIONS: Record<DieValue, [number, number][]> = {
  1: [[1, 1]],
  2: [[0, 0], [2, 2]],
  3: [[0, 0], [1, 1], [2, 2]],
  4: [[0, 0], [0, 2], [2, 0], [2, 2]],
  5: [[0, 0], [0, 2], [1, 1], [2, 0], [2, 2]],
  6: [[0, 0], [0, 2], [1, 0], [1, 2], [2, 0], [2, 2]],
};

function DiePips({ value }: { value: DieValue }) {
  const positions = PIP_POSITIONS[value];
  return (
    <div className="die-pip-grid" aria-hidden="true">
      {Array.from({ length: 9 }, (_, idx) => {
        const r = Math.floor(idx / 3);
        const c = idx % 3;
        const hasPip = positions.some(([pr, pc]) => pr === r && pc === c);
        return <span key={idx} className={hasPip ? 'pip' : 'pip pip--empty'} />;
      })}
    </div>
  );
}

export function Dice({ value, held, onClick, disabled = false, rolling = false, settling = false }: { value?: DieValue; held?: boolean; onClick?: () => void; disabled?: boolean; rolling?: boolean; settling?: boolean }) {
  const cls = ['die', held ? 'held' : '', rolling ? 'rolling' : '', settling ? 'settling' : ''].filter(Boolean).join(' ');
  return (
    <button
      type="button"
      className={cls}
      onClick={onClick}
      disabled={disabled}
      aria-pressed={held}
      aria-label={value ? `Die ${value}${held ? ' held' : ''}` : 'Empty die'}
    >
      {value ? <DiePips value={value} /> : '?'}
    </button>
  );
}

export function RewardBadge({ children }: PropsWithChildren) {
  return <span className="reward-badge">{children}</span>;
}

export function ChunkyBadge({ children, className = '' }: PropsWithChildren<{ className?: string }>) {
  return <span className={`chunky-badge ${className}`.trim()}>{children}</span>;
}
