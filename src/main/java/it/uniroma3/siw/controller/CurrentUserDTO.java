package it.uniroma3.siw.controller;

public class CurrentUserDTO {
    private String username;
    boolean isOauth;

    public CurrentUserDTO(String username, boolean isOauth) {
        this.username = username;
        this.isOauth = isOauth;
    }

    public String getUsername() {
        return username;
    }

    public boolean isOauth() {
        return isOauth;
    }
}
