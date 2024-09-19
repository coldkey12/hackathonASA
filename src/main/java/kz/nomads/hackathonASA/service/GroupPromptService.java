package kz.nomads.hackathonASA.service;

import kz.nomads.hackathonASA.model.GroupPrompt;
import kz.nomads.hackathonASA.repository.GroupPromptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupPromptService {

    @Autowired
    private GroupPromptRepository groupPromptRepository;

    public void addGroupPrompt(GroupPrompt prompt) {
        groupPromptRepository.save(prompt);
    }

    public List<GroupPrompt> getGroupPromptListByChatId(Long id) {
        return groupPromptRepository.findGroupPromptByChatId(id);
    }
}
