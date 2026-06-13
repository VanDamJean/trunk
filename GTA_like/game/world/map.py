"""
타일 기반 맵 시스템
━━━━━━━━━━━━━━━━━━━━
"""
from game.config import TILE_SIZE, TILE_ROAD, TILE_SIDEWALK, TILE_GRASS, TILE_BUILDING, TILE_WATER

class GameMap:
    ZONE_DOWNTOWN    = 0
    ZONE_RESIDENTIAL = 1
    ZONE_INDUSTRIAL  = 2

    def __init__(self, width_tiles=80, height_tiles=40):
        self.width = width_tiles
        self.height = height_tiles
        self.tile_size = TILE_SIZE
        self.grid = [[TILE_GRASS for _ in range(self.width)] for _ in range(self.height)]
        self._generate_map()

    def _generate_map(self):
        W, H = self.width, self.height

        # 가장자리 물
        for x in range(W):
            self.grid[0][x] = TILE_WATER
            self.grid[H - 1][x] = TILE_WATER
        for y in range(H):
            self.grid[y][0] = TILE_WATER
            self.grid[y][W - 1] = TILE_WATER

        # ── 구역 1: Downtown (좌측 1/3) ──
        self._build_zone_downtown(1, 1, W // 3 - 2, H - 2)

        # 구역 구분 수직 도로
        mid1 = W // 3
        for y in range(1, H - 1):
            for x in range(mid1, mid1 + 3):
                if 0 <= x < W:
                    self.grid[y][x] = TILE_ROAD
            self.grid[y][mid1 - 1] = TILE_SIDEWALK
            self.grid[y][mid1 + 3] = TILE_SIDEWALK

        # ── 구역 2: Residential (중간 1/3) ──
        self._build_zone_residential(mid1 + 4, 1, W // 3 - 6, H - 2)

        mid2 = W * 2 // 3
        for y in range(1, H - 1):
            for x in range(mid2, mid2 + 3):
                if 0 <= x < W:
                    self.grid[y][x] = TILE_ROAD
            self.grid[y][mid2 - 1] = TILE_SIDEWALK
            self.grid[y][mid2 + 3] = TILE_SIDEWALK

        # ── 구역 3: Industrial (우측 1/3) ──
        self._build_zone_industrial(mid2 + 4, 1, W - mid2 - 6, H - 2)

    def _build_zone_downtown(self, ox, oy, w, h):
        """십자형 도로 + 건물 블록"""
        road_w = 3
        cx = ox + w // 2
        cy = oy + h // 2

        for x in range(ox, ox + w):
            for y in range(cy - road_w // 2, cy + road_w // 2 + 1):
                if 0 < y < self.height - 1: self.grid[y][x] = TILE_ROAD
            if 0 < cy - road_w // 2 - 1 < self.height - 1:
                self.grid[cy - road_w // 2 - 1][x] = TILE_SIDEWALK
            if 0 < cy + road_w // 2 + 1 < self.height - 1:
                self.grid[cy + road_w // 2 + 1][x] = TILE_SIDEWALK

        for y in range(oy, oy + h):
            for x in range(cx - road_w // 2, cx + road_w // 2 + 1):
                if 0 < x < self.width - 1: self.grid[y][x] = TILE_ROAD
            if 0 < cx - road_w // 2 - 1 < self.width - 1:
                self.grid[y][cx - road_w // 2 - 1] = TILE_SIDEWALK
            if 0 < cx + road_w // 2 + 1 < self.width - 1:
                self.grid[y][cx + road_w // 2 + 1] = TILE_SIDEWALK

        for bx, by, bw, bh in [(ox+1, oy+1, 6, 5), (cx+3, oy+1, 6, 5),
                                (ox+1, cy+3, 6, 5), (cx+3, cy+3, 6, 5)]:
            self._fill_block(bx, by, bw, bh, TILE_BUILDING)

    def _build_zone_residential(self, ox, oy, w, h):
        """격자형 도로 + 주거 블록"""
        road_w = 2
        for y in range(oy, oy + h, 8):
            for x in range(ox, ox + w):
                for dy in range(road_w):
                    if 0 < y + dy < self.height - 1:
                        self.grid[y + dy][x] = TILE_ROAD
                if 0 < y - 1 < self.height - 1: self.grid[y - 1][x] = TILE_SIDEWALK
                if 0 < y + road_w < self.height - 1: self.grid[y + road_w][x] = TILE_SIDEWALK

        for x in range(ox, ox + w, 10):
            for y in range(oy, oy + h):
                for dx in range(road_w):
                    if 0 < x + dx < self.width - 1:
                        self.grid[y][x + dx] = TILE_ROAD

        # 소형 건물들
        for bx in range(ox + 3, ox + w - 3, 10):
            for by in range(oy + 3, oy + h - 3, 8):
                self._fill_block(bx, by, 4, 4, TILE_BUILDING)

    def _build_zone_industrial(self, ox, oy, w, h):
        """넓은 공장 블록 + 직선 도로"""
        road_w = 3
        for y in range(oy, oy + h, 10):
            for x in range(ox, ox + w):
                for dy in range(road_w):
                    if 0 < y + dy < self.height - 1:
                        self.grid[y + dy][x] = TILE_ROAD

        # 대형 공장 건물
        for bx in range(ox + 2, ox + w - 2, 12):
            for by in range(oy + 4, oy + h - 4, 10):
                self._fill_block(bx, by, 8, 5, TILE_BUILDING)

    def _fill_block(self, ox, oy, w, h, tile):
        for y in range(oy, oy + h):
            for x in range(ox, ox + w):
                if 0 <= x < self.width and 0 <= y < self.height:
                    self.grid[y][x] = tile

    def get_tile(self, x_pixel, y_pixel):
        tx = int(x_pixel // self.tile_size)
        ty = int(y_pixel // self.tile_size)
        if 0 <= tx < self.width and 0 <= ty < self.height:
            return self.grid[ty][tx]
        return TILE_WATER

    def is_solid(self, x_pixel, y_pixel):
        return self.get_tile(x_pixel, y_pixel) in (TILE_BUILDING, TILE_WATER)

    def get_zone(self, x_pixel):
        """픽셀 x좌표로 구역 반환"""
        zone_w = self.width * self.tile_size / 3
        if x_pixel < zone_w:
            return self.ZONE_DOWNTOWN
        elif x_pixel < zone_w * 2:
            return self.ZONE_RESIDENTIAL
        return self.ZONE_INDUSTRIAL
