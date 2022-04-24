package com.example.fashionshop.validation.commons;

import com.example.fashionshop.model.commons.Description;
import com.example.fashionshop.validation.ValidationConstants;

public final class DescriptionValidator {
    public static boolean validateDescription(Description description) {
        return description.getComment().length() > ValidationConstants.PRODUCT_DESCRIPTION_MIN_LENGTH &&
                description.getComment().length() < ValidationConstants.PRODUCT_DESCRIPTION_MAX_LENGTH;
    }
}
