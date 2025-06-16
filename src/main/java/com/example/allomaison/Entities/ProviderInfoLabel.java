package com.example.allomaison.Entities;

import jakarta.persistence.*;
import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "ProviderInfoLabels")
public class ProviderInfoLabel {

    @EmbeddedId
    private Id id;

    @ManyToOne
    @MapsId("providerId")
    @JoinColumn(name = "provider_id")
    private ProviderInfo provider;

    @ManyToOne
    @MapsId("labelId")
    @JoinColumn(name = "label_id")
    private ProviderLabel label;

    public ProviderInfoLabel() {}

    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }

    public ProviderInfo getProvider() {
        return provider;
    }

    public void setProvider(ProviderInfo provider) {
        this.provider = provider;
    }

    public ProviderLabel getLabel() {
        return label;
    }

    public void setLabel(ProviderLabel label) {
        this.label = label;
    }

    @Embeddable
    public static class Id implements Serializable {
        @Getter
        private Long providerId;
        private Long labelId;

        public Id() {}

        public Id(Long providerId, Long labelId) {
            this.providerId = providerId;
            this.labelId = labelId;
        }

        public void setProviderId(Long providerId) {
            this.providerId = providerId;
        }

        public void setLabelId(Long labelId) {
            this.labelId = labelId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Id that)) return false;
            return Objects.equals(providerId, that.providerId) &&
                    Objects.equals(labelId, that.labelId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(providerId, labelId);
        }
    }
}
