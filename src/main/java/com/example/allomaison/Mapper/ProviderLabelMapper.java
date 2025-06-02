package com.example.allomaison.Mapper;

import com.example.allomaison.DTOs.ProviderLabelDTO;
import com.example.allomaison.Entities.ProviderLabel;

public class ProviderLabelMapper {

    public static ProviderLabelDTO toDTO(ProviderLabel label) {
        return new ProviderLabelDTO(label.getLabelId(), label.getName());
    }
}
