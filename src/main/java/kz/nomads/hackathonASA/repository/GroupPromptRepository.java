package kz.nomads.hackathonASA.repository;

import kz.nomads.hackathonASA.model.GroupPrompt;
import kz.nomads.hackathonASA.model.Prompt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupPromptRepository extends JpaRepository<GroupPrompt, Long> {
    List<GroupPrompt> findGroupPromptByChatId(Long id);
}
