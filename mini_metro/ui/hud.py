import pygame
from utils.colors import Colors
from utils.constants import WIDTH, HEIGHT, STATION_RADIUS

class HUD:
    def __init__(self, game):
        self.game = game
        self.font = pygame.font.SysFont("Helvetica", 32)
        self.font_small = pygame.font.SysFont("Helvetica", 20)
        self.font_big = pygame.font.SysFont("Helvetica", 72)
        
    def draw(self, screen):
        # 점수 (상단 중앙, 큰 숫자)
        score_text = self.font.render(str(self.game.score), True, Colors.TEXT)
        screen.blit(score_text, (WIDTH // 2 - score_text.get_width() // 2, 18))
        
        # 하단 노선 패널
        y_offset = HEIGHT - 55
        for i, line in enumerate(self.game.lines):
            color = line.color
            x = 20 + i * 55
            rect = pygame.Rect(x, y_offset, 45, 45)
            
            if i == self.game.active_line_index:
                # 활성 노선: 채워진 원
                pygame.draw.circle(screen, color, (x + 22, y_offset + 22), 20)
                pygame.draw.circle(screen, Colors.BLACK, (x + 22, y_offset + 22), 20, 3)
            else:
                # 비활성: 테두리만
                pygame.draw.circle(screen, color, (x + 22, y_offset + 22), 18, 3)
                
            # 노선에 연결된 역 개수 표시
            count = len(line.stations)
            if count > 0:
                ct = self.font_small.render(str(count), True, Colors.TEXT_LIGHT)
                screen.blit(ct, (x + 22 - ct.get_width() // 2, y_offset + 46))
            
        # 게임오버 오버레이
        if self.game.game_over:
            overlay = pygame.Surface((WIDTH, HEIGHT), pygame.SRCALPHA)
            overlay.fill((250, 248, 240, 180))
            screen.blit(overlay, (0, 0))
            
            go_text = self.font_big.render("GAME OVER", True, Colors.TEXT)
            screen.blit(go_text, (WIDTH // 2 - go_text.get_width() // 2, HEIGHT // 2 - 60))
            
            score_label = self.font.render(f"Passengers: {self.game.score}", True, Colors.TEXT_LIGHT)
            screen.blit(score_label, (WIDTH // 2 - score_label.get_width() // 2, HEIGHT // 2 + 30))
