package com.autumnflix.streaming.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@Table(name = "user")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "dob", nullable = false)
    private LocalDate dob;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Embedded
    private PaymentMethod paymentMethod;

    @Embedded
    private Credit credit;

    @Embedded
    private IdentificationDocument identificationDocument;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
                          CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "plan_id")
    private Plan plan;

    @ManyToMany
    @JoinTable(
            name = "user_watched_entertain_work",
            joinColumns = @JoinColumn(
                    name = "user_id",
                    foreignKey = @ForeignKey(
                            name = "fk_user_watched_entertain_work_user_user_id"
                    )
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "entertainment_work_id"
            )
    )
    private List<EntertainmentWork> watchedEntertainWorks = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "user_favorites_entertain_work",
            joinColumns = @JoinColumn(
                    name = "user_id",
                    foreignKey = @ForeignKey(
                            name = "fk_user_favorites_ew_user_user_id"
                    )
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "entertainment_work_id"
            )
    )
    private Set<EntertainmentWork> favoriteEntertainWorks = new HashSet<>();

    public void addFavoritesEntertainWorks(EntertainmentWork entertainmentWork){
        this.getFavoriteEntertainWorks().add(entertainmentWork);
    }

    public void removeFavoritesEntertainWorks(EntertainmentWork entertainmentWork){
        this.getFavoriteEntertainWorks().remove(entertainmentWork);
    }

    public void addWatchedEntertainWorks(EntertainmentWork entertainmentWork){
        this.getWatchedEntertainWorks().add(entertainmentWork);
    }

    public void removeWatchedEntertainWorks(EntertainmentWork entertainmentWork){
        this.getWatchedEntertainWorks().remove(entertainmentWork);
    }


}
