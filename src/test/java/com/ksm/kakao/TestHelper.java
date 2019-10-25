package com.ksm.kakao;

import com.ksm.kakao.repository.EcotourismProgram;
import com.ksm.kakao.repository.ProgramRepository;
import com.ksm.kakao.repository.RegionRepository;
import com.ksm.kakao.repository.ServiceRegion;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

public class TestHelper {
    public static RegionRepository createDemoRegionRepository() {
        ServiceRegion depth1 = new ServiceRegion();
        depth1.setName("서울시");
        depth1.setCode("reg1111");

        ServiceRegion depth2 = new ServiceRegion();
        depth2.setName("송파구");
        depth2.setCode("reg1234");
        depth2.setParent(depth1);
        depth2.setParentCode("reg1111");

        RegionRepository regionRepository = Mockito.mock(RegionRepository.class);
        Mockito.when(regionRepository.findById(Mockito.anyString())).thenAnswer(invocation -> {
            String code = invocation.getArgument(0);

            if ("reg1234".equals(code)) {
                return Optional.of(depth2);
            } else {
                return Optional.empty();
            }
        });
        Mockito.when(regionRepository.findByName(Mockito.anyString())).thenAnswer(invocation -> {
            String name = invocation.getArgument(0);

            if ("송파구".equals(name)) {
                return Optional.of(depth2);
            } else {
                return Optional.empty();
            }
        });
        Mockito.when(regionRepository.findByNameContains(Mockito.anyString())).thenAnswer(invocation -> {
            ArrayList<ServiceRegion> list = new ArrayList<>();

            String name = invocation.getArgument(0);
            if ("송파구".contains(name)) {
                list.add(depth2);
            }

            return list;
        });

        return regionRepository;
    }

    public static ProgramRepository createDemoProgramRepository() {
        ServiceRegion serviceRegion = new ServiceRegion();
        serviceRegion.setCode("reg1234");
//        serviceRegion.setName("서울");

        EcotourismProgram dbDto = new EcotourismProgram();
        dbDto.setId("test-id");
        dbDto.setName("test-name");
        dbDto.setTheme("test-theme");
        dbDto.setRegionCode("reg1234");
        dbDto.setIntroduce("test-introduce");
        dbDto.setDetail("test-detail");
        dbDto.setRegion(new ServiceRegion());

        EcotourismProgram dbDto2 = new EcotourismProgram();
        dbDto2.setId("id");
        dbDto2.setName("name");
        dbDto2.setTheme("theme");
        dbDto2.setRegionCode("reg1234");
        dbDto2.setIntroduce("introduce");
        dbDto2.setDetail("detail");
        dbDto2.setRegion(new ServiceRegion());

        ProgramRepository programRepository = Mockito.mock(ProgramRepository.class);
        Mockito.when(programRepository.findById(Mockito.anyString())).thenAnswer(invocation -> {
            String id = invocation.getArgument(0);

            if ("test-id".equals(id)) {
                return Optional.of(dbDto);
            } else {
                return Optional.empty();
            }
        });
        Mockito.when(programRepository.findByRegionCode(Mockito.anyString())).thenAnswer(invocation -> {
            String regionCode = invocation.getArgument(0);

            if ("reg1234".equals(regionCode)) {
                ArrayList<EcotourismProgram> list = new ArrayList<>();
                list.add(dbDto);
                return list;
            } else {
                return Collections.emptyList();
            }
        });
        Mockito.when(programRepository.findByParentRegionCode(Mockito.anyString())).thenAnswer(invocation -> {
            ArrayList<Map<String, Object>> list = new ArrayList<>();

            HashMap<String, Object> m = new HashMap<>();
            m.put("id", dbDto.getId());
            m.put("name", dbDto.getName());
            m.put("theme", dbDto.getTheme());
            m.put("introduce", dbDto.getIntroduce());
            m.put("detail", dbDto.getDetail());
            list.add(m);

            m = new HashMap<>();
            m.put("id", dbDto2.getId());
            m.put("name", dbDto2.getName());
            m.put("theme", dbDto2.getTheme());
            m.put("introduce", dbDto2.getIntroduce());
            m.put("detail", dbDto2.getDetail());
            list.add(m);


            return list;
        });
        Mockito.when(programRepository.findByIntroduceContains(Mockito.anyString())).thenAnswer(invocation -> {
            HashMap<String, Object> m = new HashMap<>();
            m.put("cnt", 2);
            m.put("region_code", "reg1234");

            ArrayList<Map<String, Object>> list = new ArrayList<>();
            list.add(m);
            return list;
        });
        Mockito.when(programRepository.findAll(Mockito.any(Pageable.class))).thenAnswer(invocation -> {
            ArrayList<EcotourismProgram> data = new ArrayList<>();
            data.add(dbDto);

            return new PageImpl<>(data, PageRequest.of(0, 10), 1);
        });

        return programRepository;
    }
}
