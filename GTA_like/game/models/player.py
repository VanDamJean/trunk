"""
플레이어 모델 (Player Model)
━━━━━━━━━━━━━━━━━━━━━━━━━━━━
pygame에 의존하지 않는 순수 로직.
위치, 각도, 이동 계산만 담당한다.
"""
import math
from game.config import (
    PLAYER_RADIUS, PLAYER_SPEED, PLAYER_TURN_SPEED, PLAYER_REVERSE_RATIO,
    PLAYER_MAX_HP, PLAYER_MAX_ARMOR
)


class PlayerModel:
    def __init__(self, x, y):
        self.x = x
        self.y = y
        self.radius = PLAYER_RADIUS
        self.speed = PLAYER_SPEED
        self.turn_speed = PLAYER_TURN_SPEED
        self.angle = 0.0
        self.in_vehicle = False
        self.hp = PLAYER_MAX_HP
        self.armor = 0
        self.current_weapon = "pistol"

    def update(self, move_forward, move_backward, turn_left, turn_right, game_map):
        """
        방향키 상태를 bool 값으로 받아 위치를 갱신한다.
        """
        if self.in_vehicle:
            return

        # 회전
        if turn_left:
            self.angle -= self.turn_speed
        if turn_right:
            self.angle += self.turn_speed

        # 전진/후진
        move_speed = 0.0
        if move_forward:
            move_speed = self.speed
        elif move_backward:
            move_speed = -self.speed * PLAYER_REVERSE_RATIO

        new_x = self.x + math.cos(math.radians(self.angle)) * move_speed
        new_y = self.y + math.sin(math.radians(self.angle)) * move_speed

        # 충돌 판정 (맵)
        if game_map:
            # 타일의 중심이 아니라 반지름을 고려한 충돌 판정은 일단 단순 4포인트 검사로 대체
            if not game_map.is_solid(new_x, self.y):
                self.x = new_x
            if not game_map.is_solid(self.x, new_y):
                self.y = new_y
        else:
            self.x = new_x
            self.y = new_y
