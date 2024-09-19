package kz.nomads.hackathonASA.service;

import kz.nomads.hackathonASA.model.Prompt;
import kz.nomads.hackathonASA.repository.PromptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PromptService {

    @Autowired
    private PromptRepository promptRepository;

    public void addPrompt(Prompt prompt) {
        promptRepository.save(prompt);
    }

    public List<Prompt> getPromptListByChatId(Long id) {
        return promptRepository.findPromptListByChatId(id);
    }
}
