package web.servlets;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import web.UsersSQL;
import web.classes.AuthService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

public class LoginServlet extends HttpServlet {

    private static final String HARDCODED_PASSWORD = "12345";
    private Configuration cfg;
    private UsersSQL userProfileDAO;

    public LoginServlet(UsersSQL userProfileDAO, Configuration cfg) {
        this.cfg = cfg;
        this.userProfileDAO = userProfileDAO;
    }

        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Проверяем, есть ли атрибут сессии "username"
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("username") != null) {
            // Если пользователь уже аутентифицирован, перенаправляем на домашнюю страницу
            response.sendRedirect(request.getContextPath() + "/users");
        } else {
            // Если пользователь не аутентифицирован, отображаем страницу входа
            Template template = cfg.getTemplate("login.html");
            try {
                template.process(null, response.getWriter());
            } catch (TemplateException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Получаем логин и пароль из запроса
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // Проверяем, совпадает ли введенный пароль с захардкоженным паролем
        if (password.equals(HARDCODED_PASSWORD)) {
            // Успешная аутентификация, устанавливаем атрибут сессии
            HttpSession session = request.getSession();
            session.setAttribute("username", username);
            int userId = 0;
            try {
                userId = UsersSQL.getUserIdByUsername(username);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            // Успешная аутентификация, устанавливаем атрибут сессии с ID пользователя
            session.setAttribute("userId", userId);

            // Перенаправляем на домашнюю страницу
            response.sendRedirect(request.getContextPath() + "/users");
        } else {
            // Неудачная аутентификация, перенаправляем на страницу входа с сообщением об ошибке
            response.sendRedirect(request.getContextPath() + "/login?error=1");
        }
    }
}
