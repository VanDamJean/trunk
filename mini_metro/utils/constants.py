from enum import Enum

class ShapeType(Enum):
    CIRCLE = 0
    TRIANGLE = 1
    SQUARE = 2
    STAR = 3
    CROSS = 4

FPS = 60
WIDTH = 1280
HEIGHT = 720

# 역/승객 크기를 원작 비율로 키움
STATION_RADIUS = 22
PASSENGER_SIZE = 8

MAX_CAPACITY = 6
OVERFLOW_MAX = 45.0

# 열차
TRAIN_WIDTH = 28
TRAIN_HEIGHT = 14
TRAIN_SPEED = 120.0

# 노선
LINE_THICKNESS = 10
LINE_OFFSET = 8  # 겹치는 구간 offset
