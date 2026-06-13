# 🏗️ 코드 아키텍처 (Architecture Guide)

> GTA-Like 프로젝트의 코드 구조 및 안드로이드 포팅 가이드

---

## 1. 전체 구조 (MVC 패턴)

```
┌─────────────────────────────────────────────────────┐
│                    main.py                          │
│              (게임 루프 오케스트레이터)               │
│                                                     │
│   ┌──────────┐    ┌──────────┐    ┌──────────┐     │
│   │Controller│───▶│  Model   │◀───│   View   │     │
│   │ (입력)   │    │ (로직)   │    │ (렌더링) │     │
│   └──────────┘    └──────────┘    └──────────┘     │
│                                                     │
│   pygame 키보드    순수 Python     pygame 그리기     │
│   ──────────      ──────────     ──────────         │
│   Android에서     ✅ 그대로 사용   Android에서       │
│   터치로 교체                      교체              │
└─────────────────────────────────────────────────────┘
```

---

## 2. 폴더 구조

```
GTA_Like/
├── main.py                          # 엔트리포인트 (게임 루프)
│
├── game/
│   ├── __init__.py
│   ├── config.py                    # ⭐ 설정값 (pygame 무관)
│   │
│   ├── models/                      # ⭐ 순수 로직 (pygame 무관)
│   │   ├── __init__.py
│   │   ├── player.py                #   플레이어 위치/각도/이동
│   │   ├── vehicle.py               #   차량 물리 (가속/조향/마찰)
│   │   ├── bullet.py                #   총알 궤적
│   │   ├── citizen.py               #   시민 NPC 배회 AI
│   │   ├── police.py                #   경찰 NPC 추격 AI
│   │   ├── gang.py                  #   갱단 NPC (호감도/공격)
│   │   ├── boss.py                  #   보스 몬스터
│   │   ├── mission.py               #   공중전화 및 미션 로직
│   │   ├── garage.py                #   차량 수리/수배 해제 가라지
│   │   ├── expgem.py                #   경험치 젬 (뱀서 요소)
│   │   └── pickup.py                #   무기/아이템 픽업
│   │
│   ├── world/                       # ⭐ 맵 관련 데이터 (pygame 무관)
│   │   ├── __init__.py
│   │   └── map.py                   #   타일 기반 맵 데이터 및 충돌
│   │
│   ├── systems/                     # ⭐ 게임 시스템 (pygame 무관)
│   │   ├── __init__.py
│   │   └── world.py                 #   월드 매니저 (전투/스폰/수배/레벨업)
│   │
│   ├── views/                       # 🔄 렌더링 (플랫폼별 교체)
│   │   ├── __init__.py
│   │   └── renderer.py              #   Pygame 전용 렌더러
│   │
│   └── controllers/                 # 🔄 입력 (플랫폼별 교체)
│       ├── __init__.py
│       └── input_handler.py         #   Pygame 키보드 입력 핸들러
│
├── GTA2_ANALYSIS.md                 # GTA 2 원작 분석
├── GAME_DESIGN.md                   # 게임 설계서
├── ARCHITECTURE.md                  # 이 문서
└── venv/                            # Python 가상환경
```

### 아이콘 범례
- ⭐ = **이식 시 그대로 사용** (pygame import 없음)
- 🔄 = **이식 시 교체 대상** (pygame 전용 코드)

---

## 3. 데이터 흐름 (매 프레임)

```
1. Controller (input_handler.py)
   │  pygame 키보드 상태를 읽는다
   │  키코드를 플랫폼 무관한 dict로 변환한다
   │
   │  inputs = {
   │      "forward": True/False,    # W키 or 가상 조이스틱 위
   │      "backward": True/False,   # S키 or 가상 조이스틱 아래
   │      "left": True/False,       # A키 or 가상 조이스틱 좌
   │      "right": True/False,      # D키 or 가상 조이스틱 우
   │  }
   │
   ▼
2. Model (world.py → models/*.py)
   │  inputs dict를 받아 모든 로직을 실행한다
   │  - 플레이어 이동 (player.update)
   │  - 차량 물리 (vehicle.update)
   │  - 자동 사격 + 충돌 판정
   │  - NPC AI 업데이트
   │  - 레벨업 / 수배 시스템
   │  - 카메라 위치 계산
   │
   ▼
3. View (renderer.py)
   │  world 객체의 상태를 읽기만 한다 (수정 안함)
   │  - world.player.x/y → 플레이어 그리기
   │  - world.vehicle → 차량 그리기
   │  - world.citizens → 시민 그리기
   │  - world.wanted_level → HUD 별 표시
   └──▶ 화면 출력
```

---

## 4. 핵심 설계 원칙

### 4.1 Model은 절대 pygame을 import하지 않는다

```python
# ✅ 올바른 예 (models/player.py)
import math
from game.config import PLAYER_SPEED

class PlayerModel:
    def update(self, forward, backward, left, right):
        # bool 값만 받는다. pygame.K_w 같은 것은 모른다.
        if forward:
            self.x += math.cos(self.angle) * PLAYER_SPEED

# ❌ 나쁜 예 (절대 이러면 안됨)
import pygame  # Model에서 pygame import 금지!
class PlayerModel:
    def update(self, keys):
        if keys[pygame.K_w]:  # 플랫폼 종속!
            ...
```

### 4.2 Controller는 입력을 "번역"만 한다

```python
# PC (현재)
inputs = {
    "forward": keys[pygame.K_w],   # 키보드
    "left": keys[pygame.K_a],
}

# Android (나중에 교체)
inputs = {
    "forward": joystick.y < -0.3,  # 가상 조이스틱
    "left": joystick.x < -0.3,
}
```
→ 둘 다 같은 `{"forward": True, "left": True}` dict를 만들어서 Model에 넘긴다.

### 4.3 View는 읽기 전용

```python
# renderer.py - world의 데이터를 읽기만 한다
def render(self, world):
    x = world.player.x   # 읽기 ✅
    world.player.x = 100  # 쓰기 ❌ (하면 안됨)
```

---

## 5. 안드로이드 포팅 전략

### 5.1 방법: Buildozer (pygame-ce → APK)

```bash
# Buildozer로 pygame 앱을 APK로 변환
pip install buildozer
buildozer init        # buildozer.spec 생성
buildozer android debug  # APK 빌드
```

### 5.2 교체해야 할 파일 (🔄 표시된 것들)

| 파일 | PC 버전 | Android 버전 |
|------|---------|-------------|
| `controllers/input_handler.py` | pygame 키보드 | 터치 가상 조이스틱 |
| `views/renderer.py` | pygame.draw | pygame.draw (동일) 또는 OpenGL |
| `main.py` | PC 루프 | Android 루프 (거의 동일) |

### 5.3 교체하지 않는 파일 (⭐ 그대로 사용)

| 파일 | 역할 |
|------|------|
| `config.py` | 설정값 (해상도만 변경) |
| `models/player.py` | 플레이어 로직 |
| `models/vehicle.py` | 차량 물리 |
| `models/bullet.py` | 총알 궤적 |
| `models/citizen.py` | 시민 AI |
| `models/police.py` | 경찰 AI |
| `systems/world.py` | 전체 게임 로직 |

> **7개 파일 중 5개**를 그대로 재사용할 수 있다.

### 5.4 Android 터치 컨트롤러 예시 (나중에 만들 파일)

```python
# controllers/touch_handler.py (미래)
class TouchInputHandler:
    def __init__(self, screen_w, screen_h):
        # 화면 좌하단에 가상 조이스틱 배치
        self.joystick_center = (120, screen_h - 120)
        self.joystick_radius = 80

    def process(self, touch_events):
        inputs = {"forward": False, "backward": False,
                  "left": False, "right": False}

        for touch in touch_events:
            dx = touch.x - self.joystick_center[0]
            dy = touch.y - self.joystick_center[1]
            if abs(dx) > 20:
                inputs["left"] = dx < 0
                inputs["right"] = dx > 0
            if abs(dy) > 20:
                inputs["forward"] = dy < 0
                inputs["backward"] = dy > 0

        return inputs
```

---

## 6. 파일별 pygame 의존성 체크

| 파일 | `import pygame`? | 이식 시 |
|------|:-:|------|
| `config.py` | ❌ | 그대로 |
| `models/player.py` | ❌ | 그대로 |
| `models/vehicle.py` | ❌ | 그대로 |
| `models/bullet.py` | ❌ | 그대로 |
| `models/citizen.py` | ❌ | 그대로 |
| `models/police.py` | ❌ | 그대로 |
| `models/gang.py` | ❌ | 그대로 |
| `models/boss.py` | ❌ | 그대로 |
| `models/mission.py` | ❌ | 그대로 |
| `models/garage.py` | ❌ | 그대로 |
| `models/expgem.py` | ❌ | 그대로 |
| `models/pickup.py` | ❌ | 그대로 |
| `world/map.py` | ❌ | 그대로 |
| `systems/world.py` | ❌ | 그대로 |
| `views/renderer.py` | ✅ | 교체 |
| `views/bitmap_font.py`| ✅ | (Pygame 전용) 교체 필요 |
| `controllers/input_handler.py` | ✅ | 교체 |
| `main.py` | ❌ (간접) | 약간 수정 |

> **pygame을 직접 import하는 파일은 렌더러와 인풋 핸들러뿐입니다.**
> 안드로이드 포팅 시 `views/` 와 `controllers/` 폴더 내의 파일만 모바일 터치/그래픽용으로 교체하면 됩니다.
