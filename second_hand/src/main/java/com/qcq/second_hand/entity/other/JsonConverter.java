package com.qcq.second_hand.entity.other;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.List;

@Converter
public class JsonConverter implements AttributeConverter<List<String>, String> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<String> imageUrls) {
        try {
            return objectMapper.writeValueAsString(imageUrls);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert image URLs to JSON", e);
        }
    }

    @Override
    public List<String> convertToEntityAttribute(String json) {
        try {
            return objectMapper.readValue(json, List.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert JSON to image URLs", e);
        }
    }
}