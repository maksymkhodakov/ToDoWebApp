package com.example.todowebapp.domain.enumerated;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public enum UserRole {
    ROLE_BASIC_USER(List.of(FeatureAction.BASIC_FEATURE)),

    ROLE_STANDARD_USER(List.of(
            FeatureAction.BASIC_FEATURE,
            FeatureAction.STANDARD_FEATURE
    )),

    ROLE_PREMIUM_USER(List.of(
            FeatureAction.BASIC_FEATURE,
            FeatureAction.STANDARD_FEATURE,
            FeatureAction.PREMIUM_FEATURE
    )),

    ROLE_ADMIN(List.of(
            FeatureAction.BASIC_FEATURE,
            FeatureAction.STANDARD_FEATURE,
            FeatureAction.PREMIUM_FEATURE,
            FeatureAction.ADMIN_FEATURE
    ));

    private final List<FeatureAction> features;
}
