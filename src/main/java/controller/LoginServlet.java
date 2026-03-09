package controller;

import dao.UserDAO;
import model.User;
import service.AuthService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        UserDAO userDAO = new UserDAO();

        User user = userDAO.findByEmail(email);

        if(user == null) {
            response.sendRedirect("login.jsp?error=Invalid credentials");
            return;
        }

        boolean valid = AuthService.verifyPassword(password, user.getPasswordHash());

        if(!valid) {
            response.sendRedirect("login.jsp?error=Invalid credentials");
            return;
        }

        HttpSession session = request.getSession();

        session.setAttribute("userId", user.getId());
        session.setAttribute("userEmail", user.getEmail());

        response.sendRedirect("dashboard.jsp");
    }
}