package com.example.allomaison.Entities;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Entity
@Table(name = "ProviderApplicationCertificates")
@Data
public class ProviderApplicationCertificate {

    @Embeddable
    @Data
    public static class Id implements Serializable {
        private Long applicationId;
        private Long certificateId;
    }

    @EmbeddedId
    private Id id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicationId", insertable = false, updatable = false)
    private ProviderApplication application;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "certificateId", insertable = false, updatable = false)
    private ProviderCertificate certificate;
}
