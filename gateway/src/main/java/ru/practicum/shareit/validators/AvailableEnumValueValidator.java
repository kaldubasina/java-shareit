package ru.practicum.shareit.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AvailableEnumValueValidator implements ConstraintValidator<AvailableEnumValue, String> {
    private List<String> acceptedValues;

    @Override
    public void initialize(AvailableEnumValue annotation) {
        acceptedValues = Stream.of(annotation.enumClass().getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isValid(String state, ConstraintValidatorContext context) {
        if (state == null || acceptedValues.contains(state)) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate("Unknown state: " + state)
                .addConstraintViolation();
        return false;
    }
}
