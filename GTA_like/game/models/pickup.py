"""
아이템 픽업 모델 (Pickup Model)
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
"""

class PickupModel:
    def __init__(self, x, y, item_type):
        self.x = x
        self.y = y
        self.item_type = item_type
        self.radius = 12
