package it.uniroma3.siw.controller;

public class CurrentUserDTO {
    private String username;

    public CurrentUserDTO(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
