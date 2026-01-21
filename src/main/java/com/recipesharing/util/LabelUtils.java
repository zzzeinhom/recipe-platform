package com.recipesharing.util;

import com.recipesharing.exception.BadRequestException;
import org.springframework.stereotype.Service;

@Service
public class LabelUtils {
    public String normalizeLabelName(String name) {
        if (name == null) {
            return null;
        }

        String normalized = name.trim().replace(" ", "-").toLowerCase();

        if (normalized.isBlank()) {
            throw new BadRequestException("Label name cannot be empty or whitespace only");
        }

        return normalized;
    }}

