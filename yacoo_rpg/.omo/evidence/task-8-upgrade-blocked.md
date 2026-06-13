Task 8 insufficient coins verification

Implementation checks:
- UpgradeScreen computes blocked state via canUpgrade().
- With 0 coins, level-1 upgrade cost 25 blocks the Upgrade button.
- Level 10 displays Max Level and blocks upgrade.

Result:
- npm test -- --run passed.
- npm run build passed.
