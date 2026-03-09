package controller;

import dao.UserDAO;
import model.User;
import service.AuthService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirm = request.getParameter("confirmPassword");

        if(email == null || password == null || confirm == null) {
            response.sendRedirect("register.jsp?error=Invalid input");
            return;
        }

        if(!password.equals(confirm)) {
            response.sendRedirect("register.jsp?error=Passwords do not match");
            return;
        }

        UserDAO userDAO = new UserDAO();

        if(userDAO.emailExists(email)) {
            response.sendRedirect("register.jsp?error=Email already registered");
            return;
        }

        String hashed = AuthService.hashPassword(password);

        User user = new User(email, hashed);

        boolean created = userDAO.createUser(user);

        if(created) {
            response.sendRedirect("login.jsp?success=Account created successfully");
        } else {
            response.sendRedirect("register.jsp?error=Registration failed");
        }
    }
}