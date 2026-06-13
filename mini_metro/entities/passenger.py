from utils.constants import ShapeType
import time

class PassengerState:
    WAITING = 0
    ON_TRAIN = 1
    ARRIVED = 2

class Passenger:
    def __init__(self, destination_shape: ShapeType):
        self.destination_shape = destination_shape
        self.state = PassengerState.WAITING
        self.spawn_time = time.time()
