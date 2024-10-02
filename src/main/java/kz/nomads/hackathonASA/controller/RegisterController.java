package kz.nomads.hackathonASA.controller;

import kz.nomads.hackathonASA.model.User;
import kz.nomads.hackathonASA.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegisterController {

    @Autowired
    private UserService userService;

    @GetMapping("/registerPage")
    public String registerPage() {
        return "registerPage";
    }

    @PostMapping("/register")
    public String register(User user, @RequestParam String confirmPassword) {
        if (user.getPassword().equals(confirmPassword) && !user.getUsername().isEmpty() && userService.isUnique(user.getUsername())) {
            userService.registerUser(user);
            return "redirect:/loginPage";
        }
        return "registerPage";
    }
}
