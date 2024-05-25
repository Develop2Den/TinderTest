package web.classes;

public class User {
    private int id;
    private String name;
    private String photo_url;

    // Конструктор
    public User(int id, String name, String url) {
        this.id = id;
        this.name = name;
        this.photo_url = url;
    }

    // Геттеры и сеттеры
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String url) {
        this.photo_url = url;
    }
}
