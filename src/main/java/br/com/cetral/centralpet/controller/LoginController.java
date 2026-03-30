package br.com.cetral.centralpet.controller;

import br.com.cetral.centralpet.dto.LoginDto;
import br.com.cetral.centralpet.service.LoginService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginDto loginDto) {
        return loginService.login(loginDto.getUsername(),  loginDto.getPassword());
    }
}
