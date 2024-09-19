package kz.nomads.hackathonASA.service;

import kz.nomads.hackathonASA.model.GroupChat;
import kz.nomads.hackathonASA.repository.GroupChatRepository;
import kz.nomads.hackathonASA.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GroupChatService {

    @Autowired
    private GroupChatRepository groupChatRepository;

    public GroupChat createGroupChat(GroupChat groupChat) {
        return groupChatRepository.save(groupChat);
    }

    public Optional<GroupChat> getGroupChatById(Long id) {
        return groupChatRepository.findById(id);
    }

    public List<GroupChat> getGroupChatByUserId(Long id) {
        return groupChatRepository.getGroupChatByOwnerId(id);
    }
}
