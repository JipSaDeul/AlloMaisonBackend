package com.example.allomaison.Services;

import com.example.allomaison.DTOs.ProviderLabelDTO;
import com.example.allomaison.Entities.ProviderLabel;
import com.example.allomaison.Mapper.ProviderLabelMapper;
import com.example.allomaison.Repositories.ProviderLabelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProviderLabelService {

    private final ProviderLabelRepository labelRepository;

    @SuppressWarnings("unused")
    public List<ProviderLabelDTO> getAllLabels() {
        return ((List<ProviderLabel>) labelRepository.findAll()).stream()
                .map(ProviderLabelMapper::toDTO)
                .toList();
    }

    public ProviderLabel getOrCreateLabelEntity(String rawName) {
        String normalized = normalizeName(rawName);
        return labelRepository.findByName(normalized)
                .orElseGet(() -> {
                    ProviderLabel newLabel = new ProviderLabel();
                    newLabel.setName(normalized);
                    return labelRepository.save(newLabel);
                });
    }

    static String normalizeName(String input) {
        if (input == null) return "";
        return input
                .toLowerCase()
                .replace("-", " ")
                .replace("_", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }
}
