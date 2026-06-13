# 05 — 코딩 가이드라인 (작업 전 필독)

> 이 규칙은 **안드로이드 포팅 가능성**과 **테스트 안정성**을 위한 전제다. 위반하면 후속 마일스톤(M5)이 막힌다.

---

## 1. 아키텍처 원칙: `game/`는 순수하게

`src/game/` 폴더는 **순수 TypeScript 로직 레이어**다. 게임의 모든 규칙·수치·계산이 여기 있고, 나중에 Kotlin으로 1:1 포팅된다.

**`game/` 안에서 금지:**
- React import (`useState`, JSX 등) 금지
- DOM/`window`/`document`/`localStorage` 직접 접근 금지 (단, `storage.ts`는 예외적으로 `Storage`를 **인자로 주입**받음 — 현재 패턴 유지)
- 전역 `Math.random()` / `Date.now()` 직접 호출 금지 → **반드시 주입**(아래 §2)
- 콘솔/네트워크/타이머 등 부수효과 금지

**레이어 책임:**
| 레이어 | 책임 | 부수효과 |
|---|---|---|
| `game/` | 규칙·수치·계산(순수) | 없음 |
| `hooks/` | React 상태 ↔ `game/` 연결, 저장 트리거 | 있음(React) |
| `screens/`, `components/` | 렌더·입력 | 있음(React/DOM) |

> UI에서 게임 규칙을 계산하지 말 것. 규칙이 필요하면 `game/`에 순수 함수를 추가하고 호출만 한다.

---

## 2. RNG는 항상 주입

이미 `Rng` 타입(`type Rng = () => number`)이 있고 `rollDice`, `rewards`가 이 패턴을 쓴다. **모든 무작위성은 이 패턴을 따른다.**

```ts
// 좋음: 기본값은 Math.random, 테스트/재현 시 주입 가능
export function createChapterMap(chapter: number, rng: Rng = Math.random): ChapterMap { ... }

// 나쁨: 내부에서 Math.random 직접 호출 (테스트 불가, 포팅 시 비결정)
export function createChapterMap(chapter: number): ChapterMap {
  const r = Math.random(); // 금지
}
```
- 런 생성 시 `seed`를 저장하고, 시드 기반 PRNG를 쓰면 재현/디버그/이어하기가 쉬워진다(권장).
- 테스트에서는 고정 rng(예: `() => 0.9`)로 결과를 단언한다(기존 `useGameState`의 `forceResult` 참고).

---

## 3. 저장 데이터는 평면 JSON

- `MetaSave`/`RunState`는 **`JSON.stringify` → `parse` 후 동일**해야 한다(클래스 인스턴스/함수/Map/Set/Date 금지, 원시값·배열·플레인 객체만).
- 안드로이드에서 DataStore로 매핑하므로 중첩은 얕고 명확하게.
- 스키마 변경 시 **반드시 `version` 올리고 마이그레이션 + 테스트** 추가(`03` §4).
- 손상/구버전 데이터는 절대 throw하지 말고 기본값으로 폴백(현재 `loadSave` 패턴 유지).

---

## 4. 타입 안전성

- TypeScript strict 전제. `any` 지양, 외부 입력은 타입 가드로 좁힌다(`storage.ts`의 `isRecord`/`isValidSave` 패턴).
- 유니온/리터럴 타입 적극 사용(`NodeType`, `Rarity`, `Screen` 등).
- 공개 함수는 입력/출력 타입을 명시한다.

---

## 5. 테스트

- **새 순수 모듈마다 단위 테스트 추가**(vitest). 기존 `*.test.ts`와 같은 위치/패턴.
- 반드시 테스트로 고정할 것:
  - 데미지 공식(눈값/족보/보정별), 족보 판정(주사위 2~5개 각각)
  - 마이그레이션(v1→v2), 손상 데이터 폴백
  - 맵 생성·보상 후보의 결정성(같은 seed→같은 결과)
  - 불변식(`03` §5)
- **기존 테스트를 깨지 말 것.** 동작 변경이 불가피하면 테스트도 갱신하고 사유를 남긴다.
- 명령:
  ```bash
  npm test -- --run     # 단위 테스트
  npm run build         # tsc -b + vite build (타입 검증 포함)
  npm run qa            # playwright 브라우저 QA (dev 서버 필요)
  ```
  (실행 절차 상세는 `README.ko.md` 참고. playwright 브라우저 미설치 시 `npx playwright install chromium`.)

---

## 6. 모바일 / 한 손 UX

- 레이아웃 기준 **세로 375×812**. 한 손으로 전 과정 조작 가능해야 함.
- 핵심 조작(주사위/족보/필살기/다음 노드)은 **하단 엄지존**에. 정보는 상단.
- 터치 타겟 ≥ 44px. 작은 칩/배지라도 탭 영역 확보.
- 기존 `Shell`/`BottomNav` 레이아웃을 활용해 화면을 추가한다.

---

## 7. UI / 자산 규칙

- **외부 게임 자산(딸깍 다이스 포함) 이미지/사운드/코드 복사 금지.** 메커니즘만 참고.
- 그림/아이콘은 프로젝트 자체 CSS/SVG 컴포넌트로(`components/ui.tsx`, `components/GameArt.tsx`). 새로 그리기 전에 **기존 컴포넌트 재사용 우선**(인벤토리는 `01` §4.4).
- UI 문구는 현재 영어로 통일. 새 UI도 영어로 일관 유지(코드 식별자는 항상 영어). 한글화가 필요하면 별도 작업.

---

## 8. 사운드 / 햅틱 추상화 (M4 대비)

플랫폼별 구현이 다르므로 **인터페이스로 분리**한다.
```ts
// 예: game/ 밖(예: src/platform/)에 둔다 — game/는 순수 유지
interface Feedback {
  play(sound: SoundId): void;
  vibrate(pattern: VibratePattern): void;
}
```
- `game/` 로직은 "언제 피드백이 나야 하는지"의 이벤트만 반환/노출하고, 실제 재생은 플랫폼 레이어가 담당.
- 웹: Web Audio / Vibration API. 안드로이드: 네이티브.

---

## 9. 변경 관리

- 마일스톤 단위로 작업하고, 끝낼 때 §5의 3종(빌드/테스트/QA)을 통과시킨다.
- 한 번에 너무 많이 바꾸지 말 것. 특히 M1의 Meta/Run 분리는 화면이 깨지기 쉬우니 작은 단위로 커밋.
- 상수/밸런스 값은 가급적 `constants.ts`에 모은다(흩뿌리지 말 것).
- 커밋/PR 설명에 "어떤 마일스톤의 어떤 항목"인지, 기존 테스트를 바꿨다면 그 이유를 남긴다.

---

## 10. 빠른 시작 체크 (작업 시작 전)
1. `00-INDEX.md`로 방향 3가지(하이브리드/런+메타/안드로이드 대비) 확인.
2. `01` 현재 상태 & 갭, `02` 디자인 공식, `03` 타입 숙지.
3. `04`의 **M1부터** 체크박스 따라 진행.
4. 매 단계 §5 3종 통과 유지.
5. `game/` 순수성과 RNG 주입 규칙을 절대 어기지 않기.
