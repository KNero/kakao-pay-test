package com.ksm.kakao.controller.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class SaveRequest {
    @NotEmpty
    private String name;

    @NotEmpty
    private String region;

    private String theme;
    private String introduce;
    private String detail;
}
