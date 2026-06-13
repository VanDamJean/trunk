"""
총알 모델 (Bullet Model)
━━━━━━━━━━━━━━━━━━━━━━━━━
"""
import math
from game.config import BULLET_RADIUS

class BulletModel:
    def __init__(self, x, y, angle, speed, damage):
        self.x = x
        self.y = y
        self.angle = angle
        self.speed = speed
        self.damage = damage
        self.radius = BULLET_RADIUS
        self.start_x = x
        self.start_y = y

    def update(self):
        self.x += math.cos(math.radians(self.angle)) * self.speed
        self.y += math.sin(math.radians(self.angle)) * self.speed
