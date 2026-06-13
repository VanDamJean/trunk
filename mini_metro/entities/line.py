class Line:
    def __init__(self, color):
        self.color = color
        self.stations = []
        self.trains = []
        self.is_loop = False
        
    def add_station(self, station):
        if station not in self.stations:
            self.stations.append(station)
            station.connected_lines.append(self)
