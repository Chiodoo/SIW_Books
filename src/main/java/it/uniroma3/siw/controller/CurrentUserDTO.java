package it.uniroma3.siw.controller;

public class CurrentUserDTO {

    private String username;
    private boolean isOauth;
    private boolean isAdmin;
    private Long userId;

    public CurrentUserDTO(String username, boolean isOauth, boolean isAdmin, Long userId) {
        this.username = username;
        this.isOauth = isOauth;
        this.isAdmin = isAdmin;
        this.userId = userId;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public String getUsername() {
        return username;
    }

    public boolean isOauth() {
        return isOauth;
    }

    public Long getUserId() {
        return userId;
    }
}
