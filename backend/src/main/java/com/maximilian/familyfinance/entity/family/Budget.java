package com.maximilian.familyfinance.entity.family;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static jakarta.persistence.GenerationType.IDENTITY;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "budgets")
public class Budget {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "family_id", nullable = false)
    private Family family;

    @Column(nullable = false)
    private BigDecimal budgetLimit;

    @Column(nullable = false)
    private YearMonth period;

    @Column(nullable = false)
    private BigDecimal amount;

    @OneToMany(
            mappedBy = "budget",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<Transaction> transactions = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.amount = BigDecimal.ZERO;
        this.period = YearMonth.now();
        if (this.budgetLimit == null) {
            this.budgetLimit = BigDecimal.ZERO;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Budget budget = (Budget) o;
        return Objects.equals(getId(), budget.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
