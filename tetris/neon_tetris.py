import pygame
import random

# 초기화
import os
os.environ['SDL_AUDIODRIVER'] = 'dummy'
pygame.init()

# 화면 크기 설정
SCREEN_WIDTH = 800
SCREEN_HEIGHT = 700
BLOCK_SIZE = 30
GRID_WIDTH = 10
GRID_HEIGHT = 20
PLAY_WIDTH = GRID_WIDTH * BLOCK_SIZE
PLAY_HEIGHT = GRID_HEIGHT * BLOCK_SIZE

# 색상 정의 (네온 스타일)
BLACK = (0, 0, 0)
WHITE = (255, 255, 255)
NEON_CYAN = (0, 255, 255)
NEON_BLUE = (0, 102, 255)
NEON_ORANGE = (255, 153, 51)
NEON_YELLOW = (255, 255, 0)
NEON_GREEN = (51, 255, 51)
NEON_PURPLE = (153, 51, 255)
NEON_RED = (255, 51, 51)
GRAY = (40, 40, 40)

# 테트리스 블록 모양 정의
SHAPES = [
    [[1, 1, 1, 1]], # I
    [[1, 1], [1, 1]], # O
    [[0, 1, 0], [1, 1, 1]], # T
    [[0, 1, 1], [1, 1, 0]], # S
    [[1, 1, 0], [0, 1, 1]], # Z
    [[1, 0, 0], [1, 1, 1]], # J
    [[0, 0, 1], [1, 1, 1]]  # L
]

SHAPE_COLORS = [NEON_CYAN, NEON_YELLOW, NEON_PURPLE, NEON_GREEN, NEON_RED, NEON_BLUE, NEON_ORANGE]

class Piece:
    def __init__(self, x, y, shape):
        self.x = x
        self.y = y
        self.shape = shape
        self.color = SHAPE_COLORS[SHAPES.index(shape)]
        self.rotation = 0

def create_grid(locked_pos={}):
    grid = [[BLACK for _ in range(GRID_WIDTH)] for _ in range(GRID_HEIGHT)]
    for y in range(GRID_HEIGHT):
        for x in range(GRID_WIDTH):
            if (x, y) in locked_pos:
                grid[y][x] = locked_pos[(x, y)]
    return grid

def convert_shape_format(piece):
    positions = []
    shape = piece.shape
    
    # 회전 구현
    for _ in range(piece.rotation % 4):
        shape = [list(row) for row in zip(*shape[::-1])]
        
    for i, row in enumerate(shape):
        for j, column in enumerate(row):
            if column == 1:
                positions.append((piece.x + j, piece.y + i))
                
    return positions

def valid_space(piece, grid):
    accepted_pos = [[(j, i) for j in range(GRID_WIDTH) if grid[i][j] == BLACK] for i in range(GRID_HEIGHT)]
    accepted_pos = [item for sublist in accepted_pos for item in sublist]
    
    formatted = convert_shape_format(piece)
    
    for pos in formatted:
        if pos not in accepted_pos:
            if pos[1] > -1:
                return False
    return True

def check_lost(positions):
    for pos in positions:
        x, y = pos
        if y < 1:
            return True
    return False

def get_shape():
    return Piece(5, 0, random.choice(SHAPES))

def draw_text_middle(surface, text, size, color):
    font = pygame.font.SysFont('malgungothic', size, bold=True)
    label = font.render(text, 1, color)
    
    surface.blit(label, (SCREEN_WIDTH/2 - (label.get_width()/2), SCREEN_HEIGHT/2 - (label.get_height()/2)))

def draw_grid(surface, grid):
    sx = (SCREEN_WIDTH - PLAY_WIDTH) // 2
    sy = SCREEN_HEIGHT - PLAY_HEIGHT - 50
    
    for i in range(len(grid)):
        pygame.draw.line(surface, GRAY, (sx, sy + i*BLOCK_SIZE), (sx + PLAY_WIDTH, sy + i*BLOCK_SIZE))
        for j in range(len(grid[i])):
            pygame.draw.line(surface, GRAY, (sx + j*BLOCK_SIZE, sy), (sx + j*BLOCK_SIZE, sy + PLAY_HEIGHT))

def clear_rows(grid, locked):
    inc = 0
    for i in range(len(grid)-1, -1, -1):
        row = grid[i]
        if BLACK not in row:
            inc += 1
            ind = i
            for j in range(len(row)):
                try:
                    del locked[(j, i)]
                except:
                    continue
                    
    if inc > 0:
        for key in sorted(list(locked.keys()), key=lambda x: x[1])[::-1]:
            x, y = key
            if y < ind:
                newKey = (x, y + inc)
                locked[newKey] = locked.pop(key)
    return inc

def draw_next_shape(piece, surface):
    font = pygame.font.SysFont('malgungothic', 30)
    label = font.render('Next Shape', 1, WHITE)
    
    sx = SCREEN_WIDTH - 200
    sy = SCREEN_HEIGHT // 2 - 100
    shape = piece.shape
    
    # 회전 적용 (기본형)
    for i, row in enumerate(shape):
        for j, column in enumerate(row):
            if column == 1:
                # 네온 효과를 위해 두 번 그리기
                pygame.draw.rect(surface, piece.color, (sx + j*BLOCK_SIZE, sy + i*BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE), 0)
                pygame.draw.rect(surface, WHITE, (sx + j*BLOCK_SIZE, sy + i*BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE), 2)
                
    surface.blit(label, (sx - 10, sy - 40))

def draw_window(surface, grid, score=0):
    surface.fill(BLACK)
    
    pygame.font.init()
    font = pygame.font.SysFont('malgungothic', 60)
    label = font.render('NEON TETRIS', 1, NEON_CYAN)
    
    surface.blit(label, (SCREEN_WIDTH / 2 - (label.get_width() / 2), 30))
    
    # 점수 표시
    font = pygame.font.SysFont('malgungothic', 30)
    score_label = font.render(f'Score: {score}', 1, WHITE)
    surface.blit(score_label, (50, SCREEN_HEIGHT // 2 - 100))
    
    sx = (SCREEN_WIDTH - PLAY_WIDTH) // 2
    sy = SCREEN_HEIGHT - PLAY_HEIGHT - 50
    
    for i in range(len(grid)):
        for j in range(len(grid[i])):
            if grid[i][j] != BLACK:
                # 네온 효과: 내부 색상 + 외부 흰색 테두리
                pygame.draw.rect(surface, grid[i][j], (sx + j*BLOCK_SIZE, sy + i*BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE), 0)
                pygame.draw.rect(surface, WHITE, (sx + j*BLOCK_SIZE, sy + i*BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE), 1)
            
    pygame.draw.rect(surface, NEON_PURPLE, (sx, sy, PLAY_WIDTH, PLAY_HEIGHT), 4)
    
    draw_grid(surface, grid)

def main():
    locked_positions = {}
    grid = create_grid(locked_positions)
    
    change_piece = False
    run = True
    current_piece = get_shape()
    next_piece = get_shape()
    clock = pygame.time.Clock()
    fall_time = 0
    fall_speed = 0.27
    score = 0
    
    win = pygame.display.set_mode((SCREEN_WIDTH, SCREEN_HEIGHT))
    pygame.display.set_caption('Neon Tetris')
    
    while run:
        grid = create_grid(locked_positions)
        fall_time += clock.get_rawtime()
        clock.tick()
        
        if fall_time / 1000 > fall_speed:
            fall_time = 0
            current_piece.y += 1
            if not (valid_space(current_piece, grid)) and current_piece.y > 0:
                current_piece.y -= 1
                change_piece = True
                
        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                run = False
                pygame.display.quit()
                
            if event.type == pygame.KEYDOWN:
                if event.key == pygame.K_LEFT:
                    current_piece.x -= 1
                    if not (valid_space(current_piece, grid)):
                        current_piece.x += 1
                if event.key == pygame.K_RIGHT:
                    current_piece.x += 1
                    if not (valid_space(current_piece, grid)):
                        current_piece.x -= 1
                if event.key == pygame.K_DOWN:
                    current_piece.y += 1
                    if not (valid_space(current_piece, grid)):
                        current_piece.y -= 1
                if event.key == pygame.K_UP:
                    current_piece.rotation += 1
                    if not (valid_space(current_piece, grid)):
                        current_piece.rotation -= 1
                        
        shape_pos = convert_shape_format(current_piece)
        
        for i in range(len(shape_pos)):
            x, y = shape_pos[i]
            if y > -1:
                grid[y][x] = current_piece.color
                
        if change_piece:
            for pos in shape_pos:
                p = (pos[0], pos[1])
                locked_positions[p] = current_piece.color
            current_piece = next_piece
            next_piece = get_shape()
            change_piece = False
            score += clear_rows(grid, locked_positions) * 10
            
        draw_window(win, grid, score)
        draw_next_shape(next_piece, win)
        pygame.display.update()
        
        if check_lost(locked_positions):
            draw_text_middle(win, "GAME OVER", 80, WHITE)
            pygame.display.update()
            pygame.time.delay(1500)
            run = False

def main_menu():
    run = True
    win = pygame.display.set_mode((SCREEN_WIDTH, SCREEN_HEIGHT))
    pygame.display.set_caption('Neon Tetris')
    
    while run:
        win.fill(BLACK)
        draw_text_middle(win, 'Press Any Key To Play', 60, NEON_CYAN)
        pygame.display.update()
        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                run = False
            if event.type == pygame.KEYDOWN:
                main()
                
    pygame.display.quit()

if __name__ == "__main__":
    main_menu()
