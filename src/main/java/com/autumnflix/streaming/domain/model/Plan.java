package com.autumnflix.streaming.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Optional;

@Entity
@Data
@Table(name = "plan")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "name", nullable = false)
    @Enumerated(EnumType.STRING)
    private PlanType type;

    @Column(name = "num_credits")
    private Integer numCredits;

    @Column(name = "value", nullable = false)
    private BigDecimal value;

    @Column(name = "description")
    private String description;

    /* Add after creating User package, and also make a mapping into user
    @OneToMany(mappedBy = "plan",cascade = {CascadeType.PERSIST,CascadeType.MERGE
    CascadeType.DETACH,CascadeType.REFRESH})
    private List<User> users;

    public void add(User tempUser){
        if (users = null) {
            users = new ArrayList<>();
        }
        users.add(tempUser);

        tempUser.setPlan(this);
    }
    */
}
