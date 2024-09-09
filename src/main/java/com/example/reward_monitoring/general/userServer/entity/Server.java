package com.example.reward_monitoring.general.userServer.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@ToString
@Table(name = "servers")
public class Server {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "기본키", example = "1")
    private int idx;

    @Column(name = "server_name", nullable = false)
    @Schema(description = "서버명")
    private String serverName;

    @Column(name = "server_url", nullable = false ,unique = true)
    @Schema(description = "서버URL")
    private String serverUrl;

    @Builder.Default
    @Comment("활성여부")
    @Column(name = "is_active", nullable = false)
    @Schema(description = "활성여부")
    private boolean isActive = true;

    @Comment("생성일자")
    @Column(name = "created_at", nullable = false, updatable = false)
    @Schema(description = "생성일자")
    private ZonedDateTime createdAt;


    @PrePersist
    protected void onCreate() {
        createdAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
    }

    @Builder
    public Server(String serverName,String serverUrl) {
        this.serverName = serverName;
        this.serverUrl = serverUrl;
    }

}
