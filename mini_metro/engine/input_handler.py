import pygame
from pygame.math import Vector2

class InputHandler:
    def __init__(self):
        self.mouse_pos = Vector2()
        self.mouse_down = False
        self.mouse_pressed = False
        self.mouse_released = False
        
    def update(self):
        self.mouse_pressed = False
        self.mouse_released = False
        self.mouse_pos = Vector2(pygame.mouse.get_pos())
        
        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                return False
            elif event.type == pygame.MOUSEBUTTONDOWN:
                if event.button == 1:
                    self.mouse_down = True
                    self.mouse_pressed = True
            elif event.type == pygame.MOUSEBUTTONUP:
                if event.button == 1:
                    self.mouse_down = False
                    self.mouse_released = True
        return True
