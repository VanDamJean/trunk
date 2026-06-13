# 주사위 굴림 애니메이션 (둥둥 떠다니며 착지)

## TL;DR
> **Summary**: 주사위 굴림 시 bounce/float 애니메이션 추가
> **Deliverables**: DiceRow 애니메이션, App 상태 연결, CSS keyframes
> **Effort**: Quick
> **Parallel**: NO

## Context

### Original Request
주사위 굴리기 버튼 클릭 시 주사위가 둥둥 떠올랐다가 착지하는 애니메이션 추가.

### 현재 동작
- `rollDice()` 호출 시 즉시 새 주사위 값으로 교체됨
- 애니메이션 없음

### 목표 동작
- 굴리기 버튼 클릭 → 주사위가 위로 살짝 올랐다가 떨어지며 정착
- 애니메이션 중에는 숫자가 빠르게 변하다가 멈춤
- 고정된 주사위는 애니메이션 없음 (고정 상태 유지)

## TODOs

- [x] 1. App.tsx에 isRolling 상태 추가 및 rollDice 래핑

  **What to do**:
  - `useState<boolean>`로 `isRolling` 추가
  - `handleRoll` 함수에서 `setIsRolling(true)` 후 600ms 후 `setIsRolling(false)`
  - `isRolling`을 `DiceRow`에 prop으로 전달
  - `game.dice`가 변경될 때만 굴림 상태 활성화 (중복 트리거 방지)

  **File**: `src/App.tsx`

  **Acceptance Criteria**:
  - [x] `isRolling` 상태가 rollDice 호출 시 true, 600ms 후 false
  - [x] DiceRow에 `isRolling` prop 전달

- [x] 2. DiceRow에서 isRolling prop 받아 CSS 클래스 적용

  **What to do**:
  - `DiceRowProps`에 `isRolling: boolean` 추가
  - 굴리는 중이고 고정되지 않은 주사위에만 `data-rolling="true"` 속성 추가
  - 고정된 주사위는 `data-rolling="false"` 유지

  **File**: `src/components/DiceRow.tsx`

  **Acceptance Criteria**:
  - [x] `isRolling=true`일 때 고정되지 않은 주사위에 `data-rolling="true"`
  - [x] 고정된 주사위는 rolling 효과 받지 않음

- [x] 3. CSS keyframes로 bounce/float 애니메이션 추가

  **What to do**:
  - `@keyframes dice-bounce` 정의:
    - 0%: translateY(0)
    - 40%: translateY(-20px) rotate(-8deg)
    - 70%: translateY(-8px) rotate(4deg)
    - 100%: translateY(0) rotate(0)
  - `.die-button[data-rolling='true']`에 애니메이션 적용
  - `animation: dice-bounce 0.6s ease-out`
  - 숫자 토글 효과: 굴리는 중에는 임의 숫자 보여주기 (선택적)

  **File**: `src/styles.css`

  **Acceptance Criteria**:
  - [x] `data-rolling="true"` 주사위가 bounce 애니메이션 재생
  - [x] 0.6s 후 자연스럽게 정착
  - [x] 모바일에서도 부드럽게 동작 (transform 사용)

- [x] 4. (선택) 숫자 토글 효과 - 굴리는 중 숫자 빠르게 변하기

  **What to do**:
  - `isRolling` 중일 때 50ms마다 랜덤 1~6 표시
  - 600ms 후 실제 값으로 정착
  - DiceRow 내부에 로컬 state로 구현

  **File**: `src/components/DiceRow.tsx`

  **Acceptance Criteria**:
  - [x] 굴리는 중 숫자가 빠르게 변함
  - [x] 정착 후 실제 주사위 값 표시

## Final Verification

- [x] F1. 브라우저에서 굴리기 버튼 클릭 시 주사위가 둥둥 떠오르며 착지하는지 확인
- [x] F2. 고정된 주사위는 애니메이션 없이 유지되는지 확인
- [x] F3. 모바일 화면에서 애니메이션 부드러운지 확인
- [x] F4. `pnpm test`, `pnpm build` 통과
