package com.server.Dwenoeim.util.controller;

import com.server.Dwenoeim.domain.User;
import com.server.Dwenoeim.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class TestController {
    private final AuthService authService;

    @GetMapping("/testauth")
    public User test(Principal principal) {
        return authService.test(principal);
    }
}
