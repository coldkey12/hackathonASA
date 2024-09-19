package kz.nomads.hackathonASA.repository;

import kz.nomads.hackathonASA.model.AIResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AIResponseRepository extends JpaRepository<AIResponse, Long> {
    List<AIResponse> findAllByChatId(Long chatId);
}
