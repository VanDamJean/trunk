import { fireEvent, render, screen, within } from '@testing-library/react';
import { afterEach, describe, expect, it } from 'vitest';
import { App } from '../App';
import type { Dice } from '../game/categories';

describe('App', () => {
  afterEach(() => {
    delete window.__YACOO_TEST_ROLLS__;
  });

  it('renders the Yachoo title', () => {
    render(<App />);

    expect(screen.getByRole('heading', { name: '야추' })).toBeInTheDocument();
    expect(document.querySelector('.score-panel')).not.toBeNull();
    expect(document.querySelector('.dice-panel')).not.toBeNull();
    expect(screen.getByLabelText('남은 굴림 3회')).toHaveTextContent('3 left');
  });

  it('renders five accessible cube dice with pips after a roll', () => {
    const deterministicRolls: Dice[] = [[1, 2, 3, 4, 5]];
    window.__YACOO_TEST_ROLLS__ = deterministicRolls;

    render(<App />);
    fireEvent.click(screen.getByRole('button', { name: '굴리기' }));

    const diceGroup = screen.getByLabelText('주사위 고정 선택');
    const diceButtons = within(diceGroup).getAllByRole('button', { name: /\d번 주사위 [1-6]/ });

    expect(diceButtons).toHaveLength(5);
    diceButtons.forEach((button) => {
      expect(button).toHaveAttribute('data-held', 'false');
      expect(button).toHaveAttribute('data-value', expect.stringMatching(/^[1-6]$/));
      expect(button.querySelector('.die-cube')).not.toBeNull();
      expect(button.querySelector('.die-pip')).not.toBeNull();
    });
  });

  it('keeps held dice toggle accessible', () => {
    const deterministicRolls: Dice[] = [[1, 2, 3, 4, 5]];
    window.__YACOO_TEST_ROLLS__ = deterministicRolls;

    render(<App />);
    fireEvent.click(screen.getByRole('button', { name: '굴리기' }));

    const firstDie = screen.getByRole('button', { name: '1번 주사위 1' });
    fireEvent.click(firstDie);

    expect(firstDie).toHaveAttribute('aria-pressed', 'true');
    expect(firstDie).toHaveAttribute('data-held', 'true');
  });
});
