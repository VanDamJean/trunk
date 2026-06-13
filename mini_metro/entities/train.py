from pygame.math import Vector2
from utils.constants import ShapeType, TRAIN_SPEED

class TrainState:
    MOVING = 0
    BOARDING = 1

class Train:
    def __init__(self, line, start_station_index=0):
        self.line = line
        self.segment_index = start_station_index
        self.segment_progress = 0.0
        self.direction = 1
        self.speed = TRAIN_SPEED
        self.passengers = []
        self.capacity = 6
        self.state = TrainState.MOVING
        self.dwell_timer = 0
        self.position = Vector2(line.stations[start_station_index].position) if line.stations else Vector2()
        self.on_passenger_delivered = None
        
    def update(self, dt):
        if len(self.line.stations) < 2:
            return
            
        # segment_index 범위 안전 체크
        if self.segment_index >= len(self.line.stations):
            self.segment_index = len(self.line.stations) - 1
        if self.segment_index < 0:
            self.segment_index = 0
            
        if self.state == TrainState.BOARDING:
            self.dwell_timer -= dt
            if self.dwell_timer <= 0:
                self.depart()
        elif self.state == TrainState.MOVING:
            current_station = self.line.stations[self.segment_index]
            next_index = self.segment_index + self.direction
            
            if next_index < 0 or next_index >= len(self.line.stations):
                self.direction *= -1
                next_index = self.segment_index + self.direction
                
            if next_index < 0 or next_index >= len(self.line.stations):
                return
                
            next_station = self.line.stations[next_index]
            
            dist = current_station.position.distance_to(next_station.position)
            if dist == 0:
                self.arrive_at_station(next_station, next_index)
                return
                
            move_amount = self.speed * dt
            progress_delta = move_amount / dist
            
            self.segment_progress += progress_delta
            
            if self.segment_progress >= 1.0:
                self.arrive_at_station(next_station, next_index)
            else:
                self.position = current_station.position.lerp(next_station.position, self.segment_progress)
                
    def arrive_at_station(self, station, index):
        self.segment_index = index
        self.segment_progress = 0.0
        self.position = Vector2(station.position)
        
        passengers_exchanged = 0
        
        # 하차: 이 역의 도형 = 승객 목적지
        to_remove = []
        for p in self.passengers:
            if p.destination_shape == station.shape:
                to_remove.append(p)
                passengers_exchanged += 1
                
        for p in to_remove:
            self.passengers.remove(p)
            if self.on_passenger_delivered:
                self.on_passenger_delivered()
                
        # 승차: 빈 자리 있으면 태우기
        to_load = []
        for p in station.passengers:
            if len(self.passengers) + len(to_load) < self.capacity:
                if p.destination_shape != station.shape:
                    to_load.append(p)
                    passengers_exchanged += 1
                    
        for p in to_load:
            station.passengers.remove(p)
            self.passengers.append(p)
            
        if passengers_exchanged > 0:
            self.state = TrainState.BOARDING
            self.dwell_timer = 0.3 + 0.08 * passengers_exchanged
        else:
            self.depart()
            
    def depart(self):
        self.state = TrainState.MOVING
        next_index = self.segment_index + self.direction
        if next_index < 0 or next_index >= len(self.line.stations):
            self.direction *= -1
