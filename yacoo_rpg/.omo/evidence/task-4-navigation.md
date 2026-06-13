Task 4 navigation verification

Verified by:
- src/App.test.tsx navigates Home -> Equipment -> Upgrade without a router.
- Playwright QA navigates Home -> Combat -> Result -> Upgrade -> Home.

Result:
- npm test -- --run passed.
- npm run qa passed.
