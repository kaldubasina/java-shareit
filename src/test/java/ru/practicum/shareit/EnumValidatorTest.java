package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.validators.AvailableEnumValue;
import ru.practicum.shareit.validators.AvailableEnumValueValidator;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class EnumValidatorTest {
    @InjectMocks
    private AvailableEnumValueValidator validator;
    @Mock
    private ConstraintValidatorContext context;

    @Test
    void shouldReturnTrueForNull() {
        validator.initialize(createAnnotation());
        assertTrue(validator.isValid(null, context));
    }

    @Test
    void shouldReturnTrueForValidValue() {
        validator.initialize(createAnnotation());
        assertTrue(validator.isValid("ALL", context));
        assertTrue(validator.isValid("PAST", context));
        assertTrue(validator.isValid("REJECTED", context));
    }

    private AvailableEnumValue createAnnotation() {
        return new AvailableEnumValue() {
            @Override
            public Class<? extends Enum<?>> enumClass() {
                return State.class;
            }

            @Override
            public String message() {
                return "";
            }

            @Override
            public Class<?>[] groups() {
                return new Class[0];
            }

            @Override
            public Class<? extends Payload>[] payload() {
                return new Class[0];
            }

            @Override
            public Class<AvailableEnumValue> annotationType() {
                return AvailableEnumValue.class;
            }
        };
    }
}
