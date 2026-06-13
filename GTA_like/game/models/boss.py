"""
보스 모델 (Boss Model)
━━━━━━━━━━━━━━━━━━━━━━━
큰 HP와 체력 바를 가진 특별한 적.
"""
import math
import random
from game.config import POLICE_RADIUS


class BossModel:
    TYPES = {
        "tank_boss": {
            "hp": 500, "speed": 1.5, "radius": 28,
            "color": (80, 200, 80), "damage": 10, "exp": 200,
            "name": "TANK BOSS"
        },
        "swat_boss": {
            "hp": 300, "speed": 3.0, "radius": 20,
            "color": (50, 50, 200), "damage": 5, "exp": 150,
            "name": "SWAT BOSS"
        },
        "gang_boss": {
            "hp": 250, "speed": 2.5, "radius": 18,
            "color": (200, 30, 200), "damage": 4, "exp": 120,
            "name": "GANG BOSS"
        },
    }

    def __init__(self, x, y, boss_type="tank_boss"):
        stats = self.TYPES.get(boss_type, self.TYPES["tank_boss"])
        self.x = x
        self.y = y
        self.boss_type = boss_type
        self.name = stats["name"]
        self.hp = stats["hp"]
        self.max_hp = stats["hp"]
        self.speed = stats["speed"]
        self.radius = stats["radius"]
        self.color = stats["color"]
        self.damage = stats["damage"]
        self.exp_reward = stats["exp"]
        self.unit_type = "boss"  # 경찰 AI 인터페이스 호환

    def update(self, target_x, target_y, game_map=None):
        angle = math.atan2(target_y - self.y, target_x - self.x)
        new_x = self.x + math.cos(angle) * self.speed
        new_y = self.y + math.sin(angle) * self.speed
        if game_map:
            if not game_map.is_solid(new_x, self.y):
                self.x = new_x
            if not game_map.is_solid(self.x, new_y):
                self.y = new_y
        else:
            self.x = new_x
            self.y = new_y
