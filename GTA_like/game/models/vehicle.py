"""
차량 모델 (Vehicle Model)
━━━━━━━━━━━━━━━━━━━━━━━━━━
pygame에 의존하지 않는 순수 물리 로직.
"""
import math
from game.config import (
    VEHICLE_ENTER_RANGE, VEHICLE_EXIT_OFFSET,
    VEHICLE_MAX_HP, VEHICLE_TYPES
)


class VehicleModel:
    def __init__(self, x, y, vehicle_type="sedan"):
        self.x = x
        self.y = y
        vt = VEHICLE_TYPES.get(vehicle_type, VEHICLE_TYPES["sedan"])
        self.vehicle_type = vehicle_type
        self.width   = vt["width"]
        self.height  = vt["height"]
        self.angle   = 0.0
        self.speed   = 0.0
        self.max_speed    = vt["max_speed"]
        self.acceleration = vt["accel"]
        self.friction     = vt["friction"]
        self.turn_speed   = vt["turn"]
        self.color        = vt["color"]
        self.hp           = vt["hp"]
        self.max_hp       = vt["hp"]
        self.occupied     = False
        self.exploded     = False
        self.smoke        = False
        self.fire         = False
        self.is_traffic   = False  # NPC가 운전하는 차량인지 여부
        self.turn_timer   = 0

    def update(self, accelerate, brake, turn_left, turn_right, game_map):
        """
        조작 입력을 bool로 받아 물리 시뮬레이션.
        """
        if not self.occupied:
            # NPC 차량 (교통 AI) 로직
            if self.is_traffic and game_map:
                front_x = self.x + math.cos(math.radians(self.angle)) * (self.width / 2 + 20)
                front_y = self.y + math.sin(math.radians(self.angle)) * (self.width / 2 + 20)
                front_tile = game_map.get_tile(front_x, front_y)
                
                if front_tile == 0:  # TILE_ROAD
                    self.speed = min(self.speed + self.acceleration, self.max_speed * 0.4)
                    self.turn_timer = 0
                else:
                    self.speed = max(0, self.speed - self.friction * 3)
                    self.turn_timer += 1
                    # 길이 막히면 회전
                    if self.turn_timer > 10:
                        self.angle += self.turn_speed * 2
            else:
                # 주차된 차이거나 교통 AI가 아닌 차는 정지
                if self.speed > 0:
                    self.speed = max(0, self.speed - self.friction)
                elif self.speed < 0:
                    self.speed = min(0, self.speed + self.friction)
        else:
            self.is_traffic = False # 플레이어가 타면 교통 AI 해제
            # 탑승 조작
            if accelerate:
                self.speed = min(self.speed + self.acceleration, self.max_speed)
            elif brake:
                self.speed = max(self.speed - self.acceleration, -self.max_speed / 2)
            else:
                if self.speed > 0:
                    self.speed = max(0, self.speed - self.friction)
                elif self.speed < 0:
                    self.speed = min(0, self.speed + self.friction)

            # 조향
            if abs(self.speed) > 0.1:
                if turn_left:
                    self.angle -= self.turn_speed
                if turn_right:
                    self.angle += self.turn_speed

        # 이동 및 충돌 판정
        new_x = self.x + math.cos(math.radians(self.angle)) * self.speed
        new_y = self.y + math.sin(math.radians(self.angle)) * self.speed

        if game_map:
            # 차량 네 모서리나 중심을 기준으로 충돌 판정. 단순화하여 앞/뒤쪽 중심점 사용
            front_x = new_x + math.cos(math.radians(self.angle)) * (self.width / 2)
            front_y = new_y + math.sin(math.radians(self.angle)) * (self.width / 2)
            back_x = new_x - math.cos(math.radians(self.angle)) * (self.width / 2)
            back_y = new_y - math.sin(math.radians(self.angle)) * (self.width / 2)
            
            if not (game_map.is_solid(front_x, front_y) or game_map.is_solid(back_x, back_y)):
                self.x = new_x
                self.y = new_y
            else:
                # 충돌 시 데미지 및 튕김
                if abs(self.speed) > 2.0:
                    damage = abs(self.speed) * 2
                    self.hp -= damage
                    if self.hp <= 0:
                        self.hp = 0
                        self.exploded = True
                self.speed *= -0.5
        else:
            self.x = new_x
            self.y = new_y

        # 상태 업데이트
        ratio = self.hp / max(1, self.max_hp)
        self.smoke = ratio < 0.5
        self.fire  = ratio < 0.25
