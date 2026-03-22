package com.wastewatch.authorities;

import com.wastewatch.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "authorities")
@Getter @Setter
@NoArgsConstructor
public class AuthorityEntity extends BaseEntity {

    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "lga_id", nullable = false)
    private UUID lgaId;

    @Column(nullable = false, unique = true, length = 255)
    private String email;
}