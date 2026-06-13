import { COOKIE_NAME } from "@shared/const";
import { getSessionCookieOptions } from "./_core/cookies";
import { systemRouter } from "./_core/systemRouter";
import { publicProcedure, protectedProcedure, router } from "./_core/trpc";
import { getAllCategories, getQuizzesByCategory, saveQuizResult, getUserStats, getLeaderboard, getUserRank, getQuizResultHistory } from "./db";
import { z } from "zod";

export const appRouter = router({
  system: systemRouter,
  auth: router({
    me: publicProcedure.query(opts => opts.ctx.user),
    logout: publicProcedure.mutation(({ ctx }) => {
      const cookieOptions = getSessionCookieOptions(ctx.req);
      ctx.res.clearCookie(COOKIE_NAME, { ...cookieOptions, maxAge: -1 });
      return {
        success: true,
      } as const;
    }),
  }),

  // Quiz-related procedures
  quiz: router({
    // Get all categories
    getCategories: publicProcedure.query(async () => {
      return await getAllCategories();
    }),

    // Get quizzes by category
    getQuizzesByCategory: publicProcedure
      .input(z.object({ categoryId: z.number() }))
      .query(async ({ input }) => {
        return await getQuizzesByCategory(input.categoryId);
      }),

    // Save quiz result
    saveResult: protectedProcedure
      .input(z.object({
        categoryId: z.number(),
        totalQuestions: z.number(),
        correctAnswers: z.number(),
        score: z.string(),
        timeSpent: z.number(),
        answers: z.array(z.object({
          quizId: z.number(),
          selectedAnswer: z.number(),
          isCorrect: z.boolean(),
        })),
      }))
      .mutation(async ({ input, ctx }) => {
        if (!ctx.user) throw new Error("User not authenticated");
        
        return await saveQuizResult({
          userId: ctx.user.id,
          categoryId: input.categoryId,
          totalQuestions: input.totalQuestions,
          correctAnswers: input.correctAnswers,
          score: input.score as any,
          timeSpent: input.timeSpent,
          answers: input.answers,
        });
      }),

    // Get user stats
    getUserStats: protectedProcedure.query(async ({ ctx }) => {
      if (!ctx.user) throw new Error("User not authenticated");
      return await getUserStats(ctx.user.id);
    }),

    // Get leaderboard
    getLeaderboard: publicProcedure
      .input(z.object({ categoryId: z.number().optional(), limit: z.number().default(10) }))
      .query(async ({ input }) => {
        return await getLeaderboard(input.categoryId, input.limit);
      }),

    // Get user rank
    getUserRank: protectedProcedure
      .input(z.object({ categoryId: z.number().optional() }))
      .query(async ({ input, ctx }) => {
        if (!ctx.user) throw new Error("User not authenticated");
        return await getUserRank(ctx.user.id, input.categoryId);
      }),

    // Get quiz result history
    getResultHistory: protectedProcedure
      .input(z.object({ limit: z.number().default(10) }))
      .query(async ({ input, ctx }) => {
        if (!ctx.user) throw new Error("User not authenticated");
        return await getQuizResultHistory(ctx.user.id, input.limit);
      }),
  }),
});

export type AppRouter = typeof appRouter;
