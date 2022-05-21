package ru.gb.dto;

public class RegRequest implements BasicRequest{

    private String login;
    private String password;
    private String name;
    private String surname;
    @Override
    public String getType() {
        return "registration";
    }

    public RegRequest(String login, String password, String name, String surname) {
        this.login = login;
        this.password = password;
        this.name = name;
        this.surname = surname;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }
}
