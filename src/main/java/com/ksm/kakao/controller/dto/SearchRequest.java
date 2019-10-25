package com.ksm.kakao.controller.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SearchRequest {
    private String regionCode;
    private String region;
    private String keyword;
}
