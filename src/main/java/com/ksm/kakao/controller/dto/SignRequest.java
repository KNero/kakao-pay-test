package com.ksm.kakao.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SignRequest {
    @JsonProperty("ID")
    private String id;

    @JsonProperty("PW")
    private String password;
}
