package com.autumnflix.streaming.domain.model;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Embeddable
@Setter
@Getter
public class Credit {

    @Column(name = "credit_quantity")
    private Integer quantity;

    @Column(name = "credit_date")
    private OffsetDateTime date;
}
