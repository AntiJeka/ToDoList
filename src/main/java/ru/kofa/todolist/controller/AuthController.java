package ru.kofa.todolist.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.kofa.todolist.repository.UserRepository;
import ru.kofa.todolist.request.LoginRequest;
import ru.kofa.todolist.request.RegistrationRequest;
import ru.kofa.todolist.security.jwt.JwtUtils;
import ru.kofa.todolist.service.user.IUserService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final IUserService service;

    @GetMapping
    public String getLoginWeb() {
        return "auth";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute @Valid LoginRequest login,
                        Model model,
                        HttpServletResponse response,
                        RedirectAttributes redirectAttributes) {
        try {
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken
                            (login.getEmail(), login.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateTokenForUser(authentication);

            // Сохраняем JWT в cookie
            Cookie jwtCookie = new Cookie("jwt", jwt);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setSecure(false);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(24 * 60 * 60); // 24 часа
            response.addCookie(jwtCookie);

            return "redirect:/main";
        } catch (AuthenticationException e) {
            model.addAttribute("error", "Invalid email or password");
            return "auth";
        }
    }

    @PostMapping("/registration")
    public String registration(@ModelAttribute RegistrationRequest registration,
                               Model model,
                               HttpServletResponse response,
                               RedirectAttributes redirectAttributes) {
        if (!registration.getPassword().equals(registration.getConfirmPassword())) {
            model.addAttribute("error", "Password don't match");
            return "auth";
        }
        if (userRepository.existsByUsername(registration.getUsername())) {
            model.addAttribute("error", "Choose different name");
            return "auth";
        }
        if (userRepository.existsByEmail(registration.getEmail())) {
            model.addAttribute("error", "Choose different email");
            return "auth";
        }

        service.createUser(registration);

        try {
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken
                            (registration.getEmail(), registration.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateTokenForUser(authentication);

            // Сохраняем JWT в cookie
            Cookie jwtCookie = new Cookie("jwt", jwt);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setSecure(false);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(24 * 60 * 60); // 24 часа
            response.addCookie(jwtCookie);
            return "redirect:/main";
        } catch (AuthenticationException e) {
            model.addAttribute("error", "Authentication failed after registration");
            return "auth";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpServletResponse response) {
        // Очищаем Cookie
        Cookie jwtCookie = new Cookie("jwt", null);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(false);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0);
        response.addCookie(jwtCookie);

        SecurityContextHolder.clearContext();
        return "redirect:/auth";
    }
}