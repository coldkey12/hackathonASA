package kz.nomads.hackathonASA.repository;

import kz.nomads.hackathonASA.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> getByUserId(Long id);
}
