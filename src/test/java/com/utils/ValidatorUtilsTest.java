package com.utils;

import com.entity.EIException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import javax.validation.constraints.NotNull;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ValidatorUtils unit tests.
 */
public class ValidatorUtilsTest {

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testValidateEntity_success() {
        ValidSample sample = new ValidSample();
        sample.setName("ok");
        assertDoesNotThrow(() -> ValidatorUtils.validateEntity(sample));
    }

    @Test
    public void testValidateEntity_failure() {
        ValidSample sample = new ValidSample();
        EIException ex = assertThrows(EIException.class, () -> ValidatorUtils.validateEntity(sample));
        assertEquals("name must not be null", ex.getMsg());
    }

    public static class ValidSample {
        @NotNull(message = "name must not be null")
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
