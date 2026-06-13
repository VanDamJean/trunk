"""
갱단원 모델 (Gang Member Model)
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
"""
import math
import random
from game.config import POLICE_RADIUS, POLICE_SPEED, GANGS


class GangModel:
    def __init__(self, x, y, gang_name):
        self.x = x
        self.y = y
        self.gang_name = gang_name
        self.radius = POLICE_RADIUS
        self.speed = POLICE_SPEED * 0.9
        self.angle = random.uniform(0, 360)
        self.hp = 30
        self.state = "patrol"   # patrol / attack
        self.patrol_timer = random.randint(60, 180)
        gang_data = GANGS.get(gang_name, {})
        self.color = gang_data.get("color", (180, 40, 180))
        self.enemy_of = gang_data.get("enemy_of", [])

    def update(self, player_x, player_y, player_gang_rep, game_map=None):
        """
        player_gang_rep: 플레이어와 이 갱단의 호감도 (int, -3~+3)
        """
        dist = math.hypot(self.x - player_x, self.y - player_y)
        
        # 적대적이면 추격
        if player_gang_rep <= -1 and dist < 300:
            self.state = "attack"
        elif dist > 400:
            self.state = "patrol"
        
        if self.state == "attack":
            angle = math.atan2(player_y - self.y, player_x - self.x)
            new_x = self.x + math.cos(angle) * self.speed
            new_y = self.y + math.sin(angle) * self.speed
        else:
            # 순찰
            self.patrol_timer -= 1
            if self.patrol_timer <= 0:
                self.angle = random.uniform(0, 360)
                self.patrol_timer = random.randint(60, 180)
            new_x = self.x + math.cos(math.radians(self.angle)) * self.speed * 0.5
            new_y = self.y + math.sin(math.radians(self.angle)) * self.speed * 0.5

        if game_map:
            if not game_map.is_solid(new_x, self.y):
                self.x = new_x
            if not game_map.is_solid(self.x, new_y):
                self.y = new_y
        else:
            self.x = new_x
            self.y = new_y
