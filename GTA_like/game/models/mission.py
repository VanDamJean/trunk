"""
공중전화 미션 모델 (Mission Model)
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
"""
import random
from game.config import MISSION_PHONE_RADIUS, MISSION_OBJECTIVE_RADIUS


MISSION_POOL = [
    {
        "id": "m001", "gang": "yakuza", "title": "배달부",
        "description": "패키지를 목적지까지 운반하라!",
        "type": "delivery",
        "reward_money": 5000, "reward_rep": 1, "reward_exp": 30,
        "time_limit": 120,
    },
    {
        "id": "m002", "gang": "loonies", "title": "소탕 작전",
        "description": "구역 내 경찰 3명을 처치하라!",
        "type": "kill",
        "target": "police", "target_count": 3,
        "reward_money": 8000, "reward_rep": 1, "reward_exp": 50,
        "time_limit": 90,
    },
    {
        "id": "m003", "gang": "zaibatsu", "title": "시범 폭파",
        "description": "차량을 훔쳐서 지정 장소에 가져다놔라!",
        "type": "steal_vehicle",
        "reward_money": 10000, "reward_rep": 2, "reward_exp": 60,
        "time_limit": 150,
    },
    {
        "id": "m004", "gang": "yakuza", "title": "살인 청부",
        "description": "타겟 NPC를 제거하라!",
        "type": "assassinate",
        "reward_money": 15000, "reward_rep": 1, "reward_exp": 80,
        "time_limit": 180,
    },
    {
        "id": "m005", "gang": "loonies", "title": "킬 프렌지",
        "description": "60초 안에 최대한 많이 처치하라!",
        "type": "kill_frenzy",
        "reward_money": 3000, "reward_rep": 0, "reward_exp": 100,
        "time_limit": 60,
    },
]


class PhoneModel:
    """공중전화 인터랙션 포인트"""
    def __init__(self, x, y):
        self.x = x
        self.y = y
        self.radius = MISSION_PHONE_RADIUS
        self.available = True
        self.cooldown = 0

    def update(self):
        if self.cooldown > 0:
            self.cooldown -= 1
            if self.cooldown == 0:
                self.available = True

    def get_mission(self):
        if not self.available:
            return None
        self.available = False
        self.cooldown = 600  # 10초 뒤 재활성화
        return random.choice(MISSION_POOL).copy()


class MissionState:
    """진행 중인 미션 상태"""
    def __init__(self, data, world_cx, world_cy):
        self.data = data
        self.active = True
        self.time_left = data.get("time_limit", None)
        self.kill_count = 0
        self.kill_target = data.get("target_count", 0)
        self.type = data.get("type", "delivery")
        # 목적지 (맵 중앙 기준 랜덤 오프셋)
        import random
        self.objective_x = world_cx + random.randint(-400, 400)
        self.objective_y = world_cy + random.randint(-400, 400)
        self.objective_radius = MISSION_OBJECTIVE_RADIUS

    def update(self):
        if self.time_left is not None:
            self.time_left -= 1
            if self.time_left <= 0:
                self.active = False
                return "timeout"
        return None

    def check_objective(self, player_x, player_y, kill_delta=0):
        import math
        self.kill_count += kill_delta

        if self.type in ("delivery", "steal_vehicle"):
            dist = math.hypot(player_x - self.objective_x, player_y - self.objective_y)
            if dist < self.objective_radius:
                self.active = False
                return "complete"

        elif self.type in ("kill", "assassinate"):
            if self.kill_count >= self.kill_target:
                self.active = False
                return "complete"

        elif self.type == "kill_frenzy":
            # 타이머로만 판단, 시간 종료 시 자동 완료
            if self.time_left is not None and self.time_left <= 0:
                self.active = False
                return "complete"

        return None
