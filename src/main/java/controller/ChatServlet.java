package controller;

import dao.ChatDAO;
import dao.MessageDAO;
import service.AIService;
import service.RateLimitService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/chat")
public class ChatServlet extends HttpServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer userId = (Integer) request.getSession().getAttribute("userId");

        if (userId == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String prompt = request.getParameter("prompt");
        String chatIdParam = request.getParameter("chatId");

        if (prompt == null || prompt.trim().isEmpty()) {
            response.sendRedirect("dashboard");
            return;
        }

        prompt = prompt.trim();

        RateLimitService rateLimitService = new RateLimitService();

        if (!rateLimitService.canSendMessage(userId)) {
            response.sendRedirect("dashboard?error=Daily message limit reached");
            return;
        }

        ChatDAO chatDAO = new ChatDAO();
        MessageDAO messageDAO = new MessageDAO();

        int chatId;

        if (chatIdParam == null || chatIdParam.trim().isEmpty()) {
            String title = generateTitle(prompt);
            chatId = chatDAO.createChat(userId, title);
        } else {
            chatId = Integer.parseInt(chatIdParam);
            if (!chatDAO.belongsToUser(chatId, userId)) {
                response.sendRedirect("dashboard");
                return;
            }
        }

        if (chatId == -1) {
            response.sendRedirect("dashboard");
            return;
        }

        messageDAO.saveMessage(chatId, "USER", prompt);

        String aiReply = AIService.generateResponse(prompt);

        messageDAO.saveMessage(chatId, "AI", aiReply);

        rateLimitService.recordUsage(userId);

        response.sendRedirect("dashboard?chatId=" + chatId);
    }

    private String generateTitle(String prompt) {
        String cleaned = prompt.trim().replaceAll("\\s+", " ");
        if (cleaned.length() <= 40) {
            return cleaned;
        }
        return cleaned.substring(0, 40) + "...";
    }
}