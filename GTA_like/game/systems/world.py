"""
게임 월드 (Game World)
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
모든 엔티티와 시스템 상태를 관리하는 중앙 매니저.
pygame에 의존하지 않는다.
"""
import math
import random
from game.config import (
    VEHICLE_ENTER_RANGE, VEHICLE_EXIT_OFFSET,
    CITIZEN_MAX_COUNT, CITIZEN_SPAWN_CHANCE, CITIZEN_SPAWN_DIST,
    POLICE_SPAWN_CHANCE, POLICE_SPAWN_DIST,
    GANG_SPAWN_DIST, GANG_MAX_PER_ZONE, GANGS,
    GARAGE_REPAINT_COST, GARAGE_RADIUS,
    AUTOFIRE_COOLDOWN_MIN, AUTOFIRE_COOLDOWN_DECREASE, MULTISHOT_SPREAD,
    INITIAL_BULLET_COUNT, MULTISHOT_LEVEL_INTERVAL,
    EXP_PER_CITIZEN, EXP_PER_POLICE, EXP_PER_GANG, EXP_BASE_TO_NEXT, EXP_GROWTH_RATE,
    BULLET_MAX_RANGE, CAMERA_LERP,
    SCREEN_WIDTH, SCREEN_HEIGHT,
    WEAPONS, UPGRADES, VEHICLE_TYPES,
    KILL_FRENZY_DURATION, KILL_FRENZY_BONUS_EXP,
    PLAYER_MAX_HP, PLAYER_MAX_ARMOR
)
from game.models.player import PlayerModel
from game.models.vehicle import VehicleModel
from game.models.bullet import BulletModel
from game.models.citizen import CitizenModel
from game.models.police import PoliceModel
from game.models.gang import GangModel
from game.models.boss import BossModel
from game.models.pickup import PickupModel
from game.models.expgem import ExpGemModel
from game.models.mission import PhoneModel, MissionState
from game.models.garage import GarageModel
from game.world.map import GameMap


class GameWorld:
    """
    모든 게임 상태를 관리한다.
    렌더러(View)는 이 객체를 읽기만 하고,
    컨트롤러는 이 객체의 메서드를 호출만 한다.
    """

    def __init__(self):
        self.map = GameMap(80, 40)  # 80×40 타일 — 3개 구역
        # Downtown 시작: 좌측 1/3 중앙
        cx = (13 * 64) + 32
        cy = (20 * 64) + 32

        self.player = PlayerModel(cx, cy)
        # 다양한 차종 배치
        self.vehicles = [
            VehicleModel(cx + 120, cy, "sedan"),
            VehicleModel(cx - 120, cy, "sports"),
            VehicleModel(cx, cy + 120, "taxi"),
            VehicleModel(cx, cy - 120, "truck"),
        ]
        self.current_vehicle = None

        self.bullets      = []
        self.citizens     = []
        self.police_list  = []
        self.gang_members = []
        self.pickups      = []
        self.exp_gems     = []
        self.bloods       = []      # (x, y) 튜플

        self.bosses = []
        self.boss_defeated = False

        # 최적화용 공간 해시 그리드
        self.spatial_grid = {}

        # 수배
        self.wanted_level   = 0
        self.wanted_float   = 0.0
        self.wanted_decay_timer = 0

        # 갱단 호감도
        self.gang_rep = {g: 0 for g in GANGS}

        # 구역 해금 (Downtown은 기본, 나머지는 돈으로 해금)
        self.unlocked_zones = {0: True, 1: False, 2: False}  # 0=Downtown
        self.zone_unlock_cost = {1: 50000, 2: 200000}  # Residential, Industrial

        # 웨이브 시스템 (게임 경과 프레임)
        self.wave_frame    = 0   # 총 경과 프레임
        self.wave_index    = 0   # 현재 웨이브 단계
        self.boss_spawned  = False
        # 웨이브 타임라인 (초 기준)
        self.wave_timeline = [
            (0,   "CITIZENS ONLY"),
            (120, "POLICE ★"),
            (300, "POLICE ★★ + GANGS"),
            (600, "SWAT ★★★★"),
            (900, "FBI ★★★★★"),
            (1200,"ARMY ★★★★★★"),
            (1500,"BOSS WAVE!"),
        ]

        # 미션
        self.phones  = [PhoneModel(cx + 300, cy), PhoneModel(cx - 300, cy + 200)]
        self.garages = [GarageModel(cx + 250, cy - 250)]
        self.current_mission: MissionState | None = None
        self.mission_message = ""
        self.mission_timer = 0  # 메시지 표시 타이머

        # Kill Frenzy
        self.kill_frenzy_active  = False
        self.kill_frenzy_timer   = 0
        self.kill_frenzy_count   = 0
        self.kill_frenzy_pickup_timer = 600  # 처음 픽업 스폰까지

        # 뱀서 시스템
        self.player_level  = 1
        self.player_exp    = 0
        self.exp_to_next   = EXP_BASE_TO_NEXT
        self.fire_timer    = 0
        self.bullet_count  = INITIAL_BULLET_COUNT
        self.damage_mult   = 1.0
        self.range_bonus   = 0
        self.pierce_count  = 0
        self.orbital_count = 0
        self.orbital_angle = 0.0
        self.explode_on_hit = False
        self.upgrade_pending = []  # 레벨업 시 선택지 3개

        # 경제
        self.money = 0

        # 카메라
        self.cam_x = cx
        self.cam_y = cy

        # 이벤트 로그
        self.events = []

        # 초기 시민 스폰
        for _ in range(10):
            self._spawn_citizens(force=True)
        # 초기 갱단 스폰
        for g in GANGS:
            for _ in range(2):
                self._spawn_gang(g, force=True)

    # ─── 입력 처리 ───

    def try_enter_exit_vehicle(self):
        if not self.player.in_vehicle:
            closest_v, min_dist = None, VEHICLE_ENTER_RANGE
            for v in self.vehicles:
                if v.hp <= 0: continue
                d = math.hypot(self.player.x - v.x, self.player.y - v.y)
                if d < min_dist:
                    min_dist = d
                    closest_v = v
            if closest_v:
                self.player.in_vehicle = True
                closest_v.occupied = True
                self.current_vehicle = closest_v
        else:
            if self.current_vehicle:
                self.player.in_vehicle = False
                self.current_vehicle.occupied = False
                ex = self.current_vehicle.x - math.cos(math.radians(self.current_vehicle.angle + 90)) * VEHICLE_EXIT_OFFSET
                ey = self.current_vehicle.y - math.sin(math.radians(self.current_vehicle.angle + 90)) * VEHICLE_EXIT_OFFSET
                if self.map.is_solid(ex, ey):
                    ex = self.current_vehicle.x + math.cos(math.radians(self.current_vehicle.angle + 90)) * VEHICLE_EXIT_OFFSET
                    ey = self.current_vehicle.y + math.sin(math.radians(self.current_vehicle.angle + 90)) * VEHICLE_EXIT_OFFSET
                self.player.x = ex
                self.player.y = ey
                self.current_vehicle = None

    def choose_upgrade(self, idx):
        """레벨업 선택지에서 하나를 선택"""
        if not self.upgrade_pending or idx >= len(self.upgrade_pending):
            return
        choice = self.upgrade_pending[idx]
        self.upgrade_pending = []
        self._apply_upgrade(choice["id"])

    # ─── 메인 업데이트 ───

    def _build_spatial_grid(self):
        self.spatial_grid.clear()
        targets = self.police_list + self.gang_members + self.citizens + self.bosses
        for t in targets:
            cx, cy = int(t.x // 128), int(t.y // 128)
            if (cx, cy) not in self.spatial_grid:
                self.spatial_grid[(cx, cy)] = []
            self.spatial_grid[(cx, cy)].append(t)

    def _get_nearby_targets(self, x, y, radius):
        nearby = []
        cell_radius = int(radius // 128) + 1
        cx, cy = int(x // 128), int(y // 128)
        for dx in range(-cell_radius, cell_radius + 1):
            for dy in range(-cell_radius, cell_radius + 1):
                cell = (cx + dx, cy + dy)
                if cell in self.spatial_grid:
                    nearby.extend(self.spatial_grid[cell])
        return nearby

    def update(self, inputs):
        fwd   = inputs.get("forward", False)
        bwd   = inputs.get("backward", False)
        left  = inputs.get("left", False)
        right = inputs.get("right", False)

        # 레벨업 선택 대기 중이면 게임 일시정지
        if self.upgrade_pending:
            return

        # 1. 공간 해시 그리드 구축 (최적화)
        self._build_spatial_grid()

        # 플레이어 이동
        self.player.update(fwd, bwd, left, right, self.map)

        # 차량 업데이트
        for v in self.vehicles:
            if v.hp <= 0: continue
            if v.occupied and self.current_vehicle == v:
                v.update(fwd, bwd, left, right, self.map)
            else:
                v.update(False, False, False, False, self.map)
            if v.exploded and not v.hp <= 0:
                pass  # 이미 처리됨
            if v.hp <= 0 and not v.exploded:
                v.exploded = True
                self.events.append("💥 차량 폭발!")
                self.wanted_float = min(6.0, self.wanted_float + 0.5)
                if self.player.in_vehicle and self.current_vehicle == v:
                    self.player.in_vehicle = False
                    self._damage_player(50)
                    self.current_vehicle = None

        # 경찰 충돌 피해
        for p in self.police_list:
            d = math.hypot(p.x - self.player.x, p.y - self.player.y)
            if d < p.radius + self.player.radius:
                dmg = {"police": 1, "swat": 2, "fbi": 3, "army": 5}.get(p.unit_type, 1)
                self._damage_player(dmg)

        # 갱단 충돌 피해
        for g in self.gang_members:
            if g.state == "attack":
                d = math.hypot(g.x - self.player.x, g.y - self.player.y)
                if d < g.radius + self.player.radius:
                    self._damage_player(1)

        # 사망 처리
        if self.player.hp <= 0:
            self._wasted()

        # 아이템 획득
        for pk in self.pickups[:]:
            if math.hypot(pk.x - self.player.x, pk.y - self.player.y) < self.player.radius + pk.radius:
                if pk.item_type == "frenzy":
                    self._start_kill_frenzy()
                elif pk.item_type in WEAPONS:
                    self.player.current_weapon = pk.item_type
                    self.events.append(f"🔫 무기 획득: {pk.item_type.upper()}")
                self.pickups.remove(pk)

        # 경험치 젬 획득
        for gem in self.exp_gems[:]:
            if math.hypot(gem.x - self.player.x, gem.y - self.player.y) < self.player.radius + gem.radius:
                self.player_exp += gem.value
                self.exp_gems.remove(gem)
                self._check_level_up()
            elif not gem.update():
                self.exp_gems.remove(gem)

        # 가라지 체크
        for garage in self.garages:
            if garage.is_near(self.player.x, self.player.y):
                if self.money >= GARAGE_REPAINT_COST:
                    self.money -= GARAGE_REPAINT_COST
                    self.wanted_level = 0
                    self.wanted_float = 0.0
                    self.police_list.clear()
                    if self.current_vehicle:
                        self.current_vehicle.hp = self.current_vehicle.max_hp
                    self.events.append("🔧 가라지: 수리 완료! 수배 해제!")

        # 공중전화 체크
        if not self.current_mission:
            for phone in self.phones:
                phone.update()
                if phone.available:
                    d = math.hypot(phone.x - self.player.x, phone.y - self.player.y)
                    if d < phone.radius:
                        mission_data = phone.get_mission()
                        if mission_data:
                            cx = int(self.cam_x)
                            cy = int(self.cam_y)
                            self.current_mission = MissionState(mission_data, cx, cy)
                            self.events.append(f"📞 미션: {mission_data['title']} — {mission_data['description']}")

        # 미션 업데이트
        if self.current_mission:
            result = self.current_mission.update()
            if result == "timeout":
                self.events.append("⏰ 미션 실패! 시간 초과.")
                self.current_mission = None
            elif self.current_mission and self.current_mission.active:
                check = self.current_mission.check_objective(self.player.x, self.player.y)
                if check == "complete":
                    self._complete_mission()

        # Kill Frenzy 업데이트
        if self.kill_frenzy_active:
            self.kill_frenzy_timer -= 1
            if self.kill_frenzy_timer <= 0:
                bonus = self.kill_frenzy_count * KILL_FRENZY_BONUS_EXP
                self.player_exp += bonus
                self.money += self.kill_frenzy_count * 1000
                self.kill_frenzy_active = False
                self.events.append(f"🔥 Kill Frenzy 종료! 처치: {self.kill_frenzy_count}명, 보상: EXP+{bonus}")
                self._check_level_up()

        # Kill Frenzy 픽업 주기적 스폰
        self.kill_frenzy_pickup_timer -= 1
        if self.kill_frenzy_pickup_timer <= 0:
            self.kill_frenzy_pickup_timer = 600  # 10초마다
            for _ in range(5):
                ang = random.uniform(0, math.pi * 2)
                fx = self.player.x + math.cos(ang) * 300
                fy = self.player.y + math.sin(ang) * 300
                if not self.map.is_solid(fx, fy):
                    self.pickups.append(PickupModel(fx, fy, "frenzy"))
                    break

        # 구역 해금 체크
        for zone_id, cost in self.zone_unlock_cost.items():
            if not self.unlocked_zones.get(zone_id) and self.money >= cost:
                self.unlocked_zones[zone_id] = True
                names = {1: "RESIDENTIAL", 2: "INDUSTRIAL"}
                self.events.append(f"🗺️ 게 해금: {names.get(zone_id, str(zone_id))} ZONE!")

        # 수배 감소 (은신 중)
        self._update_wanted()

        # 자동 사격 + 오비탈
        self._auto_fire()
        self._update_orbital()

        # 총알 & 충돌
        self._update_bullets()

        # 웨이브 시스템
        self.wave_frame += 1
        self._update_wave()

        # NPC 업데이트
        for c in self.citizens:
            c.update(self.map)
        for p in self.police_list:
            p.update(self.player.x, self.player.y, self.map)
        for g in self.gang_members:
            rep = self.gang_rep.get(g.gang_name, 0)
            g.update(self.player.x, self.player.y, rep, self.map)
        for boss in self.bosses:
            boss.update(self.player.x, self.player.y, self.map)
            # 보스 충돌 피해
            if math.hypot(boss.x - self.player.x, boss.y - self.player.y) < boss.radius + self.player.radius:
                self._damage_player(boss.damage)

        # 스폰
        self._spawn_police()
        self._spawn_citizens()
        self._spawn_vehicles()
        for gang_name in GANGS:
            self._spawn_gang(gang_name)

        # 카메라
        self._update_camera()

    # ─── 웨이브 시스템 ───

    def _update_wave(self):
        """wave_frame 기준 웨이브 진행"""
        elapsed_sec = self.wave_frame // 60

        # 현재 웨이브 단계 판단
        new_idx = 0
        for i, (t, _) in enumerate(self.wave_timeline):
            if elapsed_sec >= t:
                new_idx = i

        if new_idx > self.wave_index:
            self.wave_index = new_idx
            name = self.wave_timeline[self.wave_index][1]
            self.events.append(f"⚡ WAVE: {name}")

        # 보스 웨이브 (25분 = 1500초)
        if self.wave_index >= 6 and not self.boss_spawned:
            self.boss_spawned = True
            boss_type = random.choice(["tank_boss", "swat_boss", "gang_boss"])
            for _ in range(10):
                ang = random.uniform(0, math.pi * 2)
                bx = self.player.x + math.cos(ang) * 400
                by = self.player.y + math.sin(ang) * 400
                if not self.map.is_solid(bx, by):
                    self.bosses.append(BossModel(bx, by, boss_type))
                    self.events.append(f"👾 BOSS WAVE! {BossModel.TYPES[boss_type]['name']} 등장!")
                    break

    def _start_kill_frenzy(self):
        if self.kill_frenzy_active:
            # 이미 진행 중이면 타이머만 연장
            self.kill_frenzy_timer = min(self.kill_frenzy_timer + 120, KILL_FRENZY_DURATION * 60)
            return
        self.kill_frenzy_active = True
        self.kill_frenzy_timer = KILL_FRENZY_DURATION * 60  # 초 → 프레임
        self.kill_frenzy_count = 0
        self.events.append("🔥 KILL FRENZY START! 60초 동안 최대한 많이 처치하라!")

    # ─── 자동 사격 (뱀서) ───

    def _auto_fire(self):
        if self.player.in_vehicle:
            return

        w_data = WEAPONS[self.player.current_weapon]
        base_cooldown = w_data["cooldown"]
        cooldown = max(AUTOFIRE_COOLDOWN_MIN,
                       base_cooldown - (self.player_level - 1) * AUTOFIRE_COOLDOWN_DECREASE)
        self.fire_timer += 1
        if self.fire_timer < cooldown:
            return

        targets = self._get_nearby_targets(self.player.x, self.player.y, w_data["range"] + self.range_bonus)
        if not targets:
            return

        closest, min_dist = None, w_data["range"] + self.range_bonus
        for t in targets:
            d = math.hypot(t.x - self.player.x, t.y - self.player.y)
            if d < min_dist:
                min_dist = d
                closest = t

        if not closest:
            return

        self.fire_timer = 0
        angle = math.degrees(math.atan2(closest.y - self.player.y, closest.x - self.player.x))

        if self.player.current_weapon == "fist":
            self._apply_damage(closest, w_data["damage"])
            return

        spread_count = w_data.get("spread", 1)
        bonus_bullets = (self.player_level - 1) // MULTISHOT_LEVEL_INTERVAL
        total_bullets = (INITIAL_BULLET_COUNT + bonus_bullets) * spread_count
        base_spread = 25.0 if spread_count > 1 else MULTISHOT_SPREAD

        spawn_x = self.player.x + math.cos(math.radians(self.player.angle)) * 15
        spawn_y = self.player.y + math.sin(math.radians(self.player.angle)) * 15

        for i in range(total_bullets):
            offset = (i - (total_bullets - 1) / 2) * base_spread
            dmg = int(w_data["damage"] * self.damage_mult)
            b = BulletModel(spawn_x, spawn_y, angle + offset, w_data["bullet_speed"], dmg)
            b.pierce_left = self.pierce_count
            b.explode_on_hit = self.explode_on_hit or w_data.get("explode", False)
            self.bullets.append(b)

    def _update_orbital(self):
        """오비탈: 플레이어 주위를 회전하는 방어 총알"""
        if self.orbital_count == 0:
            return
        self.orbital_angle = (self.orbital_angle + 2) % 360
        # 오비탈은 시각적 표현만 world에 저장, 렌더러가 읽어서 그림
        self.orbital_positions = []
        for i in range(self.orbital_count):
            a = self.orbital_angle + i * (360 / self.orbital_count)
            ox = self.player.x + math.cos(math.radians(a)) * 40
            oy = self.player.y + math.sin(math.radians(a)) * 40
            self.orbital_positions.append((ox, oy))
            # 오비탈이 닿은 적 처리
            nearby = self._get_nearby_targets(ox, oy, 20)
            for t in nearby:
                if math.hypot(t.x - ox, t.y - oy) < 10 + t.radius:
                    self._apply_damage(t, 5)

    # ─── 총알 & 충돌 ───

    def _update_bullets(self):
        for bullet in self.bullets[:]:
            bullet.update()
            hit = False
            nearby = self._get_nearby_targets(bullet.x, bullet.y, 40)
            for target in nearby:
                if math.hypot(bullet.x - target.x, bullet.y - target.y) < target.radius + bullet.radius:
                    # 폭발탄
                    if getattr(bullet, "explode_on_hit", False):
                        self._explosion(bullet.x, bullet.y, 60, 80)
                    else:
                        self._apply_damage(target, bullet.damage)

                    # 관통
                    if getattr(bullet, "pierce_left", 0) > 0:
                        bullet.pierce_left -= 1
                    else:
                        if bullet in self.bullets:
                            self.bullets.remove(bullet)
                        hit = True
                        break

            if not hit and math.hypot(bullet.x - bullet.start_x, bullet.y - bullet.start_y) > BULLET_MAX_RANGE:
                if bullet in self.bullets:
                    self.bullets.remove(bullet)

    def _explosion(self, x, y, radius, damage):
        """폭발: 반경 내 모든 적 피해"""
        self.bloods.append((x, y))
        nearby = self._get_nearby_targets(x, y, radius)
        for t in nearby:
            if math.hypot(t.x - x, t.y - y) < radius:
                self._apply_damage(t, damage)

    def _apply_damage(self, target, damage):
        self.bloods.append((target.x, target.y))
        kill_x, kill_y = target.x, target.y
        exp_gain = 0
        killed = False

        if target in self.citizens:
            self.citizens.remove(target)
            self.wanted_float = min(6.0, self.wanted_float + 1.0)
            self._update_wanted_level()
            exp_gain = EXP_PER_CITIZEN
            killed = True
        elif target in self.police_list:
            self.police_list.remove(target)
            self.wanted_float = min(6.0, self.wanted_float + 2.0)
            self._update_wanted_level()
            exp_gain = EXP_PER_POLICE
            killed = True
        elif target in self.gang_members:
            self.gang_members.remove(target)
            self.gang_rep[target.gang_name] = max(-3, self.gang_rep[target.gang_name] - 1)
            exp_gain = EXP_PER_GANG
            killed = True
        elif target in self.bosses:
            # 보스는 HP 차감, 0이 되면 제거
            target.hp -= damage
            if target.hp <= 0:
                self.bosses.remove(target)
                exp_gain = target.exp_reward
                self.money += 50000
                self.boss_spawned = False  # 다음 보스 웨이브 가능하게
                self.events.append(f"💀 BOSS DEFEATED! 보상: EXP+{exp_gain}, $50,000!")
                killed = True
            else:
                return  # 보스가 아직 살아있으면 젬 드롭 안 함

        if not killed:
            return

        # Kill Frenzy 카운트
        if self.kill_frenzy_active:
            self.kill_frenzy_count += 1

        # 미션 kill 카운트
        if self.current_mission:
            result = self.current_mission.check_objective(self.player.x, self.player.y, kill_delta=1)
            if result == "complete":
                self._complete_mission()

        # 경험치 젬 드롭
        for _ in range(max(1, exp_gain)):
            self.exp_gems.append(ExpGemModel(
                kill_x + random.uniform(-10, 10),
                kill_y + random.uniform(-10, 10),
                1
            ))

        # 무기 픽업 드롭 (10%)
        if random.random() < 0.1:
            drop = random.choice(["uzi", "shotgun", "pistol"])
            self.pickups.append(PickupModel(kill_x, kill_y, drop))

        self.player_exp += exp_gain
        self._check_level_up()

    def _damage_player(self, damage):
        if self.player.armor > 0:
            self.player.armor = max(0, self.player.armor - damage)
        else:
            self.player.hp -= damage

    def _wasted(self):
        self.events.append("💀 WASTED! 병원에서 부활합니다.")
        self.player.hp    = PLAYER_MAX_HP
        self.player.armor = 0
        self.player.money = max(0, self.money - int(self.money * 0.1))
        self.money        = self.player.money
        self.wanted_level = 0
        self.wanted_float = 0.0
        self.police_list.clear()
        self.player.current_weapon = "pistol"
        self.player.x = (20 * 64) + 32
        self.player.y = (20 * 64) + 32
        self.player.in_vehicle = False
        if self.current_vehicle:
            self.current_vehicle.occupied = False
            self.current_vehicle = None

    # ─── 수배 ───

    def _update_wanted(self):
        """경찰 시야 밖이면 수배 서서히 감소"""
        near_police = any(
            math.hypot(p.x - self.player.x, p.y - self.player.y) < 300
            for p in self.police_list
        )
        if not near_police and self.wanted_float > 0:
            self.wanted_decay_timer += 1
            if self.wanted_decay_timer >= 60:  # 1초마다
                self.wanted_decay_timer = 0
                self.wanted_float = max(0.0, self.wanted_float - 0.1)
                self._update_wanted_level()

    def _update_wanted_level(self):
        self.wanted_level = min(6, int(self.wanted_float))

    # ─── 레벨업 / 업그레이드 ───

    def _check_level_up(self):
        # 이미 업그레이드 선택 대기 중이면 스킵
        if self.upgrade_pending:
            return
        if self.player_exp >= self.exp_to_next:
            self.player_level += 1
            self.player_exp -= self.exp_to_next
            self.exp_to_next = int(self.exp_to_next * EXP_GROWTH_RATE)
            # 랜덤 3개 업그레이드 선택지 제시
            choices = random.sample(UPGRADES, min(3, len(UPGRADES)))
            self.upgrade_pending = choices
            self.events.append(f"⬆️ LEVEL UP! Lv.{self.player_level} — 업그레이드를 선택하세요! (1/2/3 키)")

    def _apply_upgrade(self, upgrade_id):
        if upgrade_id == "fire_rate":
            pass  # 쿨다운은 레벨 기준 자동 계산되므로 별도 처리 불필요
        elif upgrade_id == "multishot":
            self.bullet_count += 1
        elif upgrade_id == "pierce":
            self.pierce_count += 1
        elif upgrade_id == "damage":
            self.damage_mult *= 1.2
        elif upgrade_id == "range":
            self.range_bonus += 50
        elif upgrade_id == "speed":
            self.player.speed = min(self.player.speed + 0.5, 8.0)
        elif upgrade_id == "heal":
            self.player.hp = min(PLAYER_MAX_HP, self.player.hp + 20)
        elif upgrade_id == "armor":
            self.player.armor = min(PLAYER_MAX_ARMOR, self.player.armor + 25)
        elif upgrade_id == "orbital":
            self.orbital_count = min(3, self.orbital_count + 1)
        elif upgrade_id == "explode":
            self.explode_on_hit = True
        self.events.append(f"✅ 업그레이드 적용: {upgrade_id}")

    # ─── 미션 ───

    def _complete_mission(self):
        data = self.current_mission.data
        reward_money = data.get("reward_money", 0)
        reward_rep   = data.get("reward_rep", 0)
        reward_exp   = data.get("reward_exp", 0)
        gang         = data.get("gang", "")

        self.money      += reward_money
        self.player_exp += reward_exp
        if gang in self.gang_rep:
            self.gang_rep[gang] = min(3, self.gang_rep[gang] + reward_rep)

        self.events.append(f"✅ 미션 완료! 보상: ${reward_money:,} | EXP +{reward_exp}")
        self.current_mission = None
        self._check_level_up()

    # ─── 스폰 ───

    def _spawn_police(self, force=False):
        if self.wanted_level > 0 and len(self.police_list) < self.wanted_level * 2:
            if force or random.random() < POLICE_SPAWN_CHANCE:
                unit_type = "police"
                if self.wanted_level >= 6: unit_type = random.choice(["fbi", "army"])
                elif self.wanted_level >= 5: unit_type = random.choice(["swat", "fbi"])
                elif self.wanted_level >= 3: unit_type = random.choice(["police", "swat"])
                for _ in range(10):
                    ang = random.uniform(0, math.pi * 2)
                    px = self.player.x + math.cos(ang) * POLICE_SPAWN_DIST
                    py = self.player.y + math.sin(ang) * POLICE_SPAWN_DIST
                    if not self.map.is_solid(px, py):
                        self.police_list.append(PoliceModel(px, py, unit_type))
                        break

    def _spawn_citizens(self, force=False):
        if len(self.citizens) < CITIZEN_MAX_COUNT:
            if force or random.random() < CITIZEN_SPAWN_CHANCE:
                for _ in range(10):
                    ang = random.uniform(0, math.pi * 2)
                    cx = self.player.x + math.cos(ang) * CITIZEN_SPAWN_DIST
                    cy = self.player.y + math.sin(ang) * CITIZEN_SPAWN_DIST
                    if not self.map.is_solid(cx, cy):
                        self.citizens.append(CitizenModel(cx, cy))
                        break

    def _spawn_vehicles(self):
        # 화면 밖에 차량을 주기적으로 스폰 (최대 15대)
        if len(self.vehicles) < 15 and random.random() < 0.05:
            for _ in range(10):
                ang = random.uniform(0, math.pi * 2)
                vx = self.player.x + math.cos(ang) * 1000  # 화면 밖
                vy = self.player.y + math.sin(ang) * 1000
                if self.map.get_tile(vx, vy) == 0:  # TILE_ROAD(0) 에만 스폰
                    v_type = random.choice(list(VEHICLE_TYPES.keys()))
                    v = VehicleModel(vx, vy, v_type)
                    v.is_traffic = True
                    # 초기 진행 방향 설정 (대략 도로를 향하도록)
                    v.angle = math.degrees(ang) + 180 + random.uniform(-20, 20)
                    # 이미 차가 너무 가까우면 취소
                    too_close = False
                    for other_v in self.vehicles:
                        if math.hypot(v.x - other_v.x, v.y - other_v.y) < 60:
                            too_close = True
                            break
                    if not too_close:
                        self.vehicles.append(v)
                        break

    def _spawn_gang(self, gang_name, force=False):
        count = sum(1 for g in self.gang_members if g.gang_name == gang_name)
        if count < GANG_MAX_PER_ZONE:
            if force or random.random() < 0.005:
                for _ in range(10):
                    ang = random.uniform(0, math.pi * 2)
                    gx = self.player.x + math.cos(ang) * GANG_SPAWN_DIST
                    gy = self.player.y + math.sin(ang) * GANG_SPAWN_DIST
                    if not self.map.is_solid(gx, gy):
                        self.gang_members.append(GangModel(gx, gy, gang_name))
                        break

    # ─── 카메라 ───

    def _update_camera(self):
        if self.player.in_vehicle and self.current_vehicle:
            tx, ty = self.current_vehicle.x, self.current_vehicle.y
        else:
            tx, ty = self.player.x, self.player.y
        self.cam_x += (tx - self.cam_x) * CAMERA_LERP
        self.cam_y += (ty - self.cam_y) * CAMERA_LERP
