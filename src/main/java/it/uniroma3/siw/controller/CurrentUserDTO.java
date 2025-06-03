package it.uniroma3.siw.controller;

public class CurrentUserDTO {
    private String username;
    private boolean isAdmin;

    public CurrentUserDTO(String username, boolean isAdmin) {
        this.username = username;
        this.isAdmin = isAdmin;
    }

    public String getUsername() {
        return username;
    }

    public boolean isAdmin() {
        return isAdmin;
    }
}
