package com.example.allomaison.Mapper;

import com.example.allomaison.Entities.ProviderCertificate;
import com.example.allomaison.DTOs.ProviderCertificateDTO;

public class ProviderCertificateMapper {
    public static ProviderCertificateDTO toDTO(ProviderCertificate cert) {
        return new ProviderCertificateDTO(cert.getCertificateId(), cert.getFileUrl());
    }
}
