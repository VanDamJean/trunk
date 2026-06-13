import pygame
import random
import os
import io
import math
import struct
import wave

pygame.init()


def _wav_mono_s16(freq_hz, duration_ms, volume=0.28, sample_rate=22050):
    """짧은 사인파 비프를 WAV 바이트로 생성 (외부 파일 불필요)."""
    n = max(1, int(sample_rate * duration_ms / 1000))
    frames = bytearray()
    for i in range(n):
        t = i / max(n - 1, 1)
        env = min(t * 30, (1 - t) * 4, 1.0)
        s = int(32767 * volume * env * math.sin(2 * math.pi * freq_hz * i / sample_rate))
        s = max(-32768, min(32767, s))
        frames.extend(struct.pack('<h', s))
    buf = io.BytesIO()
    with wave.open(buf, 'wb') as w:
        w.setnchannels(1)
        w.setsampwidth(2)
        w.setframerate(sample_rate)
        w.writeframes(bytes(frames))
    buf.seek(0)
    return buf.read()


def _wav_korobeiniki_style_bgm(sample_rate=22050, volume=0.22):
    """8비트 테트리스 느낌 민요풍 루프 (전통 멜로디 '코로베이니키' 유사 진행, 개인용 절차 음원)."""
    # A4=440 기준 12평균율
    def hz(semi_from_a4):
        return 440.0 * (2.0 ** (semi_from_a4 / 12.0))

    A4, B4, C5, D5, E5 = hz(0), hz(2), hz(3), hz(5), hz(7)
    s, m, l, r = 145, 270, 400, 380  # 짧음/중간/김/쉼 ms

    # (주파수 Hz, 길이 ms) — 0 = 쉼
    phrase = [
        (E5, s), (E5, s), (B4, s), (C5, s), (D5, s), (D5, s), (C5, s), (B4, s),
        (A4, s), (A4, s), (C5, s), (E5, s), (D5, s), (C5, s), (B4, l),
        (B4, s), (C5, s), (D5, s), (E5, s), (C5, m), (A4, m), (A4, m), (0, r),
    ]
    # 한 번 더 이어 붙여 루프가 자연스럽게
    phrase = phrase + phrase

    frames = bytearray()
    for freq, duration_ms in phrase:
        n = max(1, int(sample_rate * duration_ms / 1000))
        if freq <= 0:
            frames.extend(b'\x00\x00' * n)
            continue
        for i in range(n):
            phase = 2 * math.pi * freq * i / sample_rate
            # 얇은 스퀘어(고조파 하나 섞어 클릭 완화)
            sq = 1.0 if math.sin(phase) >= 0 else -1.0
            soft = 0.72 * sq + 0.28 * math.sin(phase)
            t = i / max(n - 1, 1)
            env = min(t * 55.0, (1.0 - t) * 12.0, 1.0)
            sample = int(32767 * volume * env * soft)
            sample = max(-32768, min(32767, sample))
            frames.extend(struct.pack('<h', sample))

    buf = io.BytesIO()
    with wave.open(buf, 'wb') as w:
        w.setnchannels(1)
        w.setsampwidth(2)
        w.setframerate(sample_rate)
        w.writeframes(bytes(frames))
    buf.seek(0)
    return buf.read()


class GameAudio:
    """mixer 미사용/실패 시 무음으로 동작."""

    def __init__(self):
        self.ok = False
        self.muted = False
        self._sounds = {}
        self._bgm_sound = None
        self._bgm_channel = None
        try:
            pygame.mixer.init(frequency=22050, size=-16, channels=1, buffer=512)
            self._sounds['lock'] = pygame.mixer.Sound(io.BytesIO(_wav_mono_s16(180, 45, 0.22)))
            self._sounds['hard'] = pygame.mixer.Sound(io.BytesIO(_wav_mono_s16(90, 70, 0.3)))
            self._sounds['hold'] = pygame.mixer.Sound(io.BytesIO(_wav_mono_s16(520, 55, 0.2)))
            self._sounds['rotate'] = pygame.mixer.Sound(io.BytesIO(_wav_mono_s16(340, 28, 0.15)))
            self._sounds['move'] = pygame.mixer.Sound(io.BytesIO(_wav_mono_s16(240, 18, 0.08)))
            self._sounds['clear1'] = pygame.mixer.Sound(io.BytesIO(_wav_mono_s16(440, 120, 0.26)))
            self._sounds['clear2'] = pygame.mixer.Sound(io.BytesIO(_wav_mono_s16(550, 120, 0.26)))
            self._sounds['clear3'] = pygame.mixer.Sound(io.BytesIO(_wav_mono_s16(660, 130, 0.27)))
            self._sounds['clear4'] = pygame.mixer.Sound(io.BytesIO(_wav_mono_s16(780, 150, 0.28)))
            self._sounds['gameover'] = pygame.mixer.Sound(io.BytesIO(_wav_mono_s16(110, 380, 0.35)))
            self._bgm_sound = pygame.mixer.Sound(io.BytesIO(_wav_korobeiniki_style_bgm()))
            self._bgm_sound.set_volume(0.42)
            self.ok = True
        except (NotImplementedError, pygame.error, Exception):
            self._sounds = {}
            self._bgm_sound = None

    def play(self, name):
        if not self.ok or self.muted or name not in self._sounds:
            return
        self._sounds[name].play()

    def play_clear(self, rows):
        if not self.ok or self.muted or rows < 1:
            return
        key = f'clear{min(rows, 4)}'
        self.play(key)

    def start_bgm(self):
        if not self.ok or self.muted or self._bgm_sound is None:
            return
        if self._bgm_channel and self._bgm_channel.get_busy():
            return
        self._bgm_channel = self._bgm_sound.play(loops=-1)

    def stop_bgm(self):
        if self._bgm_sound is not None:
            self._bgm_sound.stop()
        self._bgm_channel = None

    def start_file_bgm_if_any(self):
        if not self.ok or self.muted:
            return
        base = os.path.dirname(os.path.abspath(__file__))
        for name in ('bgm.ogg', 'bgm.mp3', 'bgm.wav'):
            path = os.path.join(base, name)
            if os.path.isfile(path):
                try:
                    pygame.mixer.music.load(path)
                    pygame.mixer.music.set_volume(0.35)
                    pygame.mixer.music.play(-1)
                    return
                except (pygame.error, NotImplementedError):
                    continue
        self.start_bgm()

    def stop_file_bgm(self):
        try:
            pygame.mixer.music.stop()
        except (pygame.error, NotImplementedError):
            pass
        self.stop_bgm()


AUDIO = GameAudio()


def play_lock_feedback(rows_cleared, hard_drop=False):
    """블록 고정 시 효과음 (줄 삭제 > 하드드롭 > 일반 고정)."""
    if rows_cleared > 0:
        AUDIO.play_clear(rows_cleared)
    elif hard_drop:
        AUDIO.play('hard')
    else:
        AUDIO.play('lock')


# --- 화면 설정 ---
SCREEN_WIDTH = 800
SCREEN_HEIGHT = 700
BLOCK_SIZE = 30
GRID_WIDTH = 10
GRID_HEIGHT = 20
PLAY_WIDTH = GRID_WIDTH * BLOCK_SIZE
PLAY_HEIGHT = GRID_HEIGHT * BLOCK_SIZE

# --- 사이드 패널 영역 ---
SIDE_LEFT = 160   # 왼쪽 홀드 영역 너비
SIDE_RIGHT = 160  # 오른쪽 정보 영역 너비
GRID_X = SIDE_LEFT  # 그리드 시작 X

# --- 색상 (네온 스타일) ---
BLACK = (0, 0, 0)
WHITE = (255, 255, 255)
GRAY = (40, 40, 40)
DARK_GRAY = (20, 20, 20)
NEON_CYAN = (0, 255, 255)
NEON_BLUE = (0, 102, 255)
NEON_ORANGE = (255, 153, 51)
NEON_YELLOW = (255, 255, 0)
NEON_GREEN = (51, 255, 51)
NEON_PURPLE = (153, 51, 255)
NEON_RED = (255, 51, 51)
GHOST_COLOR = (80, 80, 80)

# --- 블록 모양 정의 ---
SHAPES = [
    [[1, 1, 1, 1]],                       # I
    [[1, 1], [1, 1]],                     # O
    [[0, 1, 0], [1, 1, 1]],               # T
    [[0, 1, 1], [1, 1, 0]],               # S
    [[1, 1, 0], [0, 1, 1]],               # Z
    [[1, 0, 0], [1, 1, 1]],               # J
    [[0, 0, 1], [1, 1, 1]]                # L
]

SHAPE_COLORS = [NEON_CYAN, NEON_YELLOW, NEON_PURPLE, NEON_GREEN, NEON_RED, NEON_BLUE, NEON_ORANGE]

# 레벨별 낙하 속도 (초 단위)
LEVEL_SPEEDS = [0.27, 0.25, 0.23, 0.21, 0.19, 0.17, 0.15, 0.13, 0.11, 0.09, 0.07, 0.05]

# 동시 삭제 점수
LINE_SCORES = {0: 0, 1: 100, 2: 300, 3: 500, 4: 800}


class Piece:
    def __init__(self, x, y, shape_idx):
        self.x = x
        self.y = y
        self.shape = SHAPES[shape_idx]
        self.shape_idx = shape_idx
        self.color = SHAPE_COLORS[shape_idx]
        self.rotation = 0

    def rotated_shape(self):
        shape = self.shape
        for _ in range(self.rotation % 4):
            shape = [list(row) for row in zip(*shape[::-1])]
        return shape


def create_grid(locked_pos):
    grid = [[BLACK for _ in range(GRID_WIDTH)] for _ in range(GRID_HEIGHT)]
    for y in range(GRID_HEIGHT):
        for x in range(GRID_WIDTH):
            if (x, y) in locked_pos:
                grid[y][x] = locked_pos[(x, y)]
    return grid


def convert_shape_format(piece):
    positions = []
    shape = piece.rotated_shape()
    for i, row in enumerate(shape):
        for j, column in enumerate(row):
            if column == 1:
                positions.append((piece.x + j, piece.y + i))
    return positions


def valid_space(piece, grid):
    accepted = set()
    for y in range(GRID_HEIGHT):
        for x in range(GRID_WIDTH):
            if grid[y][x] == BLACK:
                accepted.add((x, y))
    for pos in convert_shape_format(piece):
        if pos not in accepted and pos[1] > -1:
            return False
    return True


def get_ghost_position(piece, grid):
    ghost = Piece(piece.x, piece.y, piece.shape_idx)
    ghost.rotation = piece.rotation
    ghost.y = piece.y
    while True:
        ghost.y += 1
        if not valid_space(ghost, grid):
            ghost.y -= 1
            break
    return convert_shape_format(ghost)


def check_lost(locked_pos):
    for (x, y) in locked_pos:
        if y < 1:
            return True
    return False


def get_random_shape_idx():
    return random.randint(0, len(SHAPES) - 1)


def clear_rows(grid, locked):
    """여러 줄 삭제를 올바르게 처리하고 삭제한 줄 수를 반환"""
    full_rows = []
    for y in range(GRID_HEIGHT - 1, -1, -1):
        if BLACK not in grid[y]:
            full_rows.append(y)

    for y in full_rows:
        for x in range(GRID_WIDTH):
            locked.pop((x, y), None)

    if full_rows:
        shift = len(full_rows)
        new_locked = {}
        for (x, y), color in locked.items():
            if y < min(full_rows):
                new_locked[(x, y + shift)] = color
            else:
                new_locked[(x, y)] = color
        locked.clear()
        locked.update(new_locked)

    return len(full_rows)


def get_fall_speed(level):
    idx = min(level, len(LEVEL_SPEEDS) - 1)
    return LEVEL_SPEEDS[idx]


# --- 폰트 캐시 ---
def get_font(size, bold=False):
    return pygame.font.SysFont('arial', size, bold=bold)


# --- 그리기 함수들 ---

def draw_block(surface, color, x, y, size, is_ghost=False):
    """네온 효과 블록 그리기"""
    if is_ghost:
        pygame.draw.rect(surface, GHOST_COLOR, (x, y, size, size), 0)
        pygame.draw.rect(surface, (100, 100, 100), (x, y, size, size), 1)
    else:
        pygame.draw.rect(surface, color, (x, y, size, size), 0)
        pygame.draw.rect(surface, WHITE, (x, y, size, size), 2)


def draw_mini_shape(surface, piece, sx, sy):
    """홀드/다음 블록 미리 보기 그리기"""
    shape = piece.rotated_shape() if isinstance(piece, Piece) else piece
    color = piece.color if isinstance(piece, Piece) else SHAPE_COLORS[SHAPES.index(piece)] if piece in SHAPES else WHITE
    for i, row in enumerate(shape):
        for j, column in enumerate(row):
            if column == 1:
                bx = sx + j * (BLOCK_SIZE // 2)
                by = sy + i * (BLOCK_SIZE // 2)
                draw_block(surface, color, bx, by, BLOCK_SIZE // 2)


def draw_panel_box(surface, x, y, width, height):
    """패널 배경 박스"""
    pygame.draw.rect(surface, DARK_GRAY, (x, y, width, height), border_radius=8)
    pygame.draw.rect(surface, GRAY, (x, y, width, height), 1, border_radius=8)


def draw_window(surface, grid, score, level, lines, next_piece, held_piece, can_hold=True):
    surface.fill(BLACK)

    # 타이틀
    title_font = get_font(48, True)
    title = title_font.render('QWEN TETRIS', True, NEON_CYAN)
    surface.blit(title, (GRID_X + (PLAY_WIDTH - title.get_width()) // 2, 10))

    # 왼쪽 패널: 홀드
    draw_panel_box(surface, 10, 80, SIDE_LEFT - 20, 120)
    label_font = get_font(20)
    hold_label = label_font.render('HOLD', True, WHITE)
    surface.blit(hold_label, (10 + (SIDE_LEFT - 20 - hold_label.get_width()) // 2, 60))
    if held_piece is not None:
        draw_mini_shape(surface, held_piece, 50, 100)
    # 홀드 가능 여부 표시
    if not can_hold:
        dim = pygame.Surface((SIDE_LEFT - 20, 120), pygame.SRCALPHA)
        dim.fill((0, 0, 0, 120))
        surface.blit(dim, (10, 80))

    # 오른쪽 패널: 다음 블록, 레벨, 점수, 줄 수
    rx = GRID_X + PLAY_WIDTH + 10
    draw_panel_box(surface, rx, 80, SIDE_RIGHT - 20, 120)
    next_label = label_font.render('NEXT', True, WHITE)
    surface.blit(next_label, (rx + (SIDE_RIGHT - 20 - next_label.get_width()) // 2, 60))
    draw_mini_shape(surface, next_piece, rx + 30, 100)

    # 레벨
    draw_panel_box(surface, rx, 220, SIDE_RIGHT - 20, 60)
    lv_label = label_font.render('LEVEL', True, WHITE)
    surface.blit(lv_label, (rx + (SIDE_RIGHT - 20 - lv_label.get_width()) // 2, 205))
    lv_val = get_font(30, True).render(str(level), True, NEON_YELLOW)
    surface.blit(lv_val, (rx + (SIDE_RIGHT - 20 - lv_val.get_width()) // 2, 230))

    # 점수
    draw_panel_box(surface, rx, 300, SIDE_RIGHT - 20, 80)
    sc_label = label_font.render('SCORE', True, WHITE)
    surface.blit(sc_label, (rx + (SIDE_RIGHT - 20 - sc_label.get_width()) // 2, 285))
    sc_val = get_font(24, True).render(str(score), True, NEON_GREEN)
    surface.blit(sc_val, (rx + (SIDE_RIGHT - 20 - sc_val.get_width()) // 2, 315))

    # 삭제 줄
    draw_panel_box(surface, rx, 400, SIDE_RIGHT - 20, 60)
    ln_label = label_font.render('LINES', True, WHITE)
    surface.blit(ln_label, (rx + (SIDE_RIGHT - 20 - ln_label.get_width()) // 2, 385))
    ln_val = get_font(30, True).render(str(lines), True, NEON_PURPLE)
    surface.blit(ln_val, (rx + (SIDE_RIGHT - 20 - ln_val.get_width()) // 2, 410))

    # 조작법 안내 (하단)
    ctrl_font = get_font(13)
    controls = [
        'Arrow Keys: Move/Rotate',
        'Down: Soft Drop',
        'Space: Hard Drop',
        'C: Hold',
        'M: Mute',
        'ESC: Pause'
    ]
    for i, text in enumerate(controls):
        c_label = ctrl_font.render(text, True, GRAY)
        surface.blit(c_label, (rx + 5, 500 + i * 22))

    # 게임 그리드 외각
    pygame.draw.rect(surface, NEON_PURPLE, (GRID_X, 60, PLAY_WIDTH, PLAY_HEIGHT), 3)

    # 블럭 그리기
    sy_offset = 60
    for y in range(GRID_HEIGHT):
        for x in range(GRID_WIDTH):
            if grid[y][x] != BLACK:
                draw_block(surface, grid[y][x], GRID_X + x * BLOCK_SIZE, sy_offset + y * BLOCK_SIZE, BLOCK_SIZE)

    # 그리드 선
    for i in range(GRID_HEIGHT + 1):
        pygame.draw.line(surface, GRAY, (GRID_X, sy_offset + i * BLOCK_SIZE), (GRID_X + PLAY_WIDTH, sy_offset + i * BLOCK_SIZE))
    for j in range(GRID_WIDTH + 1):
        pygame.draw.line(surface, GRAY, (GRID_X + j * BLOCK_SIZE, sy_offset), (GRID_X + j * BLOCK_SIZE, sy_offset + PLAY_HEIGHT))


def draw_game_with_piece(surface, grid, current_piece, ghost_positions, score, level, lines, next_piece, held_piece, can_hold):
    draw_window(surface, grid, score, level, lines, next_piece, held_piece, can_hold)
    sy_offset = 60
    # 고스트 피스
    for (gx, gy) in ghost_positions:
        if gy >= 0:
            draw_block(surface, None, GRID_X + gx * BLOCK_SIZE, sy_offset + gy * BLOCK_SIZE, BLOCK_SIZE, is_ghost=True)
    # 현재 피스
    shape = current_piece.rotated_shape()
    for i, row in enumerate(shape):
        for j, column in enumerate(row):
            if column == 1:
                px = current_piece.x + j
                py = current_piece.y + i
                if py >= 0:
                    draw_block(surface, current_piece.color, GRID_X + px * BLOCK_SIZE, sy_offset + py * BLOCK_SIZE, BLOCK_SIZE)


def draw_text_middle(surface, text, size, color):
    font = get_font(size, True)
    label = font.render(text, True, color)
    surface.blit(label, (SCREEN_WIDTH // 2 - label.get_width() // 2, SCREEN_HEIGHT // 2 - label.get_height() // 2))


# --- 메인 게임 ---

def main(win):
    AUDIO.start_file_bgm_if_any()
    locked_positions = {}
    grid = create_grid(locked_positions)

    current_piece = Piece(4, 0, get_random_shape_idx())
    next_piece = Piece(4, 0, get_random_shape_idx())
    held_piece = None
    can_hold = True

    clock = pygame.time.Clock()
    fall_time = 0
    score = 0
    level = 0
    lines = 0
    paused = False

    while True:
        fall_speed = get_fall_speed(level)
        grid = create_grid(locked_positions)
        fall_time += clock.get_rawtime()
        clock.tick()

        # 일시정지 화면
        if paused:
            draw_game_with_piece(win, grid, current_piece, [], score, level, lines, next_piece, held_piece, can_hold)
            draw_text_middle(win, 'PAUSED', 60, NEON_CYAN)
            pygame.display.update()

            for event in pygame.event.get():
                if event.type == pygame.QUIT:
                    AUDIO.stop_file_bgm()
                    pygame.display.quit()
                    return False
                if event.type == pygame.KEYDOWN:
                    if event.key == pygame.K_ESCAPE:
                        paused = False
            continue

        # 자동 낙하
        if fall_time / 1000 > fall_speed:
            fall_time = 0
            current_piece.y += 1
            if not valid_space(current_piece, grid):
                current_piece.y -= 1
                # 조각 고정
                shape_pos = convert_shape_format(current_piece)
                for (px, py) in shape_pos:
                    if py >= 0:
                        locked_positions[(px, py)] = current_piece.color
                rows_cleared = clear_rows(grid, locked_positions)
                if rows_cleared > 0:
                    lines += rows_cleared
                    score += LINE_SCORES.get(rows_cleared, 0) * (level + 1)
                    level = lines // 10
                play_lock_feedback(rows_cleared, hard_drop=False)
                if check_lost(locked_positions):
                    AUDIO.stop_file_bgm()
                    AUDIO.play('gameover')
                    return 'gameover'
                current_piece = next_piece
                next_piece = Piece(4, 0, get_random_shape_idx())
                can_hold = True

        # 입력 처리
        change_piece = False
        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                AUDIO.stop_file_bgm()
                pygame.display.quit()
                return False
            if event.type == pygame.KEYDOWN:
                if event.key == pygame.K_LEFT:
                    current_piece.x -= 1
                    if not valid_space(current_piece, grid):
                        current_piece.x += 1
                    else:
                        AUDIO.play('move')
                elif event.key == pygame.K_RIGHT:
                    current_piece.x += 1
                    if not valid_space(current_piece, grid):
                        current_piece.x -= 1
                    else:
                        AUDIO.play('move')
                elif event.key == pygame.K_DOWN:
                    current_piece.y += 1
                    if not valid_space(current_piece, grid):
                        current_piece.y -= 1
                        fall_time = 0
                elif event.key == pygame.K_UP:
                    current_piece.rotation += 1
                    if not valid_space(current_piece, grid):
                        current_piece.rotation -= 1
                    else:
                        AUDIO.play('rotate')
                elif event.key == pygame.K_SPACE:
                    # 하드 드롭
                    while valid_space(current_piece, grid):
                        current_piece.y += 1
                    current_piece.y -= 1
                    fall_time = 0
                    change_piece = True
                elif event.key == pygame.K_c or event.key == pygame.K_LSHIFT:
                    if can_hold:
                        if held_piece is None:
                            held_piece = Piece(4, 0, current_piece.shape_idx)
                            current_piece = next_piece
                            next_piece = Piece(4, 0, get_random_shape_idx())
                        else:
                            tmp_idx = current_piece.shape_idx
                            current_piece = Piece(4, 0, held_piece.shape_idx)
                            held_piece = Piece(4, 0, tmp_idx)
                        can_hold = False
                        fall_time = 0
                        AUDIO.play('hold')
                elif event.key == pygame.K_m:
                    AUDIO.muted = not AUDIO.muted
                    if AUDIO.muted:
                        AUDIO.stop_file_bgm()
                    else:
                        AUDIO.start_file_bgm_if_any()
                elif event.key == pygame.K_ESCAPE:
                    paused = True

        if change_piece:
            shape_pos = convert_shape_format(current_piece)
            for (px, py) in shape_pos:
                if py >= 0:
                    locked_positions[(px, py)] = current_piece.color
            rows_cleared = clear_rows(grid, locked_positions)
            if rows_cleared > 0:
                lines += rows_cleared
                score += LINE_SCORES.get(rows_cleared, 0) * (level + 1)
                level = lines // 10
            play_lock_feedback(rows_cleared, hard_drop=True)
            if check_lost(locked_positions):
                AUDIO.stop_file_bgm()
                AUDIO.play('gameover')
                return 'gameover'
            current_piece = next_piece
            next_piece = Piece(4, 0, get_random_shape_idx())
            can_hold = True

        # 화면 그리기
        ghost_pos = get_ghost_position(current_piece, grid)
        draw_game_with_piece(win, grid, current_piece, ghost_pos, score, level, lines, next_piece, held_piece, can_hold)
        pygame.display.update()


def main_menu():
    win = pygame.display.set_mode((SCREEN_WIDTH, SCREEN_HEIGHT))
    pygame.display.set_caption('Qwen Tetris')

    while True:
        win.fill(BLACK)
        draw_text_middle(win, 'QWEN TETRIS', 56, NEON_CYAN)
        font = get_font(28)
        sub = font.render('Press any key to play', True, NEON_GREEN)
        win.blit(sub, (SCREEN_WIDTH // 2 - sub.get_width() // 2, SCREEN_HEIGHT // 2 + 50))
        pygame.display.update()

        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                pygame.display.quit()
                return
            if event.type == pygame.KEYDOWN:
                result = main(win)
                if result is False:
                    pygame.display.quit()
                    return
                elif result == 'gameover':
                    game_over_screen(win)


def game_over_screen(win):
    win.fill(BLACK)
    draw_text_middle(win, 'GAME OVER', 72, NEON_RED)
    font = get_font(28)
    sub = font.render('Press any key to retry', True, NEON_GREEN)
    win.blit(sub, (SCREEN_WIDTH // 2 - sub.get_width() // 2, SCREEN_HEIGHT // 2 + 60))
    pygame.display.update()

    while True:
        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                pygame.display.quit()
                return
            if event.type == pygame.KEYDOWN:
                main_menu()
                return


if __name__ == '__main__':
    main_menu()
