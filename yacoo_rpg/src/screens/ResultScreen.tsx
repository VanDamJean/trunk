import { ArtCard, Card, ChunkyBadge, PrimaryButton, RewardBadge, RewardBurst } from '../components/ui';
import { ATTACK_CATEGORIES } from '../game/constants';
import type { CombatResult } from '../game/types';

interface ResultScreenProps {
  result?: CombatResult;
  onClaim: () => void;
  onHome: () => void;
}

export function ResultScreen({ result, onClaim, onHome }: ResultScreenProps) {
  if (!result) {
    return (
      <Card className="hero-card result-empty-card">
        <h1 className="screen-title">Result</h1>
        <p className="muted">No battle result yet.</p>
        <PrimaryButton onClick={onHome}>Back Home</PrimaryButton>
      </Card>
    );
  }

  const selectedAttack = result.handUsed
    ? ATTACK_CATEGORIES.find((item) => item.category === result.handUsed)?.label ?? result.handUsed
    : undefined;

  return (
    <>
      <ArtCard className="result-hero-card">
        <RewardBurst className="result-burst">{result.outcome === 'win' ? 'WIN' : 'REST'}</RewardBurst>
        <div>
          <h1 className="screen-title">Result</h1>
          <h2>{result.outcome === 'win' ? 'Victory!' : 'Try Again'}</h2>
          <RewardBadge>Stage {result.stage}</RewardBadge>
        </div>
      </ArtCard>

      <Card className="reward-card">
        <div className="reward-total" aria-label={`Coins earned ${result.coinsEarned}`}>
          <span>Coins earned</span>
          <strong>{result.coinsEarned}</strong>
        </div>
        {selectedAttack && <p>Selected attack: <strong>{selectedAttack}</strong></p>}
        {result.duplicateItemName && <p>Duplicate {result.duplicateItemName} converted to bonus coins.</p>}
        <ChunkyBadge>{result.outcome === 'win' ? 'Path cleared' : 'Regroup'}</ChunkyBadge>
        <PrimaryButton onClick={onClaim}>Claim Reward</PrimaryButton>
        <PrimaryButton className="secondary" onClick={onHome}>Back Home</PrimaryButton>
      </Card>
    </>
  );
}
