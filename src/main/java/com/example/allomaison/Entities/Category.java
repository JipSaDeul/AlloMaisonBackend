package com.example.allomaison.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

@Getter
@Immutable
@Entity
@Table(name = "Categories")
public class Category {

    @Id
    private Integer catId;

    @Column(unique = true, nullable = false)
    private String name;
}
