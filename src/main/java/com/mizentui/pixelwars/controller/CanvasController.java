package com.mizentui.pixelwars.controller;

import com.mizentui.pixelwars.model.Pixel;
import com.mizentui.pixelwars.model.User;
import com.mizentui.pixelwars.repository.PixelRepository;
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
import java.util.*;

@RestController
public class CanvasController {

    @Autowired
    private PixelRepository pixelRepository;

    @Autowired
    private UserRepository userRepository;

    private String announce = "";

    @GetMapping("/canvas/get_pixels")
    public void get_pixels(HttpServletResponse response) throws IOException {
        List<Pixel> pixels = pixelRepository.getLastPixels();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean admin = false;
        if (authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            DefaultOidcUser oidcUser = (DefaultOidcUser) authentication.getPrincipal();
            User user = userRepository.findByEmailAndName(oidcUser.getEmail(), oidcUser.getAttribute("name"));
            if (user != null) {
                admin = user.isAdmin();
            }
        }
        response.setContentType("application/json");
        response.getWriter().print(getPixelsJsonArray(pixels, admin));
        response.setStatus(200);
    }

    private static JSONArray getPixelsJsonArray(List<Pixel> pixels, boolean admin) {
        JSONArray jsonArray = new JSONArray();
        for (Pixel pixel : pixels) {
            User author = pixel.getAuthor();
            if (!author.getStatus().contains("ban")) {
                JSONObject jsonPixel = new JSONObject();
                jsonPixel.put("x", pixel.getX().toString());
                jsonPixel.put("y", pixel.getY().toString());
                jsonPixel.put("color", pixel.getColor());
                if (admin) {
                    jsonPixel.put("author", author.toString());
                }
                jsonArray.put(jsonPixel);
            }
        }
        return jsonArray;
    }

    @PostMapping(path = "/canvas/set_pixel", consumes = "application/json", produces = "application/json")
    public void set_pixel(@RequestBody Map<String, String> data, HttpServletResponse response) throws IOException {
        long place_timeout = 3;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            DefaultOidcUser oidcUser = (DefaultOidcUser) authentication.getPrincipal();
            User author = userRepository.findByEmailAndName(oidcUser.getEmail(), oidcUser.getAttribute("name"));
            if (author == null) {
                author = new User(oidcUser.getEmail(), oidcUser.getAttribute("name"), oidcUser.getPicture(), oidcUser.getSubject(), "user");
                userRepository.save(author);
            }
            JSONObject json = new JSONObject();
            if (author.isAdmin() || author.getStatus().toLowerCase().contains("creator")) {
                place_timeout = 0;
            }
            json.put("timeout", place_timeout);
            response.getWriter().print(json);
            List<Pixel> pixels = author.getPixels();
            if (pixels != null) {
                pixels.sort(Comparator.comparing(Pixel::getTimestamp).reversed());
            }
            if (author.getStatus().toLowerCase().contains("ban")) {
                response.setStatus(403);
            } else if (pixels == null || (pixels.isEmpty() || pixels.getLast() != null && Math.abs(System.currentTimeMillis() - pixels.getLast().getTimestamp()) > place_timeout * 1000)) {
                pixelRepository.save(new Pixel(Integer.parseInt(data.get("x")), Integer.parseInt(data.get("y")), data.get("color"), System.currentTimeMillis(), author));
                response.setStatus(200);
            } else {
                response.setStatus(230);
            }
        } else {
            response.setStatus(401);
        }
    }

    @PostMapping(path = "/canvas/get_top_users", consumes = "application/json", produces = "application/json")
    public void get_top_users(@RequestBody Map<String, String> data, HttpServletResponse response) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean admin = false;
        if (authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            DefaultOidcUser oidcUser = (DefaultOidcUser) authentication.getPrincipal();
            User user = userRepository.findByEmailAndName(oidcUser.getEmail(), oidcUser.getAttribute("name"));
            if (user != null) {
                admin = user.isAdmin();
            }
        }
        JSONArray jsonArray = new JSONArray();
        List<User> topUsers = new ArrayList<>();
        userRepository.findAllById(pixelRepository.getTopAuthorsId(Long.parseLong(data.get("count")))).forEach(topUsers::add);
        topUsers.sort(Comparator.comparing(User::getPixelsCount).reversed());
        for (User user : topUsers) {
            JSONObject jsonUser = new JSONObject();
            jsonUser.put("name", user.getName());
            jsonUser.put("picture", user.getPicture());
            jsonUser.put("pixel_count", user.getPixelsCount());
            if (admin) {
                jsonUser.put("user_info", user.toString());
            }
            jsonArray.put(jsonUser);
        }
        response.setContentType("application/json");
        response.getWriter().print(jsonArray);
        response.setStatus(200);
    }

    @GetMapping("/canvas/get_pixels_count")
    public void get_pixels_count(HttpServletResponse response) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            DefaultOidcUser oidcUser = (DefaultOidcUser) authentication.getPrincipal();
            response.setContentType("application/json");
            User user = userRepository.findByEmailAndName(oidcUser.getEmail(), oidcUser.getAttribute("name"));
            if (user != null) {
                response.getWriter().print("{\"count\": " + user.getPixelsCount() + '}');
            } else {
                response.getWriter().print("{\"count\": 0}");
            }
            response.setStatus(200);
        }
    }

    @GetMapping("/canvas/get_announcement")
    public void get_announce(HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.getWriter().print("{\"announcement\": \"" + announce + "\"}");
        response.setStatus(200);
    }

    @PostMapping(path = "/canvas/set_announcement", consumes = "application/json", produces = "application/json")
    public void set_announce(@RequestBody Map<String, String> data, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            DefaultOidcUser oidcUser = (DefaultOidcUser) authentication.getPrincipal();
            User user = userRepository.findByEmailAndName(oidcUser.getEmail(), oidcUser.getAttribute("name"));
            if (user != null && user.isAdmin()) {
                announce = data.get("announcement");
                response.setStatus(200);
            }
            else {
                response.setStatus(403);
            }
        }
    }

}