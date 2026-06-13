import { PRODUCERS } from '../config.js';

export function createProducerProgressState(values = {}) {
  return PRODUCERS.reduce((progress, producer) => {
    if (producer.producesProducer) {
      progress[producer.id] = finiteNonNegative(values[producer.id]);
    }
    return progress;
  }, {});
}

function finiteNonNegative(value) {
  const number = Number(value);
  return Number.isFinite(number) && number >= 0 ? number : 0;
}

export function getProducerResourceTarget(producer) {
  return producer.producesResource || producer.produces || null;
}

export function getProducerSpawnTarget(producer) {
  return producer.producesProducer || null;
}

export function isProducerSpawner(producer) {
  return Boolean(getProducerSpawnTarget(producer));
}

export function applyProducerSpawnProgress(economy, producer, amount) {
  const targetProducerId = getProducerSpawnTarget(producer);
  if (!targetProducerId || amount <= 0) {
    return 0;
  }

  // 새우 중간관리자/대게 지사장/고래 대주주가 꿈틀밥 대신 하위 직원을 자동 고용한다.
  // 소수점 진행도를 버리면 심해 직장인들이 임금체불로 도망가므로 반드시 보존한다.
  const totalProgress = finiteNonNegative(economy.producerProgress[producer.id]) + finiteNonNegative(amount);
  const hired = Math.floor(totalProgress);
  economy.producerProgress[producer.id] = totalProgress - hired;

  if (hired > 0) {
    economy.producers[targetProducerId] += hired;
    economy.stats.autoHires += hired;
  }

  return hired;
}
