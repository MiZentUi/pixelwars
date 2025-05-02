package com.mizentui.pixelwars.controller;

import com.mizentui.pixelwars.model.Color;
import com.mizentui.pixelwars.model.User;
import com.mizentui.pixelwars.repository.ColorRepository;
import com.mizentui.pixelwars.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @Autowired
    private ColorRepository colorRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String index(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Iterable<Color> colors = colorRepository.findAll();
        model.addAttribute("colors", colors);
        if (authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            DefaultOidcUser user = (DefaultOidcUser) authentication.getPrincipal();
            model.addAttribute("authorized", "true");
            model.addAttribute("username", user.getAttribute("name"));
            model.addAttribute("picture_url", user.getPicture());
        } else {
            model.addAttribute("authorized", "");
            model.addAttribute("username", "Log in");
        }
        return "index";
    }

    @GetMapping("/admin")
    public String admin(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            DefaultOidcUser oidcUser = (DefaultOidcUser) authentication.getPrincipal();
            User user = userRepository.findByEmailAndName(oidcUser.getEmail(), oidcUser.getAttribute("name"));
            if (user != null) {
                if (user.isAdmin()) {
                    model.addAttribute("admin", "true");
                    model.addAttribute("users", userRepository.findAll());
                }
            }
        }
        return "admin";
    }

}
