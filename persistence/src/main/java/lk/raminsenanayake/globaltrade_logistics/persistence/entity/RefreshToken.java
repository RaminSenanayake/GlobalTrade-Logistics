package lk.raminsenanayake.globaltrade_logistics.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_token",
        indexes = {
                @Index(columnList = "token", unique = true),
        })
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private String username;

    @Column(unique = true)
    @NonNull
    private String token;

    @NonNull
    private LocalDateTime expiryAt;

    @CreationTimestamp
    private LocalDateTime createdAt;

}
