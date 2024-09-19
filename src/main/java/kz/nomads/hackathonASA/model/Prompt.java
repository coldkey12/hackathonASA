package kz.nomads.hackathonASA.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "PROMPTS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prompt implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "TEXT", unique = true, nullable = false, columnDefinition = "TEXT")
    private String text;

    @Column(name = "CHAT_ID", nullable = false)
    private Long chatId;

    private LocalDateTime joinDate;

    @PrePersist
    public void prePersist(){
        joinDate = LocalDateTime.now();
    }
}
