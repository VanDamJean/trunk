import pygame
import math
from utils.constants import ShapeType, STATION_RADIUS, PASSENGER_SIZE, TRAIN_WIDTH, TRAIN_HEIGHT, LINE_THICKNESS
from utils.colors import Colors

class Renderer:
    def __init__(self, screen):
        self.screen = screen
        
    def draw_station(self, station, is_hovered=False):
        pos = (int(station.position.x), int(station.position.y))
        r = int(STATION_RADIUS * station.visual_scale)
        
        # 과부하 경고 타이머 (원형 게이지)
        if station.is_overflowing:
            gauge_r = r + 10
            ratio = station.overflow_timer / station.overflow_max
            # 배경 원
            pygame.draw.circle(self.screen, (228, 68, 68, 80), pos, gauge_r, 3)
            # 진행 아크
            rect = pygame.Rect(pos[0] - gauge_r, pos[1] - gauge_r, gauge_r * 2, gauge_r * 2)
            start_angle = math.pi / 2
            end_angle = start_angle + ratio * 2 * math.pi
            pygame.draw.arc(self.screen, Colors.WARNING, rect, start_angle, end_angle, 4)
        
        # hover 시 하이라이트 배경 원
        if is_hovered:
            pygame.draw.circle(self.screen, Colors.HOVER_HIGHLIGHT, pos, r + 8)
        
        # 역 도형 렌더링 (두꺼운 테두리 + 배경색 채움)
        stroke = 4
        if station.shape == ShapeType.CIRCLE:
            pygame.draw.circle(self.screen, Colors.STATION_FILL, pos, r)
            pygame.draw.circle(self.screen, Colors.STATION_STROKE, pos, r, stroke)
        elif station.shape == ShapeType.TRIANGLE:
            h = r * 1.1  # 살짝 큰 삼각형
            points = [
                (pos[0], pos[1] - h),
                (pos[0] - h * 0.95, pos[1] + h * 0.65),
                (pos[0] + h * 0.95, pos[1] + h * 0.65)
            ]
            pygame.draw.polygon(self.screen, Colors.STATION_FILL, points)
            pygame.draw.polygon(self.screen, Colors.STATION_STROKE, points, stroke)
        elif station.shape == ShapeType.SQUARE:
            s = r * 0.85
            rect = pygame.Rect(pos[0] - s, pos[1] - s, s * 2, s * 2)
            pygame.draw.rect(self.screen, Colors.STATION_FILL, rect)
            pygame.draw.rect(self.screen, Colors.STATION_STROKE, rect, stroke)
        elif station.shape == ShapeType.STAR:
            self._draw_star(pos, r, Colors.STATION_FILL, Colors.STATION_STROKE, stroke)
        elif station.shape == ShapeType.CROSS:
            self._draw_cross(pos, r, Colors.STATION_FILL, Colors.STATION_STROKE, stroke)
            
        # 승객을 역 아래에 가로 한 줄로 정렬
        p_count = len(station.passengers)
        if p_count > 0:
            total_width = p_count * (PASSENGER_SIZE * 2 + 4) - 4
            start_x = pos[0] - total_width // 2 + PASSENGER_SIZE
            for i, p in enumerate(station.passengers):
                p_x = start_x + i * (PASSENGER_SIZE * 2 + 4)
                p_y = pos[1] + r + 14
                self._draw_passenger_shape(p.destination_shape, (p_x, p_y), PASSENGER_SIZE, Colors.PASSENGER)
            
    def _draw_passenger_shape(self, shape, pos, size, color):
        """승객 도형을 채워서 그리기"""
        if shape == ShapeType.CIRCLE:
            pygame.draw.circle(self.screen, color, pos, size)
        elif shape == ShapeType.TRIANGLE:
            points = [
                (pos[0], pos[1] - size),
                (pos[0] - size, pos[1] + size * 0.7),
                (pos[0] + size, pos[1] + size * 0.7)
            ]
            pygame.draw.polygon(self.screen, color, points)
        elif shape == ShapeType.SQUARE:
            s = size * 0.85
            rect = pygame.Rect(pos[0] - s, pos[1] - s, s * 2, s * 2)
            pygame.draw.rect(self.screen, color, rect)
        elif shape == ShapeType.STAR:
            self._draw_star(pos, size, color, color, 0)
        elif shape == ShapeType.CROSS:
            self._draw_cross(pos, size, color, color, 0)
            
    def _draw_star(self, pos, r, fill, stroke, width):
        points = []
        for i in range(10):
            angle = math.pi / 2 + i * math.pi / 5
            rad = r if i % 2 == 0 else r * 0.45
            points.append((pos[0] + rad * math.cos(angle), pos[1] - rad * math.sin(angle)))
        pygame.draw.polygon(self.screen, fill, points)
        if width > 0:
            pygame.draw.polygon(self.screen, stroke, points, width)
    
    def _draw_cross(self, pos, r, fill, stroke, width):
        arm = r * 0.35
        pts = [
            (pos[0] - arm, pos[1] - r), (pos[0] + arm, pos[1] - r),
            (pos[0] + arm, pos[1] - arm), (pos[0] + r, pos[1] - arm),
            (pos[0] + r, pos[1] + arm), (pos[0] + arm, pos[1] + arm),
            (pos[0] + arm, pos[1] + r), (pos[0] - arm, pos[1] + r),
            (pos[0] - arm, pos[1] + arm), (pos[0] - r, pos[1] + arm),
            (pos[0] - r, pos[1] - arm), (pos[0] - arm, pos[1] - arm),
        ]
        pygame.draw.polygon(self.screen, fill, pts)
        if width > 0:
            pygame.draw.polygon(self.screen, stroke, pts, width)
            
    def draw_line(self, line):
        if len(line.stations) >= 2:
            for i in range(len(line.stations) - 1):
                p1 = (int(line.stations[i].position.x), int(line.stations[i].position.y))
                p2 = (int(line.stations[i+1].position.x), int(line.stations[i+1].position.y))
                pygame.draw.line(self.screen, line.color, p1, p2, LINE_THICKNESS)
                # 각 끝점에 둥근 캡
                pygame.draw.circle(self.screen, line.color, p1, LINE_THICKNESS // 2)
                pygame.draw.circle(self.screen, line.color, p2, LINE_THICKNESS // 2)
            
    def draw_train(self, train):
        if len(train.line.stations) < 2:
            return
            
        pos = (int(train.position.x), int(train.position.y))
        
        # 이동 방향 계산 → 열차 회전
        current_idx = train.segment_index
        next_idx = current_idx + train.direction
        if 0 <= next_idx < len(train.line.stations):
            target = train.line.stations[next_idx].position
        else:
            target = train.line.stations[current_idx].position
            
        dx = target.x - train.position.x
        dy = target.y - train.position.y
        angle = math.atan2(-dy, dx) if (dx != 0 or dy != 0) else 0
        
        # 캡슐형 열차 (회전된 사각형 + 양쪽 둥근 캡)
        w, h = TRAIN_WIDTH, TRAIN_HEIGHT
        
        # Surface에 열차 그리기 (투명 배경)
        surf = pygame.Surface((w + h, h + 4), pygame.SRCALPHA)
        # 본체 사각형
        pygame.draw.rect(surf, train.line.color, (h // 2, 2, w, h), border_radius=4)
        # 양쪽 둥근 캡
        pygame.draw.circle(surf, train.line.color, (h // 2, 2 + h // 2), h // 2)
        pygame.draw.circle(surf, train.line.color, (h // 2 + w, 2 + h // 2), h // 2)
        
        # 승객 미니 도형을 열차 안에 표시
        for i, p in enumerate(train.passengers):
            px = h // 2 + 5 + (i % 3) * 8
            py = 5 + (i // 3) * 7
            self._draw_passenger_shape_on_surface(surf, p.destination_shape, (px, py), 3, Colors.BACKGROUND)
        
        # 회전 적용
        angle_deg = math.degrees(angle)
        rotated = pygame.transform.rotate(surf, angle_deg)
        rect = rotated.get_rect(center=pos)
        self.screen.blit(rotated, rect)
        
    def _draw_passenger_shape_on_surface(self, surface, shape, pos, size, color):
        """Surface 위에 승객 도형 그리기"""
        if shape == ShapeType.CIRCLE:
            pygame.draw.circle(surface, color, pos, size)
        elif shape == ShapeType.TRIANGLE:
            points = [(pos[0], pos[1] - size), (pos[0] - size, pos[1] + size), (pos[0] + size, pos[1] + size)]
            pygame.draw.polygon(surface, color, points)
        elif shape == ShapeType.SQUARE:
            pygame.draw.rect(surface, color, (pos[0] - size, pos[1] - size, size * 2, size * 2))

    def draw_pulse(self, pos, radius, color, alpha):
        """역에 달라붙을 때 시각적인 파동 이펙트 그리기"""
        if radius <= 0:
            return
        surf = pygame.Surface((int(radius) * 2, int(radius) * 2), pygame.SRCALPHA)
        pygame.draw.circle(surf, (*color, int(alpha)), (int(radius), int(radius)), int(radius), 3)
        self.screen.blit(surf, (pos[0] - int(radius), pos[1] - int(radius)))
