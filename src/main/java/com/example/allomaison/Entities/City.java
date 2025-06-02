package com.example.allomaison.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

@Getter
@Immutable
@Entity
@Table(name = "Cities")
public class City {
    @Id
    private Integer zipcode;

    private String place;
    private String province;
}
