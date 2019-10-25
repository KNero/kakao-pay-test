package com.ksm.kakao.service;

import org.junit.Test;

import javax.validation.ValidationException;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class DtoValidatorTest {
    @NotEmpty(message = "a is empty.")
    private String a = "";

    @NotNull
    private String b;

    @Test(expected = ValidationException.class)
    public void validateTest1() {
        DtoValidator validator = new DtoValidator();

        DtoValidatorTest testDto = new DtoValidatorTest();
        validator.validate(testDto);
    }

    @Test(expected = ValidationException.class)
    public void validateTest2() {
        DtoValidator validator = new DtoValidator();

        DtoValidatorTest testDto = new DtoValidatorTest();
        testDto.a = "not-empty";
        validator.validate(testDto);
    }

    @Test
    public void validateTest3() {
        DtoValidator validator = new DtoValidator();

        DtoValidatorTest testDto = new DtoValidatorTest();
        testDto.a = "not-empty";
        testDto.b = "not-null";

        validator.validate(testDto);
    }
}
