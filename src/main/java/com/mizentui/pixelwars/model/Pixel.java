package com.mizentui.pixelwars.model;

import jakarta.persistence.*;

@Entity
public class Pixel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Integer x;
    private Integer y;
    private String color;
    private Long timestamp;

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User author;

    public Pixel() {}

    public Pixel(Integer x, Integer y, String color, Long timestamp, User author) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.timestamp = timestamp;
        this.author = author;
    }

    public Integer getX() {
        return x;
    }

    public Integer getY() {
        return y;
    }

    public String getColor() {
        return color;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public User getAuthor() {
        return author;
    }

    @Override
    public String toString() {
        return "Pixel{" +
                "id=" + id +
                ", x=" + x +
                ", y=" + y +
                ", color='" + color + '\'' +
                ", timestamp=" + timestamp +
                ", author='" + author + '\'' +
                '}';
    }
}
