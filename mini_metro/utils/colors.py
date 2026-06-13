class Colors:
    # 원작 Mini Metro 스타일 색감
    BACKGROUND = (250, 248, 240)   # 크림/베이지 톤
    BLACK = (45, 45, 45)           # 순수 검정 대신 약간 부드러운 다크
    WHITE = (255, 255, 255)
    STATION_FILL = (250, 248, 240) # 배경과 동일 (역 내부)
    STATION_STROKE = (45, 45, 45)  # 역 테두리
    PASSENGER = (45, 45, 45)       # 승객 (채워진 도형)

    # 노선 색상 (원작에 가까운 채도 높은 색)
    LINE_RED = (228, 68, 68)
    LINE_BLUE = (60, 130, 200)
    LINE_GREEN = (80, 180, 80)
    LINE_YELLOW = (230, 190, 50)
    LINE_PURPLE = (160, 90, 200)
    LINE_ORANGE = (230, 140, 50)
    LINE_CYAN = (50, 190, 180)

    # UI
    TEXT = (45, 45, 45)
    TEXT_LIGHT = (140, 140, 140)
    UI_BG = (235, 233, 225)
    WARNING = (228, 68, 68)
    HOVER_HIGHLIGHT = (200, 200, 190)  # 역 hover 시 배경 원

    # 노선 색상 리스트 (순서대로)
    LINE_COLORS = [LINE_RED, LINE_BLUE, LINE_GREEN, LINE_YELLOW, LINE_PURPLE, LINE_ORANGE, LINE_CYAN]
