package jp.co.monocrea.user.entity;

public class User {
    public Long id;
    public String name;

    public User() {}

    public User(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
