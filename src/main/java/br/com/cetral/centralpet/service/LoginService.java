package br.com.cetral.centralpet.service;

import org.springframework.stereotype.Service;

@Service
public class LoginService {

    public String login(String username, String password) {
        if(username == null || password == null) {
            return "Username or password are null";
        }
        if(username.isEmpty() || password.isEmpty()) {
            return "Username or password are empty";
        }
        return "Usuário logado: " + username;
    }
}