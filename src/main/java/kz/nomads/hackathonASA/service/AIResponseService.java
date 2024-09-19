package kz.nomads.hackathonASA.service;

import kz.nomads.hackathonASA.model.AIResponse;
import kz.nomads.hackathonASA.repository.AIResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AIResponseService {

    @Autowired
    private AIResponseRepository aiResponseRepository;

    public void addResponse(AIResponse aiResponse) {
        aiResponseRepository.save(aiResponse);
    }

    public List<AIResponse> getAIResponsesByChatId(Long chatId) {
        return aiResponseRepository.findAllByChatId(chatId);
    }
}
