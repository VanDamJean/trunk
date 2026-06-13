"""
경찰 NPC 모델 (Police Model)
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
"""
import math
from game.config import POLICE_RADIUS, POLICE_SPEED, SWAT_SPEED, FBI_SPEED, ARMY_SPEED


class PoliceModel:
    def __init__(self, x, y, unit_type="police"):
        self.x = x
        self.y = y
        self.radius = POLICE_RADIUS
        self.unit_type = unit_type
        
        if unit_type == "army":
            self.speed = ARMY_SPEED
            self.radius = POLICE_RADIUS + 2 # 좀 더 큼
        elif unit_type == "fbi":
            self.speed = FBI_SPEED
        elif unit_type == "swat":
            self.speed = SWAT_SPEED
        else:
            self.speed = POLICE_SPEED

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
