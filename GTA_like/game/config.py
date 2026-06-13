"""
게임 설정값 (Config)
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
pygame에 의존하지 않는 순수 데이터.
Android/iOS 이식 시 그대로 가져간다.
"""

# ── 화면 ──
SCREEN_WIDTH = 800
SCREEN_HEIGHT = 600
FPS = 60

# ── 플레이어 ──
PLAYER_RADIUS = 10
PLAYER_SPEED = 3.0
PLAYER_TURN_SPEED = 4.0
PLAYER_REVERSE_RATIO = 0.5
PLAYER_MAX_HP = 100
PLAYER_MAX_ARMOR = 100

# ── 차량 ──
VEHICLE_WIDTH = 40
VEHICLE_HEIGHT = 20
VEHICLE_MAX_SPEED = 10.0
VEHICLE_ACCELERATION = 0.2
VEHICLE_FRICTION = 0.05
VEHICLE_TURN_SPEED = 3.0
VEHICLE_ENTER_RANGE = 40
VEHICLE_EXIT_OFFSET = 30
VEHICLE_MAX_HP = 100

# ── 총알 ──
BULLET_SPEED = 15.0
BULLET_RADIUS = 3
BULLET_MAX_RANGE = 1000

# ── 시민 ──
CITIZEN_RADIUS = 10
CITIZEN_SPEED = 1.0
CITIZEN_DIR_CHANGE_MIN = 60
CITIZEN_DIR_CHANGE_MAX = 180
CITIZEN_MAX_COUNT = 10
CITIZEN_SPAWN_CHANCE = 0.01
CITIZEN_SPAWN_DIST = 500

# ── 경찰/수배 ──
POLICE_RADIUS = 10
POLICE_SPEED = 3.5
POLICE_SPAWN_CHANCE = 0.02
POLICE_SPAWN_DIST = 400
SWAT_SPEED = 4.0
FBI_SPEED = 4.5
ARMY_SPEED = 2.0

# ── 무기 데이터 ──
WEAPONS = {
    "fist":    {"range": 40,  "cooldown": 20,  "damage": 5,   "color": (200, 200, 200), "bullet_speed": 0,    "pierce": 0, "explode": False, "spread": 1},
    "pistol":  {"range": 400, "cooldown": 60,  "damage": 15,  "color": (255, 255, 0),   "bullet_speed": 15.0, "pierce": 0, "explode": False, "spread": 1},
    "uzi":     {"range": 300, "cooldown": 10,  "damage": 8,   "color": (255, 150, 0),   "bullet_speed": 20.0, "pierce": 0, "explode": False, "spread": 1},
    "shotgun": {"range": 150, "cooldown": 50,  "damage": 25,  "color": (255, 80,  0),   "bullet_speed": 18.0, "pierce": 0, "explode": False, "spread": 3},  # 3발 부채꼴
    "rocket":  {"range": 500, "cooldown": 120, "damage": 100, "color": (255, 50,  50),  "bullet_speed": 10.0, "pierce": 0, "explode": True,  "spread": 1},
}

# ── 차량 타입 데이터 ──
VEHICLE_TYPES = {
    "sedan":   {"max_speed": 6.0,  "accel": 0.2,  "friction": 0.05, "turn": 3.0, "hp": 80,  "color": (180, 180, 60),  "width": 40, "height": 20},
    "sports":  {"max_speed": 10.0, "accel": 0.3,  "friction": 0.04, "turn": 4.0, "hp": 60,  "color": (220, 60,  60),  "width": 38, "height": 18},
    "taxi":    {"max_speed": 6.0,  "accel": 0.2,  "friction": 0.05, "turn": 3.0, "hp": 80,  "color": (240, 220, 40),  "width": 42, "height": 20},
    "truck":   {"max_speed": 4.0,  "accel": 0.1,  "friction": 0.06, "turn": 2.0, "hp": 150, "color": (80,  120, 80),  "width": 50, "height": 24},
    "police":  {"max_speed": 8.0,  "accel": 0.25, "friction": 0.04, "turn": 3.5, "hp": 80,  "color": (50,  50,  200), "width": 42, "height": 20},
    "gang":    {"max_speed": 9.0,  "accel": 0.25, "friction": 0.04, "turn": 3.0, "hp": 70,  "color": (120, 40,  120), "width": 40, "height": 20},
}

# ── 갱단 ──
GANGS = {
    "yakuza":  {"color": (200, 50,  50),  "enemy_of": ["loonies"]},
    "loonies": {"color": (50,  200, 50),  "enemy_of": ["yakuza"]},
    "zaibatsu":{"color": (50,  50,  200), "enemy_of": ["yakuza", "loonies"]},
}
GANG_SPAWN_DIST = 400
GANG_MAX_PER_ZONE = 6

# ── 미션 ──
MISSION_PHONE_RADIUS = 30
MISSION_OBJECTIVE_RADIUS = 40

# ── 가라지 ──
GARAGE_RADIUS = 50
GARAGE_REPAINT_COST = 5000
GARAGE_BOMB_COST    = 10000
GARAGE_GUN_COST     = 25000

# ── Kill Frenzy ──
KILL_FRENZY_DURATION = 60  # 초
KILL_FRENZY_BONUS_EXP = 50

# ── 뱀서 업그레이드 ──
UPGRADES = [
    {"id": "fire_rate",   "name": "연사속도 ↑",   "desc": "쿨다운 -5f"},
    {"id": "multishot",   "name": "멀티샷 ↑",     "desc": "총알 +1발"},
    {"id": "pierce",      "name": "관통 ↑",       "desc": "적 관통 +1회"},
    {"id": "damage",      "name": "데미지 ↑",     "desc": "데미지 +20%"},
    {"id": "range",       "name": "사거리 ↑",     "desc": "사거리 +50"},
    {"id": "speed",       "name": "이동속도 ↑",   "desc": "속도 +0.5"},
    {"id": "heal",        "name": "체력 회복",     "desc": "HP +20"},
    {"id": "armor",       "name": "아머 획득",     "desc": "아머 +25"},
    {"id": "orbital",     "name": "오비탈",        "desc": "회전 방어총알"},
    {"id": "explode",     "name": "폭발탄",        "desc": "착탄 시 폭발"},
]

# ── 전투 / 뱀서 ──
AUTOFIRE_COOLDOWN_MIN = 5
AUTOFIRE_COOLDOWN_DECREASE = 5
MULTISHOT_SPREAD = 15         # 도(degree)
INITIAL_BULLET_COUNT = 1
MULTISHOT_LEVEL_INTERVAL = 3  # 3레벨마다 총알 +1

# ── 경험치 ──
EXP_PER_CITIZEN = 1
EXP_PER_POLICE = 3
EXP_PER_GANG = 2
EXP_BASE_TO_NEXT = 5
EXP_GROWTH_RATE = 1.5

# ── 카메라 ──
CAMERA_LERP = 0.1

# ── 맵 / 타일 ──
GRID_SIZE = 100
TILE_SIZE = 64

# 타일 타입
TILE_ROAD = 0
TILE_SIDEWALK = 1
TILE_GRASS = 2
TILE_BUILDING = 3
TILE_WATER = 4

# ── 색상 (렌더러 전용이지만, 모바일에서도 동일 값 사용) ──
COLORS = {
    "white":   (255, 255, 255),
    "black":   (0,   0,   0),
    "gray":    (100, 100, 100),
    "grid":    (90,  90,  90),
    "player":  (200, 50,  50),
    "vehicle": (50,  50,  200),
    "bullet":  (255, 255, 0),
    "citizen": (150, 200, 150),
    "police":  (50,  50,  255),
    "swat":    (20,  20,  150),
    "fbi":     (10,  10,  50),
    "army":    (50,  100, 50),
    "blood":   (150, 0,   0),
    "exp_gem": (0,   255, 180),
    "yakuza":  (200, 50,  50),
    "loonies": (50,  200, 50),
    "zaibatsu":(50,  50,  200),
    "gang":    (180, 40,  180),
    "phone":   (50,  220, 220),
    "garage":  (200, 180, 50),
    "frenzy":  (255, 80,  0),
    # 타일 색상
    "tile_road":     (60,  60,  60),
    "tile_sidewalk": (120, 120, 120),
    "tile_grass":    (50,  150, 50),
    "tile_building": (150, 100, 50),
    "tile_water":    (50,  150, 255),
}
