package web.classes;

import java.sql.Timestamp;

public class Liked {
    private int id;
    private int userId;
    private int likedUserId;
    private Timestamp timestamp;

    public Liked(int id, int userId, int likedUserId, Timestamp timestamp) {
        this.id = id;
        this.userId = userId;
        this.likedUserId = likedUserId;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public int getLikedUserId() {
        return likedUserId;
    }

    public Timestamp getCreated_at() {
        return timestamp;
    }
}
