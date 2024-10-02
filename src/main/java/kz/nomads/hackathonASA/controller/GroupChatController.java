package kz.nomads.hackathonASA.controller;

import jakarta.servlet.http.HttpServletRequest;
import kz.nomads.hackathonASA.model.*;
import kz.nomads.hackathonASA.service.AIResponseGroupService;
import kz.nomads.hackathonASA.service.GroupChatService;
import kz.nomads.hackathonASA.service.GroupPromptService;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class GroupChatController {
    @Value("${openai.api.url}")
    private String url;

    @Autowired
    private GroupChatService groupChatService;

    @Autowired
    private UserService userService;

    @Autowired
    private GroupPromptService groupPromptService;

    @Autowired
    private AIResponseGroupService aiResponseGroupService;

    @Autowired
    RestTemplate restTemplate;

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
