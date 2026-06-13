"""
Pygame 렌더러 (View Layer)
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
pygame에 의존하는 모든 렌더링 로직.
Android 포팅 시 이 파일만 교체한다.
"""
import math
import random
import pygame
from game.config import SCREEN_WIDTH, SCREEN_HEIGHT, COLORS, VEHICLE_TYPES, WEAPONS
from game.world.map import TILE_ROAD, TILE_SIDEWALK, TILE_GRASS, TILE_BUILDING, TILE_WATER
from game.views.bitmap_font import draw_text, text_width


TILE_COLORS = {
    TILE_ROAD:     COLORS["tile_road"],
    TILE_SIDEWALK: COLORS["tile_sidewalk"],
    TILE_GRASS:    COLORS["tile_grass"],
    TILE_BUILDING: COLORS["tile_building"],
    TILE_WATER:    COLORS["tile_water"],
}

WEAPON_COLORS = {
    "fist":    (200, 200, 200),
    "pistol":  (255, 255, 0),
    "uzi":     (255, 150, 0),
    "shotgun": (255, 80,  0),
    "rocket":  (255, 50,  50),
}


class PygameRenderer:
    def __init__(self):
        pygame.init()
        self.width  = SCREEN_WIDTH
        self.height = SCREEN_HEIGHT
        self.screen = pygame.display.set_mode((self.width, self.height))
        pygame.display.set_caption("GTA-Like Prototype")
        self.clock = pygame.time.Clock()
        # 한글 IME가 WASD 키를 가로채지 못하도록 텍스트 입력 모드 비활성화
        try:
            pygame.key.stop_text_input()
        except Exception:
            pass
            
        # ── 스프라이트 로드 ──
        self.sprites = {}
        sprite_files = ['player', 'police', 'car', 'boss']
        for name in sprite_files:
            try:
                img = pygame.image.load(f'assets/sprites/{name}.bmp').convert()
                img.set_colorkey((255, 0, 255)) # 마젠타 색상 투명 처리
                self.sprites[name] = img
            except Exception as e:
                print(f"Failed to load sprite {name}: {e}")

    def render(self, world, input_handler=None):
        cam_x = world.cam_x
        cam_y = world.cam_y

        self.screen.fill((40, 40, 40))

        # ── 맵 타일 ──
        self._draw_map(world.map, cam_x, cam_y)

        # ── 혈흔 ──
        for bx, by in world.bloods[-60:]:  # 최근 60개만
            dx = int(bx - cam_x + self.width // 2)
            dy = int(by - cam_y + self.height // 2)
            pygame.draw.circle(self.screen, COLORS["blood"], (dx, dy), 8)

        # ── 경험치 젬 ──
        for gem in world.exp_gems:
            self._draw_circle(gem.x, gem.y, gem.radius, COLORS["exp_gem"], cam_x, cam_y)

        # ── 픽업 아이템 ──
        for pk in world.pickups:
            w_color = WEAPON_COLORS.get(pk.item_type, (200, 200, 50))
            self._draw_circle(pk.x, pk.y, pk.radius, w_color, cam_x, cam_y)
            # 안쪽 흰 점
            dx = int(pk.x - cam_x + self.width // 2)
            dy = int(pk.y - cam_y + self.height // 2)
            pygame.draw.circle(self.screen, (255, 255, 255), (dx, dy), 3)

        # ── 공중전화 ──
        for phone in world.phones:
            color = COLORS["phone"] if phone.available else (100, 100, 100)
            self._draw_circle(phone.x, phone.y, phone.radius, color, cam_x, cam_y)

        # ── 가라지 ──
        for garage in world.garages:
            self._draw_circle(garage.x, garage.y, garage.radius, COLORS["garage"], cam_x, cam_y)

        # ── 미션 목적지 ──
        if world.current_mission and world.current_mission.active:
            mx = world.current_mission.objective_x
            my = world.current_mission.objective_y
            dx = int(mx - cam_x + self.width // 2)
            dy = int(my - cam_y + self.height // 2)
            pygame.draw.circle(self.screen, (255, 255, 100), (dx, dy), 20, 3)

        # ── 시민 ──
        for c in world.citizens:
            if 'player' in self.sprites:
                self._draw_sprite(c.x, c.y, c.radius*2, c.radius*2, getattr(c, 'angle', 0), 'player', cam_x, cam_y)
            else:
                self._draw_circle(c.x, c.y, c.radius, COLORS["citizen"], cam_x, cam_y)

        # ── 갱단원 ──
        for g in world.gang_members:
            if 'player' in self.sprites:
                self._draw_sprite(g.x, g.y, g.radius*2, g.radius*2, getattr(g, 'angle', 0), 'player', cam_x, cam_y)
            else:
                color = COLORS.get(g.gang_name, COLORS["gang"])
                self._draw_circle(g.x, g.y, g.radius, color, cam_x, cam_y)

        # ── 경찰 ──
        for p in world.police_list:
            if 'police' in self.sprites:
                self._draw_sprite(p.x, p.y, p.radius*2, p.radius*2, getattr(p, 'angle', 0), 'police', cam_x, cam_y)
            else:
                p_color = COLORS.get(p.unit_type, COLORS["police"])
                self._draw_circle(p.x, p.y, p.radius, p_color, cam_x, cam_y)

        # ── 총알 ──
        w_color = WEAPON_COLORS.get(world.player.current_weapon, COLORS["bullet"])
        for b in world.bullets:
            self._draw_circle(b.x, b.y, b.radius, w_color, cam_x, cam_y)

        # ── 차량 ──
        for v in world.vehicles:
            if v.hp > 0:
                self._draw_vehicle(v, cam_x, cam_y)
            elif v.exploded:
                dx = int(v.x - cam_x + self.width // 2)
                dy = int(v.y - cam_y + self.height // 2)
                pygame.draw.circle(self.screen, (30, 30, 30), (dx, dy), 22)

        # ── 보스 ──
        for boss in world.bosses:
            dx = int(boss.x - cam_x + self.width // 2)
            dy = int(boss.y - cam_y + self.height // 2)
            if 'boss' in self.sprites:
                self._draw_sprite(boss.x, boss.y, boss.radius*2, boss.radius*2, 0, 'boss', cam_x, cam_y)
            else:
                pygame.draw.circle(self.screen, boss.color, (dx, dy), boss.radius)
                pygame.draw.circle(self.screen, (255, 255, 255), (dx, dy), boss.radius, 2)
            # 보스 HP 바
            hp_ratio = boss.hp / max(1, boss.max_hp)
            bar_w = boss.radius * 2
            pygame.draw.rect(self.screen, (100, 0, 0),   (dx - boss.radius, dy - boss.radius - 8, bar_w, 5))
            pygame.draw.rect(self.screen, (255, 50, 50),  (dx - boss.radius, dy - boss.radius - 8, int(bar_w * hp_ratio), 5))

        # ── 오비탈 ──
        if hasattr(world, "orbital_positions"):
            for ox, oy in world.orbital_positions:
                self._draw_circle(ox, oy, 8, (0, 255, 255), cam_x, cam_y)

        # ── 플레이어 ──
        if not world.player.in_vehicle:
            if 'player' in self.sprites:
                self._draw_sprite(world.player.x, world.player.y, world.player.radius*2, world.player.radius*2, world.player.angle, 'player', cam_x, cam_y)
            else:
                self._draw_player(world.player, cam_x, cam_y)

        # ── HUD ──
        self._draw_hud(world)

        # ── 레벨업 선택 UI ──
        if world.upgrade_pending:
            self._draw_upgrade_ui(world.upgrade_pending)

        # ── 터치 UI (가상 조이스틱) ──
        if input_handler:
            self._draw_touch_ui(input_handler)

        pygame.display.flip()

    def tick(self, fps):
        return self.clock.tick(fps)

    def quit(self):
        pygame.quit()

    # ─── 내부 헬퍼 ───

    def _draw_map(self, game_map, cam_x, cam_y):
        start_tx = max(0, int((cam_x - self.width  // 2) // game_map.tile_size))
        end_tx   = min(game_map.width,  int((cam_x + self.width  // 2) // game_map.tile_size) + 1)
        start_ty = max(0, int((cam_y - self.height // 2) // game_map.tile_size))
        end_ty   = min(game_map.height, int((cam_y + self.height // 2) // game_map.tile_size) + 1)

        for ty in range(start_ty, end_ty):
            for tx in range(start_tx, end_tx):
                tile_type = game_map.grid[ty][tx]
                color = TILE_COLORS.get(tile_type, COLORS["black"])
                px = int(tx * game_map.tile_size - cam_x + self.width  // 2)
                py = int(ty * game_map.tile_size - cam_y + self.height // 2)
                pygame.draw.rect(self.screen, color, (px, py, game_map.tile_size, game_map.tile_size))
                if tile_type == TILE_BUILDING:
                    pygame.draw.rect(self.screen, (100, 50, 20), (px, py, game_map.tile_size, game_map.tile_size), 2)
                elif tile_type == TILE_SIDEWALK:
                    pygame.draw.rect(self.screen, (90, 90, 90), (px, py, game_map.tile_size, game_map.tile_size), 1)

    def _draw_circle(self, x, y, radius, color, cam_x, cam_y):
        dx = int(x - cam_x + self.width  // 2)
        dy = int(y - cam_y + self.height // 2)
        if -radius < dx < self.width + radius and -radius < dy < self.height + radius:
            pygame.draw.circle(self.screen, color, (dx, dy), max(1, int(radius)))

    def _draw_sprite(self, x, y, w, h, angle, sprite_name, cam_x, cam_y):
        dx = int(x - cam_x + self.width  // 2)
        dy = int(y - cam_y + self.height // 2)
        if -max(w,h) < dx < self.width + max(w,h) and -max(w,h) < dy < self.height + max(w,h):
            img = self.sprites[sprite_name]
            # 크기 맞추기 (원본 이미지가 다를 수 있으므로)
            if img.get_width() != w or img.get_height() != h:
                img = pygame.transform.scale(img, (int(w), int(h)))
            rotated = pygame.transform.rotate(img, -angle)
            rect = rotated.get_rect(center=(dx, dy))
            self.screen.blit(rotated, rect.topleft)

    def _draw_player(self, player, cam_x, cam_y):
        dx = int(player.x - cam_x + self.width  // 2)
        dy = int(player.y - cam_y + self.height // 2)
        pygame.draw.circle(self.screen, COLORS["player"], (dx, dy), player.radius)
        # 방향 화살표
        end_x = dx + math.cos(math.radians(player.angle)) * (player.radius + 6)
        end_y = dy + math.sin(math.radians(player.angle)) * (player.radius + 6)
        pygame.draw.line(self.screen, COLORS["white"], (dx, dy), (int(end_x), int(end_y)), 2)

    def _draw_vehicle(self, vehicle, cam_x, cam_y):
        if 'car' in self.sprites:
            self._draw_sprite(vehicle.x, vehicle.y, vehicle.width, vehicle.height, vehicle.angle, 'car', cam_x, cam_y)
        else:
            dx = int(vehicle.x - cam_x + self.width  // 2)
            dy = int(vehicle.y - cam_y + self.height // 2)

            color = getattr(vehicle, "color", COLORS["vehicle"])
            # 연기 시 어둡게
            if getattr(vehicle, "fire", False):
                color = (200, 100, 0)
            elif getattr(vehicle, "smoke", False):
                color = tuple(max(0, c - 60) for c in color)

            surface = pygame.Surface((vehicle.width, vehicle.height), pygame.SRCALPHA)
            surface.fill(color)
            rotated = pygame.transform.rotate(surface, -vehicle.angle)
            rect = rotated.get_rect(center=(dx, dy))
            self.screen.blit(rotated, rect.topleft)

        # 연기/불꽃 파티클
        dx = int(vehicle.x - cam_x + self.width  // 2)
        dy = int(vehicle.y - cam_y + self.height // 2)
        if getattr(vehicle, "fire", False):
            for _ in range(3):
                fx = dx + random.randint(-10, 10)
                fy = dy + random.randint(-10, 10)
                pygame.draw.circle(self.screen, (255, random.randint(100, 200), 0), (fx, fy), random.randint(3, 7))
        elif getattr(vehicle, "smoke", False):
            for _ in range(2):
                fx = dx + random.randint(-8, 8)
                fy = dy + random.randint(-8, 8)
                pygame.draw.circle(self.screen, (150, 150, 150), (fx, fy), random.randint(3, 6))

    def _draw_hud(self, world):
        W, H = self.width, self.height

        # ── 체력 바 (우상단) ──
        hp_pct = max(0, world.player.hp) / 100
        pygame.draw.rect(self.screen, (80, 0, 0),    (W - 162, 10, 152, 16))
        pygame.draw.rect(self.screen, (220, 50, 50), (W - 162, 10, int(150 * hp_pct), 16))
        pygame.draw.rect(self.screen, COLORS["white"], (W - 162, 10, 152, 16), 1)

        # ── 아머 바 ──
        if world.player.armor > 0:
            ar_pct = world.player.armor / 100
            pygame.draw.rect(self.screen, (0, 0, 100),    (W - 162, 30, 152, 10))
            pygame.draw.rect(self.screen, (100, 100, 255), (W - 162, 30, int(150 * ar_pct), 10))
            pygame.draw.rect(self.screen, COLORS["white"], (W - 162, 30, 152, 10), 1)

        # ── 무기 표시 ──
        w_color = WEAPON_COLORS.get(world.player.current_weapon, (200, 200, 200))
        pygame.draw.rect(self.screen, w_color,         (W - 162, 45, 152, 14))
        pygame.draw.rect(self.screen, COLORS["white"], (W - 162, 45, 152, 14), 1)

        # ── 돈 ──
        money_color = (80, 220, 80)
        money_w = min(152, int(152 * min(1.0, world.money / 100000)))
        pygame.draw.rect(self.screen, (0, 50, 0),      (W - 162, 63, 152, 12))
        pygame.draw.rect(self.screen, money_color,     (W - 162, 63, money_w, 12))
        pygame.draw.rect(self.screen, COLORS["white"], (W - 162, 63, 152, 12), 1)

        # ── 차량 체력 (탑승 중) ──
        if world.player.in_vehicle and world.current_vehicle and world.current_vehicle.hp > 0:
            v = world.current_vehicle
            v_pct = v.hp / max(1, v.max_hp)
            pygame.draw.rect(self.screen, (0, 60, 0),    (W // 2 - 51, H - 42, 102, 12))
            pygame.draw.rect(self.screen, (50, 200, 50), (W // 2 - 51, H - 42, int(100 * v_pct), 12))
            pygame.draw.rect(self.screen, COLORS["white"], (W // 2 - 51, H - 42, 102, 12), 1)

        # ── 수배 별 (우하단) ──
        star_y = H - 30
        for i in range(6):
            color = COLORS["bullet"] if i < world.wanted_level else (50, 50, 50)
            pygame.draw.rect(self.screen, color, (W - 30 - i * 22, star_y, 18, 18))

        # ── EXP 바 (좌하단) ──
        pygame.draw.rect(self.screen, (20, 20, 20),    (10, H - 32, 152, 18))
        if world.exp_to_next > 0:
            fill = int(150 * world.player_exp / world.exp_to_next)
            pygame.draw.rect(self.screen, (0, 200, 100), (10, H - 32, fill, 18))
        pygame.draw.rect(self.screen, COLORS["white"], (10, H - 32, 152, 18), 1)

        # ── 갱단 호감도 바 ──
        gang_colors = {"yakuza": (200, 50, 50), "loonies": (50, 200, 50), "zaibatsu": (50, 50, 200)}
        for i, (gname, gcolor) in enumerate(gang_colors.items()):
            rep = world.gang_rep.get(gname, 0)
            bx = 10 + i * 55
            by = H - 55
            pygame.draw.rect(self.screen, (30, 30, 30), (bx, by, 50, 8))
            fill = int(50 * (rep + 3) / 6)
            pygame.draw.rect(self.screen, gcolor,         (bx, by, fill, 8))
            pygame.draw.rect(self.screen, COLORS["white"], (bx, by, 50, 8), 1)

        # ── 미션 상태 ──
        if world.current_mission and world.current_mission.active:
            m = world.current_mission
            tl = m.time_left
            bx, by = W // 2 - 100, 10
            pygame.draw.rect(self.screen, (0, 0, 0, 180), (bx, by, 200, 30))
            pygame.draw.rect(self.screen, (255, 220, 0),   (bx, by, 200, 30), 2)
            if tl is not None:
                bar_w = int(198 * tl / max(1, m.data.get("time_limit", 1)))
                pygame.draw.rect(self.screen, (255, 200, 0), (bx + 1, by + 1, bar_w, 28))

        # ── Kill Frenzy 표시 ──
        if world.kill_frenzy_active:
            secs_left = world.kill_frenzy_timer // 60
            pygame.draw.rect(self.screen, (180, 40, 0), (W // 2 - 65, 45, 130, 28))
            pygame.draw.rect(self.screen, (255, 150, 0), (W // 2 - 65, 45, 130, 28), 2)
            draw_text(self.screen, f"FRENZY {secs_left}S K:{world.kill_frenzy_count}",
                      W // 2 - 60, 52, (255, 220, 0), scale=1)

        # ── 웨이브 상태 (좌상단) ──
        elapsed_s = world.wave_frame // 60
        mins, secs = divmod(elapsed_s, 60)
        pygame.draw.rect(self.screen, (20, 20, 20), (10, 10, 120, 20))
        pygame.draw.rect(self.screen, (100, 100, 100), (10, 10, 120, 20), 1)
        if world.wave_index < len(world.wave_timeline):
            wave_name = world.wave_timeline[world.wave_index][1]
        else:
            wave_name = "BOSS!"
        draw_text(self.screen, f"T{mins:02d}:{secs:02d} W{world.wave_index+1}",
                  14, 14, (200, 220, 200), scale=1)

        # ── 보스 화면 표시 (HP바, 중앙) ──
        if world.bosses:
            boss = world.bosses[0]
            bw = 300
            bx = W // 2 - bw // 2
            by = 80
            pygame.draw.rect(self.screen, (60, 0, 0),   (bx, by, bw, 18))
            hp_fill = int(bw * boss.hp / max(1, boss.max_hp))
            pygame.draw.rect(self.screen, (220, 30, 30), (bx, by, hp_fill, 18))
            pygame.draw.rect(self.screen, (255, 100, 100), (bx, by, bw, 18), 2)
            draw_text(self.screen, boss.name, bx + 5, by + 4, (255, 220, 220), scale=1)

    def _draw_upgrade_ui(self, choices):
        """레벨업 업그레이드 선택 화면 (1/2/3 키)"""
        W, H = self.width, self.height
        # 반투명 오버레이 (단색으로 대체)
        overlay = pygame.Surface((W, H))
        overlay.set_alpha(160)
        overlay.fill((0, 0, 0))
        self.screen.blit(overlay, (0, 0))

        # 제목 배너
        bw, bh = 320, 36
        bx, by = W // 2 - bw // 2, H // 2 - 100
        pygame.draw.rect(self.screen, (60, 40, 160), (bx, by, bw, bh))
        pygame.draw.rect(self.screen, (200, 200, 255), (bx, by, bw, bh), 2)
        draw_text(self.screen, "LEVEL UP! CHOOSE UPGRADE",
                  bx + 10, by + 12, (255, 255, 100), scale=1)

        # 선택지 박스 3개
        box_w, box_h = 140, 80
        total_w = box_w * 3 + 20 * 2
        start_x = W // 2 - total_w // 2
        for i, choice in enumerate(choices):
            bx2 = start_x + i * (box_w + 20)
            by2 = H // 2 - box_h // 2
            # 박스
            pygame.draw.rect(self.screen, (30, 70, 140), (bx2, by2, box_w, box_h))
            pygame.draw.rect(self.screen, (100, 200, 255), (bx2, by2, box_w, box_h), 2)
            # 숫자 키 표시
            key_label = str(i + 1)
            pygame.draw.circle(self.screen, (255, 220, 0), (bx2 + 15, by2 + 15), 10)
            draw_text(self.screen, key_label, bx2 + 11, by2 + 9, (0, 0, 0), scale=2)
            # 업그레이드 이름 (영어만 표시)
            name_en = {
                "fire_rate":  "FIRE RATE UP",
                "multishot":  "MULTISHOT UP",
                "pierce":     "PIERCE UP",
                "damage":     "DAMAGE UP",
                "range":      "RANGE UP",
                "speed":      "SPEED UP",
                "heal":       "HEAL +20",
                "armor":      "ARMOR +25",
                "orbital":    "ORBITAL",
                "explode":    "EXPLODE SHOT",
            }.get(choice["id"], choice["id"].upper())
            # 2줄로 나눠 표시
            words = name_en.split()
            for wi, word in enumerate(words[:2]):
                draw_text(self.screen, word, bx2 + 8, by2 + 32 + wi * 16, (255, 255, 255), scale=1)
            # 설명 (하단)
            desc_en = {
                "fire_rate":  "-5F COOLDOWN",
                "multishot":  "+1 BULLET",
                "pierce":     "+1 PIERCE",
                "damage":     "+20%% DMG",
                "range":      "+50 RANGE",
                "speed":      "+0.5 SPD",
                "heal":       "HP+20",
                "armor":      "ARMOR+25",
                "orbital":    "ORBIT BULLET",
                "explode":    "BOOM ON HIT",
            }.get(choice["id"], "")
            draw_text(self.screen, desc_en, bx2 + 6, by2 + 65, (180, 220, 255), scale=1)

        # 하단 안내
        draw_text(self.screen, "PRESS 1 / 2 / 3 OR TOUCH TO CHOOSE",
                  W // 2 - 120, H // 2 + 70, (200, 200, 200), scale=1)

    def _draw_touch_ui(self, ih):
        """가상 조이스틱과 액션 버튼을 그린다"""
        # 조이스틱 (반투명 서페이스 활용)
        joy_surf = pygame.Surface((ih.joy_radius * 2, ih.joy_radius * 2), pygame.SRCALPHA)
        pygame.draw.circle(joy_surf, (255, 255, 255, 80), (ih.joy_radius, ih.joy_radius), ih.joy_radius, 2)
        
        # 스틱의 상대 위치 계산
        sx = ih.joy_stick[0] - ih.joy_base[0] + ih.joy_radius
        sy = ih.joy_stick[1] - ih.joy_base[1] + ih.joy_radius
        stick_color = (200, 200, 255, 200) if ih.joy_dragging else (150, 150, 150, 150)
        pygame.draw.circle(joy_surf, stick_color, (sx, sy), 30)
        pygame.draw.circle(joy_surf, (255, 255, 255, 255), (sx, sy), 30, 2)
        
        self.screen.blit(joy_surf, (ih.joy_base[0] - ih.joy_radius, ih.joy_base[1] - ih.joy_radius))

        # 액션 버튼 (탑승/하차)
        rect = ih.btn_f_rect
        btn_color = (180, 50, 50) if ih.btn_f_pressed else (100, 30, 30)
        pygame.draw.rect(self.screen, btn_color, rect, border_radius=15)
        pygame.draw.rect(self.screen, (255, 100, 100), rect, 3, border_radius=15)
        draw_text(self.screen, "ENTER", rect.x + 20, rect.y + 35, (255, 255, 255), scale=2)
        draw_text(self.screen, "(F)", rect.x + 35, rect.y + 65, (200, 200, 200), scale=1)


import random  # 렌더러 내 파티클용
