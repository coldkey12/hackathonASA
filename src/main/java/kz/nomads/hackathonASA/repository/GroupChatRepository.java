package kz.nomads.hackathonASA.repository;

import kz.nomads.hackathonASA.model.GroupChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupChatRepository extends JpaRepository<GroupChat, Long> {
    List<GroupChat> getGroupChatByOwnerId(Long id);
}
