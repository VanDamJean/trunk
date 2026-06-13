import random
from pygame.math import Vector2
from utils.constants import ShapeType, WIDTH, HEIGHT
from entities.station import Station
from entities.passenger import Passenger

class Spawner:
    def __init__(self, game):
        self.game = game
        self.station_spawn_timer = 5.0
        self.passenger_spawn_timer = 3.0
        
    def update(self, dt):
        self.station_spawn_timer -= dt
        if self.station_spawn_timer <= 0:
            self.spawn_station()
            self.station_spawn_timer = random.uniform(10.0, 20.0)
            
        self.passenger_spawn_timer -= dt
        if self.passenger_spawn_timer <= 0:
            self.spawn_passenger()
            self.passenger_spawn_timer = random.uniform(2.0, 5.0)
            
    def spawn_station(self):
        shape = random.choice([ShapeType.CIRCLE, ShapeType.TRIANGLE, ShapeType.SQUARE])
        x = random.randint(100, WIDTH - 100)
        y = random.randint(100, HEIGHT - 100)
        pos = Vector2(x, y)
        
        valid = True
        for station in self.game.stations:
            if station.position.distance_to(pos) < 80:
                valid = False
                break
                
        if valid:
            self.game.stations.append(Station(shape, pos))
            
    def spawn_passenger(self):
        if not self.game.stations:
            return
            
        station = random.choice(self.game.stations)
        
        available_shapes = [ShapeType.CIRCLE, ShapeType.TRIANGLE, ShapeType.SQUARE]
        if station.shape in available_shapes:
            available_shapes.remove(station.shape)
            
        dest_shape = random.choice(available_shapes)
        station.add_passenger(Passenger(dest_shape))
