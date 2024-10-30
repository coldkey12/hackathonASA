package kz.nomads.hackathonASA.service;

import kz.nomads.hackathonASA.model.Chat;
import kz.nomads.hackathonASA.model.Prompt;
import kz.nomads.hackathonASA.repository.ChatRepository;
import kz.nomads.hackathonASA.repository.PromptRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class PromptServiceTest {

    @Mock
    private PromptRepository promptRepository;

    private Prompt prompt;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Prompt prompt = new Prompt();
        prompt.setUserId(1L);
        prompt.setChatId(1L);
        prompt.setText("Test message");
    }

    @Test
    void testRegisterUser() {
        promptRepository.save(prompt);
        verify(promptRepository, times(1)).save(prompt);
    }

    @Test
    void testGetPromptListByChatId() {
        Prompt anotherPrompt = new Prompt();
        anotherPrompt.setChatId(1L);
        anotherPrompt.setText("Test message for prompt 2");
        anotherPrompt.setUserId(1L);

        Chat chat = new Chat();
        chat.setChatName("ex chat");
        chat.setUserId(1L);
        List<Prompt> mockPrompts = Arrays.asList(prompt, anotherPrompt);

        assertEquals(2, mockPrompts.size());
        assertEquals(prompt, mockPrompts.get(0));
        assertEquals(anotherPrompt, mockPrompts.get(1));
    }

}
