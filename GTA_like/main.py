"""
GTA-Like 게임 엔트리포인트
━━━━━━━━━━━━━━━━━━━━━━━━━━━━
MVC 구조의 메인 루프.
Model(world) ←→ Controller(input) → View(renderer)
"""
import sys
import random
import pygame
import subprocess
from game.config import FPS
from game.systems.world import GameWorld
from game.views.renderer import PygameRenderer
from game.controllers.input_handler import PygameInputHandler


def main():
    world = GameWorld()
    renderer = PygameRenderer()
    input_handler = PygameInputHandler()

    afplay_process = None
    # BGM 랜덤 재생 로직
    try:
        bgm_list = ['assets/sounds/bgm_01.mp3', 'assets/sounds/bgm_02.mp3', 'assets/sounds/bgm_03.mp3']
        selected_bgm = random.choice(bgm_list)
        
        try:
            pygame.mixer.music.load(selected_bgm)
            pygame.mixer.music.set_volume(0.3)
            pygame.mixer.music.play(-1) # 무한 반복
            print(f"🎵 현재 재생중인 BGM (Pygame): {selected_bgm}")
        except Exception:
            # Mac 등에서 pygame.mixer 오류 시 시스템 기본 플레이어로 재생 (백그라운드)
            import subprocess
            afplay_process = subprocess.Popen(['afplay', '-v', '0.5', selected_bgm])
            print(f"🎵 현재 재생중인 BGM (afplay): {selected_bgm}")

    except Exception as e:
        print(f"BGM 재생 실패 (무시됨): {e}")

    while not input_handler.quit_requested:
        inputs = input_handler.process()

        if input_handler.action_enter_vehicle:
            world.try_enter_exit_vehicle()

        # 레벨업 업그레이드 선택
        if input_handler.upgrade_choice is not None and world.upgrade_pending:
            world.choose_upgrade(input_handler.upgrade_choice)

        world.update(inputs)

        # 이벤트 및 SFX 처리
        for event_msg in world.events:
            print(event_msg)
            # 사운드 이펙트 재생 (macOS 내장 사운드 활용)
            if "💥 차량 폭발" in event_msg:
                subprocess.Popen(['afplay', '-v', '1.0', '/System/Library/Sounds/Basso.aiff'])
            elif "무기 획득" in event_msg:
                subprocess.Popen(['afplay', '-v', '1.0', '/System/Library/Sounds/Bottle.aiff'])
            elif "LEVEL UP" in event_msg:
                subprocess.Popen(['afplay', '-v', '1.0', '/System/Library/Sounds/Glass.aiff'])
            elif "KILL FRENZY START" in event_msg:
                subprocess.Popen(['afplay', '-v', '1.0', '/System/Library/Sounds/Hero.aiff'])
            elif "BOSS DEFEATED" in event_msg:
                subprocess.Popen(['afplay', '-v', '1.0', '/System/Library/Sounds/Purr.aiff'])
            elif "가라지: 수리" in event_msg:
                subprocess.Popen(['afplay', '-v', '1.0', '/System/Library/Sounds/Tink.aiff'])
        world.events.clear()

        # 총알 발사음 (world 상태 변화를 감지하기엔 복잡하므로 무기 획득 시만 우선)
        
        renderer.render(world, input_handler)
        renderer.tick(FPS)

    renderer.quit()
    if afplay_process:
        afplay_process.terminate()
    sys.exit()

if __name__ == "__main__":
    main()
