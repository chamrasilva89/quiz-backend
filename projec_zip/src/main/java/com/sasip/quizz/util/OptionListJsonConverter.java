package com.sasip.quizz.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sasip.quizz.model.Option;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.List;

@Converter
public class OptionListJsonConverter implements AttributeConverter<List<Option>, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<Option> optionList) {
        try {
            return objectMapper.writeValueAsString(optionList);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error converting option list to JSON", e);
        }
    }

    @Override
    public List<Option> convertToEntityAttribute(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<Option>>() {});
        } catch (Exception e) {
            throw new IllegalArgumentException("Error converting JSON to option list", e);
        }
    }
}