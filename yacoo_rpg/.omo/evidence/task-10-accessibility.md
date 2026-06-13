Task 10 keyboard/accessibility verification

Implemented accessibility support:
- Real button controls for navigation, actions, dice, and upgrades.
- Focus-visible outline in CSS.
- Disabled states for blocked dice roll/resolve and upgrade actions.
- Progress bars expose aria-valuemin/aria-valuemax/aria-valuenow.
- Dice expose aria-label and aria-pressed.

Result:
- npm test -- --run passed.
- npm run qa passed.
