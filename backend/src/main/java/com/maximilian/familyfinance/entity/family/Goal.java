package com.maximilian.familyfinance.entity.family;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static jakarta.persistence.GenerationType.IDENTITY;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Goal {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal goalAmount;

    @Column(nullable = false)
    private BigDecimal accumulatedAmount;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "family_id", nullable = false)
    private Family family;

    @PrePersist
    public void prePersist() {
        if (accumulatedAmount == null || accumulatedAmount.compareTo(BigDecimal.ZERO) < 0) {
            accumulatedAmount = BigDecimal.ZERO;
        }
    }
}
