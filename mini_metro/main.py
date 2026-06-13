import pygame
import sys
import os

sys.path.append(os.path.dirname(os.path.abspath(__file__)))

from utils.constants import WIDTH, HEIGHT, FPS
from engine.game import Game

def main():
    pygame.init()
    screen = pygame.display.set_mode((WIDTH, HEIGHT))
    pygame.display.set_caption("Mini Metro Clone")
    clock = pygame.time.Clock()
    
    game = Game(screen)
    
    running = True
    while running:
        dt = clock.tick(FPS) / 1000.0
        
        running = game.handle_input()
        game.update(dt)
        game.render()
        
    pygame.quit()
    sys.exit()

if __name__ == "__main__":
    main()
