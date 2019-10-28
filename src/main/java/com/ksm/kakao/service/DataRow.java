package com.ksm.kakao.service;

import com.ksm.kakao.domain.EcotourismProgram;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DataRow {
    private String id;
    private String name;
    private String theme;
    private String region;
    private String regionCode;
    private String introduce;
    private String detail;

    public DataRow(EcotourismProgram dto) {
        id = dto.getId();
        name = dto.getName();
        theme = dto.getTheme();
        region = dto.getRegion().getFullAddress();
        regionCode = dto.getRegionCode();
        introduce = dto.getIntroduce();
        detail = dto.getDetail();
    }
}
