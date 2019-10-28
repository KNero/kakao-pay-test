package com.ksm.kakao.service;

import com.ksm.kakao.controller.ApiEcoControllerTest;
import com.ksm.kakao.entity.EcotourismProgram;
import com.ksm.kakao.repository.RegionRepository;
import com.ksm.kakao.repository.ProgramRepository;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.util.Assert;

import java.util.Optional;

public class EcoProgramServiceTest {
    /**
     * repository 전달되는 ProgramDTO 의 필수값과 ID 가 생성되었는지 검사
     */
    @Test
    public void saveDumpDataTest() throws Exception {
        ProgramRepository programRepository = Mockito.mock(ProgramRepository.class);
        Mockito.when(programRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());
        Mockito.doAnswer(invocation -> {
            EcotourismProgram dto = invocation.getArgument(0);

            Assert.notNull(dto.getId(), "region code is null");
            Assert.notNull(dto.getName(), "program name is null");
            Assert.notNull(dto.getRegionCode(), "location is null");
            ApiEcoControllerTest.checkValidLocationCode(dto.getRegionCode());

            return dto;
        }).when(programRepository).save(Mockito.any(EcotourismProgram.class));

        RegionRepository locationRepository = Mockito.mock(RegionRepository.class);
        EcoProgramService ecoProgramService = new EcoProgramService(locationRepository, programRepository, new DtoValidator());
        ecoProgramService.saveFileDataToDb();
    }
}
