package kz.nomads.hackathonASA.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "CHATS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Chat implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "USER_ID", nullable = false)
    private Long userId;

    private LocalDateTime postDate;

    @PrePersist
    public void prePersist(){
        postDate = LocalDateTime.now();
    }
}
