package it.uniroma3.siw.controller;

public class CurrentUserDTO {
    private String username;
    boolean isOauth;
    private Long userId;

    public CurrentUserDTO(String username, boolean isOauth, Long userId) {
        this.username = username;
        this.isOauth = isOauth;
        this.userId = userId;
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
