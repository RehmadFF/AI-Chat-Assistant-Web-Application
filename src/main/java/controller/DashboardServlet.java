package controller;

import dao.ChatDAO;
import dao.MessageDAO;
import model.Chat;
import model.Message;
import service.RateLimitService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer userId = (Integer) request.getSession().getAttribute("userId");

        if (userId == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        ChatDAO chatDAO = new ChatDAO();
        MessageDAO messageDAO = new MessageDAO();
        RateLimitService rateLimitService = new RateLimitService();

        List<Chat> chatList = chatDAO.getChatsByUserId(userId);
        List<Message> messages = new ArrayList<>();
        Chat selectedChat = null;

        String chatIdParam = request.getParameter("chatId");
        if (chatIdParam != null && !chatIdParam.trim().isEmpty()) {
            try {
                int chatId = Integer.parseInt(chatIdParam);
                if (chatDAO.belongsToUser(chatId, userId)) {
                    selectedChat = chatDAO.getChatById(chatId);
                    messages = messageDAO.getMessagesByChatId(chatId);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        request.setAttribute("chatList", chatList);
        request.setAttribute("selectedChat", selectedChat);
        request.setAttribute("messages", messages);
        request.setAttribute("remainingMessages", rateLimitService.getRemainingMessages(userId));
        request.setAttribute("dailyLimit", rateLimitService.getDailyLimit());
        request.setAttribute("error", request.getParameter("error"));

        request.getRequestDispatcher("dashboard.jsp").forward(request, response);
    }
}
