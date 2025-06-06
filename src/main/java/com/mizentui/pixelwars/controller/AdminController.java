package com.mizentui.pixelwars.controller;

import com.mizentui.pixelwars.model.User;
import com.mizentui.pixelwars.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@RestController
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/admin/get_users")
    public void get_users(HttpServletResponse response) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            DefaultOidcUser oidcUser = (DefaultOidcUser) authentication.getPrincipal();
            User user = userRepository.findByEmailAndName(oidcUser.getEmail(), oidcUser.getAttribute("name"));
            if (user != null) {
                if (user.isAdmin()) {
                    Iterable<User> users = userRepository.findAll();
                    response.setContentType("application/json");
                    response.getWriter().print(getUsersJsonArray(users));
                    response.setStatus(200);
                }
            }
        } else {
            response.setStatus(403);
        }
    }

    private static JSONArray getUsersJsonArray(Iterable<User> users) {
        JSONArray jsonArray = new JSONArray();
        for (User currrentUser : users) {
            JSONObject jsonUser = new JSONObject();
            jsonUser.put("id", currrentUser.getId());
            jsonUser.put("name", currrentUser.getName());
            jsonUser.put("email", currrentUser.getEmail());
            jsonUser.put("subject", currrentUser.getSubject());
            jsonUser.put("picture", currrentUser.getPicture());
            jsonUser.put("status", currrentUser.getStatus());
            jsonUser.put("pixels_count", currrentUser.getPixelsCount());
            jsonArray.put(jsonUser);
        }
        return jsonArray;
    }

    @PostMapping(path = "/admin/set_user_status", consumes = "application/json", produces = "application/json")
    public void set_user_status(@RequestBody Map<String, String> data, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            DefaultOidcUser oidcUser = (DefaultOidcUser) authentication.getPrincipal();
            User user = userRepository.findByEmailAndName(oidcUser.getEmail(), oidcUser.getAttribute("name"));
            if (user != null) {
                if (user.isAdmin()) {
                    Optional<User> optionalUser = userRepository.findById(Long.parseLong(data.get("id")));
                    if (optionalUser.isPresent() && !optionalUser.get().getStatus().equals("owner")) {
                        User changeableUser = optionalUser.get();
                        changeableUser.setStatus(data.get("status"));
                        userRepository.save(changeableUser);
                    }
                    response.setStatus(200);
                }
            }
        }
    }

}
