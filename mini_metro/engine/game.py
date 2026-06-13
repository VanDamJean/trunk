import pygame
from pygame.math import Vector2
from entities.station import Station
from entities.line import Line
from entities.train import Train
from systems.spawner import Spawner
from ui.hud import HUD
from engine.renderer import Renderer
from engine.input_handler import InputHandler
from utils.constants import ShapeType, STATION_RADIUS, OVERFLOW_MAX, WIDTH, HEIGHT, LINE_THICKNESS
from utils.colors import Colors

def point_to_segment_distance(p: Vector2, a: Vector2, b: Vector2):
    """점 p에서 선분 ab까지의 거리 + 투영점 반환"""
    ab = b - a
    ab_len_sq = ab.length_squared()
    if ab_len_sq == 0:
        return p.distance_to(a), a.copy()
    t = max(0.0, min(1.0, (p - a).dot(ab) / ab_len_sq))
    proj = a + ab * t
    return p.distance_to(proj), proj

class DragMode:
    NONE = 0
    EXTEND = 1    # 노선 끝 역에서 연장
    INSERT = 2    # 선분 중간에 역 삽입


class Game:
    def __init__(self, screen):
        self.screen = screen
        self.stations = []
        self.lines = [
            Line(Colors.LINE_COLORS[0]),
            Line(Colors.LINE_COLORS[1]),
            Line(Colors.LINE_COLORS[2]),
        ]
        self.trains = []
        self.score = 0
        self.game_over = False

        self.spawner = Spawner(self)
        self.hud = HUD(self)
        self.renderer = Renderer(screen)
        self.input = InputHandler()

        self.active_line_index = 0
        self.drag_mode = DragMode.NONE
        self.drag_line = None          # 편집 중인 Line 객체
        self.drag_start_station = None # EXTEND 모드: 시작역
        self.drag_insert_seg = None    # INSERT 모드: (seg_start_idx, seg_end_idx)
        self.drag_current_pos = Vector2()
        self.hovered_station = None
        self.snap_proj = None          # INSERT 미리보기용 투영점
        self.drag_path = []            # 임시 노선 경로
        self.drag_extend_end = True    # True: 뒤로 연장, False: 앞으로 연장
        self.last_hovered_station = None
        self.snap_pulses = []          # 스냅 파동 리스트

        self.init_game()

    def init_game(self):
        self.stations.append(Station(ShapeType.CIRCLE,   Vector2(WIDTH // 4,       HEIGHT // 2)))
        self.stations.append(Station(ShapeType.TRIANGLE, Vector2(WIDTH // 2,       HEIGHT // 3)))
        self.stations.append(Station(ShapeType.SQUARE,   Vector2(3 * WIDTH // 4,  HEIGHT // 2)))

    def add_score(self):
        self.score += 1

    # ────────────────────────────────────────────────────────────
    # 헬퍼
    # ────────────────────────────────────────────────────────────
    def _get_hovered_station(self, mouse_pos, snap_range=None):
        if snap_range is None:
            snap_range = STATION_RADIUS + 30
        best, best_d = None, snap_range
        for s in self.stations:
            d = s.position.distance_to(mouse_pos)
            if d < best_d:
                best_d = d
                best = s
        return best

    def _hit_segment(self, mouse_pos, threshold=12):
        """
        마우스 위치에서 가장 가까운 노선 선분을 찾는다.
        반환: (line, seg_start_idx, seg_end_idx) 또는 None
        """
        best_line, best_seg, best_d = None, None, threshold
        for line in self.lines:
            if len(line.stations) < 2:
                continue
            for i in range(len(line.stations) - 1):
                a = line.stations[i].position
                b = line.stations[i + 1].position
                d, _ = point_to_segment_distance(mouse_pos, a, b)
                if d < best_d:
                    best_d = d
                    best_line = line
                    best_seg = (i, i + 1)
        return (best_line, best_seg) if best_line else None

    def _spawn_train_for_line(self, line):
        train = Train(line, 0)
        train.on_passenger_delivered = self.add_score
        self.trains.append(train)
        line.trains.append(train)

    # ────────────────────────────────────────────────────────────
    # 입력
    # ────────────────────────────────────────────────────────────
    def handle_input(self):
        if not self.input.update():
            return False

        if self.game_over:
            return True

        keys = pygame.key.get_pressed()
        if keys[pygame.K_1]: self.active_line_index = 0
        if keys[pygame.K_2]: self.active_line_index = 1
        if keys[pygame.K_3]: self.active_line_index = 2

        mouse_pos = self.input.mouse_pos
        # 하드 스냅 범위: STATION_RADIUS + 40 (62px)
        self.hovered_station = self._get_hovered_station(mouse_pos, snap_range=STATION_RADIUS + 40)
        
        # 드래그 중 새로운 역에 스냅될 때 시각 피드백 파동 트리거
        if self.drag_mode != DragMode.NONE and self.hovered_station and self.hovered_station != self.last_hovered_station:
            color = self.drag_line.color if self.drag_line else Colors.LINE_COLORS[self.active_line_index]
            self.snap_pulses.append({
                "pos": (int(self.hovered_station.position.x), int(self.hovered_station.position.y)),
                "radius": 10.0,
                "color": color,
                "alpha": 255.0
            })
        self.last_hovered_station = self.hovered_station

        # 자석 당김(Magnetic Pull) 계산
        closest_station = None
        min_d = 999999.0
        for s in self.stations:
            d = s.position.distance_to(mouse_pos)
            if d < min_d:
                min_d = d
                closest_station = s

        # 하드 스냅(62px)과 자석 당김 시작 범위(140px) 사이의 보간 처리
        hard_snap = STATION_RADIUS + 40
        if closest_station and min_d < 140:
            if min_d <= hard_snap:
                self.drag_current_pos = closest_station.position
            else:
                # 마우스가 다가갈수록 선이 역 쪽으로 매끄럽게 빨려 들어가는 연출 (이차원 이징)
                ratio = (min_d - hard_snap) / (140 - hard_snap)
                ratio = ratio * ratio
                self.drag_current_pos = closest_station.position + (mouse_pos - closest_station.position) * ratio
        else:
            self.drag_current_pos = mouse_pos

        # ── 마우스 눌림 ───────────────────────────────────────────
        if self.input.mouse_pressed:
            if self.hovered_station:
                # 역 클릭 → EXTEND 모드
                self.drag_mode = DragMode.EXTEND
                self.drag_line = self.lines[self.active_line_index]
                for i, line in enumerate(self.lines):
                    if line.stations and (line.stations[0] == self.hovered_station
                                          or line.stations[-1] == self.hovered_station):
                        self.active_line_index = i
                        self.drag_line = line
                        break
                self.drag_start_station = self.hovered_station
                
                # 드래그 방향(시작점/끝점) 감지 및 임시 경로 복사
                if self.drag_line.stations:
                    if self.hovered_station == self.drag_line.stations[0]:
                        self.drag_extend_end = False
                        self.drag_path = list(self.drag_line.stations)
                    else:
                        self.drag_extend_end = True
                        self.drag_path = list(self.drag_line.stations)
                else:
                    self.drag_extend_end = True
                    self.drag_path = [self.hovered_station]
            else:
                # 역 없음 → 선분 클릭인지 확인 → INSERT 모드
                hit = self._hit_segment(mouse_pos)
                if hit:
                    line, (si, ei) = hit
                    self.drag_mode = DragMode.INSERT
                    self.drag_line = line
                    self.drag_insert_seg = (si, ei)
                    self.active_line_index = self.lines.index(line)
                    self.drag_path = list(line.stations)
                else:
                    # 하단 패널 클릭
                    y_offset = HEIGHT - 55
                    for i in range(len(self.lines)):
                        rect = pygame.Rect(20 + i * 55, y_offset, 45, 45)
                        if rect.collidepoint(mouse_pos):
                            self.active_line_index = i
                            break

        # ── 드래그 중: 임시 경로 실시간 갱신 (snapping) ─────────────
        if self.drag_mode == DragMode.EXTEND:
            s = self.hovered_station
            if s and s != self.drag_start_station:
                if self.drag_extend_end:
                    # 끝 역에서 연장
                    if s not in self.drag_path:
                        self.drag_path.append(s)
                        self.drag_start_station = s
                    elif len(self.drag_path) >= 2 and s == self.drag_path[-2]:
                        # 역방향 삭제 (되감기)
                        self.drag_path.pop()
                        self.drag_start_station = s
                else:
                    # 첫 역에서 연장 (앞으로 연장)
                    if s not in self.drag_path:
                        self.drag_path.insert(0, s)
                        self.drag_start_station = s
                    elif len(self.drag_path) >= 2 and s == self.drag_path[1]:
                        # 역방향 삭제 (되감기)
                        self.drag_path.pop(0)
                        self.drag_start_station = s

        elif self.drag_mode == DragMode.INSERT:
            s = self.hovered_station
            si, ei = self.drag_insert_seg
            if s:
                if s not in self.drag_line.stations:
                    # 중간에 스냅된 역 끼워 넣기 미리보기
                    self.drag_path = list(self.drag_line.stations)
                    self.drag_path.insert(ei, s)
                else:
                    self.drag_path = list(self.drag_line.stations)
            else:
                self.drag_path = list(self.drag_line.stations)

        # ── 마우스 놓음: 여기서 실제 연결/삽입 최종 확정 (Commit) ────
        if self.input.mouse_released:
            if self.drag_mode == DragMode.EXTEND:
                line = self.drag_line
                if len(self.drag_path) >= 2:
                    prepend_count = 0
                    if line.stations and line.stations[0] in self.drag_path:
                        prepend_count = self.drag_path.index(line.stations[0])
                        
                    # 기존 연결 데이터 삭제
                    for s in line.stations:
                        if line in s.connected_lines:
                            s.connected_lines.remove(line)
                            
                    had_no_stations = len(line.stations) == 0
                    line.stations = list(self.drag_path)
                    
                    # 새로운 연결 데이터 추가
                    for s in line.stations:
                        if line not in s.connected_lines:
                            s.connected_lines.append(line)
                            
                    # 새로운 노선이면 열차 스폰
                    if had_no_stations:
                        self._spawn_train_for_line(line)
                    else:
                        # 앞으로 연장된 경우 열차 인덱스 보정
                        for t in line.trains:
                            t.segment_index += prepend_count

            elif self.drag_mode == DragMode.INSERT:
                line = self.drag_line
                s = self.hovered_station
                si, ei = self.drag_insert_seg
                if s and s not in line.stations:
                    line.stations.insert(ei, s)
                    s.connected_lines.append(line)
                    for t in line.trains:
                        if t.segment_index >= ei:
                            t.segment_index += 1

            self.drag_mode = DragMode.NONE
            self.drag_line = None
            self.drag_path = []
            self.drag_start_station = None
            self.drag_insert_seg = None
            self.snap_proj = None

        return True

    # ────────────────────────────────────────────────────────────
    # 업데이트
    # ────────────────────────────────────────────────────────────
    def update(self, dt):
        if self.game_over:
            return

        self.spawner.update(dt)

        for s in self.stations:
            is_hovered = (s == self.hovered_station)
            s.update(dt, is_hovered)
            if s.overflow_timer >= OVERFLOW_MAX:
                self.game_over = True

        for t in self.trains:
            t.update(dt)

        # 스냅 파동 업데이트 (확장 및 페이드아웃)
        for pulse in self.snap_pulses[:]:
            pulse["radius"] += 150 * dt  # 팽창 속도
            pulse["alpha"] -= 450 * dt   # 페이드 속도
            if pulse["alpha"] <= 0:
                self.snap_pulses.remove(pulse)

    # ────────────────────────────────────────────────────────────
    # 렌더
    # ────────────────────────────────────────────────────────────
    def render(self):
        self.screen.fill(Colors.BACKGROUND)

        # 노선 그리기
        for line in self.lines:
            if self.drag_mode != DragMode.NONE and self.drag_line == line:
                # 드래그 편집 중인 노선은 임시 경로(drag_path)로 그림
                if len(self.drag_path) >= 2:
                    points = [(int(s.position.x), int(s.position.y)) for s in self.drag_path]
                    pygame.draw.lines(self.screen, line.color, line.is_loop, points, LINE_THICKNESS)
                    for p in points:
                        pygame.draw.circle(self.screen, line.color, p, LINE_THICKNESS // 2)
            else:
                self.renderer.draw_line(line)

        # 드래그 미리보기 고무줄 선
        if self.drag_mode == DragMode.EXTEND and self.drag_line:
            if self.drag_path:
                p1_station = self.drag_path[-1] if self.drag_extend_end else self.drag_path[0]
                p1 = (int(p1_station.position.x), int(p1_station.position.y))
                p2 = (int(self.drag_current_pos.x), int(self.drag_current_pos.y))
                color = self.drag_line.color
                pygame.draw.line(self.screen, color, p1, p2, LINE_THICKNESS)
                pygame.draw.circle(self.screen, color, p1, LINE_THICKNESS // 2)
                pygame.draw.circle(self.screen, color, p2, LINE_THICKNESS // 2)

        elif self.drag_mode == DragMode.INSERT and self.drag_line and self.drag_insert_seg:
            # 스냅된 역이 없을 때만 마우스 커서를 향해 꺾이는 고무줄 선을 그림 (스냅 시엔 drag_path가 그림)
            if not self.hovered_station:
                line = self.drag_line
                si, ei = self.drag_insert_seg
                if si < len(line.stations) and ei < len(line.stations):
                    a = (int(line.stations[si].position.x), int(line.stations[si].position.y))
                    b = (int(line.stations[ei].position.x), int(line.stations[ei].position.y))
                    c = (int(self.drag_current_pos.x), int(self.drag_current_pos.y))
                    pygame.draw.line(self.screen, line.color, a, c, LINE_THICKNESS)
                    pygame.draw.line(self.screen, line.color, c, b, LINE_THICKNESS)
                    pygame.draw.circle(self.screen, line.color, a, LINE_THICKNESS // 2)
                    pygame.draw.circle(self.screen, line.color, c, LINE_THICKNESS // 2)
                    pygame.draw.circle(self.screen, line.color, b, LINE_THICKNESS // 2)

        # 스냅 파동 그리기
        for pulse in self.snap_pulses:
            self.renderer.draw_pulse(pulse["pos"], pulse["radius"], pulse["color"], max(0, int(pulse["alpha"])))

        # 노선 편집 그랩 핸들 그리기 (드래그하지 않을 때 마우스가 닿으면 표시)
        if self.drag_mode == DragMode.NONE and self.hovered_station:
            active_line = self.lines[self.active_line_index]
            is_endpoint = False
            for line in self.lines:
                if line.stations and (line.stations[0] == self.hovered_station or line.stations[-1] == self.hovered_station):
                    pos = (int(self.hovered_station.position.x), int(self.hovered_station.position.y))
                    r = int(STATION_RADIUS * self.hovered_station.visual_scale) + 8
                    pygame.draw.circle(self.screen, line.color, pos, r, 4)
                    is_endpoint = True
                    break
            if not is_endpoint and not active_line.stations:
                pos = (int(self.hovered_station.position.x), int(self.hovered_station.position.y))
                r = int(STATION_RADIUS * self.hovered_station.visual_scale) + 8
                pygame.draw.circle(self.screen, active_line.color, pos, r, 4)

        # 역
        for s in self.stations:
            is_hovered = (s == self.hovered_station)
            self.renderer.draw_station(s, is_hovered)

        # 열차
        for t in self.trains:
            self.renderer.draw_train(t)

        self.hud.draw(self.screen)
        pygame.display.flip()
