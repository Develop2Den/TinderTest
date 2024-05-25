package web.servlets;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import web.UserProfile;
import web.UsersSQL;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LikedProfilesServlet extends HttpServlet {

    private UsersSQL userProfileDAO;
    private Configuration cfg;

    public LikedProfilesServlet(UsersSQL userProfileDAO, Configuration cfg) {
        this.userProfileDAO = userProfileDAO;
        this.cfg = cfg;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String username = (String) session.getAttribute("username");
        int currentUserId;
        try {
            currentUserId = userProfileDAO.getUserIdByUsername(username);
            if (currentUserId == -1) {
                throw new ServletException("User not found");
            }
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }

        List<UserProfile> likedProfiles;
        try {
            likedProfiles = userProfileDAO.getLikedProfiles(currentUserId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (likedProfiles == null) {
            likedProfiles = new ArrayList<>();
        }

        try (InputStream resourceStream = getClass().getClassLoader().getResourceAsStream("templates/like-page.html")) {
            if (resourceStream == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            String likedPageHtml = new String(resourceStream.readAllBytes());

            StringBuilder profilesHtml = new StringBuilder();
            for (UserProfile profile : likedProfiles) {
                profilesHtml.append("<div class=\"col-md-4\">");
                profilesHtml.append("<div class=\"card mb-4 shadow-sm\">");
                profilesHtml.append("<img class=\"card-img-top\" src=\"").append(profile.getPhotoUrl()).append("\" alt=\"Profile Photo\">");
                profilesHtml.append("<div class=\"card-body\">");
                profilesHtml.append("<h5 class=\"card-title\">").append(profile.getName()).append("</h5>");
                profilesHtml.append("<a href=\"/messages/").append(profile.getId()).append("\">Chat</a>");
                profilesHtml.append("</div>");
                profilesHtml.append("</div>");
                profilesHtml.append("</div>");
            }

            likedPageHtml = likedPageHtml.replace("<!-- PLACEHOLDER FOR LIKED PROFILES -->", profilesHtml.toString());

            resp.setContentType("text/html; charset=UTF-8");
            resp.getWriter().write(likedPageHtml);
        }
    }
}
