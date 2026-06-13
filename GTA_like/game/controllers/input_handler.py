"""
Pygame 입력 핸들러 (Input Handler)
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
키보드 입력을 플랫폼 무관한 dict로 변환한다.

한글 IME 대응 & 안드로이드 포팅 대비:
  가상 조이스틱과 화면 터치 버튼을 구현하여, 키보드가 먹통이 되는 현상을 우회한다.
  (마우스 클릭/드래그는 모바일에서 터치 이벤트로 동일하게 작동)
"""
import math
import pygame

# WASD 스캔코드 (IME 무관하게 물리적 키 위치로 판단)
_KEY_FORWARD  = pygame.K_w
_KEY_BACKWARD = pygame.K_s
_KEY_LEFT     = pygame.K_a
_KEY_RIGHT    = pygame.K_d


class PygameInputHandler:
    def __init__(self):
        self.quit_requested = False
        self.action_enter_vehicle = False
        self.upgrade_choice = None

        # 직접 관리하는 키 상태
        self._held = {
            "forward":  False,
            "backward": False,
            "left":     False,
            "right":    False,
        }

        # --- 가상 조이스틱 UI 데이터 ---
        # 렌더러가 화면 크기를 알 수 있으므로 임의의 절대/상대 위치 사용 (나중에 화면 크기 대응)
        self.joy_base = (120, 480)   # 왼쪽 아래 (임시 y좌표, process에서 화면크기 맞춰 갱신)
        self.joy_radius = 70
        self.joy_stick = self.joy_base
        self.joy_dragging = False

        self.btn_f_rect = pygame.Rect(800 - 140, 600 - 140, 100, 100) # 오른쪽 아래
        self.btn_f_pressed = False

    def process(self):
        self.action_enter_vehicle = False
        self.upgrade_choice = None

        screen = pygame.display.get_surface()
        if screen:
            W, H = screen.get_size()
            self.joy_base = (140, H - 140)
            self.btn_f_rect.x = W - 140
            self.btn_f_rect.y = H - 140
            if not self.joy_dragging:
                self.joy_stick = self.joy_base

        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                self.quit_requested = True

            # --- 터치(마우스) 이벤트 ---
            elif event.type == pygame.MOUSEBUTTONDOWN:
                if event.button == 1:
                    # 조이스틱 터치 확인
                    dist = math.hypot(event.pos[0] - self.joy_base[0], event.pos[1] - self.joy_base[1])
                    if dist <= self.joy_radius + 30: # 터치 판정 조금 넓게
                        self.joy_dragging = True
                        self.joy_stick = event.pos
                    
                    # 액션(탑승/하차) 버튼 터치 확인
                    elif self.btn_f_rect.collidepoint(event.pos):
                        self.btn_f_pressed = True
                        self.action_enter_vehicle = True

                    # 업그레이드 버튼 터치 확인 (화면 중앙에 3개)
                    if screen:
                        W, H = screen.get_size()
                        box_w, box_h = 140, 80
                        total_w = box_w * 3 + 20 * 2
                        start_x = W // 2 - total_w // 2
                        for i in range(3):
                            bx = start_x + i * (box_w + 20)
                            by = H // 2 - box_h // 2
                            btn_rect = pygame.Rect(bx, by, box_w, box_h)
                            if btn_rect.collidepoint(event.pos):
                                self.upgrade_choice = i

            elif event.type == pygame.MOUSEBUTTONUP:
                if event.button == 1:
                    self.joy_dragging = False
                    self.joy_stick = self.joy_base
                    self.btn_f_pressed = False

            elif event.type == pygame.MOUSEMOTION:
                if self.joy_dragging:
                    dx = event.pos[0] - self.joy_base[0]
                    dy = event.pos[1] - self.joy_base[1]
                    dist = math.hypot(dx, dy)
                    if dist > self.joy_radius:
                        dx = dx * self.joy_radius / dist
                        dy = dy * self.joy_radius / dist
                    self.joy_stick = (self.joy_base[0] + dx, self.joy_base[1] + dy)


            # --- 키보드 이벤트 (기존 백업) ---
            elif event.type == pygame.KEYDOWN:
                if event.key in (_KEY_FORWARD,): self._held["forward"] = True
                if event.key in (_KEY_BACKWARD,): self._held["backward"] = True
                if event.key in (_KEY_LEFT,): self._held["left"] = True
                if event.key in (_KEY_RIGHT,): self._held["right"] = True
                if event.key == pygame.K_f: self.action_enter_vehicle = True
                elif event.key == pygame.K_1: self.upgrade_choice = 0
                elif event.key == pygame.K_2: self.upgrade_choice = 1
                elif event.key == pygame.K_3: self.upgrade_choice = 2

            elif event.type == pygame.KEYUP:
                if event.key in (_KEY_FORWARD,): self._held["forward"] = False
                if event.key in (_KEY_BACKWARD,): self._held["backward"] = False
                if event.key in (_KEY_LEFT,): self._held["left"] = False
                if event.key in (_KEY_RIGHT,): self._held["right"] = False

        # --- 조이스틱 입력값을 방향키 bool로 변환 ---
        joy_f, joy_b, joy_l, joy_r = False, False, False, False
        if self.joy_dragging:
            threshold = 20
            dx = self.joy_stick[0] - self.joy_base[0]
            dy = self.joy_stick[1] - self.joy_base[1]
            if dy < -threshold: joy_f = True
            if dy > threshold:  joy_b = True
            if dx < -threshold: joy_l = True
            if dx > threshold:  joy_r = True

        keys = pygame.key.get_pressed()
        return {
            "forward":  self._held["forward"]  or bool(keys[_KEY_FORWARD])  or joy_f,
            "backward": self._held["backward"] or bool(keys[_KEY_BACKWARD]) or joy_b,
            "left":     self._held["left"]     or bool(keys[_KEY_LEFT])     or joy_l,
            "right":    self._held["right"]    or bool(keys[_KEY_RIGHT])    or joy_r,
        }
