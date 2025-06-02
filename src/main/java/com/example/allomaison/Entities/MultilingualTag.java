package com.example.allomaison.Entities;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "MultilingualTags")
@Getter
public class MultilingualTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tag;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private com.example.allomaison.Utils.MultilingualUtil.Language language;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
}
