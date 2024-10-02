package kz.nomads.hackathonASA.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kz.nomads.hackathonASA.model.*;
import kz.nomads.hackathonASA.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {
    @Value("${openai.api.url}")
    private String url;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private ChatService chatService;

    @Autowired
    private GroupChatService groupChatService;

    @GetMapping("/homePage")
    public String homePage(Model model, HttpServletRequest req) {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("currentUser");

        if (user != null) {
            List<Chat> chatsByUserId = chatService.chatsByUserId(user.getId());
            System.out.println(chatsByUserId.size());

            List<GroupChat> groupChats = groupChatService.findAll();

            List<GroupChat> nonOwnerGroups = new ArrayList<>();

            System.out.println(nonOwnerGroups.size() + " SIZE OF NON OWNER");

            for (GroupChat gc : groupChats) {
                if (gc.getUsers().contains(user) && gc.getOwnerId() != user.getId()) {
                    nonOwnerGroups.add(gc);
                }
            }

            System.out.println(nonOwnerGroups.size() + " SIZE OF NON OWNER");

            model.addAttribute("groupChats", groupChats);
            model.addAttribute("nonOwnerGroups", nonOwnerGroups); // Pass non-owner groups to the model
            model.addAttribute("chats", chatsByUserId);
            System.out.println(nonOwnerGroups.size() + " size of non owner groups");
        }

        model.addAttribute("currentUser", user);
        return "homePage";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest req) {
        HttpSession session = req.getSession();
        session.removeAttribute("currentUser");
        return "redirect:/loginPage";
    }

    @PostMapping("/hitOpenAi")
    public String getOpenaiResponse(@RequestBody String prompt) {
        ChatCompletionRequest chatCompletionRequest =
                new ChatCompletionRequest("gpt-3.5-turbo", prompt);

        ChatCompletionResponse response =
                restTemplate.postForObject(url, chatCompletionRequest,
                        ChatCompletionResponse.class);

        return response.getChoices().get(0).getMessage().getContent();
    }

}
