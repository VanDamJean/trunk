# SOURCE_OF_TRUTH — Yacoo RPG Android

> 이 문서는 모든 에이전트(Antigravity, Opus, Gemini, OpenCode, Codex 등)가 **반드시 먼저 읽어야 하는 단일 기준서**다.
> 코드 수정 전 이 문서의 "수정 금지"와 "수정 대상"을 먼저 확인한다.
> 이 문서가 기존 ANTIGRAVITY_*.md, *_BRIEF.md 와 충돌하면 **이 문서가 우선**한다.

---

## 1. Android 앱 프로젝트 경로 (유일한 작업 대상)

```
/Users/a1/Desktop/manus/yacoo_rpg-android
```

- 패키지: `com.yacoo.rpg`
- Git root: `/Users/a1/Desktop/manus` (부모 워크스페이스)
- 빌드: `JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" ./gradlew assembleDebug`
- 이 경로 **외부**의 파일은 참조 전용이며 수정 금지(섹션 6 참조).

---

## 2. 전투 화면 진입점 (Entry Point)

**파일**: `app/src/main/java/com/yacoo/rpg/ui/screens/CombatScreen.kt`

- Navigation graph(`NavGraph.kt`)에서 `Screen.COMBAT` route로 진입.
- `CombatScreen` 은 전투 화면의 최상위 컴포저블이며, 하위에 `CombatArena` + `CombatDiceBoard` 를 배치한다.
- 전투 종료 후 보상 플로우는 `onFinish` / `onPickReward` 콜백으로 처리.

---

## 3. 전투 UI 구성 파일 (Combat UI Component Map)

> 이 3개 파일이 **현재 전투 UI의 전부**다. 이외의 파일로 전투 UI를 흩뿌리지 말 것.

| 파일 | 경로 | 역할 | 수정 시 주의 |
|---|---|---|---|
| **CombatScreen.kt** | `app/src/main/java/com/yacoo/rpg/ui/screens/CombatScreen.kt` | 전체 배경, HUD, 화면 비율, 하위 컴포넌트 배치, 종료 콜백 | 최상위 레이아웃/비율 변경 시 전체 영향 |
| **CombatArena.kt** | `app/src/main/java/com/yacoo/rpg/ui/components/CombatArena.kt` | 전투판, 캐릭터, HP, 피격/공격 피드백 연출 | 애니메이션/타이밍 변경 시 게임 느낌 직접 영향 |
| **CombatDiceBoard.kt** | `app/src/main/java/com/yacoo/rpg/ui/components/CombatDiceBoard.kt` | 주사위, 족보 카드, CTA 버튼, 롤/홀드 인터랙션 | 입력 응답성 직접 영향 |

---

## 4. 최신 전투 UI 기준 커밋 (Combat UI Baseline)

```
2c3fa33  Redesign combat screen presentation
```

- 이 커밋이 **전투 UI의 현재 Source of Truth**다.
- 전투 화면 리디자인 작업 시 이 커밋을 기준점으로 삼는다.
- 되돌릴 때는 `git revert 2c3fa33` 만 허용 (`git reset` 금지).

---

## 5. 최신 ignore 정리 커밋 (Repo Hygiene Baseline)

```
1b7b995  Add project gitignore for local artifacts
```

- `.gitignore` 가 이 커밋에서 처음 도입됨.
- 신규 에이전트/IDE/빌드 부산물(`.DS_Store`, `.gradle/`, `.idea/`, `.playwright-mcp/`, `.omo/boulder.json`, `.omo/run-continuation/*.json`, `.omo/evidence/*.txt` 등)은 더 이상 `git status` 에 노출되지 않는다.
- `.gitignore` 규칙을 변경할 때는 이 커밋의 의도(신규 부산물 노출 방지)를 훼손하지 말 것.

---

## 6. 수정 금지 / 참조 전용 경로 (READ-ONLY)

> 아래 경로들은 **절대 수정/삭제/이동 금지**. 참조만 허용.

| 경로 | 용도 | 금지 이유 |
|---|---|---|
| `/Users/a1/Desktop/video_test/yacoo_screen_rescue_mockup/` | 스크린 리스큐 HTML/CSS 목업 | 디자인 참조용 목업. 작업 결과물 아님. |
| `/Users/a1/Desktop/video_test/yacoo_combat_visual_drafts/` | 전투 비주얼 드래프트 (HTML/CSS/이미지) | 디자인 탐색용. 작업 결과물 아님. |
| `/Users/a1/Desktop/manus/yacoo_rpg-android/.omo/` | OpenCode/에이전트 런타임 상태 | 세션/증거/플랜. 런타임이 관리함. |
| `/Users/a1/Desktop/manus/yacoo_rpg-android/.playwright-mcp/` | Playwright MCP 런타임 로그 | 자동화 부산물. 이미 ignore 됨. |

---

## 7. 최신 디자인 레퍼런스 (Design Reference)

**기준 이미지**:
```
/Users/a1/Desktop/video_test/yacoo_combat_visual_drafts/combat_visual_direction_textless_v1.png
```

> ⚠️ **사용 규칙 (CRITICAL)**:
> - 이 이미지를 **통째로(app 에 드래그 가능한 비트맵으로) 앱에 붙이지 말 것**.
> - 이미지를 **파츠/컴포넌트 단위로 분해**하여 Jetpack Compose 코드(`CombatArena.kt`, `CombatDiceBoard.kt`)로 **재현**할 것.
> - 재현 단위: 배경 레이어 / 캐릭터 슬롯 / HP 바 / 주사위 트레이 / 족보 카드 / CTA 버튼 / 피드백 이펙트.
> - 외부 게임의 캐릭터, 로고, 아이콘, 폰트, 에셋을 복사하지 말 것. 가져올 것은 **레이아웃 문법, 색, 밀도, 리듬**만.

참고 보조 자료 (같은 폴더):
- `combat_visual_direction_v1.png` — 텍스트 포함 버전 (참고용)
- `app.js`, `styles.css`, `index.html` — HTML/CSS 프로토타입 (레이아웃 참고용, 앱 코드에 직접 복사 금지)

---

## 8. 현재 보류 중인 변경 (Pending Changes — DO NOT TOUCH)

> 아래 파일들은 현재 `git status` 에 노출되어 있으나 **사용자가 결정하기 전까지 에이전트가 임의로 커밋/수정/되돌리지 않는다.**

| 파일 | 상태 | 비고 |
|---|---|---|
| `app/src/main/java/com/yacoo/rpg/navigation/NavGraph.kt` | M | 보상 플로우를 Combat 쪽으로 인라인(진행 중) |
| `app/src/main/java/com/yacoo/rpg/ui/components/DiceView.kt` | M | 색상 토큰 → hex 치환, 그라데이션 (리디자인) |
| `app/src/main/java/com/yacoo/rpg/ui/screens/RewardPickScreen.kt` | M | 다크/네온 재디자인 (리디자인) |
| `ANTIGRAVITY_3_REFERENCE_UI_REDESIGN_DIRECTIVE.md` | ?? | 작업 지시문 |
| `ANTIGRAVITY_TOTAL_REDESIGN_REBUILD_DIRECTIVE.md` | ?? | 작업 지시문 |
| `YACOO_RPG_GAME_PLAN_V1.md` | ?? | 게임 기획서 V1 |
| `dice_wayfarer_thumb.png` | ?? | 레퍼런스 이미지 (용도 미확정) |

**규칙**: 새 작업을 시작하기 전에 반드시 `git status --short` 로 위 파일들이 여전히 보존되어 있는지 확인.

---

## 9. 에이전트 작업 규칙 (Agent Rules)

> 이 규칙은 모든 에이전트가 **작업 시작 전** 반드시 확인한다.

### 9.1 시작 전 체크리스트
1. **`git status --short` 먼저 실행** → 보류 중인 변경(섹션 8)이 손상되지 않았는지 확인.
2. **`git log --oneline -3` 확인** → 기준 커밋(`2c3fa33`, `1b7b995`)이 유효한지 확인.
3. 작업 대상 파일이 **섹션 3(전투 UI) 또는 섹션 2(진입점)** 에 있는지 확인.
4. 수정하려는 파일이 **섹션 6(수정 금지)** 에 없는지 확인.

### 9.2 절대 금지
- ❌ **무관한(unrelated) 변경을 되돌리지 말 것** — `git checkout -- .`, `git restore .`, `git reset --hard` 금지.
- ❌ **Kotlin 소스 수정 전 대상 파일을 확인하지 않고 edit 하지 말 것** — 특히 `CombatScreen/Arena/DiceBoard` 외 파일에 실수로 손대지 말 것.
- ❌ **목업 폴더 수정 금지** — `/Users/a1/Desktop/video_test/*` 하위 어떤 파일도 수정/삭제/생성 금지.
- ❌ **APK 빌드/설치가 필요 없으면 하지 말 것** — 작업이 UI 코드 수정만으로 끝난다면 `assembleDebug` / `installDebug` 금지. 사용자가 명시적으로 요청한 경우에만 빌드.
- ❌ **`git add -A` / `git commit` / `git push` 금지** — 사용자가 명시적으로 지시한 경우에만.
- ❌ **`.omo/`, `.playwright-mcp/` 내 파일을 수정/삭제 금지**.
- ❌ **레퍼런스 이미지를 통째로 앱에 임베드 금지** (섹션 7).

### 9.3 허용되는 작업
- ✅ 섹션 2/3 경로 내 Kotlin 파일 수정 (사용자 명시적 요청 시).
- ✅ Compose 코드로 레퍼런스를 **컴포넌트 단위로 재현**.
- ✅ `lsp_diagnostics` / `git status` / `git diff` / `git log` 같은 읽기 전용 검사.
- ✅ 사용자가 명시적으로 승인한 파일만 stage/commit.

### 9.4 작업 종료 후 체크리스트
1. `git status --short` 출력 → 보류 변경(섹션 8)이 보존되었는지 확인.
2. 변경한 파일 목록 출력 → 의도한 파일만 수정되었는지 확인.
3. `lsp_diagnostics` 로 변경 파일 타입/문제 확인.
4. 사용자에게 **수정 요약 + 검증 결과** 보고 후 대기.

---

## 10. 문서 우선순위 (Document Precedence)

충돌 시 우선순위 (높은 순):

1. **이 문서 (SOURCE_OF_TRUTH.md)**
2. `YACOO_RPG_GAME_PLAN_V1.md` (게임 방향성)
3. `ANTIGRAVITY_3_REFERENCE_UI_REDESIGN_DIRECTIVE.md` (3-레퍼런스 리디자인 지시)
4. `ANTIGRAVITY_TOTAL_REDESIGN_REBUILD_DIRECTIVE.md` (전면 리빌드 지시)
5. `ANTIGRAVITY_OPUS_UI_REDESIGN_BRIEF.md` (Opus 브리프)
6. `ANTIGRAVITY_HANDOFF.md` (초기 핸드오프)
7. `ART_DIRECTION_OVERHAUL_BRIEF.md` / `IMPLEMENTATION_OVERHAUL_BRIEF.md` (superseded, 참고용)

> 이전 문서들이 서로 다른 디자인 방향(밝은 카툰 vs 다크 판타지 vs 네온)을 제시할 수 있다.
> **전투 UI의 현재 기준은 섹션 4의 커밋 `2c3fa33` 과 섹션 7의 레퍼런스 이미지**다. 문서가 충돌하면 코드 베이스라인을 따른다.

---

## 11. 변경 이력 (Change Log)

| 날짜 | 내용 |
|---|---|
| 2026-06-24 | 최초 작성. 기준 커밋 `2c3fa33`(전투 UI), `1b7b995`(.gitignore) |
