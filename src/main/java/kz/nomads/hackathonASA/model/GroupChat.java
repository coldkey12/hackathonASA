package kz.nomads.hackathonASA.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "GROUP_CHATS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupChat implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "GROUP_CHAT_NAME", nullable = false)
    private String groupChatName;

    @Column(name = "OWNER_ID", nullable = false)
    private Long ownerId;

    @ManyToMany
    @JoinTable(
            name = "group_chat_users",
            joinColumns = @JoinColumn(name = "group_chat_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> users;

    private LocalDateTime initDate;

    @PrePersist
    public void prePersist(){
        initDate = LocalDateTime.now();
    }
}
