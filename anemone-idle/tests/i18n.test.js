import { describe, expect, it } from 'vitest';
import { getTranslationCoverage, missionText, producerText, resourceLabel, t, upgradeText } from '../src/i18n.js';
import { MISSION_DEFINITIONS, PRODUCERS, RESOURCE_KEYS, UPGRADE_DEFINITIONS } from '../src/config.js';

describe('i18n', () => {
  it('covers every required key in English and Korean', () => {
    expect(getTranslationCoverage()).toEqual({ en: [], ko: [] });
  });

  it('translates game entities for both locales', () => {
    for (const locale of ['en', 'ko']) {
      expect(t(locale, 'appTitle')).not.toBe('appTitle');
      RESOURCE_KEYS.forEach((resource) => expect(resourceLabel(locale, resource)).toBeTruthy());
      PRODUCERS.forEach((producer) => expect(producerText(locale, producer).name).toBeTruthy());
      UPGRADE_DEFINITIONS.forEach((upgrade) => expect(upgradeText(locale, upgrade).description).toBeTruthy());
      MISSION_DEFINITIONS.forEach((mission) => expect(missionText(locale, mission).title).toBeTruthy());
    }
  });

  it('includes Korean post-feed company-work feedback copy', () => {
    expect(t('ko', 'feedFeedbackTitle')).toBe('말미잘이 꿈틀밥을 먹고 출근했습니다!');
    expect(t('ko', 'feedFeedbackFood')).toContain('꿈틀밥');
    expect(t('ko', 'feedFeedbackLifetime')).toContain('누적');
    expect(t('ko', 'feedFeedbackRate')).toContain('현재');
  });
});
