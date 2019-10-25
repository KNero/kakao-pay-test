package com.ksm.kakao.service.search;

import com.ksm.kakao.repository.ProgramRepository;
import com.ksm.kakao.repository.RegionRepository;

import java.util.Map;

public interface Finder {
    void find(ProgramRepository programRepository, RegionRepository regionRepository);

    Map<String, Object> getResult();
}
