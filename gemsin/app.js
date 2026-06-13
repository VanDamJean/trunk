const views = Array.from(document.querySelectorAll(".view"));
const data = window.MOCK_DATA || {};
let walletCredit = 982;
let leaderboardSortHigh = true;
let activeGameId = null;
const STORAGE_KEY = "gemsin-mock-state-v1";
const defaultState = {
  walletCredit: 982,
  leaderboardSortHigh: true,
  leaderboardRange: "Weekly",
  historyFilter: "all",
  lastView: "onboarding-1",
  followingMap: {},
};

function loadState() {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (!raw) return { ...defaultState };
    const parsed = JSON.parse(raw);
    return { ...defaultState, ...parsed };
  } catch {
    return { ...defaultState };
  }
}

let appState = loadState();
walletCredit = Number(appState.walletCredit || 982);
leaderboardSortHigh = !!appState.leaderboardSortHigh;

function saveState() {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(appState));
}

function showView(targetId) {
  views.forEach((view) => {
    view.classList.toggle("hidden", view.id !== targetId);
  });
  appState.lastView = targetId;
  saveState();
}

document.querySelectorAll(".next-onboarding, .meta-btn, .tab-nav").forEach((button) => {
  button.addEventListener("click", () => {
    const target = button.dataset.target;
    if (target) showView(target);
  });
});

function renderGameCards() {
  const container = document.getElementById("home-game-grid");
  if (!container || !data.games) return;
  container.innerHTML = data.games
    .map(
      (g) => `
      <article class="game-card ${g.className}" data-game-id="${g.id}">
        <div>
          <h3>${g.title}</h3>
          <p>${g.mode}</p>
        </div>
        <button class="start-btn" data-start-id="${g.id}">Start Game</button>
      </article>`
    )
    .join("");
}

function renderOnboarding() {
  if (!data.onboarding) return;
  data.onboarding.forEach((item) => {
    const badge = document.getElementById(`${item.id}-badge`);
    const title = document.getElementById(`${item.id}-title`);
    const desc = document.getElementById(`${item.id}-desc`);
    if (badge) badge.textContent = item.badge;
    if (title) title.textContent = item.title;
    if (desc) desc.textContent = item.description;
  });
}

function renderLeaderboardRows(rows) {
  const leaderboardList = document.getElementById("leaderboard-list");
  if (!leaderboardList) return;
  if (!rows.length) {
    leaderboardList.innerHTML = "";
    return;
  }
  leaderboardList.innerHTML = rows
    .map(
      (r, idx) => `
      <div class="rank-row ${r.me ? "you" : ""}" data-player="${r.name}" data-score="${r.score}">
        <b>${String(idx + 1).padStart(2, "0")}</b>
        <span class="row-media">
          ${
            r.avatar
              ? `<img class="avatar-img avatar-sm" src="${r.avatar}" alt="${r.name}" />`
              : `<span class="avatar-fallback avatar-sm">${r.initials || r.name.slice(0, 2).toUpperCase()}</span>`
          }
          <span>${r.name}</span>
        </span>
        <em>${r.score.toLocaleString()}</em>
      </div>`
    )
    .join("");
}

function renderLeaderboardPodium(rows) {
  const podium = document.getElementById("leaderboard-podium");
  if (!podium) return;
  if (!rows.length) {
    podium.innerHTML = "";
    return;
  }
  const ordered = [rows[1], rows[0], rows[2]].filter(Boolean);
  podium.innerHTML = ordered
    .map((r) => {
      const rank = rows.findIndex((x) => x.name === r.name) + 1;
      const tone = rank === 1 ? "gold" : rank === 2 ? "silver" : "bronze";
      return `
      <article class="podium-card ${tone}">
        <div class="podium-avatar-wrap">
          ${rank === 1 ? '<span class="crown">👑</span>' : ""}
          ${
            r.avatar
              ? `<img class="avatar-img podium-avatar" src="${r.avatar}" alt="${r.name}" />`
              : `<span class="avatar-fallback podium-avatar">${r.initials || r.name.slice(0, 2).toUpperCase()}</span>`
          }
        </div>
        <strong>@${r.name}</strong>
        <small>${r.score.toLocaleString()} tiket</small>
      </article>`;
    })
    .join("");
}

function getSortedLeaderboard() {
  return [...(data.leaderboard || [])].sort((a, b) =>
    leaderboardSortHigh ? b.score - a.score : a.score - b.score
  );
}

function applyLeaderboardView() {
  const keyword = (leaderboardSearch?.value || "").toLowerCase().trim();
  const sorted = getSortedLeaderboard();
  const filtered = keyword ? sorted.filter((r) => r.name.toLowerCase().includes(keyword)) : sorted;
  const podiumRows = filtered.slice(0, 3);
  const listRows = filtered.slice(3);
  renderLeaderboardPodium(podiumRows);
  renderLeaderboardRows(listRows);
  updateLeaderboardEmptyState(filtered.length);
}

function renderPacks() {
  const packWrap = document.getElementById("ticket-packs");
  if (!packWrap || !data.packs) return;
  packWrap.innerHTML =
    `<h3 class="sub-head">Top Up Packs</h3>` +
    data.packs
      .map(
        (p) => `
      <button class="pack-row" data-mock-action="${p.key}">
        <span>${p.name}</span><em>${p.credit} Credit</em><b>${p.price}</b>
      </button>`
      )
      .join("");
}

function renderFriends() {
  const list = document.getElementById("friends-list");
  if (!list || !data.friends) return;
  data.friends = data.friends.map((f) => ({
    ...f,
    following:
      typeof appState.followingMap[f.name] === "boolean"
        ? appState.followingMap[f.name]
        : f.following,
  }));
  list.innerHTML = data.friends
    .map(
      (f) => `
      <div class="friend-row" data-name="${f.name}">
        ${
          f.avatar
            ? `<img class="avatar-img friend-avatar" src="${f.avatar}" alt="${f.name}" />`
            : `<span class="friend-avatar avatar-fallback">${f.initials}</span>`
        }
        <div><strong>${f.name}</strong><small>${f.status}</small></div>
        <button class="follow-btn ${f.following ? "following" : ""}" data-name="${f.name}">
          ${f.following ? "Following" : "Follow"}
        </button>
      </div>`
    )
    .join("");
}

function renderHistory() {
  const list = document.getElementById("history-list");
  if (!list || !data.history) return;
  list.innerHTML = data.history
    .map(
      (h) => `
      <div class="history-row" data-range="${h.range}">
        <div class="row-media">
          ${
            h.thumb
              ? `<img class="thumb-img history-thumb" src="${h.thumb}" alt="${h.game}" />`
              : `<span class="thumb-fallback history-thumb">${h.icon || "🎮"}</span>`
          }
          <div><strong>${h.game}</strong><small>${h.time}</small></div>
        </div>
        <em>Score ${h.score}</em>
      </div>`
    )
    .join("");
  updateHistoryEmptyState();
}

function updateLeaderboardEmptyState(totalVisible = null) {
  const empty = document.getElementById("leaderboard-empty");
  const visibleCount =
    totalVisible === null
      ? document.querySelectorAll("#leaderboard-list .rank-row:not(.hidden-row)").length
      : totalVisible;
  empty?.classList.toggle("hidden", visibleCount > 0);
}

function updateHistoryEmptyState() {
  const empty = document.getElementById("history-empty");
  const visibleCount = document.querySelectorAll("#history-list .history-row:not(.hidden-row)").length;
  empty?.classList.toggle("hidden", visibleCount > 0);
}

function bindGameInteractions() {
  document.querySelectorAll(".start-btn").forEach((button) => {
    button.addEventListener("click", (event) => {
      event.stopPropagation();
      const gameId = button.dataset.startId;
      const game = data.games?.find((g) => g.id === gameId);
      window.alert(`${game?.title || "Game"} mock start (backend belum tersedia)`);
    });
  });

  document.querySelectorAll(".game-card").forEach((card) => {
    card.addEventListener("click", () => {
      const gameId = card.dataset.gameId;
      const game = data.games?.find((g) => g.id === gameId);
      if (!game) return;
      activeGameId = game.id;
      const modal = document.getElementById("game-detail-modal");
      const title = document.getElementById("modal-game-title");
      const desc = document.getElementById("modal-game-desc");
      const mode = document.getElementById("modal-game-mode");
      const difficulty = document.getElementById("modal-game-difficulty");
      if (title) title.textContent = game.title;
      if (desc) desc.textContent = game.desc;
      if (mode) mode.textContent = game.mode;
      if (difficulty) difficulty.textContent = `Difficulty: ${game.difficulty}`;
      modal?.classList.remove("hidden");
    });
  });
}

function bindDynamicActionButtons() {
  document.querySelectorAll("[data-mock-action]").forEach((button) => {
    button.addEventListener("click", () => {
      const action = button.dataset.mockAction;
      const messages = {
        "daily-claim": "Daily reward claimed: +5 credit (mock)",
        "buy-starter": "Starter Pack purchased: +50 credit (mock)",
        "buy-pro": "Pro Pack purchased: +250 credit (mock)",
        "buy-elite": "Elite Pack purchased: +700 credit (mock)",
      };
      if (action === "daily-claim") walletCredit += 5;
      if (action === "buy-starter") walletCredit += 50;
      if (action === "buy-pro") walletCredit += 250;
      if (action === "buy-elite") walletCredit += 700;
      const creditEl = document.getElementById("wallet-credit");
      if (creditEl) creditEl.textContent = String(walletCredit);
      appState.walletCredit = walletCredit;
      saveState();
      window.alert(messages[action] || "Mock action triggered");
    });
  });
}

function bindFriendButtons() {
  document.querySelectorAll(".follow-btn").forEach((button) => {
    button.addEventListener("click", () => {
      const name = button.dataset.name || "Friend";
      const following = button.classList.toggle("following");
      button.textContent = following ? "Following" : "Follow";
      appState.followingMap[name] = following;
      saveState();
      window.alert(`${name} ${following ? "followed" : "unfollowed"} (mock)`);
    });
  });
}

const leaderboardSearch = document.getElementById("leaderboard-search");
const leaderboardList = document.getElementById("leaderboard-list");
const leaderboardSortBtn = document.getElementById("leaderboard-sort-btn");
const leaderboardRangeBtn = document.getElementById("leaderboard-range-btn");
const leaderboardRegionTabs = document.querySelectorAll("#leaderboard-region-tabs .segment-btn");
const leaderboardGameTrigger = document.getElementById("leaderboard-game-trigger");
const leaderboardGameMenu = document.getElementById("leaderboard-game-menu");
const friendsSearch = document.getElementById("friends-search");
const editProfileForm = document.getElementById("edit-profile-form");

if (leaderboardSearch) {
  leaderboardSearch.addEventListener("input", () => {
    applyLeaderboardView();
  });
}

if (leaderboardSortBtn && leaderboardList) {
  leaderboardSortBtn.textContent = leaderboardSortHigh ? "Sort: High" : "Sort: Low";
  leaderboardSortBtn.addEventListener("click", () => {
    leaderboardSortHigh = !leaderboardSortHigh;
    appState.leaderboardSortHigh = leaderboardSortHigh;
    applyLeaderboardView();
    leaderboardSortBtn.textContent = leaderboardSortHigh ? "Sort: High" : "Sort: Low";
    saveState();
  });
}

if (leaderboardRangeBtn) {
  leaderboardRangeBtn.textContent = appState.leaderboardRange || "Weekly";
  leaderboardRangeBtn.addEventListener("click", () => {
    leaderboardRangeBtn.textContent =
      leaderboardRangeBtn.textContent === "Weekly" ? "Monthly" : "Weekly";
    appState.leaderboardRange = leaderboardRangeBtn.textContent;
    saveState();
  });
}

leaderboardRegionTabs.forEach((tab) => {
  tab.addEventListener("click", () => {
    leaderboardRegionTabs.forEach((t) => t.classList.remove("active"));
    tab.classList.add("active");
  });
});

if (leaderboardGameTrigger && leaderboardGameMenu) {
  leaderboardGameTrigger.addEventListener("click", () => {
    leaderboardGameMenu.classList.toggle("hidden");
  });
  leaderboardGameMenu.querySelectorAll(".dropdown-item").forEach((item) => {
    item.addEventListener("click", () => {
      leaderboardGameMenu.querySelectorAll(".dropdown-item").forEach((i) => i.classList.remove("active"));
      item.classList.add("active");
      leaderboardGameTrigger.textContent = `${item.textContent} ▾`;
      leaderboardGameMenu.classList.add("hidden");
    });
  });
  document.addEventListener("click", (event) => {
    const target = event.target;
    if (!leaderboardGameMenu.contains(target) && !leaderboardGameTrigger.contains(target)) {
      leaderboardGameMenu.classList.add("hidden");
    }
  });
}

if (friendsSearch) {
  friendsSearch.addEventListener("input", () => {
    const keyword = friendsSearch.value.toLowerCase().trim();
    document.querySelectorAll("#friends-list .friend-row").forEach((row) => {
      const name = (row.getAttribute("data-name") || "").toLowerCase();
      row.classList.toggle("hidden-row", keyword.length > 0 && !name.includes(keyword));
    });
  });
}

document.querySelectorAll(".history-filter").forEach((btn) => {
  btn.addEventListener("click", () => {
    const mode = btn.getAttribute("data-history-filter") || "all";
    document.querySelectorAll(".history-filter").forEach((b) => b.classList.remove("active-filter"));
    btn.classList.add("active-filter");
    document.querySelectorAll("#history-list .history-row").forEach((row) => {
      const range = row.getAttribute("data-range");
      const hide = mode !== "all" && range !== mode;
      row.classList.toggle("hidden-row", hide);
    });
    updateHistoryEmptyState();
    appState.historyFilter = mode;
    saveState();
  });
});

const closeModalBtn = document.getElementById("close-modal-btn");
const modal = document.getElementById("game-detail-modal");
const modalStartBtn = document.getElementById("modal-start-btn");

closeModalBtn?.addEventListener("click", () => modal?.classList.add("hidden"));
modal?.addEventListener("click", (event) => {
  if (event.target === modal) modal.classList.add("hidden");
});
modalStartBtn?.addEventListener("click", () => {
  const game = data.games?.find((g) => g.id === activeGameId);
  window.alert(`${game?.title || "Game"} started from detail modal (mock)`);
  modal?.classList.add("hidden");
});

editProfileForm?.addEventListener("submit", (event) => {
  event.preventDefault();
  window.alert("Profil berhasil disimpan (mock)");
  showView("profile-screen");
});

renderGameCards();
renderOnboarding();
applyLeaderboardView();
renderPacks();
renderFriends();
renderHistory();
bindGameInteractions();
bindDynamicActionButtons();
bindFriendButtons();
document.getElementById("wallet-credit").textContent = String(walletCredit);
if (appState.historyFilter && appState.historyFilter !== "all") {
  const target = document.querySelector(`.history-filter[data-history-filter="${appState.historyFilter}"]`);
  target?.click();
}
updateHistoryEmptyState();
showView(appState.lastView || "onboarding-1");
