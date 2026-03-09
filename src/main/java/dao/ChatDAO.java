package dao;

import model.Chat;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChatDAO {

    public int createChat(int userId, String title) {
        String sql = "INSERT INTO chats(user_id, title) VALUES(?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, userId);
            ps.setString(2, title);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    public List<Chat> getChatsByUserId(int userId) {
        List<Chat> chats = new ArrayList<>();
        String sql = "SELECT * FROM chats WHERE user_id = ? ORDER BY updated_at DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Chat chat = new Chat();
                chat.setId(rs.getInt("id"));
                chat.setUserId(rs.getInt("user_id"));
                chat.setTitle(rs.getString("title"));
                chat.setCreatedAt(rs.getTimestamp("created_at"));
                chat.setUpdatedAt(rs.getTimestamp("updated_at"));
                chats.add(chat);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return chats;
    }

    public Chat getChatById(int chatId) {
        String sql = "SELECT * FROM chats WHERE id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, chatId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Chat chat = new Chat();
                chat.setId(rs.getInt("id"));
                chat.setUserId(rs.getInt("user_id"));
                chat.setTitle(rs.getString("title"));
                chat.setCreatedAt(rs.getTimestamp("created_at"));
                chat.setUpdatedAt(rs.getTimestamp("updated_at"));
                return chat;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean belongsToUser(int chatId, int userId) {
        String sql = "SELECT id FROM chats WHERE id = ? AND user_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, chatId);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}