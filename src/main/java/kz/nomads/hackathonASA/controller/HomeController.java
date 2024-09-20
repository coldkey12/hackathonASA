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
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Value("${openai.api.url}")
    private String url;

    @Autowired
    AIResponseService aiResponseService;

    @Autowired
    AIResponseGroupService aiResponseGroupService;

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


    //////////////////////////////////////


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

        model.addAttribute("promptList", promptList);
        model.addAttribute("aiResponseList", aiResponseList);
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

    ////////////////////////////////////////////

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

    @GetMapping("/openGroupChat/{chatId}")
    public String getGroupChat(@PathVariable Long chatId, Model model, HttpServletRequest req) {
        User user = (User) req.getSession().getAttribute("currentUser");
        List<GroupChat> groupChatList = groupChatService.findAll();
        GroupChat groupChat = groupChatService.getGroupChatById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group chat Id:" + chatId));

        List<GroupPrompt> groupPromptList = groupPromptService.getGroupPromptListByChatId(chatId);

        List<User> groupChatUsers = groupChat.getUsers(); // Ensure this method exists in GroupChat

        Map<Long, String> userMap = groupChatUsers.stream()
                .collect(Collectors.toMap(User::getId, User::getUsername));

        List<AIResponseGroup> aiResponseList = aiResponseGroupService.getAIResponsesByChatId(chatId);

        List<User> allUsers = userService.getAllUsers();

        model.addAttribute("allUsers", allUsers);
        model.addAttribute("allGroups", groupChatList);
        model.addAttribute("aiResponseList", aiResponseList);
        model.addAttribute("groupChat", groupChat);
        model.addAttribute("groupPromptList", groupPromptList);
        model.addAttribute("currentUser", user);
        model.addAttribute("userMap", userMap); // Add the filtered user map to the model
        return "groupChat";
    }


    @PostMapping("/addGroupPrompt")
    public String addGroupPrompt(GroupPrompt groupPrompt, HttpServletRequest req,@RequestParam String aiStatus) {
        User user = (User) req.getSession().getAttribute("currentUser");
        groupPrompt.setUserId(user.getId());

        if (aiStatus.equals("ON")) {
            ChatCompletionRequest chatCompletionRequest =
                    new ChatCompletionRequest("gpt-3.5-turbo", groupPrompt.getText());

            ChatCompletionResponse response =
                    restTemplate.postForObject(url, chatCompletionRequest,
                            ChatCompletionResponse.class);

            String answer = response.getChoices().get(0).getMessage().getContent();

            AIResponseGroup aiResponse = AIResponseGroup.builder()
                    .text(answer)
                    .chatId(groupPrompt.getChatId())
                    .promptText(groupPrompt.getText())
                    .build();

            aiResponseGroupService.addResponse(aiResponse);
        }

        groupPromptService.addGroupPrompt(groupPrompt);
        return "redirect:/openGroupChat/" + groupPrompt.getChatId();
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

    //////////////////////////////////////////////////////////////////

    @PostMapping("/addUserToGroup")
    public String addUserToGroup(@RequestParam String username, @RequestParam Long groupId, HttpServletRequest req, Model model) {
        User user = userService.getUserByUsername(username);
        model.addAttribute("currentUser", user);
        User currentUser = (User) req.getSession().getAttribute("currentUser");

        if (user == null) {
            req.setAttribute("error", "User not found.");
            return "redirect:/openGroupChat/" + groupId;
        }
        Optional<GroupChat> groupChat = groupChatService.getGroupChatById(groupId);

        if (groupChat.isEmpty()) {
            req.setAttribute("error", "Group chat not found.");
            return "redirect:/homePage";
        }

        if (groupChat.get().getUsers().contains(user) || user.getId().equals(currentUser.getId())) {
            req.setAttribute("error", "User is already in the group.");
            return "redirect:/openGroupChat/" + groupId;
        }

        groupChat.get().getUsers().add(user);
        groupChatService.saveGroupChat(groupChat.orElse(null));

        return "redirect:/openGroupChat/" + groupId;

    }

    @PostMapping("/deleteGroupChat")
    public String deleteGroupChat(@RequestParam Long chatId, HttpServletRequest req) {
        User currentUser = (User) req.getSession().getAttribute("currentUser");
        if(groupChatService.getGroupChatById(chatId).get().getOwnerId().equals(currentUser.getId())) {
            groupChatService.deleteGroupChat(chatId);
            return "redirect:/homePage";
        }
        return "redirect:/homePage";
    }
}
