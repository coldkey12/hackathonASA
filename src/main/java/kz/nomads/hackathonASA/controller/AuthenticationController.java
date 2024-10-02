package kz.nomads.hackathonASA.controller;

import jakarta.servlet.http.HttpServletRequest;
import kz.nomads.hackathonASA.model.User;
import kz.nomads.hackathonASA.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthenticationController {

    @Autowired
    private UserService userService;

    @GetMapping("/loginPage")
    public String loginPage() {
        return "loginPage";
    }

    @PostMapping("/login")
    public String login(User user, HttpServletRequest req) {
        if (userService.login(user, req)) {
            return "redirect:/homePage";
        }
        return "loginPage";
    }
}
