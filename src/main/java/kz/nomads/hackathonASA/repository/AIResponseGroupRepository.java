package kz.nomads.hackathonASA.repository;

import kz.nomads.hackathonASA.model.AIResponse;
import kz.nomads.hackathonASA.model.AIResponseGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AIResponseGroupRepository extends JpaRepository<AIResponseGroup, Long> {
    List<AIResponseGroup> findAllByChatId(Long chatId);
}
