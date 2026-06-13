"""
경험치 젬 모델 (EXP Gem Model)
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
"""

class ExpGemModel:
    def __init__(self, x, y, value=1):
        self.x = x
        self.y = y
        self.value = value
        self.radius = 6
        self.lifetime = 600  # 10초 후 사라짐

    def update(self):
        self.lifetime -= 1
        return self.lifetime > 0
