import { MISSION_DEFINITIONS, PRODUCERS, RESOURCE_KEYS, UPGRADE_DEFINITIONS } from './config.js';
import { CHAPTER_DEFINITIONS, QUEST_DEFINITIONS } from './progression/content.js';

export const SUPPORTED_LOCALES = ['en', 'ko'];
export const DEFAULT_LOCALE = 'en';

const translations = {
  en: {
    appTitle: 'Anemone Idle: Bloop Office',
    heroEyebrow: 'Totally Legal Reef Startup',
    heroTitle: 'Anemone Idle: Bloop Office',
    heroCopy: 'Mash the boss blob, hire underpaid tentacles, and turn ocean nonsense into quarterly wiggle profit.',
    navReef: 'Grind Reef',
    navCapsule: 'Loot Clam',
    navUpgrades: 'Drama Pearls',
    navSettings: 'Language Goblin',
    missionEyebrow: 'Corporate Dare',
    questEyebrow: 'Chapter Objective',
    questStatusActive: 'In progress',
    questStatusCompleted: 'Ready to claim',
    endOfContentTitle: 'Chapter 1 Office Reef complete',
    endOfContentCopy: 'Next community expedition is still being argued about in a clam meeting.',
    chapterDashboardEyebrow: 'Chapter Desk',
    chapterProgress: '{percent}% complete',
    nextUnlockLabel: 'Next unlock',
    bottleneckLabel: 'Current bottleneck',
    productionLabel: 'Production',
    spawnProgressLabel: '{name} auto-approval {percent}%',
    progressProducer: 'Employees',
    progressResource: 'Earned',
    progressStat: 'Records',
    progressUpgrade: 'Upgrades',
    progressQuest: 'Prerequisite',
    progressGeneric: 'Progress',
    chapter1Title: 'Chapter 1: Office Reef Startup',
    chapter1Description: 'Turn a basement anemone company into a tiny self-feeding org chart.',
    questFirstInternsTitle: 'Staff the noodle pit',
    questFirstInternsDescription: 'Own 5 Intern Tentacles and prove the company has arms.',
    questFirstShrimpTitle: 'Invent management',
    questFirstShrimpDescription: 'Own 3 Shrimp Middle Managers so meetings can reproduce.',
    questFirstCapsuleTitle: 'Trust a weird clam',
    questFirstCapsuleDescription: 'Claim 1 loot clam reward without asking legal.',
    questFirstUpgradeTitle: 'Staple on strategy',
    questFirstUpgradeDescription: 'Buy 1 Drama Pearl upgrade and call it operational excellence.',
    questFirstCrabTitle: 'Open a crab branch',
    questFirstCrabDescription: 'Own 1 Crab Branch Boss so shrimp hiring becomes automatic.',
    questChapter1CompleteTitle: 'Survive the first office reef',
    questChapter1CompleteDescription: 'Own a crab, buy 2 upgrades, and open a clam to finish Chapter 1.',
    nextUnlockFirstShrimp: 'First Shrimp Middle Manager',
    nextUnlockFirstCapsule: 'Loot Clam and first upgrade route',
    nextUnlockFirstUpgrade: 'Drama Pearl upgrade',
    nextUnlockFirstCrab: 'Crab Branch Boss wall',
    nextUnlockChapterComplete: 'Chapter 1 completion checklist',
    nextUnlockComingSoon: 'Next community expedition: coming soon',
    progressLabel: '{title} progress',
    claimReward: 'Yoink {reward}',
    perSecond: '+{amount}/s',
    buyCost: 'Hire · {cost} {icon}',
    reefEyebrow: 'Profit Aquarium',
    reefTitle: 'Make the office wiggle',
    pulseEyebrow: 'Main Character Button',
    pulseTitle: 'SLAP THE ANEMONE CEO',
    pulseCopy: 'Every tap converts confidence, bubbles, and mild panic into Wiggle Food. Middle management keeps pretending to help.',
    pulseButton: 'BLOOP! +{amount} {icon}',
    feedFeedbackEyebrow: 'Clocked In',
    feedFeedbackTitle: 'The anemone ate Wiggle Food and went to work!',
    feedFeedbackFood: 'Food eaten',
    feedFeedbackLifetime: 'Lifetime record up',
    feedFeedbackRate: 'Current production',
    capsuleEyebrow: 'Suspicious Free Thing',
    capsuleReadyTitle: 'The loot clam is screaming',
    capsuleRipeningTitle: 'Loot clam is making soup noises',
    capsuleIdleTitle: 'Order one questionable loot clam',
    capsuleCopy: 'Capsules cough up resources after a dramatic wait. Nobody signed the safety waiver.',
    openCapsule: 'Crack the clam',
    readyIn: 'Still burping for {time}',
    startCapsule: 'Shake {time} loot clam',
    upgradesEyebrow: 'Drama Pearl Lab',
    upgradesTitle: 'Buy ridiculous advantages',
    owned: 'Installed',
    buy: 'Do it',
    ownedUpgrade: 'Already stapled to the business model',
    settingsEyebrow: 'Options Basement',
    settingsTitle: 'Choose your nonsense language',
    settingsCopy: 'Flip every label between English and Korean. The tiny reef goblin remembers your choice on this device.',
    english: 'English',
    korean: '한국어',
    activeLanguage: 'Currently yelling',
    idleExplainerTitle: 'How the scam earns',
    idleExplainerCopy: 'Idle income starts after you hire producers. The giant bloop button is still the fastest way to feel powerful and damp.',
    noticeInitial: 'A tiny anemone opens a basement company with zero permits.',
    noticeBuyProducer: 'New employee acquired. HR is a crab with a clipboard.',
    noticeNeedResources: 'Insufficient wet money. Wiggle harder.',
    noticeBuyUpgrade: 'Drama Pearl installed. Productivity now smells expensive.',
    noticeUpgradeBlocked: 'The upgrade committee threw soup at your proposal.',
    noticeMissionClaimed: 'Corporate dare completed. Please accept these damp prizes.',
    noticeMissionBlocked: 'Not enough nonsense yet. Continue embarrassing the ocean.',
    noticeCapsuleBusy: 'One suspicious clam is already rattling in the corner.',
    noticeCapsuleStarted: 'Loot clam ordered. It immediately started whispering.',
    noticeCapsuleWaiting: 'The clam says no. The clam is dramatic.',
    noticeCapsuleClaimed: 'BLOOP JACKPOT! The clam paid rent in resources.',
    noticePulse: 'CEO slap generated {amount} Wiggle Food and one tiny lawsuit.',
    noticeAdBuffClaimed: 'Reward ad approved. Executive soup makes production 300% louder for 30 seconds.',
    noticeAdBuffCooldown: 'Executive soup is cooling down. The shrimp lawyers said wait.',
    noticeLocale: 'Language goblin changed the subtitles.',
    noticeOffline: 'While you vanished for {minutes} minutes, the reef held a productivity karaoke night.',
    noticeOfflineCapped: 'The reef banked four hours of unattended nonsense and then needed a snack.',
    resourcePlankton: 'Wiggle Food',
    resourcePearls: 'Drama Pearls',
    resourceTideEnergy: 'Bloop Power',
    producerDriftPolypsName: 'Intern Tentacles',
    producerDriftPolypsFlavor: 'Tiny unpaid arms wave spreadsheets until snacks fall out of the sea.',
    producerCleanerShrimpName: 'Shrimp Middle Managers',
    producerCleanerShrimpFlavor: 'They point at bubbles, skim Wiggle Food, and automatically hire Intern Tentacles.',
    producerCrabBranchBossName: 'Crab Branch Bosses',
    producerCrabBranchBossFlavor: 'Regional claw executives stamp paperwork until Shrimp Middle Managers appear.',
    producerWhaleShareholderName: 'Whale Shareholders',
    producerWhaleShareholderFlavor: 'Boardroom whales inhale Drama Pearls and exhale Crab Branch Bosses.',
    producerShellNurseryName: 'Clam Influencer Farm',
    producerShellNurseryFlavor: 'Glossy clams post thirst traps until Drama Pearls appear from pure cringe.',
    producerMoonCurrentName: 'Suspicious Moon Hose',
    producerMoonCurrentFlavor: 'A hose from the moon drips Bloop Power. Legal asked us not to explain.',
    upgradeSilkTentaclesName: 'Executive Noodle Arms',
    upgradeSilkTentaclesDescription: 'All producers flail 25% faster with board-approved jazz hands.',
    upgradePrismReefName: 'Premium Rainbow Excuse',
    upgradePrismReefDescription: 'All producers flail 35% faster because it is shiny and therefore mostly true.',
    upgradeDeepBloomName: 'Clam Microwave Setting',
    upgradeDeepBloomDescription: 'Capsules finish 35% sooner and complain 200% louder.',
    missionFirstBloomTitle: 'Staff the noodle pit',
    missionFirstBloomDescription: 'Own 5 Intern Tentacles before they unionize.',
    missionShrimpShiftTitle: 'Invent management',
    missionShrimpShiftDescription: 'Own 3 Shrimp Middle Managers with tiny clipboards.',
    missionPearlCacheTitle: 'Become mildly famous',
    missionPearlCacheDescription: 'Collect 25 lifetime Drama Pearls from clam clout.',
    missionBranchBossTitle: 'Open a crab branch',
    missionBranchBossDescription: 'Own 1 Crab Branch Boss and pretend the org chart is healthy.',
    missionTidalGiftTitle: 'Trust a weird clam',
    missionTidalGiftDescription: 'Claim 1 loot clam reward without asking questions.',
    missionWhaleBoardroomTitle: 'Flood the boardroom',
    missionWhaleBoardroomDescription: 'Own 1 Whale Shareholder before finance notices the splash damage.',
    missionPrismaticGrowthTitle: 'File the shiny paperwork',
    missionPrismaticGrowthDescription: 'Buy 2 Drama Pearl upgrades and call it strategy.'
  },
  ko: {
    appTitle: '말미잘 방치형: 뿅 회사',
    heroEyebrow: '합법인 척하는 산호초 스타트업',
    heroTitle: '말미잘 방치형: 뿅 회사',
    heroCopy: '사장 말미잘을 두들기고, 촉수 알바를 고용하고, 바다 헛소리를 분기별 꿈틀 수익으로 바꾸세요.',
    navReef: '노동 산호초',
    navCapsule: '전리품 조개',
    navUpgrades: '허세 진주',
    navSettings: '언어 도깨비',
    missionEyebrow: '회사식 무리수',
    questEyebrow: '장 목표',
    questStatusActive: '진행 중',
    questStatusCompleted: '보상 수령 가능',
    endOfContentTitle: '1장 업무 산호초 완료',
    endOfContentCopy: '다음 커뮤니티 원정은 조개 회의실에서 준비 중입니다.',
    chapterDashboardEyebrow: '장 업무판',
    chapterProgress: '{percent}% 완료',
    nextUnlockLabel: '다음 해금',
    bottleneckLabel: '현재 병목',
    productionLabel: '생산량',
    spawnProgressLabel: '{name} 자동 결재 {percent}%',
    progressProducer: '직원 수',
    progressResource: '누적 자원',
    progressStat: '기록',
    progressUpgrade: '업그레이드',
    progressQuest: '선행 목표',
    progressGeneric: '진행도',
    chapter1Title: '1장: 업무 산호초 창업',
    chapter1Description: '지하 말미잘 회사를 직원이 자동 증식하는 작은 조직도로 키우세요.',
    questFirstInternsTitle: '국수 구덩이 채용',
    questFirstInternsDescription: '인턴 촉수 5개를 보유해서 회사에 팔이 있다는 걸 증명하세요.',
    questFirstShrimpTitle: '관리직 발명',
    questFirstShrimpDescription: '새우 중간관리자 3마리를 보유해서 회의가 번식하게 하세요.',
    questFirstCapsuleTitle: '이상한 조개 믿기',
    questFirstCapsuleDescription: '법무팀에 묻지 말고 전리품 조개 보상 1회를 받으세요.',
    questFirstUpgradeTitle: '전략 스테이플러질',
    questFirstUpgradeDescription: '허세 진주 업그레이드 1개를 사고 운영 고도화라고 우기세요.',
    questFirstCrabTitle: '대게 지사 열기',
    questFirstCrabDescription: '대게 지사장 1마리를 보유해서 새우 고용을 자동화하세요.',
    questChapter1CompleteTitle: '첫 업무 산호초 생존',
    questChapter1CompleteDescription: '대게 1마리, 업그레이드 2개, 조개 1회를 끝내면 1장이 닫힙니다.',
    nextUnlockFirstShrimp: '첫 새우 중간관리자',
    nextUnlockFirstCapsule: '전리품 조개와 첫 업그레이드 루트',
    nextUnlockFirstUpgrade: '허세 진주 업그레이드',
    nextUnlockFirstCrab: '대게 지사장 벽',
    nextUnlockChapterComplete: '1장 완료 체크리스트',
    nextUnlockComingSoon: '다음 커뮤니티 원정 준비 중',
    progressLabel: '{title} 진행도',
    claimReward: '{reward} 슬쩍하기',
    perSecond: '+{amount}/초',
    buyCost: '고용 · {cost} {icon}',
    reefEyebrow: '수익 수족관',
    reefTitle: '사무실을 꿈틀거리게 하라',
    pulseEyebrow: '주인공 버튼',
    pulseTitle: '사장 말미잘 찰싹!',
    pulseCopy: '누를 때마다 자신감, 거품, 약간의 공포가 꿈틀밥으로 변합니다. 중간관리자는 계속 바쁜 척합니다.',
    pulseButton: '뿅! +{amount} {icon}',
    feedFeedbackEyebrow: '출근 완료',
    feedFeedbackTitle: '말미잘이 꿈틀밥을 먹고 출근했습니다!',
    feedFeedbackFood: '먹은 꿈틀밥',
    feedFeedbackLifetime: '누적 기록 증가',
    feedFeedbackRate: '현재 생산량',
    capsuleEyebrow: '수상한 공짜 물건',
    capsuleReadyTitle: '전리품 조개가 소리칩니다',
    capsuleRipeningTitle: '전리품 조개가 국 끓는 소리를 냅니다',
    capsuleIdleTitle: '수상한 전리품 조개 주문',
    capsuleCopy: '캡슐은 극적인 기다림 뒤 자원을 토해냅니다. 안전 동의서는 아무도 안 봤습니다.',
    openCapsule: '조개 와장창',
    readyIn: '{time} 동안 더 트림 중',
    startCapsule: '{time} 조개 흔들기',
    upgradesEyebrow: '허세 진주 연구소',
    upgradesTitle: '말도 안 되는 이점 구매',
    owned: '장착됨',
    buy: '지르기',
    ownedUpgrade: '이미 사업 모델에 스테이플러로 박았습니다',
    settingsEyebrow: '옵션 지하실',
    settingsTitle: '헛소리 언어 선택',
    settingsCopy: '모든 문구를 영어와 한국어로 바꿉니다. 작은 산호초 도깨비가 이 기기에 선택을 기억합니다.',
    english: 'English',
    korean: '한국어',
    activeLanguage: '현재 외치는 중',
    idleExplainerTitle: '이 사기가 돈 버는 법',
    idleExplainerCopy: '생산자를 고용한 뒤부터 방치 수입이 시작됩니다. 거대한 뿅 버튼은 여전히 축축한 권력을 느끼는 가장 빠른 길입니다.',
    noticeInitial: '작은 말미잘이 무허가 지하 회사를 열었습니다.',
    noticeBuyProducer: '새 직원 확보. 인사팀은 집게 든 게 한 마리입니다.',
    noticeNeedResources: '축축한 돈이 부족합니다. 더 꿈틀거리세요.',
    noticeBuyUpgrade: '허세 진주 장착. 생산성이 비싸 보이는 냄새를 냅니다.',
    noticeUpgradeBlocked: '업그레이드 위원회가 제안서에 국물을 던졌습니다.',
    noticeMissionClaimed: '회사식 무리수 완료. 축축한 상품을 받아가세요.',
    noticeMissionBlocked: '아직 헛소리가 부족합니다. 바다를 더 민망하게 하세요.',
    noticeCapsuleBusy: '수상한 조개 하나가 이미 구석에서 덜컹거립니다.',
    noticeCapsuleStarted: '전리품 조개 주문 완료. 즉시 속삭이기 시작했습니다.',
    noticeCapsuleWaiting: '조개가 싫답니다. 조개가 유난입니다.',
    noticeCapsuleClaimed: '뿅 잭팟! 조개가 자원으로 월세를 냈습니다.',
    noticePulse: '사장 찰싹으로 꿈틀밥 {amount}과 작은 소송 하나가 생성됐습니다.',
    noticeAdBuffClaimed: '광고 결재 완료. 임원용 국물이 30초 동안 전체 생산을 300% 시끄럽게 만듭니다.',
    noticeAdBuffCooldown: '임원용 국물이 식는 중입니다. 새우 법무팀이 기다리랍니다.',
    noticeLocale: '언어 도깨비가 자막을 바꿨습니다.',
    noticeOffline: '{minutes}분 동안 사라진 사이 산호초가 생산성 노래방을 열었습니다.',
    noticeOfflineCapped: '산호초가 4시간치 방치 헛소리를 저장하고 간식이 필요해졌습니다.',
    resourcePlankton: '꿈틀밥',
    resourcePearls: '허세 진주',
    resourceTideEnergy: '뿅 파워',
    producerDriftPolypsName: '인턴 촉수',
    producerDriftPolypsFlavor: '작은 무급 팔들이 바다에서 간식이 떨어질 때까지 스프레드시트를 흔듭니다.',
    producerCleanerShrimpName: '새우 중간관리자',
    producerCleanerShrimpFlavor: '거품을 가리키고 꿈틀밥을 삥뜯고 인턴 촉수를 자동으로 고용합니다.',
    producerCrabBranchBossName: '대게 지사장',
    producerCrabBranchBossFlavor: '지역본부 집게로 결재도장을 찍어 새우 중간관리자를 자동 증식합니다.',
    producerWhaleShareholderName: '고래 대주주',
    producerWhaleShareholderFlavor: '허세 진주를 들이마시고 대게 지사장을 물보라처럼 뿜는 바다 재벌입니다.',
    producerShellNurseryName: '조개 인플루언서 농장',
    producerShellNurseryFlavor: '윤기 나는 조개들이 관종 포즈를 올리면 민망함에서 허세 진주가 생깁니다.',
    producerMoonCurrentName: '수상한 달빛 호스',
    producerMoonCurrentFlavor: '달에서 온 호스가 뿅 파워를 흘립니다. 법무팀은 설명하지 말랬습니다.',
    upgradeSilkTentaclesName: '임원용 국수 팔',
    upgradeSilkTentaclesDescription: '모든 생산자가 이사회 승인 재즈핸드로 25% 더 빠르게 허우적댑니다.',
    upgradePrismReefName: '프리미엄 무지개 핑계',
    upgradePrismReefDescription: '반짝거리니까 대충 맞다는 이유로 모든 생산자가 35% 더 빠르게 허우적댑니다.',
    upgradeDeepBloomName: '조개 전자레인지 모드',
    upgradeDeepBloomDescription: '캡슐이 35% 더 빨리 끝나고 200% 더 크게 불평합니다.',
    missionFirstBloomTitle: '국수 구덩이 채용',
    missionFirstBloomDescription: '인턴 촉수가 노조 만들기 전에 5개 보유.',
    missionShrimpShiftTitle: '관리직 발명',
    missionShrimpShiftDescription: '작은 클립보드를 든 새우 중간관리자 3마리 보유.',
    missionPearlCacheTitle: '살짝 유명해지기',
    missionPearlCacheDescription: '조개 관종력으로 누적 허세 진주 25개 모으기.',
    missionBranchBossTitle: '대게 지사 열기',
    missionBranchBossDescription: '대게 지사장 1마리를 보유하고 조직도가 멀쩡한 척하기.',
    missionTidalGiftTitle: '이상한 조개 믿기',
    missionTidalGiftDescription: '질문하지 말고 전리품 조개 보상 1회 받기.',
    missionWhaleBoardroomTitle: '이사회 침수시키기',
    missionWhaleBoardroomDescription: '재무팀이 물보라를 보기 전에 고래 대주주 1마리 보유.',
    missionPrismaticGrowthTitle: '반짝이 서류 제출',
    missionPrismaticGrowthDescription: '허세 진주 업그레이드 2개를 사고 전략이라고 부르기.'
  }
};

const producerKeys = {
  driftPolyps: 'DriftPolyps',
  cleanerShrimp: 'CleanerShrimp',
  crabBranchBoss: 'CrabBranchBoss',
  whaleShareholder: 'WhaleShareholder',
  shellNursery: 'ShellNursery',
  moonCurrent: 'MoonCurrent'
};

const missionKeys = {
  'first-bloom': 'FirstBloom',
  'shrimp-shift': 'ShrimpShift',
  'pearl-cache': 'PearlCache',
  'branch-boss': 'BranchBoss',
  'tidal-gift': 'TidalGift',
  'whale-boardroom': 'WhaleBoardroom',
  'prismatic-growth': 'PrismaticGrowth'
};

const chapterKeys = {
  'chapter1-office-reef': 'chapter1'
};

const questKeys = {
  'quest-first-interns': 'questFirstInterns',
  'quest-first-shrimp': 'questFirstShrimp',
  'quest-first-capsule': 'questFirstCapsule',
  'quest-first-upgrade': 'questFirstUpgrade',
  'quest-first-crab': 'questFirstCrab',
  'quest-chapter1-complete': 'questChapter1Complete'
};

const resourceKeys = {
  plankton: 'Plankton',
  pearls: 'Pearls',
  tideEnergy: 'TideEnergy'
};

const upgradeKeys = {
  silkTentacles: 'SilkTentacles',
  prismReef: 'PrismReef',
  deepBloom: 'DeepBloom'
};

export function normalizeLocale(locale) {
  return SUPPORTED_LOCALES.includes(locale) ? locale : DEFAULT_LOCALE;
}

export function t(locale, key, params = {}) {
  const dictionary = translations[normalizeLocale(locale)];
  const template = dictionary[key] || translations[DEFAULT_LOCALE][key] || key;
  return Object.entries(params).reduce((text, [name, value]) => text.replaceAll(`{${name}}`, value), template);
}

export function resourceLabel(locale, resourceId) {
  return t(locale, `resource${resourceKeys[resourceId]}`);
}

export function producerText(locale, producer) {
  const key = producerKeys[producer.id];
  return {
    name: t(locale, `producer${key}Name`),
    flavor: t(locale, `producer${key}Flavor`)
  };
}

export function upgradeText(locale, upgrade) {
  const key = upgradeKeys[upgrade.id];
  return {
    name: t(locale, `upgrade${key}Name`),
    description: t(locale, `upgrade${key}Description`)
  };
}

export function missionText(locale, mission) {
  const key = missionKeys[mission.id];
  return {
    title: t(locale, `mission${key}Title`),
    description: t(locale, `mission${key}Description`)
  };
}

export function chapterText(locale, chapter) {
  const key = chapterKeys[chapter.id];
  return {
    title: t(locale, `${key}Title`),
    description: t(locale, `${key}Description`)
  };
}

export function progressionQuestText(locale, quest) {
  const key = questKeys[quest.id];
  return {
    title: t(locale, `${key}Title`),
    description: t(locale, `${key}Description`),
    nextUnlock: t(locale, quest.nextUnlockKey)
  };
}

export function formatReward(locale, reward) {
  return RESOURCE_KEYS
    .filter((resource) => reward[resource])
    .map((resource) => `${formatResourceAmount(reward[resource])} ${resourceLabel(locale, resource)}`)
    .join(', ');
}

function formatResourceAmount(value) {
  const amount = Number(value);
  if (!Number.isFinite(amount) || amount < 0) return '0.0';
  if (amount >= 1000000000000000) return amount.toExponential(1).replace('e+', 'e');
  if (amount >= 1000000000000) return `${(amount / 1000000000000).toFixed(1)}t`;
  if (amount >= 1000000000) return `${(amount / 1000000000).toFixed(1)}b`;
  if (amount >= 1000000) return `${(amount / 1000000).toFixed(1)}m`;
  if (amount >= 10000) return `${(amount / 1000).toFixed(1)}k`;
  if (amount >= 1000) return Math.floor(amount).toLocaleString('en-US');
  if (amount >= 100) return Math.floor(amount).toString();
  return amount.toFixed(amount < 10 ? 1 : 0);
}

export function getTranslationCoverage() {
  const requiredKeys = [
    'appTitle', 'heroEyebrow', 'heroTitle', 'heroCopy', 'navReef', 'navCapsule', 'navUpgrades', 'navSettings',
    'missionEyebrow', 'progressLabel', 'claimReward', 'perSecond', 'buyCost', 'reefEyebrow', 'reefTitle',
    'pulseEyebrow', 'pulseTitle', 'pulseCopy', 'pulseButton', 'feedFeedbackEyebrow', 'feedFeedbackTitle',
    'feedFeedbackFood', 'feedFeedbackLifetime', 'feedFeedbackRate', 'capsuleEyebrow', 'capsuleReadyTitle',
    'capsuleRipeningTitle', 'capsuleIdleTitle', 'capsuleCopy', 'openCapsule', 'readyIn', 'startCapsule',
    'upgradesEyebrow', 'upgradesTitle', 'owned', 'buy', 'ownedUpgrade', 'settingsEyebrow', 'settingsTitle',
    'settingsCopy', 'english', 'korean', 'activeLanguage', 'idleExplainerTitle', 'idleExplainerCopy',
    'noticeInitial', 'noticeBuyProducer', 'noticeNeedResources', 'noticeBuyUpgrade', 'noticeUpgradeBlocked',
    'noticeMissionClaimed', 'noticeMissionBlocked', 'noticeCapsuleBusy', 'noticeCapsuleStarted', 'noticeCapsuleWaiting',
    'noticeCapsuleClaimed', 'noticePulse', 'noticeAdBuffClaimed', 'noticeAdBuffCooldown', 'noticeLocale', 'noticeOffline', 'noticeOfflineCapped',
    'questEyebrow', 'questStatusActive', 'questStatusCompleted', 'endOfContentTitle', 'endOfContentCopy',
    'chapterDashboardEyebrow', 'chapterProgress', 'nextUnlockLabel', 'bottleneckLabel', 'productionLabel', 'spawnProgressLabel',
    'progressProducer', 'progressResource', 'progressStat', 'progressUpgrade', 'progressQuest', 'progressGeneric',
    ...RESOURCE_KEYS.map((resource) => `resource${resourceKeys[resource]}`),
    ...PRODUCERS.flatMap((producer) => [`producer${producerKeys[producer.id]}Name`, `producer${producerKeys[producer.id]}Flavor`]),
    ...UPGRADE_DEFINITIONS.flatMap((upgrade) => [`upgrade${upgradeKeys[upgrade.id]}Name`, `upgrade${upgradeKeys[upgrade.id]}Description`]),
    ...MISSION_DEFINITIONS.flatMap((mission) => [`mission${missionKeys[mission.id]}Title`, `mission${missionKeys[mission.id]}Description`]),
    ...CHAPTER_DEFINITIONS.flatMap((chapter) => [`${chapterKeys[chapter.id]}Title`, `${chapterKeys[chapter.id]}Description`]),
    ...QUEST_DEFINITIONS.flatMap((quest) => [`${questKeys[quest.id]}Title`, `${questKeys[quest.id]}Description`, quest.nextUnlockKey])
  ];

  return Object.fromEntries(SUPPORTED_LOCALES.map((locale) => [
    locale,
    requiredKeys.filter((key) => !translations[locale][key])
  ]));
}
