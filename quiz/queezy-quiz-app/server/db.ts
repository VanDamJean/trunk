import { eq, desc, and } from "drizzle-orm";
import { drizzle } from "drizzle-orm/mysql2";
import { InsertUser, users, categories, quizzes, quizResults, userStats, leaderboard } from "../drizzle/schema";
import { ENV } from './_core/env';

let _db: ReturnType<typeof drizzle> | null = null;

// Lazily create the drizzle instance so local tooling can run without a DB.
export async function getDb() {
  if (!_db && process.env.DATABASE_URL) {
    try {
      _db = drizzle(process.env.DATABASE_URL);
    } catch (error) {
      console.warn("[Database] Failed to connect:", error);
      _db = null;
    }
  }
  return _db;
}

export async function upsertUser(user: InsertUser): Promise<void> {
  if (!user.openId) {
    throw new Error("User openId is required for upsert");
  }

  const db = await getDb();
  if (!db) {
    console.warn("[Database] Cannot upsert user: database not available");
    return;
  }

  try {
    const values: InsertUser = {
      openId: user.openId,
    };
    const updateSet: Record<string, unknown> = {};

    const textFields = ["name", "email", "loginMethod"] as const;
    type TextField = (typeof textFields)[number];

    const assignNullable = (field: TextField) => {
      const value = user[field];
      if (value === undefined) return;
      const normalized = value ?? null;
      values[field] = normalized;
      updateSet[field] = normalized;
    };

    textFields.forEach(assignNullable);

    if (user.lastSignedIn !== undefined) {
      values.lastSignedIn = user.lastSignedIn;
      updateSet.lastSignedIn = user.lastSignedIn;
    }
    if (user.role !== undefined) {
      values.role = user.role;
      updateSet.role = user.role;
    } else if (user.openId === ENV.ownerOpenId) {
      values.role = 'admin';
      updateSet.role = 'admin';
    }

    if (!values.lastSignedIn) {
      values.lastSignedIn = new Date();
    }

    if (Object.keys(updateSet).length === 0) {
      updateSet.lastSignedIn = new Date();
    }

    await db.insert(users).values(values).onDuplicateKeyUpdate({
      set: updateSet,
    });
  } catch (error) {
    console.error("[Database] Failed to upsert user:", error);
    throw error;
  }
}

export async function getUserByOpenId(openId: string) {
  const db = await getDb();
  if (!db) {
    console.warn("[Database] Cannot get user: database not available");
    return undefined;
  }

  const result = await db.select().from(users).where(eq(users.openId, openId)).limit(1);

  return result.length > 0 ? result[0] : undefined;
}

// Quiz-related queries
export async function getAllCategories() {
  const db = await getDb();
  if (!db) return [];

  return await db.select().from(categories).orderBy(categories.order);
}

export async function getQuizzesByCategory(categoryId: number) {
  const db = await getDb();
  if (!db) return [];

  return await db.select().from(quizzes).where(eq(quizzes.categoryId, categoryId)).orderBy(quizzes.order);
}

export async function saveQuizResult(result: typeof quizResults.$inferInsert) {
  const db = await getDb();
  if (!db) throw new Error("Database not available");

  const insertedResult = await db.insert(quizResults).values(result);
  
  // Update user stats
  const existingStats = await db.select().from(userStats).where(eq(userStats.userId, result.userId)).limit(1);
  
  if (existingStats.length > 0) {
    const stats = existingStats[0];
    const newTotalQuizzes = (stats.totalQuizzes || 0) + 1;
    const newTotalScore = parseFloat((stats.totalScore || 0).toString()) + parseFloat(result.score.toString());
    const newAverageScore = newTotalScore / newTotalQuizzes;
    
    await db.update(userStats).set({
      totalQuizzes: newTotalQuizzes,
      totalScore: newTotalScore.toString(),
      averageScore: newAverageScore.toString(),
      totalTimeSpent: (stats.totalTimeSpent || 0) + result.timeSpent,
      lastPlayedAt: new Date(),
    }).where(eq(userStats.userId, result.userId));
  } else {
    await db.insert(userStats).values({
      userId: result.userId,
      totalQuizzes: 1,
      totalScore: result.score.toString(),
      averageScore: result.score.toString(),
      totalTimeSpent: result.timeSpent,
      lastPlayedAt: new Date(),
    });
  }

  return insertedResult;
}

export async function getUserStats(userId: number) {
  const db = await getDb();
  if (!db) return null;

  const result = await db.select().from(userStats).where(eq(userStats.userId, userId)).limit(1);
  return result.length > 0 ? result[0] : null;
}

export async function getLeaderboard(categoryId?: number, limit: number = 10) {
  const db = await getDb();
  if (!db) return [];

  if (categoryId) {
    return await db.select().from(leaderboard).where(eq(leaderboard.categoryId, categoryId)).orderBy(leaderboard.rank).limit(limit);
  } else {
    return await db.select().from(leaderboard).where(eq(leaderboard.categoryId, null as any)).orderBy(leaderboard.rank).limit(limit);
  }
}

export async function getUserRank(userId: number, categoryId?: number) {
  const db = await getDb();
  if (!db) return null;

  if (categoryId) {
    const result = await db.select().from(leaderboard).where(and(eq(leaderboard.userId, userId), eq(leaderboard.categoryId, categoryId))).limit(1);
    return result.length > 0 ? result[0] : null;
  } else {
    const result = await db.select().from(leaderboard).where(and(eq(leaderboard.userId, userId), eq(leaderboard.categoryId, null as any))).limit(1);
    return result.length > 0 ? result[0] : null;
  }
}

export async function getQuizResultHistory(userId: number, limit: number = 10) {
  const db = await getDb();
  if (!db) return [];

  return await db.select().from(quizResults).where(eq(quizResults.userId, userId)).orderBy(desc(quizResults.createdAt)).limit(limit);
}
