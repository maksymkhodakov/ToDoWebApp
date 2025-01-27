package com.example.todowebapp.domain.enumerated;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FeatureAction {
    BASIC_FEATURE("basic.stuff"),
    STANDARD_FEATURE("standard.stuff"),
    PREMIUM_FEATURE("premium.stuff"),
    ADMIN_FEATURE("admin.stuff");
    private final String name;
}
