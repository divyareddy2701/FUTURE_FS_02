package com.divya.minicrm.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * There's no /login endpoint here on purpose: this API uses HTTP Basic Auth,
 * so the browser sends credentials with every request. This endpoint just lets
 * the frontend confirm "yes, those credentials are valid" right after the user
 * types them in, before showing the dashboard.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @GetMapping("/me")
    public Map<String, String> me(Authentication authentication) {
        return Map.of("username", authentication.getName());
    }
}
