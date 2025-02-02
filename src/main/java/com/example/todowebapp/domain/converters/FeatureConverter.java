package com.example.todowebapp.domain.converters;

import com.example.todowebapp.domain.dto.FeatureDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


@Converter
public class FeatureConverter implements AttributeConverter<List<FeatureDTO>, String> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<FeatureDTO> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }
        try {
            return mapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting list of FeatureDTO to JSON", e);
        }
    }

    @Override
    public List<FeatureDTO> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return mapper.readValue(dbData, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting JSON to list of FeatureDTO", e);
        }
    }
}
