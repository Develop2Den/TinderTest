package web;

import web.classes.Liked;
import web.classes.Message;
import web.dao.DAO;
import web.utils.ConnectionManager;

import javax.servlet.http.HttpSession;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsersSQL implements DAO<UserProfile> {

    private List<UserProfile> likedProfiles;

    public UsersSQL() {
        this.likedProfiles = new ArrayList<>();
    }
// Методы для юзера
    @Override
    public List<UserProfile> getAll() throws SQLException {
        List<UserProfile> users = new ArrayList<>();
        String sql = "SELECT id, name, photo_url FROM users";

        try (Connection conn = ConnectionManager.open();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String photoUrl = rs.getString("photo_url");
                users.add(new UserProfile(id, name, photoUrl));
            }
        }
        return users;
    }

    @Override
    public UserProfile getById(int id) throws SQLException {
        String sql = "SELECT id, name, photo_url FROM users WHERE id = ?";
        try (Connection conn = ConnectionManager.open();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("name");
                    String photoUrl = rs.getString("photo_url");
                    return new UserProfile(id, name, photoUrl);
                }
            }
        }
        return null;
    }

    @Override
    public void save(UserProfile user) throws SQLException {
        String sql = "INSERT INTO users (name, photo_url) VALUES (?, ?)";
        try (Connection conn = ConnectionManager.open();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getPhotoUrl());
            stmt.executeUpdate();
        }
    }

    @Override
    public void update(UserProfile user) throws SQLException {
        String sql = "UPDATE users SET name = ?, photo_url = ? WHERE id = ?";
        try (Connection conn = ConnectionManager.open();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getPhotoUrl());
            stmt.setInt(3, user.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = ConnectionManager.open();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    // Метод для лайка профиля
    public void likeProfile(int userId1, int userId2) throws SQLException {

        String sql = "INSERT INTO likes (user_id1, user_id2) VALUES (?, ?)";
        try (Connection conn = ConnectionManager.open();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId1);
            stmt.setInt(2, userId2);
            stmt.executeUpdate();
        }
    }

    // Метод для получения всех профилей, лайкнутых конкретным пользователем
    public List<UserProfile> getLikedProfiles(int userId) throws SQLException {
        List<UserProfile> likedProfiles = new ArrayList<>();
        String sql = "SELECT u.id, u.name, u.photo_url " +
                "FROM users u " +
                "JOIN likes l ON u.id = l.user_id2 " +
                "WHERE l.user_id1 = ?";

        try (Connection conn = ConnectionManager.open();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    String photoUrl = rs.getString("photo_url");
                    likedProfiles.add(new UserProfile(id, name, photoUrl));
                }
            }
        }
        return likedProfiles;
    }

    // Метод для получения всех лайков
    public List<Liked> getAllLikes() throws SQLException {
        List<Liked> likes = new ArrayList<>();
        String sql = "SELECT id, user_id1, user_id2, timestamp FROM likes";
        try (Connection conn = ConnectionManager.open();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                int userId1 = rs.getInt("user_id1");
                int userId2 = rs.getInt("user_id2");
                Timestamp timestamp = rs.getTimestamp("created_at");
                likes.add(new Liked(id, userId1, userId2, timestamp));
            }
        }
        return likes;
    }
    // Метод для получения id юзера по имени
    public static int getUserIdByUsername(String username) throws SQLException {
        int userId = -1;

        try (Connection connection  = ConnectionManager.open()) {
            String sql = "SELECT id FROM users WHERE name = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, username);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        userId = resultSet.getInt("id");
                    }
                }
            }
        }
        return userId;
    }
//Метод получения всех сообщений
public List<Message> getMessages(int userId1, int userId2) throws SQLException {
    List<Message> messages = new ArrayList<>();
    String sql = "SELECT m.id, m.sender_id, m.receiver_id, m.content, m.timestamp, u.name AS sender_username " +
            "FROM massages m " +
            "JOIN users u ON m.sender_id = u.id " +
            "WHERE (m.sender_id = ? AND m.receiver_id = ?) OR (m.sender_id = ? AND m.receiver_id = ?) " +
            "ORDER BY m.timestamp";

    try (Connection conn = ConnectionManager.open();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, userId1);
        stmt.setInt(2, userId2);
        stmt.setInt(3, userId2);
        stmt.setInt(4, userId1);
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                int senderId = rs.getInt("sender_id");
                int receiverId = rs.getInt("receiver_id");
                String content = rs.getString("content");
                Timestamp timestamp = rs.getTimestamp("timestamp");
                String senderUsername = rs.getString("sender_username");
                messages.add(new Message(id, senderId, receiverId, content, timestamp, senderUsername));
            }
        }
    }
    return messages;
}

    public void saveMessage(int senderId, int receiverId, String content) throws SQLException {
        String sql = "INSERT INTO massages (sender_id, receiver_id, content) VALUES (?, ?, ?)";
        try (Connection conn = ConnectionManager.open();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, senderId);
            stmt.setInt(2, receiverId);
            stmt.setString(3, content);
            stmt.executeUpdate();
        }
    }

    public int getCurrentUserIdFromSession(HttpSession session) {
        // Получаем атрибут сессии с ключом "userId"
        Object userIdAttribute = session.getAttribute("userId");

        // Проверяем, что атрибут существует и является целым числом
        if (userIdAttribute instanceof Integer) {
            return (int) userIdAttribute;
        } else {
            // Если атрибут отсутствует или не является целым числом, возвращаем -1 или другое значение по умолчанию
            return -1;
        }
    }
}
