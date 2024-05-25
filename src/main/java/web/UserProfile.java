package web;

public class UserProfile {
    private int id;
    private String name;
    private String photoUrl;

    public UserProfile(int id, String name, String photoUrl) {
        this.id = id;
        this.name = name;
        this.photoUrl = photoUrl;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }


    public String getPhotoUrl() {
        return photoUrl;
    }
}
