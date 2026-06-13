# Learnings

## 2026-06-03 Direct Continuation
- Subagent auth failures were bypassed by direct local implementation.
- Vite/Vitest config must import `defineConfig` from `vitest/config` when inline `test` config is present.
- `src/vite-env.d.ts` is required for `import.meta.env` typing.
- The environment lacked `typescript-language-server`, so `lsp_diagnostics` could not run; `npm run build` with `tsc -b` provided TypeScript verification.
- Playwright package installation does not install browsers automatically; `npx playwright install chromium` was required before browser QA.
- React Testing Library button queries must disambiguate duplicate visible labels when both page buttons and bottom navigation share names.
