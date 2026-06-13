import pygame
import os

pygame.init()
pygame.display.set_mode((1, 1), pygame.HIDDEN)

os.makedirs('assets/sprites', exist_ok=True)
bg_color = (255, 0, 255) # Magenta for transparency colorkey

def make_sprite(filename, w, h, draw_func):
    surf = pygame.Surface((w, h))
    surf.fill(bg_color)
    draw_func(surf, w, h)
    pygame.image.save(surf, f'assets/sprites/{filename}.bmp')

# Player
def draw_player(s, w, h):
    pygame.draw.circle(s, (220, 200, 180), (w//2, h//2), w//2)
    pygame.draw.circle(s, (50, 50, 50), (w//2 + 5, h//2 - 5), 3) # Eye
    pygame.draw.circle(s, (50, 50, 50), (w//2 + 5, h//2 + 5), 3) # Eye
make_sprite('player', 30, 30, draw_player)

# Police
def draw_police(s, w, h):
    pygame.draw.circle(s, (50, 100, 255), (w//2, h//2), w//2)
    pygame.draw.rect(s, (20, 20, 20), (w//2, h//2-4, 8, 8)) # Gun
    pygame.draw.rect(s, (255, 255, 0), (w//2-5, h//2-5, 10, 10)) # Badge
make_sprite('police', 30, 30, draw_police)

# Car
def draw_car(s, w, h):
    pygame.draw.rect(s, (200, 50, 50), (0, 0, w, h), border_radius=5)
    pygame.draw.rect(s, (50, 50, 50), (w//4, 2, w//2, h-4)) # Roof
    pygame.draw.rect(s, (255, 255, 100), (w-5, 2, 5, 5)) # Headlight
    pygame.draw.rect(s, (255, 255, 100), (w-5, h-7, 5, 5)) # Headlight
make_sprite('car', 60, 30, draw_car)

# Boss
def draw_boss(s, w, h):
    pygame.draw.circle(s, (150, 50, 150), (w//2, h//2), w//2)
    pygame.draw.circle(s, (255, 0, 0), (w//2 + 8, h//2 - 8), 6) # Big Red Eye
make_sprite('boss', 56, 56, draw_boss)

print("BMP Sprites generated successfully.")
pygame.quit()
