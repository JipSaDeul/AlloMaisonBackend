package com.example.allomaison.Mapper;

import com.example.allomaison.DTOs.ProviderApplicationDTO;
import com.example.allomaison.DTOs.ProviderCertificateDTO;
import com.example.allomaison.Entities.ProviderApplication;

import java.util.List;

public class ProviderApplicationMapper {

    public static ProviderApplicationDTO toDTO(
            ProviderApplication app,
            List<ProviderCertificateDTO> certDTOs
    ) {

        return new ProviderApplicationDTO(
                app.getApplicationId(),
                app.getUserId(),
                app.getCatId(),
                app.getDescription(),
                app.getCityZipcode(),
                app.getStatus(),
                certDTOs
        );
    }
}
