package com.example.allomaison.Entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "ProviderCertificates")
@Data
public class ProviderCertificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long certificateId;

    private String fileUrl; // Path to the certificate file
}
