package com.example.allomaison.Services;

import com.example.allomaison.DTOs.*;
import com.example.allomaison.DTOs.Projections.ReviewWithCustomerId;
import com.example.allomaison.DTOs.Requests.NoticeRequest;
import com.example.allomaison.DTOs.Responses.ProviderReviewSummary;
import com.example.allomaison.Entities.*;
import com.example.allomaison.Mapper.*;
import com.example.allomaison.Repositories.*;
import com.example.allomaison.Utils.MultilingualUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class ProviderService {

    private final ProviderInfoRepository providerInfoRepository;
    private final ProviderInfoLabelRepository infoLabelRepository;
    private final ProviderLabelService labelService;
    private final CategoryRepository categoryRepository;
    private final CityRepository cityRepository;
    private final UserRepository userRepository;
    private final ProviderApplicationRepository applicationRepository;
    private final ProviderApplicationCertificateRepository applicationCertificateRepository;
    private final ProviderApplicationService providerApplicationService;
    private final ReviewRepository reviewRepository;
    private final NoticeService noticeService;
    private final MultilingualUtil multilingualUtil;
    private final MultilingualUtil.Language language = MultilingualUtil.Language.ENGLISH;

    public boolean updateProviderInfo(ProviderInfoDTO dto) {
        Optional<ProviderInfo> infoOpt = providerInfoRepository.findById(dto.getProviderId());
        if (infoOpt.isEmpty()) return false;

        ProviderInfo entity = infoOpt.get();
        ProviderInfoMapper.updateEntity(entity, dto);
        providerInfoRepository.save(entity);

        updateLabels(entity, dto.getLabels());
        return true;
    }

    private void updateLabels(ProviderInfo entity, List<String> newLabelsRaw) {
        Long providerId = entity.getProviderId();
        List<String> newLabels = newLabelsRaw.stream()
                .map(ProviderLabelService::normalizeName)
                .filter(s -> !s.isBlank())
                .distinct()
                .toList();

        // Current labels in the database
        Set<String> existingLabelNames = infoLabelRepository.findByIdProviderId(providerId).stream()
                .map(link -> link.getLabel().getName())
                .collect(Collectors.toSet());

        // Labels to add
        Set<String> toAdd = new HashSet<>(newLabels);
        toAdd.removeAll(existingLabelNames);

        // Labels to remove
        Set<String> toRemove = new HashSet<>(existingLabelNames);
        newLabels.forEach(toRemove::remove);

        // Addition
        for (String labelName : toAdd) {
            ProviderLabel labelEntity = labelService.getOrCreateLabelEntity(labelName);

            ProviderInfoLabel link = new ProviderInfoLabel();
            ProviderInfoLabel.Id id = new ProviderInfoLabel.Id();
            id.setProviderId(providerId);
            id.setLabelId(labelEntity.getLabelId());

            link.setId(id);
            link.setProvider(entity);
            link.setLabel(labelEntity);

            infoLabelRepository.save(link);
        }

        // Removal
        for (ProviderInfoLabel link : infoLabelRepository.findByIdProviderId(providerId)) {
            if (toRemove.contains(link.getLabel().getName())) {
                infoLabelRepository.delete(link);
            }
        }
    }

    public boolean isProvider(Long userId) {
        return providerInfoRepository.existsById(userId);
    }

    public Optional<ProviderInfo> getProviderInfo(Long providerId) {
        return providerInfoRepository.findById(providerId);
    }

    @SuppressWarnings("unused")
    public Optional<ProviderDTO> getProviderDTOById(Long providerId) {
        if (!providerInfoRepository.existsById(providerId)) {
            return Optional.empty();
        }

        // Load base provider info
        ProviderInfo info = providerInfoRepository.findById(providerId).orElseThrow();

        // Load category DTO
        Category category = categoryRepository.findById(info.getCatId()).orElse(null);
        if (category == null) return Optional.empty();
        CategoryDTO categoryDTO = CategoryMapper.toDTO(category);

        // Load city DTO
        City city = cityRepository.findByZipcode(info.getCityZipcode()).orElse(null);
        if (city == null) return Optional.empty();
        CityDTO cityDTO = CityMapper.toDTO(city);

        // Load label DTOs
        List<ProviderLabelDTO> labelDTOs;
        labelDTOs = infoLabelRepository.findByIdProviderId(providerId).stream()
                .map(ProviderInfoLabel::getLabel)
                .map(ProviderLabelMapper::toDTO)
                .toList();

        // Load certificate DTOs via approved application
        List<ProviderCertificateDTO> certDTOs = applicationRepository.findByUserId(providerId).stream()
                .filter(app -> app.getStatus() == ProviderApplication.ApplicationStatus.APPROVED)
                .findFirst()
                .map(app -> applicationCertificateRepository.findByIdApplicationId(app.getApplicationId()).stream()
                        .map(ProviderApplicationCertificate::getCertificate)
                        .map(ProviderCertificateMapper::toDTO)
                        .toList()
                )
                .orElse(List.of());

        return Optional.of(
                ProviderInfoMapper.toFullDTO(
                        info,
                        categoryDTO,
                        cityDTO,
                        labelDTOs,
                        certDTOs
                )
        );
    }

    public boolean approveApplicationAndCreateProvider(ProviderApplicationDTO applicationDTO) {
        Long userId = applicationDTO.userId();

        // If the user already has a provider info, we don't need to approve again
        if (providerInfoRepository.existsById(userId)) {
            return false;
        }

        // try to update the application status to APPROVED
        boolean updated = providerApplicationService.updateApplicationStatus(
                applicationDTO.applicationId(),
                ProviderApplication.ApplicationStatus.APPROVED
        );
        if (!updated) return false;

        // construct and save the ProviderInfo entity
        ProviderInfo info = new ProviderInfo();
        info.setProviderId(userId);
        info.setCatId(applicationDTO.catId()); // immutable
        info.setDescription(applicationDTO.description());
        info.setCityZipcode(applicationDTO.cityZipcode());
        info.setServiceArea("");
        info.setPriceRange("");
        info.setServiceOffered("[]");

        providerInfoRepository.save(info);

        NoticeRequest notice = new NoticeRequest();
        notice.setUserId(userId);
        notice.setTitle(multilingualUtil.resolve("application.approved.title", language));
        notice.setContent(multilingualUtil.resolve("application.approved.content", language));
        notice.setType(NoticeMessage.Type.NOTICE);
        notice.setTargets(NoticeMessage.Target.PERSONAL);

        noticeService.postNotice(notice);

        return true;
    }

    public boolean rejectApplication(ProviderApplicationDTO applicationDTO) {
        Long userId = applicationDTO.userId();

        // If the user already has a provider info, we don't need to approve again
        if (providerInfoRepository.existsById(userId)) {
            return false;
        }

        // try to update the application status to REJECTED
        boolean rejected = providerApplicationService.updateApplicationStatus(
                applicationDTO.applicationId(),
                ProviderApplication.ApplicationStatus.REJECTED
        );
        if (!rejected) return false;

        // rejecting an application does not create a ProviderInfo entity

        NoticeRequest notice = new NoticeRequest();
        notice.setUserId(userId);
        notice.setTitle(multilingualUtil.resolve("application.rejected.title", language));
        notice.setContent(multilingualUtil.resolve("application.rejected.content", language));
        notice.setType(NoticeMessage.Type.WARNING);
        notice.setTargets(NoticeMessage.Target.PERSONAL);

        noticeService.postNotice(notice);

        return true;
    }

    private List<ReviewDTO> getProviderReviews(Long providerId) {
        if (!providerInfoRepository.existsById(providerId)) return null;

        return reviewRepository.findCompletedReviewsByProviderId(providerId).stream()
                .map(ReviewMapper::toDTO)
                .toList();
    }

    @SuppressWarnings("unused")
    public Optional<Double> getProviderRating(Long providerId) {
        List<ReviewDTO> reviews = getProviderReviews(providerId);
        if (reviews == null || reviews.isEmpty()) return Optional.empty();

        double averageRating = reviews.stream()
                .mapToDouble(ReviewDTO::ranking)
                .average()
                .orElse(0.0);

        return Optional.of(averageRating);
    }

    public List<ProviderDTO> getAllProviders() {
        List<ProviderInfo> providerEntities = providerInfoRepository.findAll();

        return providerEntities.stream()
                .map(info -> {
                    Long providerId = info.getProviderId();

                    Category category = categoryRepository.findById(info.getCatId()).orElse(null);
                    if (category == null) return null;

                    City city = cityRepository.findByZipcode(info.getCityZipcode()).orElse(null);
                    if (city == null) return null;

                    List<ProviderLabelDTO> labelDTOs = infoLabelRepository.findByIdProviderId(providerId).stream()
                            .map(ProviderInfoLabel::getLabel)
                            .map(ProviderLabelMapper::toDTO)
                            .toList();

                    List<ProviderCertificateDTO> certDTOs = applicationRepository.findByUserId(providerId).stream()
                            .filter(app -> app.getStatus() == ProviderApplication.ApplicationStatus.APPROVED)
                            .findFirst()
                            .map(app -> applicationCertificateRepository.findByIdApplicationId(app.getApplicationId()).stream()
                                    .map(ProviderApplicationCertificate::getCertificate)
                                    .map(ProviderCertificateMapper::toDTO)
                                    .toList()
                            )
                            .orElse(List.of());

                    return ProviderInfoMapper.toFullDTO(
                            info,
                            CategoryMapper.toDTO(category),
                            CityMapper.toDTO(city),
                            labelDTOs,
                            certDTOs
                    );
                })
                .filter(Objects::nonNull)
                .toList();
    }

    public Optional<ProviderReviewSummary> getProviderReviewSummary(Long providerId) {
        List<ReviewWithCustomerId> reviewProjections =
                reviewRepository.findCompletedReviewsWithCustomerId(providerId);

        if (reviewProjections.isEmpty()) return Optional.empty();

        double avgRating = reviewProjections.stream()
                .mapToInt(r -> Optional.ofNullable(r.getRanking()).orElse(0))
                .average()
                .orElse(0.0);

        List<Long> customerIds = reviewProjections.stream()
                .map(ReviewWithCustomerId::getCustomerId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        // batch load userId -> userName map
        Map<Long, String> idToName = StreamSupport.stream(
                userRepository.findAllById(customerIds).spliterator(), false
        ).collect(Collectors.toMap(User::getUserId, User::getUserName));

        List<ProviderReviewSummary.CustomerReview> reviews = reviewProjections.stream()
                .map(r -> ProviderReviewSummary.CustomerReview.builder()
                        .author(idToName.getOrDefault(r.getCustomerId(), "Unknown"))
                        .content(Optional.ofNullable(r.getReviewText()).orElse(""))
                        .build())
                .toList();

        return Optional.of(ProviderReviewSummary.builder()
                .rating(avgRating)
                .customerReviews(reviews)
                .build());
    }

    public List<String> getProviderLabels(Long providerId) {
        return infoLabelRepository.findByIdProviderId(providerId).stream()
                .map(ProviderInfoLabel::getLabel)
                .map(ProviderLabel::getName)
                .toList();
    }

    public Optional<String> getCategoryNameById(Integer catId) {
        return categoryRepository.findById(catId).map(Category::getName);
    }

    public Optional<String> getCityNameByZip(Integer zipcode) {
        return cityRepository.findByZipcode(zipcode).map(City::getPlace);
    }


    public Optional<String> getProviderName(Long providerId) {
        return userRepository.findById(providerId).map(User::getUserName);
    }

}
