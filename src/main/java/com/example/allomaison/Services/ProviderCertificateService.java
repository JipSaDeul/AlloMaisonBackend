package com.example.allomaison.Services;

import com.example.allomaison.Entities.ProviderCertificate;
import com.example.allomaison.Repositories.ProviderCertificateRepository;
import com.example.allomaison.Utils.FileStorageUtil;
import com.example.allomaison.Utils.FileUploadResult;
import com.example.allomaison.DTOs.ProviderCertificateDTO;
import com.example.allomaison.Mapper.ProviderCertificateMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProviderCertificateService {

    private final ProviderCertificateRepository certificateRepository;

    public Optional<ProviderCertificateDTO> uploadCertificate(MultipartFile file) {
        FileUploadResult result = FileStorageUtil.saveGeneralFile(file, "cert");
        if (!result.isSuccessful()) return Optional.empty();

        ProviderCertificate cert = new ProviderCertificate();
        cert.setFileUrl(result.getUrl());
        ProviderCertificate saved = certificateRepository.save(cert);
        return Optional.of(ProviderCertificateMapper.toDTO(saved));
    }
}
