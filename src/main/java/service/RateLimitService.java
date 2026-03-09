package service;

import dao.RateLimitDAO;

import java.sql.Date;
import java.time.LocalDate;

public class RateLimitService {

    private static final int DAILY_LIMIT = 10;

    private final RateLimitDAO rateLimitDAO = new RateLimitDAO();

    public boolean canSendMessage(int userId) {
        Date today = Date.valueOf(LocalDate.now());

        int count = rateLimitDAO.getTodayCount(userId, today);

        if (count == 0) {
            rateLimitDAO.createUsageRow(userId, today);
            count = rateLimitDAO.getTodayCount(userId, today);
        }

        return count < DAILY_LIMIT;
    }

    public boolean recordUsage(int userId) {
        Date today = Date.valueOf(LocalDate.now());

        int count = rateLimitDAO.getTodayCount(userId, today);

        if (count == 0) {
            rateLimitDAO.createUsageRow(userId, today);
        }

        return rateLimitDAO.incrementTodayCount(userId, today);
    }

    public int getRemainingMessages(int userId) {
        Date today = Date.valueOf(LocalDate.now());

        int count = rateLimitDAO.getTodayCount(userId, today);

        if (count == 0) {
            return DAILY_LIMIT;
        }

        return Math.max(0, DAILY_LIMIT - count);
    }

    public int getDailyLimit() {
        return DAILY_LIMIT;
    }
}
