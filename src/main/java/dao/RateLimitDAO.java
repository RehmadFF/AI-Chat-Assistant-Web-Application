package dao;

import util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RateLimitDAO {

    public int getTodayCount(int userId, Date usageDate) {
        String sql = "SELECT request_count FROM daily_usage WHERE user_id = ? AND usage_date = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setDate(2, usageDate);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("request_count");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public boolean createUsageRow(int userId, Date usageDate) {
        String sql = "INSERT INTO daily_usage(user_id, usage_date, request_count) VALUES(?, ?, 0)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setDate(2, usageDate);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean incrementTodayCount(int userId, Date usageDate) {
        String sql = "UPDATE daily_usage SET request_count = request_count + 1 WHERE user_id = ? AND usage_date = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setDate(2, usageDate);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
