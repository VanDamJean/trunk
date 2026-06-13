# Anemone Idle 진행, 밸런스, 퀘스트 재설계 계획 한글판

> 이 문서는 `.omo/plans/anemone-progression-redesign.md`를 읽기 쉽게 옮긴 한글 참고본이다. 실제 실행 기준은 원본 계획과 사용자의 “이행해라” 요청이며, 코드 ID, 파일 경로, 명령어, 테스트 이름은 의도적으로 원문 형태를 유지한다.

## TL;DR

현재 자원 클릭 프로토타입을 장 단위 idle/adventure 진행 루프로 바꾼다. 핵심은 선언형 콘텐츠, 명확한 퀘스트 상태, 저장 데이터 마이그레이션, 밸런스 시뮬레이션, 다음 목표 안내 UI다. 기술 스택은 기존 vanilla JavaScript, Vite, Vitest를 유지한다.

산출물은 다음과 같다.

- 0-15분 동안 만족스럽게 굴러가는 Chapter 1 진행 모델.
- 퀘스트, 장, 해금, 보상, 안내를 위한 선언형 조건/보상 스키마.
- 현재 `version: 1` 저장 데이터에서 새 진행 상태로 안전하게 넘어가는 마이그레이션.
- 도달 가능성과 소프트락 부재를 증명하는 balance report 및 quest graph validator.
- 장 제목, 활성 목표, 다음 해금, 생산자 자동 고용 진행도를 보여주는 UI.

작업 크기는 크고, 7개 wave로 진행한다. 핵심 경로는 Task 1 -> Task 2 -> Task 4 -> Task 7 -> Task 8 -> Task 9 -> Task 11 -> Final Verification이다.

## 배경

사용자는 원래 계획했던 “adventure 코뮤니스타” 느낌처럼 게임이 진행되지 않고, 밸런싱과 퀘스트와 성장 루프가 약하다고 지적했다. 그래서 시스템 구조를 제대로 파악하고, 무엇을 할지 Markdown 계획으로 저장하라고 요청했다.

이 작업은 단순 UI 버그가 아니다. 진행 구조를 다시 세우는 아키텍처급 작업이다. “adventure 코뮤니스타”는 전체 리네임 명령이 아니라 커뮤니티/모험 장 단위 진행감으로 해석한다. MVP는 Chapter 1이며, 0-15분짜리 완성 루프를 목표로 한다.

## 현재 상태 요약

- `src/config.js`는 3개 자원, 전역 밸런스 상수, 6개 생산자, 3개 업그레이드, 7개 flat mission을 정의한다.
- `src/economy.js`는 `baseCost * costGrowth^owned` 비용과 자원/생산자 생산을 처리한다.
- `src/logic/production.js`는 하위 생산자 자동 고용의 소수점 진행도를 보존한다.
- `src/missions.js`는 `claimed` mission ID만 저장한다. `locked`, `active`, `completed`, `claimed` 같은 진행 상태 모델은 없다.
- `src/gameState.js`는 `version: 1` 상태를 만들고 저장 데이터 sanitize만 한다. 버전 마이그레이션은 없다.
- `src/main.js`는 상단 mission strip을 `getVisibleMissions()`로 직접 렌더링한다. 장 대시보드나 다음 목표 안내는 없다.
- `docs/advanced_roadmap.md`는 감정 곡선, 0-12분 숫자 목표, 출시 전 숫자 QA 기준을 이미 갖고 있다.

## 목표

게임을 단순 누적 장난감이 아니라 구조적인 idle/adventure 진행으로 느끼게 만든다. 플레이어는 항상 현재 장 목표, 다음 해금, 병목, 고용/업그레이드가 왜 중요한지 알아야 한다.

완료 조건은 다음 명령과 상태로 검증한다.

- `npm test` 통과.
- `npm run build` 통과.
- `node scripts/balance-report.mjs --scenario chapter1-active`가 `0`으로 종료하고 `.omo/evidence/balance-chapter1-active.json` 작성.
- `node scripts/validate-progression-content.mjs`가 `0`으로 종료하고 `.omo/evidence/progression-content-validation.json` 작성.
- 브라우저 QA에서 한국어 진행 UI가 장 제목, 활성 퀘스트, 다음 해금, 비어 있지 않은 mission strip을 보여줌.

## 반드시 지킬 것

- 모든 resource, producer, upgrade, quest, chapter, zone, condition, reward ID는 안정적으로 유지한다.
- MVP 자원은 `plankton`, `pearls`, `tideEnergy`를 유지한다.
- `src/logic/production.js`의 생산자 자동 고용 및 소수점 진행도 보존을 유지한다.
- 기존 `version: 1` localStorage 저장 데이터를 마이그레이션한다.
- vanilla JS/Vite/Vitest를 유지한다.
- Chapter 1 밸런스 기준은 `docs/advanced_roadmap.md:127-137`, `docs/advanced_roadmap.md:272-283`에서 가져온다.
- Chapter 1 종료 후 빈 퀘스트 목록 대신 “준비 중” 상태를 보여준다.

## 하지 말 것

- 별도 승인 없이 전체 리네임/리테마를 “코뮤니스타”로 하지 않는다.
- React, Redux, ECS, TypeScript로 옮기지 않는다.
- backend, database, real ads, AdMob integration을 넣지 않는다.
- prestige/reset 루프를 넣지 않는다.
- mission logic을 `src/main.js`에 직접 박지 않는다.
- 숫자 기준 없는 “밸런스 개선”을 하지 않는다.
- reload, old-save migration, repeated claim에서 보상이 중복 지급되면 안 된다.

## 실행 Wave

| Wave | 작업 | 성격 |
|---:|---|---|
| 1 | Task 1, 2, 3 | 진행 콘텐츠/상태/밸런스 목표 기반 |
| 2 | Task 4, 5, 6 | 저장 마이그레이션/리포트/호환 어댑터 |
| 3 | Task 7 | gameState 연결 |
| 4 | Task 8 | mission strip UI 교체 |
| 5 | Task 9, 10 | 장 대시보드/검증 명령 |
| 6 | Task 11 | 숫자 튜닝 |
| 7 | Task 12 | 가이드 문서 갱신 |

## Task 1. Progression content schema와 validators 추출

`src/progression/content.js`를 추가해 `CHAPTER_DEFINITIONS`, `QUEST_DEFINITIONS`, `CONDITION_TYPES`, `REWARD_TYPES`를 export한다. `src/progression/validators.js`는 ID 존재, 중복 ID, 누락 참조, 순환 prerequisites, 도달 불가능한 chapter entry quest, 잘못된 reward, finite numeric target을 검증한다.

필수 quest ID는 `quest-first-interns`, `quest-first-shrimp`, `quest-first-capsule`, `quest-first-upgrade`, `quest-first-crab`, `quest-chapter1-complete`다. 기존 `MISSION_DEFINITIONS`는 아직 제거하지 않는다.

검증: `npm test -- tests/progression.test.js`

## Task 2. Quest/chapter runtime state model 정의

`src/progression/state.js`를 추가해 `createProgressionState()`, `cloneProgressionState()`, `evaluateCondition()`, `evaluateConditions()`, `refreshProgressionState()`, `claimQuestReward()`를 제공한다.

Quest status는 정확히 `locked`, `active`, `completed`, `claimed`만 쓴다. Chapter status는 `locked`, `active`, `completed`만 쓴다. 완료됐지만 미수령인 퀘스트는 reload 뒤에도 보이고 claim 가능해야 한다. 이미 수령한 보상은 다시 지급하면 안 된다.

검증: `npm test -- tests/progressionState.test.js`

## Task 3. Roadmap 기반 balance target 정의

`src/progression/balanceTargets.js`에 Chapter 1 목표를 코드로 저장한다. 목표에는 60초 intern 최소 3, 180초 shrimp 2-4, 300초 crab 가격은 보이지만 반복 구매 불가, 900초 crab 최소 1이 포함된다. 이전 기준의 15분 생산량 20,000/s 상한은 고정 `+3` 뿅 전제였으므로, 동적 뿅 적용 뒤에는 별도 밸런스 재튜닝 대상으로 본다.

검증: `npm test -- tests/balanceTargets.test.js`

## Task 4. v1 mission state에서 progression state로 저장 마이그레이션

`createInitialState()`는 `version: 2`와 `progression` 필드를 만든다. `sanitizeState()`는 `missions.claimed`만 가진 v1 save를 받아 equivalent claimed quest state로 변환해야 한다. 보상은 마이그레이션 중 다시 지급하지 않는다. `economy.producerProgress`도 보존한다.

검증: `npm test -- tests/storage.test.js tests/progressionMigration.test.js`

## Task 5. Chapter 1 balance simulator와 report script 작성

`scripts/balance-report.mjs`를 만든다. `chapter1-active` scenario는 1초마다 pulse, 가능한 quest claim, deterministic producer/upgrade 구매, capsule 시작/claim, 900초 진행을 시뮬레이션한다.

출력은 `.omo/evidence/balance-chapter1-active.json`이고, checkpoint는 60, 180, 300, 420, 720, 900초다. target 실패 시 nonzero 종료한다.

검증: `node scripts/balance-report.mjs --scenario chapter1-active`

## Task 6. 기존 mission에서 새 progression으로 가는 호환 adapter 작성

`src/progression/legacyMissionAdapter.js`를 추가한다. 기존 mission ID `first-bloom`, `shrimp-shift`, `pearl-cache`, `branch-boss`, `tidal-gift`, `whale-boardroom`, `prismatic-growth`는 명시적으로 새 quest에 mapping하거나 deprecated 처리한다.

`getVisibleProgressionQuests(state, limit = 3)`는 current UI에 필요한 visible quest를 progression status 기반으로 반환한다.

검증: `npm test -- tests/progressionAdapter.test.js tests/missions.test.js`

## Task 7. Progression actions를 game state에 연결

`advanceGame()`, `buyProducerInState()`, `pulseReefInState()`, `purchaseUpgradeInState()`, `claimCapsule()`, `claimRewardedAdBuffInState()` 후 progression status를 refresh한다. `claimQuestInState(state, questId)`를 추가한다. 기존 `claimMissionInState()`는 호환 wrapper로 유지한다.

Refresh만으로 보상을 지급하면 안 된다. 보상 지급은 claim 때만 한다.

검증: `npm test -- tests/gameState.test.js tests/progressionState.test.js`

## Task 8. Flat mission visibility를 progression quest visibility로 교체

`src/main.js`의 mission strip은 `getVisibleMissions()` 대신 progression-backed quest를 렌더링한다. 완료된 quest button은 `claimQuestInState()`를 호출한다. 비어 있으면 `1장 업무 산호초 완료 — 다음 커뮤니티 원정 준비 중` 카드를 보여준다.

검증: `npm test -- tests/i18n.test.js tests/progressionAdapter.test.js`, 브라우저 QA

## Task 9. Chapter dashboard와 next-goal guidance 추가

`renderReefPanel()`에 compact chapter dashboard를 추가한다. 표시 항목은 현재 chapter name, progress percentage, next unlock, current bottleneck, current production rate, producer-spawner progress다.

한국어 제목은 `1장: 업무 산호초 창업`, 영어 제목은 `Chapter 1: Office Reef Startup`이다.

검증: 모바일 폭 430px에서 overflow 없음, undefined/NaN 없음.

## Task 10. CI-friendly validation commands 추가

`scripts/validate-progression-content.mjs`를 추가한다. 이 스크립트는 validators와 content를 import해 `.omo/evidence/progression-content-validation.json`을 쓰고, 오류가 있으면 nonzero로 종료한다. `docs/guide.md`에는 검증 명령을 적는다.

검증: `node scripts/validate-progression-content.mjs`, `npm test`, `npm run build`

## Task 11. Simulator 기준 Chapter 1 숫자 튜닝

필요한 경우에만 `src/config.js` 생산자/업그레이드 숫자, `src/progression/content.js` quest reward, legacy mission reward, `REWARDED_AD_BUFF` 상수를 조정한다. 뿅 공식은 별도 동적 뿅 계획에 따라 `getPulsePlanktonGain()`을 기준으로 하며, 고정 `PULSE_PLANKTON_GAIN = 3` 유지 가정은 더 이상 적용하지 않는다.

검증: `node scripts/balance-report.mjs --scenario chapter1-active`가 `0`으로 종료하고 `failedTargets`가 비어 있음.

## Task 12. Player-facing guide와 developer notes 갱신

`docs/guide.md`에 Chapter 1, active quests, next unlock guidance, end-of-content state를 설명한다. 또한 `npm test`, `npm run build`, `node scripts/validate-progression-content.mjs`, `node scripts/balance-report.mjs --scenario chapter1-active` 명령을 적는다.

아직 구현하지 않은 prestige, backend, real ads, multiplayer, future chapters를 live feature처럼 설명하지 않는다.

검증: `npm test && npm run build`

## 최종 검증

모든 구현 뒤에는 다음을 실행한다.

1. `npm test`
2. `node scripts/validate-progression-content.mjs`
3. `node scripts/balance-report.mjs --scenario chapter1-active`
4. `npm run build`
5. Browser QA: 한국어 UI, active quests, claim flow, chapter dashboard, end-of-content card, console error 없음.

## 성공 기준

- 게임에 flat accumulation만 있는 것이 아니라 보이는 Chapter 1 진행 루프가 있다.
- 플레이어는 항상 active goal과 next unlock guidance를 본다.
- Quest state는 `locked`, `active`, `completed`, `claimed`를 지원하고 중복 보상을 막는다.
- 기존 저장 데이터는 안전하게 로드되고 기존 진행이 보존된다.
- 밸런스는 감이 아니라 repeatable simulation으로 증명된다.
- 구현은 vanilla JS + Vite + Vitest를 유지한다.
