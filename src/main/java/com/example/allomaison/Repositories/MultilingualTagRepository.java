package com.example.allomaison.Repositories;

import com.example.allomaison.Entities.MultilingualTag;
import com.example.allomaison.Utils.MultilingualUtil.Language;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MultilingualTagRepository extends JpaRepository<MultilingualTag, Long> {
    Optional<MultilingualTag> findByTagAndLanguage(String tag, Language language);
}
