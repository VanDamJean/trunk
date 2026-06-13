import { CATEGORY_INFO, CATEGORY_ORDER, type CategoryId, type Dice } from '../game/categories';
import { rollAllDice, type DiceRoller } from '../game/dice';
import { rollDice, scoreTurn } from '../game/state';
import type { GameResult, GameState } from '../game/types';
import { chooseCategoryForBot, chooseHoldsForBot } from './basicBot';

export function playBotTurn(initialState: GameState, roller: DiceRoller = rollAllDice): GameResult {
  if (initialState.activePlayer !== 'bot') {
    return { state: initialState, ok: false, error: '봇 차례가 아닙니다.' };
  }

  let state = initialState;

  while (state.rollCount < 3 && state.phase !== 'gameOver') {
    const rolled = rollDice(state, roller);
    if (!rolled.ok) {
      return rolled;
    }

    state = rolled.state;

    if (!state.dice) {
      return { state, ok: false, error: '봇이 굴린 주사위를 찾을 수 없습니다.' };
    }

    if (state.rollCount < 3) {
      const held = chooseHoldsForBot({ dice: state.dice, rollCount: state.rollCount, usedCategories: usedBotCategories(state) });
      state = {
        ...state,
        held,
        log: [...state.log, describeBotHolds(state.dice, held)]
      };

      if (held.every(Boolean)) {
        break;
      }
    }
  }

  if (!state.dice) {
    return { state, ok: false, error: '봇 점수 기록에 필요한 주사위가 없습니다.' };
  }

  const category = chooseCategoryForBot({ dice: state.dice, usedCategories: usedBotCategories(state) });
  if (!category) {
    return { state, ok: false, error: '봇이 선택할 수 있는 점수 칸이 없습니다.' };
  }

  return scoreTurn(
    {
      ...state,
      log: [...state.log, `봇 ${CATEGORY_INFO[category].label} 선택`]
    },
    category
  );
}

function usedBotCategories(state: GameState): CategoryId[] {
  return CATEGORY_ORDER.filter((category) => state.players.bot.scorecard[category] !== undefined);
}

function describeBotHolds(dice: Dice, held: readonly boolean[]): string {
  const heldDice = dice.filter((_, index) => held[index]);

  if (heldDice.length === 0) {
    return '봇이 고정 없이 다시 굴립니다.';
  }

  return `봇이 ${heldDice.join(', ')} 고정`;
}
