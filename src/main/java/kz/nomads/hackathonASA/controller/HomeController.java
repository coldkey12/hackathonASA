package kz.nomads.hackathonASA.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kz.nomads.hackathonASA.model.User;
import kz.nomads.hackathonASA.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @GetMapping("/homePage")
    public String homePage(Model model, HttpServletRequest req) {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("currentUser");
        model.addAttribute("currentUser", user);
        return "homePage";
    }

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

    @GetMapping("/registerPage")
    public String registerPage() {
        return "registerPage";
    }

    @PostMapping("/register")
    public String register(User user, @RequestParam String confirmPassword) {
        if (user.getPassword().equals(confirmPassword) && !user.getUsername().isEmpty()) {
            userService.registerUser(user);
            return "redirect:/homePage";
        }
        return "registerPage";
    }
}
