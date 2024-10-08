package kz.nomads.hackathonASA.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "USERS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "USERNAME", unique = true, nullable = false)
    private String username;

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @ManyToMany(mappedBy = "users")
    private List<GroupChat> groupChats;

    private LocalDateTime initDate;

    @PrePersist
    public void prePersist(){
        initDate = LocalDateTime.now();
    }
}
