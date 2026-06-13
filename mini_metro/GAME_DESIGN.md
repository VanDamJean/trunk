# 🚇 Mini Metro 클론 — 게임 설계서 (Game Design Document)

> **프로젝트명**: Mini Metro Clone (가제)
> **엔진**: Python + Pygame (프로토타입) → 추후 Android(Buildozer/Kivy) 이식
> **장르**: 미니멀리스트 교통 전략/퍼즐
> **타겟 플랫폼**: PC (프로토타입) → 모바일 이식 목표
> **기준 문서**: [GAME_ANALYSIS.md](./GAME_ANALYSIS.md)

---

## 목차
1. [게임 컨셉](#1-게임-컨셉)
2. [기술 아키텍처](#2-기술-아키텍처)
3. [Station 시스템](#3-station-시스템)
4. [Passenger 시스템](#4-passenger-시스템)
5. [Line 시스템](#5-line-시스템)
6. [Train 시스템](#6-train-시스템)
7. [자원 & 보상](#7-자원--보상)
8. [게임 흐름 & 난이도](#8-게임-흐름--난이도)
9. [맵 시스템](#9-맵-시스템)
10. [UI & HUD](#10-ui--hud)
11. [사운드](#11-사운드)
12. [모바일 이식 계획](#12-모바일-이식-계획)
13. [개발 로드맵](#13-개발-로드맵)

---

## 1. 게임 컨셉

### 1.1 핵심 루프
```
역 생성 → 승객 스폰 → 플레이어가 노선 연결
    ↑                              ↓
    ← ← 점점 더 많은 역 ← ← 열차가 승객 이송
                                   ↓
                            역 과부하 시 게임오버
```

### 1.2 핵심 재미 요소
| 요소 | 설명 |
|------|------|
| **제한된 자원** | 노선·열차·객차는 한정, 역은 무한 증가 |
| **실시간 재편성** | 언제든 노선 지우고 다시 그을 수 있음 |
| **트레이드오프** | 새 노선 추가 vs 기존 노선 강화 |
| **점진적 압박** | 시간이 갈수록 역·승객 폭증 → 긴장감 |

### 1.3 게임 모드
- **노멀 모드**: 게임오버까지 최대한 버티기 (원작)
- **엔드리스 모드**: 게임오버 없이 자유롭게 (Phase 3)

---

## 2. 기술 아키텍처

### 2.1 파일 구조
```
mini_metro/
├── main.py                  # 엔트리 포인트
├── GAME_ANALYSIS.md         # 원작 분석 문서
├── GAME_DESIGN.md           # 이 문서
│
├── engine/
│   ├── __init__.py
│   ├── game.py              # 게임 메인 루프, 상태 관리
│   ├── renderer.py          # 렌더링 엔진 (도형, 노선, 열차)
│   ├── input_handler.py     # 입력 처리 (마우스/터치 추상화)
│   └── camera.py            # 카메라 (팬/줌)
│
├── entities/
│   ├── __init__.py
│   ├── station.py           # 역 (도형, 승객 관리)
│   ├── passenger.py         # 승객 (목적지, 경로)
│   ├── line.py              # 노선 (역 연결, 색상)
│   └── train.py             # 열차 (이동, 승하차)
│
├── systems/
│   ├── __init__.py
│   ├── spawner.py           # 역/승객 스폰 매니저
│   ├── pathfinder.py        # 승객 경로 탐색 (BFS)
│   ├── resource_manager.py  # 자원 관리 (열차/객차/노선)
│   ├── score.py             # 점수 시스템
│   └── difficulty.py        # 난이도 스케일링
│
├── ui/
│   ├── __init__.py
│   ├── hud.py               # 인게임 HUD
│   ├── line_panel.py        # 하단 노선 패널
│   ├── resource_popup.py    # 주간 보상 선택 UI
│   ├── game_over.py         # 게임오버 화면
│   └── menu.py              # 메인 메뉴
│
├── maps/
│   ├── __init__.py
│   ├── base_map.py          # 맵 기본 클래스
│   ├── seoul.py             # 서울 맵 (기본)
│   └── london.py            # 런던 맵
│
├── utils/
│   ├── __init__.py
│   ├── constants.py         # 상수 정의
│   ├── colors.py            # 색상 팔레트
│   └── math_utils.py        # 벡터/거리 계산
│
└── assets/
    ├── fonts/               # 폰트
    └── sounds/              # 효과음
```

### 2.2 게임 루프
```python
# 목표 FPS: 60
while running:
    dt = clock.tick(60) / 1000.0

    handle_input(dt)          # 마우스/터치 입력
    update_spawner(dt)        # 역/승객 스폰
    update_trains(dt)         # 열차 이동 & 승하차
    update_pathfinder()       # 승객 경로 재계산
    check_overflow(dt)        # 역 과부하 체크
    update_difficulty(dt)     # 난이도 증가
    render(dt)                # 렌더링
```

### 2.3 좌표계 & 해상도
```python
# 기본 해상도 (모바일 세로 기준으로 설계)
DESIGN_WIDTH = 720
DESIGN_HEIGHT = 1280

# PC에서는 가로로 돌려서 사용
PC_WIDTH = 1280
PC_HEIGHT = 720

# 모든 좌표는 논리 좌표로 처리 → 해상도 독립적
```

---

## 3. Station 시스템

### 3.1 도형 종류 & 구현
```python
class ShapeType(Enum):
    CIRCLE = 0      # ● 원
    TRIANGLE = 1    # ▲ 삼각형
    SQUARE = 2      # ■ 사각형
    STAR = 3        # ✦ 별 (중반 이후)
    CROSS = 4       # ✚ 십자 (중반 이후)
    DIAMOND = 5     # ◆ 다이아몬드 (후반)
```

### 3.2 역 속성
```python
class Station:
    shape: ShapeType           # 역의 도형
    position: Vector2          # 맵 좌표
    passengers: List[Passenger]  # 대기 승객 목록
    connected_lines: List[Line]  # 연결된 노선
    max_capacity: int = 6      # 과부하 임계값
    overflow_timer: float = 0  # 과부하 시 카운트다운 (초)
    overflow_max: float = 45.0 # 과부하 제한 시간 (45초)
    is_overflowing: bool       # 과부하 상태 여부
```

### 3.3 역 생성 규칙
| 조건 | 값 |
|------|-----|
| 초기 역 수 | 3개 (원·삼각·사각 각 1개) |
| 생성 간격 (초반) | 30~40초 |
| 생성 간격 (후반) | 10~15초 |
| 희귀 도형 등장 | 역 10개 이상부터 |
| 최소 역 간 거리 | 80px (겹침 방지) |
| 강 위 생성 | 불가 |

### 3.4 과부하 시스템
```
승객 수 < 6  → 정상 (초록)
승객 수 ≥ 6  → 과부하 시작
              → 원형 타이머 시계방향 감소 (45초)
              → 타이머 0 도달 → 게임오버
승객 수 < 6  → 과부하 해제, 타이머 리셋
```

---

## 4. Passenger 시스템

### 4.1 승객 속성
```python
class Passenger:
    destination_shape: ShapeType  # 가고 싶은 도형
    state: PassengerState         # WAITING | ON_TRAIN | ARRIVED
    spawn_time: float             # 스폰 시각 (통계용)
```

### 4.2 스폰 규칙
- 자신이 있는 역의 도형과 **다른** 도형이 목적지
- 목적지 도형은 현재 맵에 **존재하는** 도형 중에서만 선택
- 스폰 빈도: 시간에 따라 증가 (난이도 스케일링)

```python
# 스폰 간격 (초)
spawn_interval = max(2.0, 8.0 - (game_time / 60.0))
# 초반 8초에 1명 → 후반 2초에 1명
```

### 4.3 경로 탐색 (BFS)
```python
def find_path(start_station, destination_shape, lines):
    """
    BFS로 시작역에서 목적지 도형 역까지의 경로 탐색
    노드: Station
    간선: 같은 Line에 연결된 Station 쌍
    목표: destination_shape과 같은 shape인 Station 도달
    반환: List[Line] (타야 할 노선 순서)
    """
    queue = deque([(start_station, [])])
    visited = {start_station}

    while queue:
        current, path = queue.popleft()
        if current.shape == destination_shape:
            return path

        for line in current.connected_lines:
            for neighbor in line.get_neighbors(current):
                if neighbor not in visited:
                    visited.add(neighbor)
                    queue.append((neighbor, path + [line]))

    return None  # 경로 없음 → 대기
```

### 4.4 승하차 로직
```
[열차 도착]
  1. 하차: 이 역의 shape == passenger.destination_shape → 하차, score +1
  2. 환승 하차: 현재 노선으로 목적지 못 감 → 이 역에서 다른 노선 있으면 하차
  3. 승차: 열차 빈 자리 있고 + 경로 존재 → 승차 (선착순)
```

---

## 5. Line 시스템

### 5.1 노선 속성
```python
class Line:
    color: Color               # 노선 색상
    stations: List[Station]    # 연결된 역 (순서대로)
    trains: List[Train]        # 이 노선의 열차들
    is_loop: bool = False      # 순환 노선 여부
    is_active: bool = True     # 활성 상태
```

### 5.2 사용 가능 색상 (순서대로)
| 순서 | 색상 | Hex | 초기 제공 |
|------|------|-----|----------|
| 1 | 빨강 | `#E74C3C` | ✅ |
| 2 | 파랑 | `#3498DB` | ✅ |
| 3 | 초록 | `#2ECC71` | ✅ |
| 4 | 노랑 | `#F1C40F` | ❌ (자원) |
| 5 | 보라 | `#9B59B6` | ❌ (자원) |
| 6 | 주황 | `#E67E22` | ❌ (자원) |
| 7 | 청록 | `#1ABC9C` | ❌ (자원) |

### 5.3 노선 편집 인터랙션
```
[노선 그리기]
  1. 하단 패널에서 노선 색상 선택 (또는 빈 색상 탭 터치)
  2. 역 위에서 마우스 다운 → 드래그 → 다른 역 위에서 마우스 업
  3. 두 역 사이에 선분 생성
  4. 계속 드래그하면 다음 역까지 연장

[역 삽입]
  기존 노선 끝에서 드래그 → 새 역까지

[구간 제거]
  노선 끝 역에서 안쪽으로 드래그 → 마지막 구간 삭제

[노선 삭제]
  노선 패널에서 해당 색상 길게 누르기 → 전체 삭제
  → 소속 열차/객차는 자원 풀로 반환
```

### 5.4 노선 렌더링
```python
# 각 노선은 두꺼운 선 + 약간 offset
LINE_THICKNESS = 8  # px
LINE_OFFSET = 6     # 같은 구간에 여러 노선 겹칠 때 offset

# 역 연결 선분: 직선 (곡선은 Phase 3)
# 강 건너는 구간: 점선 표현
```

---

## 6. Train 시스템

### 6.1 열차 속성
```python
class Train:
    line: Line                 # 소속 노선
    segment_index: int         # 현재 구간 인덱스
    segment_progress: float    # 구간 내 진행도 (0.0~1.0)
    direction: int = 1         # +1 정방향, -1 역방향
    speed: float = 200.0       # 픽셀/초
    passengers: List[Passenger]
    capacity: int = 6          # 기본 용량
    carriages: int = 0         # 추가 객차 수
    state: TrainState          # MOVING | BOARDING
    dwell_timer: float = 0     # 정차 타이머
```

### 6.2 이동 로직
```python
def update(self, dt):
    if self.state == MOVING:
        self.segment_progress += (self.speed * dt) / segment_length
        if self.segment_progress >= 1.0:
            self.arrive_at_station()

    elif self.state == BOARDING:
        self.dwell_timer -= dt
        if self.dwell_timer <= 0:
            self.depart()

def arrive_at_station(self):
    # 하차 처리
    # 승차 처리
    self.state = BOARDING
    self.dwell_timer = 0.3 + 0.1 * passengers_exchanged

def depart(self):
    self.state = MOVING
    # 왕복: 마지막 역이면 direction *= -1
    # 순환: 마지막 역이면 첫 역으로
```

### 6.3 열차 렌더링
```
열차 본체: 작은 직사각형 (노선 색상)
객차: 뒤에 작은 사각형 연결 (개수만큼)
승객: 열차 위에 작은 도형 아이콘 표시
```

---

## 7. 자원 & 보상

### 7.1 주간 보상 시스템
```python
WEEK_DURATION = 120.0  # 게임 내 1주 = 실시간 2분

# 매주 종료 시 자원 선택 팝업
# 2개 옵션 중 1개 선택 (원작: 고정 1개 + 선택 1개)
weekly_reward = {
    "guaranteed": Train(),          # 항상 열차 1대 지급
    "choice_a": Carriage(),         # 선택지 A
    "choice_b": Line() or Tunnel(), # 선택지 B
}
```

### 7.2 자원 풀
```python
class ResourceManager:
    available_trains: int = 0   # 배치 가능한 여분 열차
    available_carriages: int = 0
    available_tunnels: int = 0
    unlocked_lines: int = 3     # 사용 가능 노선 수
    max_lines: int = 7
```

---

## 8. 게임 흐름 & 난이도

### 8.1 시간별 진행
| 시간 | 이벤트 |
|------|--------|
| 0:00 | 역 3개 (●▲■) 시작, 노선 3색 제공 |
| 0:30 | 4번째 역 생성 |
| 1:00 | 5~6번째 역, 승객 스폰 빈도 증가 |
| 2:00 | 1주차 보상 (열차 + 선택) |
| 3:00 | 역 8~10개, 희귀 도형(★✚) 등장 |
| 4:00 | 2주차 보상, 노선 4번째 해금 가능 |
| 6:00+ | 역 15개 이상, 생존 어려워짐 |
| 10:00+ | 숙련자 영역, 끊임없는 재편성 필요 |

### 8.2 난이도 스케일링
```python
class DifficultyManager:
    def get_station_spawn_interval(self, game_time):
        # 초반 35초 → 후반 12초
        return max(12.0, 35.0 - game_time * 0.05)

    def get_passenger_spawn_interval(self, game_time):
        # 초반 8초 → 후반 1.5초
        return max(1.5, 8.0 - game_time * 0.02)

    def get_rare_shape_chance(self, station_count):
        # 역 10개 이상부터 20% 확률로 희귀 도형
        if station_count < 10:
            return 0.0
        return min(0.3, 0.05 * (station_count - 10))
```

---

## 9. 맵 시스템

### 9.1 맵 구성
```python
class GameMap:
    name: str                  # "서울", "런던" 등
    background_color: Color    # 배경색
    water_color: Color         # 강/물 색상
    river_polygons: List       # 강 폴리곤 좌표
    spawn_area: Rect           # 역 생성 가능 영역
    initial_stations: List     # 초기 역 3개 위치 & 도형
```

### 9.2 Phase 1 맵: 서울 (단순)
```
┌──────────────────────────────┐
│                              │
│      ●           ▲          │
│                              │
│  ~~~~~~~~~~한강~~~~~~~~~~    │
│                              │
│              ■               │
│                              │
└──────────────────────────────┘
강: 가로 방향 (터널 필요)
```

### 9.3 맵 확장 계획
| Phase | 맵 | 특징 |
|-------|-----|------|
| 1 | 기본 (강 없음) | 장애물 없음, 학습용 |
| 2 | 서울 | 한강 1개 (터널 필수) |
| 3 | 런던 / 도쿄 | 복잡한 강, 섬 지형 |

---

## 10. UI & HUD

### 10.1 인게임 레이아웃
```
┌──────────────────────────────────────────┐
│  [⏸]              1,234             [⏩] │  ← 상단 바
├──────────────────────────────────────────┤
│                                          │
│                                          │
│            (게임 맵 캔버스)              │
│                                          │
│                                          │
│                                          │
├──────────────────────────────────────────┤
│  [● 빨강] [● 파랑] [● 초록] [+ 잠금]   │  ← 노선 패널
│  [🚂 x1] [🚃 x2] [🌉 x0]              │  ← 자원 표시
└──────────────────────────────────────────┘
```

### 10.2 UI 상세
| 요소 | 위치 | 기능 |
|------|------|------|
| 일시정지 | 좌상 | 게임 정지 (노선 편집 가능) |
| 점수 | 상단 중앙 | 이송 승객 수 |
| 배속 | 우상 | 1x ↔ 2x 전환 |
| 노선 패널 | 하단 | 색상 선택, 활성 노선 표시 |
| 자원 표시 | 하단 | 여분 열차/객차/터널 수 |
| 주간 보상 | 중앙 팝업 | 2개 중 택 1 |
| 게임오버 | 전면 | 최종 점수, 리플레이 |
| 과부하 경고 | 역 위 | 원형 타이머 |

### 10.3 메인 메뉴
```
┌──────────────────────────────────────────┐
│                                          │
│             M I N I                      │
│             M E T R O                    │
│                                          │
│          ──────●──────                   │
│          ──●──────●──                    │
│                                          │
│           [▶ 시작하기]                   │
│           [🗺 맵 선택]                   │
│           [⚙ 설정]                      │
│                                          │
│         Best: 2,847                      │
└──────────────────────────────────────────┘
```

---

## 11. 사운드

### 11.1 효과음 목록
| 이벤트 | 설명 | 우선순위 |
|--------|------|---------|
| 노선 연결 | 부드러운 클릭음 | P1 |
| 승객 승차 | 짧은 팝 | P1 |
| 승객 하차 | 짧은 딩 | P1 |
| 역 생성 | 등장 효과음 | P1 |
| 과부하 경고 | 긴장감 비프음 | P1 |
| 게임오버 | 느린 멜로디 | P1 |
| 주간 보상 | 팡파레 | P2 |
| 열차 이동 | 부드러운 레일음 (루프) | P3 |

### 11.2 BGM
- 미니멀 앰비언트 (로우파이 느낌)
- 게임 진행에 따라 레이어 추가 (역 10개마다 악기 추가)

---

## 12. 모바일 이식 계획

### 12.1 입력 추상화
```python
class InputHandler:
    """마우스와 터치를 동일하게 처리"""
    def get_pointer_pos(self) -> Vector2:
        # PC: 마우스 위치
        # Mobile: 터치 위치
        pass

    def is_pointer_down(self) -> bool:
        # PC: 마우스 좌클릭
        # Mobile: 터치 중
        pass

    def is_pointer_up(self) -> bool:
        # PC: 마우스 릴리즈
        # Mobile: 터치 릴리즈
        pass
```

### 12.2 Android 이식 경로
```
Pygame (PC 프로토타입)
    ↓
Pygame + 입력 추상화 완료
    ↓
Buildozer로 APK 빌드 (pygame-ce + python-for-android)
  또는
Kivy 포팅 (렌더링 레이어만 교체)
    ↓
Android APK
```

### 12.3 모바일 고려사항
| 항목 | PC | Mobile |
|------|-----|--------|
| 입력 | 마우스 + 키보드 | 터치 + 제스처 |
| 해상도 | 1280×720 | 720×1280 (세로) |
| 일시정지 | Space | 상단 버튼 |
| 배속 | Tab | 상단 버튼 |
| 노선 편집 | 마우스 드래그 | 터치 드래그 |
| FPS 타겟 | 60 | 30~60 |

---

## 13. 개발 로드맵

### Phase 1: 코어 프로토타입 ✅
- [x] 프로젝트 구조 세팅 (Pygame)
- [x] 역 렌더링 (도형 3종: ●▲■)
- [x] 노선 드래그 연결 (직선)
- [x] 열차 기본 이동 (왕복)
- [x] 승객 스폰 & 목적지 표시
- [x] 승하차 로직
- [x] 기본 점수 카운터
- [x] 과부하 → 게임오버 판정
- [x] 기본 HUD (점수, 노선 패널)

### Phase 2: 핵심 시스템
- [ ] 승객 경로 탐색 (BFS, 환승 포함)
- [ ] 노선 편집 (역 추가/삭제)
- [ ] 주간 자원 보상 팝업
- [ ] 객차 시스템
- [ ] 일시정지 & 2배속
- [ ] 난이도 스케일링
- [ ] 희귀 도형 (★✚) 추가
- [ ] 과부하 타이머 시각 효과

### Phase 3: 맵 & 폴리싱
- [ ] 강/물 렌더링 + 터널 시스템
- [ ] 서울 맵 (한강)
- [ ] 메인 메뉴 & 맵 선택
- [ ] 하이스코어 저장 (JSON)
- [ ] 사운드 효과
- [ ] 엔드리스 모드
- [ ] 시각 폴리싱 (애니메이션)

### Phase 4: 모바일 이식
- [ ] 입력 추상화 (터치)
- [ ] 세로 해상도 대응
- [ ] Buildozer APK 빌드
- [ ] 터치 제스처 최적화
- [ ] 성능 최적화 (30fps 보장)

---

> **📌 다음 단계**: Phase 2 핵심 시스템 구현 진행 중
> 승객 경로 탐색(BFS) 및 노선 편집 기능 고도화 필요. 첫 프로토타입의 부족한 그래픽/조작감 개선 예정.
