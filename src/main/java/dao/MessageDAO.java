package dao;

import model.Message;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {

    public boolean saveMessage(int chatId, String sender, String messageText) {
        String sql = "INSERT INTO messages(chat_id, sender, message_text) VALUES(?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, chatId);
            ps.setString(2, sender);
            ps.setString(3, messageText);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<Message> getMessagesByChatId(int chatId) {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM messages WHERE chat_id = ? ORDER BY created_at ASC, id ASC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, chatId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Message message = new Message();
                message.setId(rs.getInt("id"));
                message.setChatId(rs.getInt("chat_id"));
                message.setSender(rs.getString("sender"));
                message.setMessageText(rs.getString("message_text"));
                message.setCreatedAt(rs.getTimestamp("created_at"));
                messages.add(message);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return messages;
    }
}