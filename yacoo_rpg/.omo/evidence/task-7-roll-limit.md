Task 7 roll limit verification

Implementation checks:
- Combat dice panel tracks rolls from 0 to 3.
- Roll Dice button is disabled when rolls >= 3.
- Resolve Attack is disabled before first roll and enabled after rolling.
- Held dice use aria-pressed and remain stable through rollDice tests.

Result:
- npm test -- --run passed.
- npm run qa passed.
