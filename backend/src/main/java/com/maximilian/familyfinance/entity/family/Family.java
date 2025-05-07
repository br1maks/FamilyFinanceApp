package com.maximilian.familyfinance.entity.family;

import com.maximilian.familyfinance.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static jakarta.persistence.GenerationType.IDENTITY;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "families")
public class Family {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String inviteCode;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToMany(
            mappedBy = "family",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private Set<FamilyMember> members = new HashSet<>();

    @OneToMany(
            mappedBy = "family",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<Budget> budgets = new ArrayList<>();

    @OneToMany(
            mappedBy = "family",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<Category> categories = new ArrayList<>();

    @OneToMany(
            mappedBy = "family",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<Goal> goals = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        if (this.getInviteCode() == null) {
            this.setInviteCode(UUID.randomUUID().toString());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Family family = (Family) o;
        return Objects.equals(getId(), family.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
