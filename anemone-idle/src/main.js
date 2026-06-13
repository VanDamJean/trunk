import './styles.css';
import { PRODUCER_MAP, PRODUCERS, RESOURCE_ICONS, UPGRADE_DEFINITIONS } from './config.js';
import { canBuyProducer, getProducerCost, getProducerRate, getTotalRates, getUpgradeCost, getUpgradeLevel } from './economy.js';
import {
  advanceGame,
  buyProducerInState,
  claimCapsule,
  claimQuestInState,
  formatResourceAmount,
  getCapsuleDurationMs,
  getPulsePlanktonGain,
  isCapsuleReady,
  pulseReefInState,
  purchaseUpgradeInState,
  setLocaleInState,
  startCapsule
} from './gameState.js';
import { chapterText, formatReward, producerText, progressionQuestText, resourceLabel, t, upgradeText } from './i18n.js';
import { getChapterDashboard, getVisibleProgressionQuests } from './progression/legacyMissionAdapter.js';
import { loadGame, saveGame } from './storage.js';

const app = document.querySelector('#app');
const loaded = loadGame(window.localStorage, Date.now());
const PRODUCER_HOLD_INITIAL_DELAY_MS = 360;
const PRODUCER_HOLD_REPEAT_MS = 120;
const PRODUCER_CLICK_SUPPRESSION_MS = 700;
let state = loaded.state;
let activeTab = 'reef';
let lastFrame = Date.now();
let feedFeedback = null;
let feedFeedbackTimer = null;
let producerHold = null;
let producerClickSuppression = null;

app.innerHTML = `
  <main class="shell" aria-live="polite">
    <header class="resource-header" data-resources></header>
    <section class="hero-panel">
      <div class="reef-orb" aria-hidden="true">
        <span class="orb-core"><i></i><i></i><b>3</b></span>
        <span class="tentacle tentacle-a"></span>
        <span class="tentacle tentacle-b"></span>
        <span class="tentacle tentacle-c"></span>
      </div>
      <div>
        <p class="eyebrow" data-hero-eyebrow></p>
        <h1 data-hero-title></h1>
        <p class="hero-copy" data-hero-copy></p>
      </div>
    </section>
    <section class="mission-strip" data-missions></section>
    <section class="notice" data-notice></section>
    <div class="fx-layer" data-fx-layer aria-hidden="true"></div>
    <section class="tab-panel" data-panel></section>
    <nav class="bottom-nav" aria-label="Game tabs">
      <button class="nav-button" data-tab="reef" type="button">🪸<span data-nav-label="reef"></span></button>
      <button class="nav-button" data-tab="capsule" type="button">🫧<span data-nav-label="capsule"></span></button>
      <button class="nav-button" data-tab="upgrades" type="button">⚪<span data-nav-label="upgrades"></span></button>
      <button class="nav-button" data-tab="settings" type="button">⚙<span data-nav-label="settings"></span></button>
    </nav>
  </main>
`;

const resourceHeader = app.querySelector('[data-resources]');
const shell = app.querySelector('.shell');
const heroPanel = app.querySelector('.hero-panel');
const missionStrip = app.querySelector('[data-missions]');
const notice = app.querySelector('[data-notice]');
const fxLayer = app.querySelector('[data-fx-layer]');
const panel = app.querySelector('[data-panel]');
const navButtons = [...app.querySelectorAll('[data-tab]')];

app.addEventListener('pointerdown', (event) => {
  const button = event.target.closest('button[data-buy-producer]');
  if (!button || event.button !== 0 || event.isPrimary === false) return;

  event.preventDefault();
  startProducerHold(button.dataset.buyProducer, button, event);
});

app.addEventListener('pointermove', (event) => {
  if (!producerHold || producerHold.pointerId !== event.pointerId) return;

  producerHold.clientX = event.clientX;
  producerHold.clientY = event.clientY;
});

app.addEventListener('pointerleave', (event) => {
  if (!producerHold || producerHold.pointerId !== event.pointerId) return;

  stopProducerHold();
});

window.addEventListener('pointerup', (event) => {
  if (!producerHold || producerHold.pointerId !== event.pointerId) return;

  stopProducerHold();
});

window.addEventListener('pointercancel', (event) => {
  if (!producerHold || producerHold.pointerId !== event.pointerId) return;

  stopProducerHold();
});

window.addEventListener('blur', () => {
  stopProducerHold();
});

app.addEventListener('click', (event) => {
  const button = event.target.closest('button');
  if (!button) return;

  if (button.dataset.tab) {
    activeTab = button.dataset.tab;
    render();
    const activeButton = app.querySelector(`[data-tab="${activeTab}"]`);
    popElement(activeButton, 'fx-pop');
    return;
  }

  if (button.dataset.buyProducer) {
    if (shouldSuppressProducerClick(button.dataset.buyProducer, event)) return;

    buyProducerOnce(button.dataset.buyProducer, button);
    return;
  }

  if (button.dataset.claimQuest) {
    const questId = button.dataset.claimQuest;
    const locale = getLocale();
    const nextState = claimQuestInState(state, questId);
    const claimed = nextState.claimed;
    state = nextState;
    persist();
    render();
    const card = app.querySelector(`[data-quest-card="${questId}"]`);
    burst(t(locale, claimed ? 'noticeMissionClaimed' : 'noticeMissionBlocked'), card || button, claimed ? 'good' : 'bad', claimed ? 2 : 1);
    popElement(card, claimed ? 'fx-pop' : 'fx-wobble');
    return;
  }

  if (button.dataset.startCapsule) {
    const locale = getLocale();
    const nextState = startCapsule(state, Date.now());
    const started = nextState.started;
    state = nextState;
    persist();
    render();
    const capsulePanel = app.querySelector('[data-capsule-panel]');
    burst(t(locale, started ? 'noticeCapsuleStarted' : 'noticeCapsuleBusy'), capsulePanel || button, started ? 'good' : 'bad', 2);
    popElement(capsulePanel, started ? 'fx-rattle' : 'fx-wobble');
    return;
  }

  if (button.dataset.claimCapsule) {
    const locale = getLocale();
    const nextState = claimCapsule(state, Date.now());
    const claimed = nextState.claimed;
    state = nextState;
    persist();
    render();
    const capsulePanel = app.querySelector('[data-capsule-panel]');
    burst(t(locale, claimed ? 'noticeCapsuleClaimed' : 'noticeCapsuleWaiting'), capsulePanel || button, claimed ? 'good' : 'bad', claimed ? 4 : 1);
    popElement(capsulePanel, claimed ? 'fx-pop' : 'fx-wobble');
    popElement(resourceHeader, claimed ? 'fx-pop' : 'fx-wobble');
    return;
  }

  if (button.dataset.buyUpgrade) {
    const upgradeId = button.dataset.buyUpgrade;
    const locale = getLocale();
    const nextState = purchaseUpgradeInState(state, upgradeId);
    const purchased = nextState.purchased;
    state = nextState;
    persist();
    render();
    const card = app.querySelector(`[data-upgrade-card="${upgradeId}"]`);
    burst(t(locale, purchased ? 'noticeBuyUpgrade' : 'noticeUpgradeBlocked'), card || button, purchased ? 'good' : 'bad', purchased ? 3 : 1);
    popElement(card, purchased ? 'fx-pop' : 'fx-wobble');
    popElement(resourceHeader, purchased ? 'fx-pop' : 'fx-wobble');
    return;
  }

  if (button.dataset.pulseReef) {
    const locale = getLocale();
    const previousLifetime = state.economy.lifetimeEarned.plankton;
    const pulseAmount = getPulsePlanktonGain(state.economy);
    state = pulseReefInState(state);
    feedFeedback = createFeedFeedback(locale, previousLifetime, state, pulseAmount);
    window.clearTimeout(feedFeedbackTimer);
    feedFeedbackTimer = window.setTimeout(() => {
      feedFeedback = null;
      render();
    }, 4200);
    persist();
    render();
    const pulseButton = app.querySelector('[data-pulse-reef]');
    burst(t(locale, 'pulseButton', { amount: formatResourceAmount(pulseAmount), icon: RESOURCE_ICONS.plankton }), pulseButton || button, 'good', 3);
    popElement(shell, 'fx-wobble');
    popElement(heroPanel, 'fx-pop');
    popElement(resourceHeader, 'fx-pop');
    return;
  }

  if (button.dataset.setLocale) {
    const nextLocale = button.dataset.setLocale;
    state = setLocaleInState(state, button.dataset.setLocale);
    persist();
    render();
    const localeButton = app.querySelector(`[data-set-locale="${nextLocale}"]`);
    burst(t(nextLocale, 'noticeLocale'), localeButton || button, 'good', 1);
    popElement(localeButton, 'fx-pop');
  }
});

function loop() {
  const now = Date.now();
  if (now - lastFrame >= 1000) {
    state = advanceGame(state, now);
    lastFrame = now;
    render();
  }
  window.requestAnimationFrame(loop);
}

window.setInterval(() => {
  persist();
}, 10000);

window.addEventListener('beforeunload', () => {
  persist();
});

function persist() {
  state = saveGame(window.localStorage, state, Date.now());
}

function render() {
  renderChrome();
  renderResources();
  renderMissions();
  renderPanel();
  notice.textContent = t(getLocale(), state.notice, state.noticeArgs);
  navButtons.forEach((button) => {
    button.classList.toggle('is-active', button.dataset.tab === activeTab);
  });
}

function getLocale() {
  return state.settings.locale;
}

function startProducerHold(producerId, button, event) {
  stopProducerHold();
  markProducerClickSuppressed(producerId);

  const purchased = buyProducerOnce(producerId, button);
  if (!purchased) return;

  producerHold = {
    producerId,
    pointerId: event.pointerId,
    clientX: event.clientX,
    clientY: event.clientY,
    timer: window.setTimeout(repeatHeldProducerPurchase, PRODUCER_HOLD_INITIAL_DELAY_MS)
  };
}

function repeatHeldProducerPurchase() {
  if (!producerHold) return;

  const { producerId } = producerHold;
  if (!isPointerStillOnProducerButton(producerId)) {
    stopProducerHold();
    return;
  }

  const purchased = buyProducerOnce(producerId);
  if (!purchased) {
    stopProducerHold();
    return;
  }

  if (producerHold) {
    producerHold.timer = window.setTimeout(repeatHeldProducerPurchase, PRODUCER_HOLD_REPEAT_MS);
  }
}

function stopProducerHold() {
  if (!producerHold) return;

  window.clearTimeout(producerHold.timer);
  markProducerClickSuppressed(producerHold.producerId);
  producerHold = null;
}

function isPointerStillOnProducerButton(producerId) {
  if (!producerHold) return false;

  const element = document.elementFromPoint(producerHold.clientX, producerHold.clientY);
  return Boolean(element?.closest?.(`button[data-buy-producer="${producerId}"]`));
}

function markProducerClickSuppressed(producerId) {
  producerClickSuppression = {
    producerId,
    until: Date.now() + PRODUCER_CLICK_SUPPRESSION_MS
  };
}

function shouldSuppressProducerClick(producerId, event) {
  const pointerType = event.pointerType || '';
  const isPointerClick = event.detail > 0 || ['mouse', 'pen', 'touch'].includes(pointerType);
  if (!isPointerClick || producerClickSuppression?.producerId !== producerId) return false;

  const shouldSuppress = Date.now() <= producerClickSuppression.until;
  producerClickSuppression = null;
  return shouldSuppress;
}

function buyProducerOnce(producerId, sourceElement) {
  const locale = getLocale();
  const producer = PRODUCERS.find((item) => item.id === producerId);
  const nextState = buyProducerInState(state, producerId);
  const purchased = nextState.purchased;
  state = nextState;
  persist();
  render();
  const card = app.querySelector(`[data-producer-card="${producerId}"]`);
  const label = purchased && producer
    ? `${producer.icon} +1 ${producerText(locale, producer).name}`
    : t(locale, 'noticeNeedResources');
  burst(label, card || sourceElement, purchased ? 'good' : 'bad', purchased ? 2 : 1);
  popElement(card, purchased ? 'fx-pop' : 'fx-wobble');
  popElement(resourceHeader, purchased ? 'fx-pop' : 'fx-wobble');
  return purchased;
}

function renderChrome() {
  const locale = getLocale();
  document.documentElement.lang = locale;
  document.title = t(locale, 'appTitle');
  app.querySelector('[data-hero-eyebrow]').textContent = t(locale, 'heroEyebrow');
  app.querySelector('[data-hero-title]').textContent = t(locale, 'heroTitle');
  app.querySelector('[data-hero-copy]').textContent = t(locale, 'heroCopy');
  app.querySelector('[data-nav-label="reef"]').textContent = t(locale, 'navReef');
  app.querySelector('[data-nav-label="capsule"]').textContent = t(locale, 'navCapsule');
  app.querySelector('[data-nav-label="upgrades"]').textContent = t(locale, 'navUpgrades');
  app.querySelector('[data-nav-label="settings"]').textContent = t(locale, 'navSettings');
}

function renderResources() {
  const locale = getLocale();
  const rates = getTotalRates(state.economy);
  resourceHeader.innerHTML = Object.keys(rates).map((resource) => `
    <article class="resource-pill">
      <span class="resource-icon">${RESOURCE_ICONS[resource]}</span>
      <span>
        <strong>${formatResourceAmount(state.economy.resources[resource])}</strong>
        <small>${resourceLabel(locale, resource)} · ${t(locale, 'perSecond', { amount: formatResourceAmount(rates[resource]) })}</small>
      </span>
    </article>
  `).join('');
}

function renderMissions() {
  const locale = getLocale();
  const quests = getVisibleProgressionQuests(state, 3);
  if (quests.length === 0) {
    missionStrip.innerHTML = `
      <article class="mission-card is-complete">
        <div>
          <p class="eyebrow">${t(locale, 'questEyebrow')}</p>
          <h2>${t(locale, 'endOfContentTitle')}</h2>
          <p>${t(locale, 'endOfContentCopy')}</p>
        </div>
      </article>
    `;
    return;
  }

  missionStrip.innerHTML = quests.map((quest) => {
    const text = progressionQuestText(locale, quest);
    const percent = Math.min(100, (quest.progress / quest.target) * 100);
    const progressText = `${t(locale, quest.progressLabelKey)} · ${Math.floor(quest.progress)} / ${quest.target}`;
    return `
      <article class="mission-card ${quest.complete ? 'is-complete' : ''}" data-quest-card="${quest.id}">
        <div>
          <p class="eyebrow">${t(locale, 'questEyebrow')}</p>
          <h2>${text.title}</h2>
          <p>${text.description}</p>
          <small>${quest.complete ? t(locale, 'questStatusCompleted') : t(locale, 'questStatusActive')}</small>
        </div>
        <div class="progress-track" aria-label="${t(locale, 'progressLabel', { title: text.title })}">
          <span style="--progress:${percent}%"></span>
        </div>
        <button type="button" data-claim-quest="${quest.id}" ${quest.complete ? '' : 'disabled'}>
          ${quest.complete ? t(locale, 'claimReward', { reward: formatReward(locale, quest.reward) }) : progressText}
        </button>
      </article>
    `;
  }).join('');
}

function renderPanel() {
  if (activeTab === 'capsule') {
    renderCapsulePanel();
    return;
  }
  if (activeTab === 'upgrades') {
    renderUpgradePanel();
    return;
  }
  if (activeTab === 'settings') {
    renderSettingsPanel();
    return;
  }
  renderReefPanel();
}

function renderReefPanel() {
  const locale = getLocale();
  const pulseAmount = getPulsePlanktonGain(state.economy);
  panel.innerHTML = `
    <div class="section-heading">
      <p class="eyebrow">${t(locale, 'reefEyebrow')}</p>
      <h2>${t(locale, 'reefTitle')}</h2>
    </div>
    <section class="pulse-panel">
      <div class="pulse-mascot" aria-hidden="true">
        <span class="mascot-face"><i></i><i></i><b>O</b></span>
      </div>
      <div>
        <p class="eyebrow">${t(locale, 'pulseEyebrow')}</p>
        <h2>${t(locale, 'pulseTitle')}</h2>
        <p>${t(locale, 'pulseCopy')}</p>
      </div>
      <button class="pulse-button" type="button" data-pulse-reef="true">
        ${t(locale, 'pulseButton', { amount: formatResourceAmount(pulseAmount), icon: RESOURCE_ICONS.plankton })}
      </button>
    </section>
    ${renderChapterDashboard(locale)}
    ${renderFeedFeedback(locale)}
    <div class="card-stack">
      ${PRODUCERS.map((producer) => {
        const text = producerText(locale, producer);
        const cost = getProducerCost(state.economy, producer.id);
        const affordable = canBuyProducer(state.economy, producer.id);
        const owned = state.economy.producers[producer.id];
        const rate = getProducerRate(state.economy, producer.id);
        const outputLabel = formatProducerOutput(locale, producer);
        return `
          <article class="producer-card ${affordable ? 'is-affordable' : 'is-locked'}" data-producer-card="${producer.id}">
            <div class="producer-icon">${producer.icon}</div>
            <div class="producer-copy">
              <div class="card-title-row">
                <h3>${text.name}</h3>
                <strong>${owned}</strong>
              </div>
              <p>${text.flavor}</p>
              <small>${t(locale, 'perSecond', { amount: formatResourceAmount(rate) })} ${outputLabel}</small>
            </div>
            <button type="button" data-buy-producer="${producer.id}" data-affordable="${affordable ? 'true' : 'false'}">
              ${t(locale, 'buyCost', { cost: formatResourceAmount(cost), icon: RESOURCE_ICONS[producer.costResource] })}
            </button>
          </article>
        `;
      }).join('')}
    </div>
  `;
}

function renderChapterDashboard(locale) {
  const dashboard = getChapterDashboard(state);
  const chapter = chapterText(locale, { id: dashboard.chapterId });
  const nextText = dashboard.nextQuest ? progressionQuestText(locale, dashboard.nextQuest) : null;
  const spawnRows = dashboard.spawnProgress.map((entry) => {
    const producer = PRODUCER_MAP[entry.producerId];
    const name = producer ? producerText(locale, producer).name : entry.producerId;
    return `<small>${t(locale, 'spawnProgressLabel', { name, percent: entry.progress })}</small>`;
  }).join('');
  return `
    <section class="chapter-dashboard">
      <div>
        <p class="eyebrow">${t(locale, 'chapterDashboardEyebrow')}</p>
        <h2>${chapter.title}</h2>
        <p>${chapter.description}</p>
      </div>
      <div class="progress-track large" aria-label="${chapter.title}">
        <span style="--progress:${dashboard.progressPercent}%"></span>
      </div>
      <dl class="chapter-stats">
        <div><dt>${t(locale, 'chapterProgress', { percent: dashboard.progressPercent })}</dt><dd>${dashboard.progressPercent}%</dd></div>
        <div><dt>${t(locale, 'nextUnlockLabel')}</dt><dd>${nextText ? nextText.nextUnlock : t(locale, 'nextUnlockComingSoon')}</dd></div>
        <div><dt>${t(locale, 'bottleneckLabel')}</dt><dd>${resourceLabel(locale, dashboard.bottleneckResource)}</dd></div>
        <div><dt>${t(locale, 'productionLabel')}</dt><dd>${formatRateSummary(locale, dashboard.rates)}</dd></div>
      </dl>
      ${spawnRows ? `<div class="spawn-progress-list">${spawnRows}</div>` : ''}
    </section>
  `;
}

function createFeedFeedback(locale, previousLifetime, nextState, pulseAmount) {
  const lifetimeDelta = nextState.economy.lifetimeEarned.plankton - previousLifetime;
  return {
    id: Date.now(),
    locale,
    amount: pulseAmount,
    lifetimeDelta,
    rates: getTotalRates(nextState.economy)
  };
}

function renderFeedFeedback(locale) {
  if (!feedFeedback) return '';
  const feedbackLocale = feedFeedback.locale || locale;
  return `
    <aside class="feed-feedback-card" role="status" aria-live="polite" data-feed-feedback="${feedFeedback.id}">
      <div class="office-motion" aria-hidden="true">
        <span class="office-desk"></span>
        <span class="office-anemone"><i></i><i></i><b>O</b></span>
        <span class="office-paper paper-a"></span>
        <span class="office-paper paper-b"></span>
        <span class="office-stamp"></span>
      </div>
      <div class="feed-feedback-copy">
        <p class="eyebrow">${t(feedbackLocale, 'feedFeedbackEyebrow')}</p>
        <h2>${t(feedbackLocale, 'feedFeedbackTitle')}</h2>
        <dl class="feed-feedback-stats">
          <div>
            <dt>${t(feedbackLocale, 'feedFeedbackFood')}</dt>
            <dd>+${formatResourceAmount(feedFeedback.amount)} ${RESOURCE_ICONS.plankton} ${resourceLabel(feedbackLocale, 'plankton')}</dd>
          </div>
          <div>
            <dt>${t(feedbackLocale, 'feedFeedbackLifetime')}</dt>
            <dd>+${formatResourceAmount(feedFeedback.lifetimeDelta)} ${resourceLabel(feedbackLocale, 'plankton')}</dd>
          </div>
          <div>
            <dt>${t(feedbackLocale, 'feedFeedbackRate')}</dt>
            <dd>${formatRateSummary(feedbackLocale, feedFeedback.rates)}</dd>
          </div>
        </dl>
      </div>
    </aside>
  `;
}

function formatRateSummary(locale, rates) {
  const activeRates = Object.entries(rates).filter(([, amount]) => amount > 0);
  const visibleRates = activeRates.length > 0 ? activeRates : [['plankton', rates.plankton || 0]];
  return visibleRates
    .map(([resource, amount]) => `${resourceLabel(locale, resource)} ${t(locale, 'perSecond', { amount: formatResourceAmount(amount) })}`)
    .join(' · ');
}

function renderCapsulePanel() {
  const locale = getLocale();
  const now = Date.now();
  const ready = isCapsuleReady(state, now);
  const remainingMs = state.capsule ? Math.max(0, state.capsule.readyAt - now) : 0;
  const durationMs = state.capsule?.durationMs || getCapsuleDurationMs(state);
  const progress = state.capsule ? Math.min(100, ((durationMs - remainingMs) / durationMs) * 100) : 0;
  panel.innerHTML = `
    <section class="capsule-panel" data-capsule-panel>
      <div class="capsule-visual ${ready ? 'is-ready' : ''}">◌</div>
      <p class="eyebrow">${t(locale, 'capsuleEyebrow')}</p>
      <h2>${state.capsule ? (ready ? t(locale, 'capsuleReadyTitle') : t(locale, 'capsuleRipeningTitle')) : t(locale, 'capsuleIdleTitle')}</h2>
      <p>${t(locale, 'capsuleCopy')}</p>
      <div class="progress-track large"><span style="--progress:${progress}%"></span></div>
      <button type="button" ${state.capsule ? (ready ? 'data-claim-capsule="true"' : 'disabled') : 'data-start-capsule="true"'}>
        ${state.capsule ? (ready ? t(locale, 'openCapsule') : t(locale, 'readyIn', { time: formatTime(remainingMs) })) : t(locale, 'startCapsule', { time: formatTime(durationMs) })}
      </button>
    </section>
  `;
}

function renderUpgradePanel() {
  const locale = getLocale();
  panel.innerHTML = `
    <div class="section-heading">
      <p class="eyebrow">${t(locale, 'upgradesEyebrow')}</p>
      <h2>${t(locale, 'upgradesTitle')}</h2>
    </div>
    <div class="card-stack">
      ${UPGRADE_DEFINITIONS.map((upgrade) => {
        const text = upgradeText(locale, upgrade);
        const level = getUpgradeLevel(state.economy, upgrade.id);
        const owned = level > 0;
        const cost = formatCost(getUpgradeCost(state.economy, upgrade.id));
        return `
          <article class="upgrade-card ${owned ? 'is-owned' : ''}" data-upgrade-card="${upgrade.id}">
            <div>
              <h3>${text.name}</h3>
              <p>${text.description}</p>
              <small>${owned ? `${t(locale, 'ownedUpgrade')} · Lv.${level} · ${cost}` : cost}</small>
            </div>
            <button type="button" data-buy-upgrade="${upgrade.id}">
              ${owned ? `Lv.${level + 1}` : t(locale, 'buy')}
            </button>
          </article>
        `;
      }).join('')}
    </div>
  `;
}

function renderSettingsPanel() {
  const locale = getLocale();
  panel.innerHTML = `
    <section class="settings-panel">
      <p class="eyebrow">${t(locale, 'settingsEyebrow')}</p>
      <h2>${t(locale, 'settingsTitle')}</h2>
      <p>${t(locale, 'settingsCopy')}</p>
      <div class="locale-grid">
        ${renderLocaleButton('en', t(locale, 'english'))}
        ${renderLocaleButton('ko', t(locale, 'korean'))}
      </div>
      <article class="idle-explainer">
        <h3>${t(locale, 'idleExplainerTitle')}</h3>
        <p>${t(locale, 'idleExplainerCopy')}</p>
      </article>
    </section>
  `;
}

function renderLocaleButton(locale, label) {
  const active = getLocale() === locale;
  return `
    <button class="locale-button ${active ? 'is-active' : ''}" type="button" data-set-locale="${locale}">
      <strong>${label}</strong>
      <small>${active ? t(getLocale(), 'activeLanguage') : ''}</small>
    </button>
  `;
}

function formatProducerOutput(locale, producer) {
  if (producer.producesProducer) {
    return producerText(locale, PRODUCER_MAP[producer.producesProducer]).name;
  }
  return resourceLabel(locale, producer.producesResource || producer.produces);
}

function formatCost(cost) {
  const locale = getLocale();
  return Object.entries(cost)
    .map(([resource, amount]) => `${formatResourceAmount(amount)} ${RESOURCE_ICONS[resource]} ${resourceLabel(locale, resource)}`)
    .join(' + ');
}

function formatTime(ms) {
  const seconds = Math.ceil(ms / 1000);
  const minutes = Math.floor(seconds / 60);
  const remainder = seconds % 60;
  return `${minutes}:${String(remainder).padStart(2, '0')}`;
}

function burst(text, target, tone = 'good', count = 1) {
  for (let index = 0; index < count; index += 1) {
    window.setTimeout(() => floatText(text, target, tone), index * 70);
  }
}

function floatText(text, target, tone) {
  const rect = target?.getBoundingClientRect?.() || shell.getBoundingClientRect();
  const layerRect = fxLayer.getBoundingClientRect();
  const item = document.createElement('span');
  item.className = `floating-text is-${tone}`;
  item.textContent = text;
  item.style.setProperty('--fx-x', `${rect.left - layerRect.left + rect.width / 2 + randomBetween(-26, 26)}px`);
  item.style.setProperty('--fx-y', `${rect.top - layerRect.top + rect.height / 2 + randomBetween(-10, 12)}px`);
  item.style.setProperty('--fx-tilt', `${randomBetween(-9, 9)}deg`);
  fxLayer.append(item);
  window.setTimeout(() => item.remove(), 980);
}

function popElement(element, className) {
  if (!element) return;
  element.classList.remove(className);
  void element.offsetWidth;
  element.classList.add(className);
  window.setTimeout(() => element.classList.remove(className), 620);
}

function randomBetween(min, max) {
  return Math.round(min + Math.random() * (max - min));
}

if (loaded.offlineSeconds > 0) {
  state.notice = loaded.capped
    ? 'noticeOfflineCapped'
    : 'noticeOffline';
  state.noticeArgs = loaded.capped ? {} : { minutes: Math.floor(loaded.offlineSeconds / 60) };
}

render();
loop();
