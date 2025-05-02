package com.mizentui.pixelwars.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String email;
    private String name;
    private String picture;
    private String subject;
    private String status;

    @OneToMany(mappedBy="author")
    private List<Pixel> pixels;

    public User() {}

    public User(String email, String name, String picture, String sub, String status) {
        this.email = email;
        this.name = name;
        this.picture = picture;
        this.subject = sub;
        this.status = status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPicture() {
        return picture;
    }

    public String getSubject() {
        return subject;
    }

    public String getStatus() {
        return status;
    }

    public List<Pixel> getPixels() {
        return pixels;
    }

    public int getPixelsCount() {
        if (pixels == null) {
            return 0;
        }
        return pixels.size();
    }

    public boolean isAdmin() {
        return status.equals("admin") || status.equals("owner");
    }

    @Override
    public String toString() {
        return "User {" +
                 "\n    id=" + id + ", " +
                 "\n    email='" + email + "', " +
                 "\n    name='" + name + "', " +
                 "\n    subject='" + subject + "', " +
                 "\n    status='" + status + '\'' +
                 "\n    pixels_count=" + getPixelsCount() + ", " +
                 "\n}";
    }
}
