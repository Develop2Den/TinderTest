package web.servlets;

import web.UserProfile;
import web.UsersSQL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsersServlet extends HttpServlet {

    private UsersSQL userProfileDAO;

    public UsersServlet(UsersSQL userProfileDAO) {
        this.userProfileDAO = userProfileDAO;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();

        List<UserProfile> likedProfiles = (List<UserProfile>) session.getAttribute("likedProfiles");
        if (likedProfiles == null) {
            likedProfiles = new ArrayList<>();
            session.setAttribute("likedProfiles", likedProfiles);
        }

        Integer profileIndex = (Integer) session.getAttribute("profileIndex");
        if (profileIndex == null) {
            profileIndex = 0;
        }

        List<UserProfile> allProfiles;
        try {
            allProfiles = userProfileDAO.getAll();
        } catch (SQLException e) {
            throw new ServletException("Unable to retrieve profiles", e);
        }

        if (profileIndex >= allProfiles.size()) {
            resp.sendRedirect("/liked");
            return;
        }

        UserProfile profile = allProfiles.get(profileIndex);

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("templates/people-list.html");
        if (inputStream == null) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        // Замена placeholders в HTML файле данными профиля
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
             PrintWriter w = resp.getWriter()) {
            br.lines().forEach(line -> {
                line = line.replace("{name}", profile.getName());
                line = line.replace("{photoUrl}", profile.getPhotoUrl());
                w.write(line);
            });
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        List<UserProfile> likedProfiles = (List<UserProfile>) session.getAttribute("likedProfiles");
        if (likedProfiles == null) {
            likedProfiles = new ArrayList<>();
            session.setAttribute("likedProfiles", likedProfiles);
        }

        Integer profileIndex = (Integer) session.getAttribute("profileIndex");
        if (profileIndex == null) {
            profileIndex = 0;
        }

        List<UserProfile> allProfiles;
        try {
            allProfiles = userProfileDAO.getAll();
        } catch (SQLException e) {
            throw new ServletException("Unable to retrieve profiles", e);
        }

        UserProfile currentProfile = allProfiles.get(profileIndex);

        String choice = req.getParameter("choice");
        if ("Yes".equalsIgnoreCase(choice)) {
            likedProfiles.add(currentProfile);
            int likedProfileId = currentProfile.getId();

            // Получаем текущего пользователя из сессии
            Integer currentUserId = (Integer) session.getAttribute("userId");
            if (currentUserId != null) {
                try {
                    userProfileDAO.likeProfile(currentUserId, currentProfile.getId());
                } catch (SQLException e) {
                    throw new ServletException("Unable to like profile", e);
                }
            }
        }

        profileIndex++;
        session.setAttribute("profileIndex", profileIndex);

        if (profileIndex >= allProfiles.size()) {
            resp.sendRedirect("/liked");
        } else {
            resp.sendRedirect("/users");
        }
    }
}
