"""
시민 NPC 모델 (Citizen Model)
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
"""
import math
import random
from game.config import CITIZEN_RADIUS, CITIZEN_SPEED, CITIZEN_DIR_CHANGE_MIN, CITIZEN_DIR_CHANGE_MAX


class CitizenModel:
    def __init__(self, x, y):
        self.x = x
        self.y = y
        self.radius = CITIZEN_RADIUS
        self.speed = CITIZEN_SPEED
        self.angle = random.uniform(0, 360)
        self.timer = 0

    def update(self, game_map=None):
        self.timer -= 1
        if self.timer <= 0:
            self.angle = random.uniform(0, 360)
            self.timer = random.randint(CITIZEN_DIR_CHANGE_MIN, CITIZEN_DIR_CHANGE_MAX)
            
        new_x = self.x + math.cos(math.radians(self.angle)) * self.speed
        new_y = self.y + math.sin(math.radians(self.angle)) * self.speed
        
        if game_map and game_map.is_solid(new_x, new_y):
            self.angle = (self.angle + 180) % 360 # 벽에 부딪히면 반대로 돔
            self.timer = random.randint(30, 60)
        else:
            self.x = new_x
            self.y = new_y
