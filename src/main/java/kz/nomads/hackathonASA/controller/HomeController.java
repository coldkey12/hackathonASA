package kz.nomads.hackathonASA.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kz.nomads.hackathonASA.model.*;
import kz.nomads.hackathonASA.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class HomeController {

    @Value("${openai.api.url}")
    private String url;

    @Autowired
    AIResponseService aiResponseService;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private PromptService promptService;

    @Autowired
    private ChatService chatService;

    @Autowired
    private UserService userService;

    @Autowired
    private GroupChatService groupChatService;
    @Autowired
    private GroupPromptService groupPromptService;

    @GetMapping("/homePage")
    public String homePage(Model model, HttpServletRequest req) {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("currentUser");
        if (user != null) {
            List<Chat> chatsByUserId = chatService.chatsByUserId(user.getId());
            System.out.println(chatsByUserId.size());
            List<GroupChat> groupChats = groupChatService.getGroupChatByUserId(user.getId());
            model.addAttribute("groupChats", groupChats);
            model.addAttribute("chats", chatsByUserId);
        }
        model.addAttribute("currentUser", user);
        return "homePage";
    }

    @GetMapping("/loginPage")
    public String loginPage() {
        return "loginPage";
    }

    @PostMapping("/addChat")
    public String addChat(Chat chat, HttpServletRequest req) {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("currentUser");
        chat.setUserId(user.getId());
        chatService.addChat(chat);
        return "redirect:/homePage";
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
        if (user.getPassword().equals(confirmPassword) && !user.getUsername().isEmpty() && userService.isUnique(user.getUsername())) {
            userService.registerUser(user);
            return "redirect:/loginPage";
        }
        return "registerPage";
    }

    @GetMapping("/openChat/{chatId}")
    public String openChat(@PathVariable Long chatId, Model model, HttpServletRequest req) {
        User user = (User) req.getSession().getAttribute("currentUser");
        Chat chat = chatService.getChatById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group chat Id:" + chatId));
        List<Prompt> promptList = promptService.getPromptListByChatId(chatId);
        List<User> userList = userService.getAllUsers();
        List<AIResponse> aiResponseList = aiResponseService.getAIResponsesByChatId(chatId);

        // Create a map of user IDs to usernames
        Map<Long, String> userMap = userList.stream()
                .collect(Collectors.toMap(User::getId, User::getUsername));

        model.addAttribute("chat", chat);
        model.addAttribute("currentUser", user);
        model.addAttribute("userMap", userMap); // Add the user map to the model
        return "chatPage";
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

        String answer =  response.getChoices().get(0).getMessage().getContent();

        AIResponse aiResponse = AIResponse.builder()
                .text(answer)
                .chatId(prompt.getChatId())
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

    @GetMapping("/logout")
    public String logout(HttpServletRequest req) {
        HttpSession session = req.getSession();
        session.removeAttribute("currentUser");
        return "redirect:/loginPage";
    }

    @PostMapping("/createGroupChat")
    public String createGroupChat(GroupChat groupChat, HttpServletRequest req) {
        groupChatService.createGroupChat(groupChat);
        return "redirect:/homePage";
    }

    @GetMapping("/openGroupChat/{id}")
    public String getGroupChat(@PathVariable Long id, Model model, HttpServletRequest req) {
        User user = (User) req.getSession().getAttribute("currentUser");
        GroupChat groupChat = groupChatService.getGroupChatById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group chat Id:" + id));
        List<GroupPrompt> groupPromptList = groupPromptService.getGroupPromptListByChatId(id);
        List<User> userList = userService.getAllUsers();

        // Create a map of user IDs to usernames
        Map<Long, String> userMap = userList.stream()
                .collect(Collectors.toMap(User::getId, User::getUsername));

        model.addAttribute("groupChat", groupChat);
        model.addAttribute("groupPromptList", groupPromptList);
        model.addAttribute("currentUser", user);
        model.addAttribute("userMap", userMap); // Add the user map to the model
        return "groupChat";
    }


    @PostMapping("/addGroupPrompt")
    public String addGroupPrompt(GroupPrompt groupPrompt, HttpServletRequest req) {
        User user = (User) req.getSession().getAttribute("currentUser");
        groupPrompt.setUserId(user.getId());
        groupPromptService.addGroupPrompt(groupPrompt);
        return "redirect:/openGroupChat/" + groupPrompt.getChatId();
    }

    @PostMapping("/hitOpenAi")
    public String getOpenaiResponse(@RequestBody String prompt){
        ChatCompletionRequest chatCompletionRequest =
                new ChatCompletionRequest("gpt-3.5-turbo", prompt);

        ChatCompletionResponse response =
                restTemplate.postForObject(url, chatCompletionRequest,
                        ChatCompletionResponse.class);

        return response.getChoices().get(0).getMessage().getContent();
    }
}
