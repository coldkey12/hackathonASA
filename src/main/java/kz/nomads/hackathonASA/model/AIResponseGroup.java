package kz.nomads.hackathonASA.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "AIResponseGroup")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIResponseGroup implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "TEXT", nullable = false, columnDefinition = "TEXT")
    private String text;

    @Column(name = "PROMPT_TEXT", unique = true, nullable = false, columnDefinition = "TEXT")
    private String promptText;

    @Column(name = "CHAT_ID", nullable = false)
    private Long chatId;

    private LocalDateTime initDate;

    @PrePersist
    public void prePersist(){
        initDate = LocalDateTime.now();
    }
}
