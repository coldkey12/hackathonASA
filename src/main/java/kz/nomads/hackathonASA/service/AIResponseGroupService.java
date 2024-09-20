package kz.nomads.hackathonASA.service;

import kz.nomads.hackathonASA.model.AIResponse;
import kz.nomads.hackathonASA.model.AIResponseGroup;
import kz.nomads.hackathonASA.repository.AIResponseGroupRepository;
import kz.nomads.hackathonASA.repository.AIResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AIResponseGroupService {

    @Autowired
    private AIResponseGroupRepository aiResponseGroupRepository;

    public void addResponse(AIResponseGroup aiResponse) {
        aiResponseGroupRepository.save(aiResponse);
    }

    public List<AIResponseGroup> getAIResponsesByChatId(Long chatId) {
        return aiResponseGroupRepository.findAllByChatId(chatId);
    }
}
