package com.example.userservice.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/home")
public class HomeController {

    @GetMapping
    public String home(Model model, @AuthenticationPrincipal OAuth2User oauthUser) {
        if (oauthUser != null) {
            model.addAttribute("name", oauthUser.getAttribute("name"));
        }
        return "home"; // Loads home.html from templates
    }
}
