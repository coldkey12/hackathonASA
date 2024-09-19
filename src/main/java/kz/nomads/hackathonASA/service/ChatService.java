package kz.nomads.hackathonASA.service;

import kz.nomads.hackathonASA.model.Chat;
import kz.nomads.hackathonASA.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;

    public void addChat(Chat chat) {
        chatRepository.save(chat);
        System.out.println("CHAT ADDED");
    }

    public List<Chat> chatsByUserId(Long id) {
        return chatRepository.getByUserId(id);
    }

    public Optional<Chat> getChatById(Long chatId) {
        return chatRepository.findById(chatId);
    }
}
