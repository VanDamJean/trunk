import math
from pygame.math import Vector2

def get_distance(p1: Vector2, p2: Vector2) -> float:
    return p1.distance_to(p2)

def point_in_circle(point: Vector2, circle_center: Vector2, radius: float) -> bool:
    return get_distance(point, circle_center) <= radius
