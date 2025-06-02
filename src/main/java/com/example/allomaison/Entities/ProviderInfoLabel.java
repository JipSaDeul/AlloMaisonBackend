package com.example.allomaison.Entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ProviderInfoLabels")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProviderInfoLabel {

    @EmbeddedId
    private Id id;

    @ManyToOne
    @MapsId("providerId")
    @JoinColumn(name = "providerId")
    private ProviderInfo provider;

    @ManyToOne
    @MapsId("labelId")
    @JoinColumn(name = "labelId")
    private ProviderLabel label;

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Id implements java.io.Serializable {
        private Long providerId;
        private Long labelId;
    }
}
