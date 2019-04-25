package com.smartflyer;

public class user {
    private String name;
    private String _id;
    private String email;
    private Integer leaderboard_count;

    public user(String name, String _id, String email, Integer leaderboard_count) {
        this.name = name;
        this._id = _id;
        this.email = email;
        this.leaderboard_count = leaderboard_count;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setLeaderboard_count(Integer leaderboard_count) {
        this.leaderboard_count = leaderboard_count;
    }

    public String getName() {
        return name;
    }

    public String get_id() {
        return _id;
    }

    public String getEmail() {
        return email;
    }

    public Integer getLeaderboard_count() {
        return leaderboard_count;
    }
}
