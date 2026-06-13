window.MOCK_DATA = {
  onboarding: [
    {
      id: "onboarding-1",
      badge: "GEMSIN",
      title: "Games Are More Fun!",
      description:
        "We'll help you find your next favorite game. Save your progress and track your achievements as you level up.",
    },
    {
      id: "onboarding-2",
      badge: "LEVEL UP",
      title: "Compete & Earn",
      description:
        "Climb the leaderboard, collect diamonds, and unlock rewards as you complete daily challenges.",
    },
    {
      id: "onboarding-3",
      badge: "COMMUNITY",
      title: "Play Together",
      description:
        "Connect with friends, share your game history, and join a community built for casual and competitive players.",
    },
  ],
  games: [
    {
      id: "flappy-bird",
      title: "Flappy Bird",
      mode: "Single Player",
      difficulty: "Easy",
      desc: "Tap to fly and avoid obstacles. Short-session game for quick score runs.",
      className: "card-flappy",
    },
    {
      id: "ular-angka",
      title: "Ular Angka",
      mode: "Single Player",
      difficulty: "Medium",
      desc: "Number-snake puzzle style game focused on timing and pattern recognition.",
      className: "card-snake",
    },
    {
      id: "tetris",
      title: "Tetris",
      mode: "Multiplayer",
      difficulty: "Medium",
      desc: "Stack blocks efficiently and compete for high lines and speed control.",
      className: "card-tetris",
    },
    {
      id: "jump-man",
      title: "Jump Man",
      mode: "Single Player",
      difficulty: "Easy",
      desc: "Arcade jumping challenge with simple controls and score progression.",
      className: "card-stickman",
    },
  ],
  leaderboard: [
    { name: "MiraZone", score: 1530, me: false, avatar: "", initials: "MZ" },
    { name: "KangPixel", score: 1420, me: false, avatar: "", initials: "KP" },
    { name: "Levronix", score: 1318, me: false, avatar: "", initials: "LX" },
    { name: "You", score: 982, me: true, avatar: "", initials: "YOU" },
    { name: "bobbybow", score: 2400, me: false, avatar: "", initials: "BB" },
    { name: "tiojunior", score: 1980, me: false, avatar: "", initials: "TJ" },
    { name: "garryjake", score: 1890, me: false, avatar: "", initials: "GJ" },
    { name: "hrmandays", score: 1000, me: false, avatar: "", initials: "HR" },
    { name: "madynguzna", score: 900, me: false, avatar: "", initials: "MG" },
    { name: "gloryaaaaaa", score: 850, me: false, avatar: "", initials: "GL" },
    { name: "katespade", score: 632, me: false, avatar: "", initials: "KS" },
    { name: "callmegrandma", score: 591, me: false, avatar: "", initials: "CG" },
  ],
  packs: [
    { key: "buy-starter", name: "Starter Pack", credit: 50, price: "$0.99" },
    { key: "buy-pro", name: "Pro Pack", credit: 250, price: "$3.99" },
    { key: "buy-elite", name: "Elite Pack", credit: 700, price: "$8.99" },
  ],
  friends: [
    { name: "KarinAyu", status: "Playing Tetris", initials: "KA", following: false, avatar: "" },
    { name: "RamaM", status: "Online 3m ago", initials: "RM", following: true, avatar: "" },
    { name: "LinaN", status: "Playing Flappy Bird", initials: "LN", following: false, avatar: "" },
  ],
  history: [
    { game: "Flappy Bird", time: "Today, 09:12", score: 247, range: "today", thumb: "", icon: "🐦" },
    { game: "Ular Angka", time: "Today, 08:44", score: 190, range: "today", thumb: "", icon: "🐍" },
    { game: "Tetris", time: "Yesterday, 21:20", score: 305, range: "yesterday", thumb: "", icon: "🧱" },
    { game: "Jump Man", time: "Yesterday, 18:53", score: 128, range: "yesterday", thumb: "", icon: "🏃" },
  ],
};
