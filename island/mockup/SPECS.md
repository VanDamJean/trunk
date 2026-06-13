# Island Mockup — 구현 스펙

> 마지막 업데이트: 2026-04-29  
> 파일: `island/mockup/index.html` (단일 파일, CSS/JS 인라인, 빌드 불필요)

---

## 구현된 화면 목록

| 화면 ID | 이름 | 상태 |
|---|---|---|
| `screen-home` | Home (스플래시) | ✅ |
| `screen-play` | Play (맵 뷰) | ✅ |
| `screen-level` | Level 선택 그리드 | ✅ |
| `screen-game` | Game Play (매치-3) | ✅ |
| `modal-level-select` | Level Select 팝업 | ✅ |
| `modal-begin` | Begin / Target 팝업 | ✅ |
| `modal-win` | You Win 팝업 | ✅ |
| `modal-settings` | Settings 팝업 | ✅ |
| `modal-edit-profile` | Edit Profile 팝업 | ✅ |
| `modal-tasks` | Tasks 팝업 | ✅ |
| `modal-golden-key` | Golden Key 팝업 | ✅ |

---

## 네비게이션 플로우

```
Home → [Play] → Play 맵
Play 맵 → [Level] → Level 그리드
Level 그리드 → [레벨 탭] → Level Select 팝업
Level Select → [▶] → Begin 팝업
Begin → [Start!] → Game Play (그리드 초기화)

Game Play → [타겟 소진 or 무브 0] → You Win 팝업
You Win → [▶/🔄] → Begin 팝업 (재시작)
You Win → [📋] → Tasks 팝업

Play 맵 → [⚙️] → Settings 팝업
Settings → [Edit Profile] → Edit Profile 팝업
Play 맵 → [📋] → Tasks 팝업
Play 맵 → [Zone 칩] → Golden Key 팝업
Play 맵 → [🏠] → Home
```

---

## 공통 컴포넌트 스펙

### 상단 바 `.top-bar`
| 속성 | 값 |
|---|---|
| 크기 | 360 × 56px |
| 배경 | `linear-gradient(180deg, #6B3A20, #4A2810)` |
| 하단 보더 | `2px solid #2A1408` |
| 젬 아이콘 (좌) | 32×32px, `#2E7D32`, border-radius 6px |
| 코인 원 | 24×24px, gold radial-gradient |
| 수량 텍스트 | white, 900w, 17px |
| + 버튼 | 22×22px 원형, `#43A047` |
| 열쇠 핍 (우) | 18×18px, `#C62828` |

### 하단 내비 `.bottom-nav`
| 속성 | 값 |
|---|---|
| 크기 | 360 × 70px, `position: absolute; bottom: 0` |
| 배경 | `linear-gradient(180deg, #5C3A20, #4A2810)` |
| 상단 보더 | `2px solid #2A1408` |
| 버튼 지름 | 48px |
| 버튼 배경 | `radial-gradient(circle at 35% 35%, #FFE082, #F5A623, #D4860A)` |
| 버튼 보더 | `3px solid #B8780A` |
| 버튼 그림자 | `0 3px 0 #7A5000` |

### 모달 `.modal`
| 속성 | 값 |
|---|---|
| 너비 | 300px |
| 배경 | `#F5E6C8` (크림 양피지) |
| border-radius | 14px |
| 보더 | `3px solid #C4A47C` |
| 리본 높이 | 52px |
| 리본 배경 | `linear-gradient(180deg, #4CAF50, #2E7D32)` |
| 리본 보더 | `3px solid #1B5E20` |
| 리본 장식 (::before/::after) | 좌우 18×36px 돌출 스크롤 효과 |
| 타이틀 | white, 900w, 22px |
| X 버튼 | 26×26px, gold radial-gradient, `3px solid #B8780A` |

### 초록 버튼 `.btn-green`
| 속성 | 값 |
|---|---|
| 높이 | 44px, width 100% |
| 배경 | `linear-gradient(180deg, #66BB6A, #43A047, #2E7D32)` |
| 보더 | `3px solid #1B5E20` |
| 그림자 | `0 4px 0 #1B5E20` |
| 텍스트 | white, 900w, 16px |
| active 피드백 | `translateY(3px)` + shadow 제거 |

---

## 화면별 스펙

### Home (`screen-home`)
| 요소 | x | y | w×h | 스펙 |
|---|---|---|---|---|
| 배경 | 0 | 0 | 360×800 | sky→sea→sand CSS gradient |
| 구름 5개 | 14~170 | 52~100 | 각 다름 | white blur 타원 (`filter: blur(1.5px)`) |
| ISLAND 로고 | 33 | 190 | 295×120 | 66px, 900w, italic, gold+purple text-shadow |
| Play 버튼 외곽 | 110 | 390 | 141×59 | 나무 갈색 frame, border-radius 10px |
| Play 버튼 내부 | 119 | 399 | 123×39 | gold gradient, 22px italic "Play" |
| 보물상자 | 105 | 530 | 150×130 | CSS art (lid + body + latch + coin 이모지) |
| 바위 2개 | 좌/우 | ~500 | 50~60px | gradient 타원 |

### Play 맵 (`screen-play`)
| 요소 | x | y | w×h | 스펙 |
|---|---|---|---|---|
| 상단 바 | 0 | 0 | 360×56 | 공통 |
| 맵 씬 | 0 | 56 | 360×604 | 야자수 2개, 집(body+roof+door+window), 바위 |
| Level 버튼 | 16 | 726 | flex×44 | `.btn-level` 초록 gradient |
| Zone 칩 | 우측 | 726 | 128×44 | 초록, zone명+진행도, 숫자 뱃지 22px |
| 하단 내비 | 0 | 730 | 360×70 | ⚙️ 설정 / 📋 태스크 / 🏠 홈 |

### Level 그리드 (`screen-level`)
| 속성 | 값 |
|---|---|
| 배경 | sky→sea→sand gradient + 해적선 🏴‍☠️ 장식 (opacity 0.12) |
| 그리드 | 4열, `gap: 9px`, `justify-content: center` |
| 셀 크기 | 74×74px, border-radius 10px, `border: 3px solid #2A1408` |
| 잠금해제 (1~10) | `#FFE082→#D4860A`, shadow `0 4px 0 #B8780A` |
| 잠금 (11~16) | `#8B3A3A→#5A1A1A`, shadow `0 4px 0 #3A1010` |
| 숫자 | white, 900w, 22px |
| 별 3개 | 13px, 채움 `#FFD700` / 빈 `rgba(0,0,0,.35)`, 셀 상단 |
| 자물쇠 | 🔒 26px |
| Back 버튼 | x:16, y:726 | flex×44 | 초록 |
| < > 페이지 버튼 | 우측 | 726 | 60×44px | 초록 |
| 하단 내비 | — | 730 | 360×70 | ⚙️ / 🏠 2개만 |

### 게임 플레이 (`screen-game`)
| 요소 | x | y | w×h | 스펙 |
|---|---|---|---|---|
| 스코어 바 | 0 | 0 | 360×78 | `linear-gradient(#6B3A20, #4A2810)` |
| Score 셀 | — | — | flex×60 | gold gradient, `sc-val` 20px 900w |
| Target 셀 | — | — | flex×60 | 🔵 아이콘 + 숫자 |
| Moves 셀 | — | — | flex×60 | 숫자 |
| 진행 바 | 16 | 80 | 328px | 별 3개 + gold fill, transition 0.3s |
| 배경 씬 | 0 | 108 | 360×130 | 🌴🏴‍☠️⛵ 이모지 장식 |
| 젬 그리드 | 30 | 248 | 300×300 | 6×6, gap 3px, `touch-action: none` |
| 툴바 | 0 | 730 | 360×70 | 🔨⚗️💉🎲 4종, 56×56px, 수량 뱃지 |

### 젬 (`.gem`)
| 속성 | 값 |
|---|---|
| 크기 | 40×40px |
| border-radius | 9px |
| 보더 | `2px solid rgba(255,255,255,.25)` |
| 하이라이트 (::after) | 흰색 타원, rotate -25deg |
| 파랑 `.b` | `#1E88E5 → #1565C0` |
| 핑크 `.p` | `#E91E8C → #AD1457` |
| 노랑 `.y` | `#FFD54F → #F9A825` |
| 초록 `.g` | `#66BB6A → #2E7D32` |
| 선택 `.selected` | white glow `0 0 0 3px #fff`, `scale(1.12)`, z-index 2 |
| 매치 `.matched` | CSS `gem-flash` 애니메이션 (0.55s): 번쩍 후 scale(0) 소멸 |
| 새 등장 `.new-drop` | CSS `gem-drop` 애니메이션 (0.32s): 위에서 튀어 내려옴 |

---

## 게임 로직 (매치-3 엔진)

### 상태 변수
| 변수 | 타입 | 설명 |
|---|---|---|
| `grid[ROWS][COLS]` | `string[][]` | 현재 젬 색상 (null = 빈 칸) |
| `dragState` | `{r,c,x,y,el}` \| `null` | 드래그 중인 젬 정보 |
| `busy` | `boolean` | 애니메이션 처리 중 입력 차단 |
| `gScore` | `number` | 현재 점수 |
| `gTarget` | `number` | 남은 파란 젬 수집 목표 |
| `gMoves` | `number` | 남은 무브 수 |

### 드래그 스왑 흐름
```
pointerdown → dragState 설정, gem.style.transition = 'none'
pointermove → gem.style.transform = translate(dx,dy) scale(1.15)  ← 실시간
pointerup   → transform 초기화 → 방향 판별(상/하/좌/우) → trySwap()
              드래그 거리 < 18px이면 무시
```

### trySwap 흐름
```
swapGrid() + refreshCell() × 2    → DOM 즉시 반영
findMatches()
  매치 없음 → 350ms 후 swap back + busy = false
  매치 있음 → Moves-- → 150ms 후 processMatches()
```

### processMatches 흐름
```
1. 매치 칸에 .matched 클래스 부여 (CSS flash 애니 0.55s)
   grid[r][c] = null, gTarget--, gScore++

2. 600ms 대기 (flash 애니 종료)

3. wasNull snapshot (gravity 전 null 칸 목록)
   applyGravity() — null 칸을 아래로 채움, 상단 빈 칸에 랜덤 젬

4. wasNull에 해당하는 칸만 createGemEl(r, c, isNew=true) → .new-drop 애니
   기존 .matched 남은 칸은 refreshCell()로 교체

5. 420ms 대기 (drop 애니 완료)
   findMatches() → 연쇄 있으면 processMatches() 재호출 (Moves 추가 소진 없음)
                 → 없으면 busy = false, checkEndGame()
```

### 함수 목록
| 함수 | 역할 |
|---|---|
| `initGrid()` | 3-in-a-row 없도록 초기 grid 생성 |
| `buildGemGrid()` | `initGrid()` + `renderAll()` |
| `renderAll()` | grid 상태 기준 전체 DOM 재빌드 |
| `createGemEl(r,c,isNew)` | gem div 생성, pointerdown 이벤트 바인딩 |
| `refreshCell(r,c,isNew)` | 특정 셀만 교체 |
| `onPointerDown/Move/Up/Cancel` | 드래그 입력 처리 |
| `trySwap(r1,c1,r2,c2)` | 스왑 시도 + 매치 판정 |
| `swapGrid()` | grid 배열만 교환 |
| `findMatches()` | 가로/세로 3개+ 탐색, Set 반환 |
| `processMatches(matches)` | 제거 + 낙하 + 연쇄 전체 처리 |
| `applyGravity()` | 열 단위 null 칸 채우기 |
| `updateHUD()` | Score / Target / 진행바 갱신 |
| `checkEndGame()` | 종료 조건 체크 → You Win 팝업 |
| `resetGame()` | 게임 상태 초기화 |
| `startGame()` | Begin 팝업 닫기 + resetGame + nav('screen-game') |

---

## 인터랙션 목록

| 동작 | 결과 |
|---|---|
| Home Play 버튼 | → Play 맵 |
| Play Level 버튼 | → Level 그리드 |
| Level 셀 클릭 (잠금해제) | → Level Select 팝업 |
| Level Select ▶ | → Begin 팝업 |
| Begin Start! | → Game Play (그리드 초기화) |
| 젬 드래그 (≥18px) | 방향 판별 → 스왑 시도 |
| 매치 발생 | flash 애니 → 낙하 → 연쇄 |
| Target = 0 or Moves = 0 | → You Win 팝업 |
| You Win ▶/🔄 | → Begin 팝업 (재시작) |
| Settings Sound/Music | opacity 토글 |
| Flag 클릭 | 선택 border 변경 |
| Tasks Do it | 완료 처리 + World Progress 증가 |
| Zone 칩 | → Golden Key 팝업 |

---

## 알려진 버그 / 미구현

| 항목 | 상태 |
|---|---|
| 연쇄 처리 시 DOM 불일치 가능성 | 🐛 수정 중 |
| 낙하 물리 애니메이션 (젬이 실제로 내려오는 연출) | ❌ 미구현 (즉시 렌더) |
| 오디오 | ❌ 미구현 |
| 레벨별 배경/테마 변경 | ❌ 미구현 |
| 리더보드 | ❌ 미구현 |
| 골든 키 실제 획득 로직 | ❌ 미구현 |
| 부스트/툴바 아이템 실제 기능 | ❌ UI만 있음 |
