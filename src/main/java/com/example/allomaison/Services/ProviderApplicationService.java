package com.example.allomaison.Services;

import com.example.allomaison.DTOs.ProviderApplicationDTO;
import com.example.allomaison.DTOs.ProviderApplicationRequest;
import com.example.allomaison.DTOs.ProviderCertificateDTO;
import com.example.allomaison.Entities.*;
import com.example.allomaison.Mapper.ProviderApplicationMapper;
import com.example.allomaison.Mapper.ProviderCertificateMapper;
import com.example.allomaison.Repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProviderApplicationService {

    private final ProviderApplicationRepository applicationRepository;
    private final ProviderApplicationCertificateRepository applicationCertificateRepository;
    private final ProviderCertificateService certificateService;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final CityRepository cityRepository;

    public Optional<ProviderApplicationDTO> registerApplication(ProviderApplicationRequest request) {
        Optional<User> userOpt = userRepository.findById(request.getUserId());
        Optional<Category> categoryOpt = categoryRepository.findByName(request.getCategory());
        Optional<City> cityOpt = cityRepository.findByZipcode(request.getCity());

        if (userOpt.isEmpty() || categoryOpt.isEmpty() || cityOpt.isEmpty()) {
            return Optional.empty();
        }

        // Check if user already has a pending application
        boolean hasPending = applicationRepository
                .findByUserId(request.getUserId())
                .stream()
                .anyMatch(app -> app.getStatus() == ProviderApplication.ApplicationStatus.PENDING);

        if (hasPending) return Optional.empty();

        // Create and save the application
        ProviderApplication app = new ProviderApplication();
        app.setUserId(request.getUserId());
        app.setCatId(categoryOpt.get().getCatId());
        app.setCityZipcode(request.getCity());
        app.setDescription(request.getDescription());
        app.setStatus(ProviderApplication.ApplicationStatus.PENDING);

        ProviderApplication savedApp = applicationRepository.save(app);

        // Upload and associate certificates
        List<ProviderCertificateDTO> certDTOs = new ArrayList<>();
        for (MultipartFile file : request.getCertificates()) {
            certificateService.uploadCertificate(file).ifPresent(dto -> {
                certDTOs.add(dto);
                ProviderApplicationCertificate relation = new ProviderApplicationCertificate();
                ProviderApplicationCertificate.Id id = new ProviderApplicationCertificate.Id();
                id.setApplicationId(savedApp.getApplicationId());
                id.setCertificateId(dto.certificateId());
                relation.setId(id);
                relation.setApplication(savedApp);
                applicationCertificateRepository.save(relation);
            });
        }

        return Optional.of(ProviderApplicationMapper.toDTO(savedApp, certDTOs));
    }

    public boolean updateApplicationStatus(Long applicationId, ProviderApplication.ApplicationStatus newStatus) {
        return applicationRepository.findById(applicationId).map(app -> {
            if (app.getStatus() != ProviderApplication.ApplicationStatus.PENDING) {
                // Only allow status change if the current status is PENDING
                System.err.println("Double check: Application status is not PENDING, cannot update.");
                return false;
            }
            app.setStatus(newStatus);
            applicationRepository.save(app);
            return true;
        }).orElse(false);
    }

    public List<ProviderApplicationDTO> getApplicationsByStatus(ProviderApplication.ApplicationStatus status) {
        List<ProviderApplication> applications = applicationRepository.findByStatus(status);
        List<ProviderApplicationDTO> dtos = new ArrayList<>();

        for (ProviderApplication app : applications) {
            List<ProviderCertificateDTO> certDTOs = getCertificatesForApplication(app.getApplicationId());
            dtos.add(ProviderApplicationMapper.toDTO(app, certDTOs));
        }

        return dtos;
    }

    public Optional<ProviderApplicationDTO> getApplicationById(Long applicationId) {
        return applicationRepository.findById(applicationId)
                .map(app -> ProviderApplicationMapper.toDTO(app, getCertificatesForApplication(app.getApplicationId())));
    }

    public Optional<ProviderApplicationDTO> getActiveApplicationByUserId(Long userId) {
        return applicationRepository.findByUserId(userId).stream()
                .filter(app -> app.getStatus() == ProviderApplication.ApplicationStatus.PENDING
                               || app.getStatus() == ProviderApplication.ApplicationStatus.APPROVED)
                .findFirst()
                .map(app -> ProviderApplicationMapper.toDTO(app, getCertificatesForApplication(app.getApplicationId())));
    }

    private List<ProviderCertificateDTO> getCertificatesForApplication(Long applicationId) {
        return applicationCertificateRepository.findByIdApplicationId(applicationId).stream()
                .map(ProviderApplicationCertificate::getCertificate)
                .map(ProviderCertificateMapper::toDTO)
                .toList();
    }

    public List<ProviderCertificateDTO> getCertificatesForProvider(Long providerId) {
        return applicationRepository.findByUserId(providerId).stream()
                .filter(app -> app.getStatus() == ProviderApplication.ApplicationStatus.APPROVED)
                .findFirst()
                .map(app -> getCertificatesForApplication(app.getApplicationId()))
                .orElse(List.of());
    }

}
