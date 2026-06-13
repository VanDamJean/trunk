import { int, mysqlEnum, mysqlTable, text, timestamp, varchar, json, boolean, decimal } from "drizzle-orm/mysql-core";

/**
 * Core user table backing auth flow.
 * Extend this file with additional tables as your product grows.
 * Columns use camelCase to match both database fields and generated types.
 */
export const users = mysqlTable("users", {
  /**
   * Surrogate primary key. Auto-incremented numeric value managed by the database.
   * Use this for relations between tables.
   */
  id: int("id").autoincrement().primaryKey(),
  /** Manus OAuth identifier (openId) returned from the OAuth callback. Unique per user. */
  openId: varchar("openId", { length: 64 }).notNull().unique(),
  name: text("name"),
  email: varchar("email", { length: 320 }),
  loginMethod: varchar("loginMethod", { length: 64 }),
  role: mysqlEnum("role", ["user", "admin"]).default("user").notNull(),
  createdAt: timestamp("createdAt").defaultNow().notNull(),
  updatedAt: timestamp("updatedAt").defaultNow().onUpdateNow().notNull(),
  lastSignedIn: timestamp("lastSignedIn").defaultNow().notNull(),
});

export type User = typeof users.$inferSelect;
export type InsertUser = typeof users.$inferInsert;

/**
 * Quiz Categories Table
 * Stores different quiz categories (e.g., Math, Science, History)
 */
export const categories = mysqlTable("categories", {
  id: int("id").autoincrement().primaryKey(),
  name: varchar("name", { length: 100 }).notNull(),
  description: text("description"),
  icon: varchar("icon", { length: 255 }), // Icon URL or emoji
  color: varchar("color", { length: 20 }).default("#8b5cf6"), // Hex color code
  order: int("order").default(0),
  createdAt: timestamp("createdAt").defaultNow().notNull(),
  updatedAt: timestamp("updatedAt").defaultNow().onUpdateNow().notNull(),
});

export type Category = typeof categories.$inferSelect;
export type InsertCategory = typeof categories.$inferInsert;

/**
 * Quiz Questions Table
 * Stores individual quiz questions with multiple choice answers
 */
export const quizzes = mysqlTable("quizzes", {
  id: int("id").autoincrement().primaryKey(),
  categoryId: int("categoryId").notNull(),
  question: text("question").notNull(),
  options: json("options").$type<string[]>().notNull(), // Array of 4 options
  correctAnswer: int("correctAnswer").notNull(), // Index of correct option (0-3)
  explanation: text("explanation"), // Explanation for the correct answer
  difficulty: mysqlEnum("difficulty", ["easy", "medium", "hard"]).default("medium"),
  order: int("order").default(0),
  createdAt: timestamp("createdAt").defaultNow().notNull(),
  updatedAt: timestamp("updatedAt").defaultNow().onUpdateNow().notNull(),
});

export type Quiz = typeof quizzes.$inferSelect;
export type InsertQuiz = typeof quizzes.$inferInsert;

/**
 * Quiz Results Table
 * Stores user's quiz attempt results and scores
 */
export const quizResults = mysqlTable("quiz_results", {
  id: int("id").autoincrement().primaryKey(),
  userId: int("userId").notNull(),
  categoryId: int("categoryId").notNull(),
  totalQuestions: int("totalQuestions").notNull(),
  correctAnswers: int("correctAnswers").notNull(),
  score: decimal("score", { precision: 5, scale: 2 }).notNull(), // Percentage score
  timeSpent: int("timeSpent").notNull(), // Time in seconds
  answers: json("answers").$type<Array<{ quizId: number; selectedAnswer: number; isCorrect: boolean }>>().notNull(),
  completedAt: timestamp("completedAt").defaultNow().notNull(),
  createdAt: timestamp("createdAt").defaultNow().notNull(),
});

export type QuizResult = typeof quizResults.$inferSelect;
export type InsertQuizResult = typeof quizResults.$inferInsert;

/**
 * User Statistics Table
 * Stores aggregated user statistics and badges
 */
export const userStats = mysqlTable("user_stats", {
  id: int("id").autoincrement().primaryKey(),
  userId: int("userId").notNull().unique(),
  totalQuizzes: int("totalQuizzes").default(0),
  totalScore: decimal("totalScore", { precision: 10, scale: 2 }).default("0.00"),
  averageScore: decimal("averageScore", { precision: 5, scale: 2 }).default("0.00"),
  totalTimeSpent: int("totalTimeSpent").default(0), // Total time in seconds
  badges: json("badges").$type<Array<{ id: string; name: string; unlockedAt: string }>>(),
  categoryStats: json("categoryStats").$type<Array<{
    categoryId: string;
    attempts: number;
    bestScore: number;
    averageScore: number;
  }>>(),
  lastPlayedAt: timestamp("lastPlayedAt"),
  createdAt: timestamp("createdAt").defaultNow().notNull(),
  updatedAt: timestamp("updatedAt").defaultNow().onUpdateNow().notNull(),
});

export type UserStat = typeof userStats.$inferSelect;
export type InsertUserStat = typeof userStats.$inferInsert;

/**
 * Leaderboard Table
 * Stores cached leaderboard rankings for performance
 */
export const leaderboard = mysqlTable("leaderboard", {
  id: int("id").autoincrement().primaryKey(),
  userId: int("userId").notNull().unique(),
  userName: varchar("userName", { length: 255 }).notNull(),
  totalScore: decimal("totalScore", { precision: 10, scale: 2 }).notNull(),
  rank: int("rank").notNull(),
  categoryId: int("categoryId"), // NULL for global leaderboard, specific ID for category
  updatedAt: timestamp("updatedAt").defaultNow().onUpdateNow().notNull(),
});

export type Leaderboard = typeof leaderboard.$inferSelect;
export type InsertLeaderboard = typeof leaderboard.$inferInsert;

/**
 * Badges Table
 * Stores badge definitions
 */
export const badges = mysqlTable("badges", {
  id: varchar("id", { length: 50 }).primaryKey(),
  name: varchar("name", { length: 100 }).notNull(),
  description: text("description"),
  icon: varchar("icon", { length: 255 }), // Icon URL or emoji
  criteria: json("criteria").$type<{
    type: "score" | "streak" | "category" | "time";
    value: number;
  }>().notNull(),
  createdAt: timestamp("createdAt").defaultNow().notNull(),
});

export type Badge = typeof badges.$inferSelect;
export type InsertBadge = typeof badges.$inferInsert;
