from pygame.math import Vector2
from utils.constants import ShapeType, MAX_CAPACITY, OVERFLOW_MAX

class Station:
    def __init__(self, shape: ShapeType, position: Vector2):
        self.shape = shape
        self.position = position
        self.passengers = []
        self.connected_lines = []
        self.max_capacity = MAX_CAPACITY
        self.overflow_timer = 0
        self.overflow_max = OVERFLOW_MAX
        self.is_overflowing = False
        self.visual_scale = 1.0
        
    def add_passenger(self, passenger):
        self.passengers.append(passenger)
        
    def update(self, dt, is_hovered=False):
        if len(self.passengers) >= self.max_capacity:
            self.is_overflowing = True
            self.overflow_timer += dt
        else:
            self.is_overflowing = False
            self.overflow_timer = max(0, self.overflow_timer - dt * 2)
            
        target_scale = 1.25 if is_hovered else 1.0
        self.visual_scale += (target_scale - self.visual_scale) * 15 * dt
