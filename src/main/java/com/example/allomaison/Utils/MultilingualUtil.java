package com.example.allomaison.Utils;

import com.example.allomaison.Entities.MultilingualTag;
import com.example.allomaison.Repositories.MultilingualTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class MultilingualUtil {

    private final MultilingualTagRepository multilingualTagRepository;

    public enum Language {
        ENGLISH("en-US"),
        CHINESE_MANDARIN("zh-Hans"),
        JOSEONJOK_MAL("ko-CN"),
        FRENCH("fr-FR");

        private final String code;

        Language(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }

        public static Language fromCode(String code) {
            for (Language lang : Language.values()) {
                if (lang.getCode().equalsIgnoreCase(code)) {
                    return lang;
                }
            }
            throw new IllegalArgumentException("Unsupported language code: " + code);
        }
    }

    public String resolve(String tag, Language language) {
        return resolve(tag, language, Map.of());
    }

    public String resolve(String tag, Language language, Map<String, String> args) {
        String raw = multilingualTagRepository.findByTagAndLanguage(tag, language)
                .map(MultilingualTag::getContent)
                .orElse("[" + tag + "]");

        // replace placeholders with provided arguments
        if (args != null && !args.isEmpty()) {
            Pattern pattern = Pattern.compile("\\$(\\w+)");
            Matcher matcher = pattern.matcher(raw);
            StringBuffer sb = new StringBuffer();

            while (matcher.find()) {
                String key = matcher.group(1);
                String replacement = args.getOrDefault(key, "{" + key + "}");
                matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
            }
            matcher.appendTail(sb);
            return sb.toString();
        }

        return raw;
    }
}
