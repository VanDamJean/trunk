"""
가라지 모델 (Garage Model)
━━━━━━━━━━━━━━━━━━━━━━━━━
"""
from game.config import GARAGE_RADIUS, GARAGE_REPAINT_COST


class GarageModel:
    """리스프레이 가라지: 진입 시 차량 수리 + 수배 해제"""
    def __init__(self, x, y):
        self.x = x
        self.y = y
        self.radius = GARAGE_RADIUS

    def is_near(self, px, py):
        import math
        return math.hypot(px - self.x, py - self.y) < self.radius
