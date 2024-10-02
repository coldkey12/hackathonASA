package kz.nomads.hackathonASA.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kz.nomads.hackathonASA.model.*;
import kz.nomads.hackathonASA.service.AIResponseService;
import kz.nomads.hackathonASA.service.ChatService;
import kz.nomads.hackathonASA.service.PromptService;
import kz.nomads.hackathonASA.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ChatController {
    @Value("${openai.api.url}")
    private String url;

    @Autowired
    private ChatService chatService;

    @Autowired
    private UserService userService;

    @Autowired
    private PromptService promptService;

    @Autowired
    private AIResponseService aiResponseService;

    @Autowired
    RestTemplate restTemplate;

    @PostMapping("/addChat")
    public String addChat(Chat chat, HttpServletRequest req) {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("currentUser");
        chat.setUserId(user.getId());
        chatService.addChat(chat);
        return "redirect:/homePage";
    }

    @GetMapping("/openChat/{chatId}")
    public String openChat(@PathVariable Long chatId, Model model, HttpServletRequest req) {
        User user = (User) req.getSession().getAttribute("currentUser");

        if(user != null && chatService.getChatById(chatId).isPresent()) {
            Chat chat = chatService.getChatById(chatId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid group chat Id:" + chatId));
            List<Prompt> promptList = promptService.getPromptListByChatId(chatId);
            List<User> userList = userService.getAllUsers();
            List<AIResponse> aiResponseList = aiResponseService.getAIResponsesByChatId(chatId);

            // Create a map of user IDs to usernames
            Map<Long, String> userMap = userList.stream()
                    .collect(Collectors.toMap(User::getId, User::getUsername));

            model.addAttribute("promptList", promptList);
            model.addAttribute("aiResponseList", aiResponseList);
            model.addAttribute("chat", chat);
            model.addAttribute("currentUser", user);
            model.addAttribute("userMap", userMap); // Add the user map to the model
            return "chatPage";
        } else {
            return "redirect:/homePage";
        }
    }

    @PostMapping("/addPrompt")
    public String addPrompt(Prompt prompt, HttpServletRequest req) {
        User user = (User) req.getSession().getAttribute("currentUser");
        prompt.setUserId(user.getId());

        ChatCompletionRequest chatCompletionRequest =
                new ChatCompletionRequest("gpt-3.5-turbo", prompt.getText());

        ChatCompletionResponse response =
                restTemplate.postForObject(url, chatCompletionRequest,
                        ChatCompletionResponse.class);

        String answer = response.getChoices().get(0).getMessage().getContent();

        AIResponse aiResponse = AIResponse.builder()
                .text(answer)
                .chatId(prompt.getChatId())
                .promptText(prompt.getText())
                .build();

        aiResponseService.addResponse(aiResponse);
        promptService.addPrompt(prompt);
        return "redirect:/openChat/" + prompt.getChatId();
    }

    @PostMapping("/savePrompt/{chatId}")
    public String savePrompt(Prompt prompt, @PathVariable Long chatId) {
        prompt.setChatId(chatId);
        promptService.addPrompt(prompt);
        return "redirect:/openChat/{" + chatId + "}";
    }

    @PostMapping("/deleteChat")
    public String deleteChat(@RequestParam Long chatId, HttpServletRequest req) {
        User currentUser = (User) req.getSession().getAttribute("currentUser");
        if(chatService.getChatById(chatId).get().getUserId().equals(currentUser.getId())) {
            chatService.deleteChat(chatId);
            return "redirect:/homePage";
        }
        return "redirect:/homePage";
    }
}
