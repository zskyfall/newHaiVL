package com.nimkid.haivl;

/**
 * Created by thang on 1/24/17.
 */

public class Post {
    private String id, title, time, image_url, author_avatar, author_name, author_id;
    private int like, love, dislike;

    public Post() {
    }

    public Post(String id, String title, String time, String image_url, String author_avatar, String author_name, String author_id, int like, int love, int dislike) {
        this.id = id;
        this.title = title;
        this.time = time;
        this.image_url = image_url;
        this.author_avatar = author_avatar;
        this.author_name = author_name;
        this.author_id = author_id;
        this.like = like;
        this.love = love;
        this.dislike = dislike;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getTime() {
        return time;
    }

    public String getAuthor_avatar() {
        return author_avatar;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public String getAuthor_id() {
        return author_id;
    }

    public int getLike() {
        return like;
    }

    public int getLove() {
        return love;
    }

    public int getDislike() {
        return dislike;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public void setAuthor_avatar(String author_avatar) {
        this.author_avatar = author_avatar;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public void setAuthor_id(String author_id) {
        this.author_id = author_id;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public void setLove(int love) {
        this.love = love;
    }

    public void setDislike(int dislike) {
        this.dislike = dislike;
    }
}

