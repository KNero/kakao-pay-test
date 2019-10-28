package com.ksm.kakao.service;

import com.ksm.kakao.controller.dto.SaveRequest;
import com.ksm.kakao.controller.dto.SearchRequest;
import com.ksm.kakao.domain.EcotourismProgram;
import com.ksm.kakao.repository.RegionRepository;
import com.ksm.kakao.repository.ProgramRepository;
import com.ksm.kakao.domain.ServiceRegion;
import com.ksm.kakao.service.search.EcoProgramFinder;
import com.ksm.kakao.service.search.Finder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;
import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class EcoProgramService {
    private RegionRepository regionRepository;
    private ProgramRepository programRepository;
    private DtoValidator dtoValidator;

    public DataRow saveNewEcoProgram(SaveRequest saveRequest) throws ValidationException {
        dtoValidator.validate(saveRequest);

        ServiceRegion serviceRegion = getServiceLocation(saveRequest.getRegion());

        EcotourismProgram program = new EcotourismProgram();
        program.setId(newProgramId());
        program.setName(saveRequest.getName());
        program.setRegionCode(serviceRegion.getCode());
        program.setTheme(saveRequest.getTheme());
        program.setIntroduce(saveRequest.getIntroduce());
        program.setDetail(saveRequest.getDetail());
        program.setRegion(serviceRegion);

        programRepository.save(program);

        log.info("save new program: {}", program);

        return new DataRow(program);
    }

    private ServiceRegion getServiceLocation(@NotNull String region) {
        if (StringUtils.isEmpty(region)) {
            return new ServiceRegion();
        }

        ServiceRegion result = null;
        String[] regionPart = region.split(" ");

        for (String part : regionPart) {
            ServiceRegion dbRegion = findOrSaveServiceLocation(part, result != null ? result.getCode() : null);

            if (dbRegion != null) {
                dbRegion.setParent(result);
                result = dbRegion;
            }
        }


        return result;
    }

    private ServiceRegion findOrSaveServiceLocation(@NotNull String regionName, String parentCode) {
        if (StringUtils.isEmpty(regionName)) {
            return null;
        }

        Optional<ServiceRegion> result = regionRepository.findByName(regionName.trim());
        if (result.isPresent()) {
            return result.get();
        } else {
            ServiceRegion newLocation = new ServiceRegion();
            newLocation.setCode(newServiceLocationCode());
            newLocation.setName(regionName);
            newLocation.setParentCode(parentCode);

            regionRepository.save(newLocation);

            log.info("save new service location. {}", newLocation);
            return newLocation;
        }
    }

    private String newServiceLocationCode() {
        while (true) {
            String code = "reg" + String.format("%04d", System.currentTimeMillis() % 10000);

            if (!regionRepository.findById(code).isPresent()) {
                return code;
            }
        }
    }

    private String newProgramId() {
        while (true) {
            String id = "prg" + String.format("%04d", System.currentTimeMillis() % 10000);

            if (!programRepository.findById(id).isPresent()) {
                return id;
            }
        }
    }

    public Map<String, Object> search(SearchRequest request) throws ValidationException {
        log.info("search condition: {}", request);

        try {
            Finder finder = EcoProgramFinder.createRegionCodeFinder(request.getRegionCode());
            finder.find(programRepository, regionRepository);

            return finder.getResult();
        } catch (IllegalArgumentException e) {
            throw new ValidationException(e.getMessage());
        }
    }

    public Map<String, Object> searchRegionProgram(SearchRequest request) throws ValidationException {
        log.info("search condition: {}", request);

        try {
            Finder finder = EcoProgramFinder.createRegionFinder(request.getRegion());
            finder.find(programRepository, regionRepository);

            return finder.getResult();
        } catch (IllegalArgumentException e) {
            throw new ValidationException(e.getMessage());
        }
    }

    public Map<String, Object> countKeywordInIntro(SearchRequest condition) throws ValidationException {
        try {
            Finder finder = EcoProgramFinder.createIntroduceCountFinder(condition.getKeyword());

            log.info("search keyword: {}", condition.getKeyword());
            finder.find(programRepository, regionRepository);

            return finder.getResult();
        } catch (IllegalArgumentException e) {
            throw new ValidationException(e.getMessage());
        }
    }

    public Map<String, Object> countInAll(SearchRequest condition) throws ValidationException {
        try {
            Finder finder = EcoProgramFinder.createAllCountFinder(condition.getKeyword());

            log.info("search keyword: {}", condition.getKeyword());
            finder.find(programRepository, regionRepository);

            return finder.getResult();
        } catch (IllegalArgumentException e) {
            throw new ValidationException(e.getMessage());
        }
    }

    public Map<String, Object> recommend(SearchRequest condition) throws ValidationException {
        Finder finder = EcoProgramFinder.createRecommendFinder(condition.getRegion(), condition.getKeyword());
        finder.find(programRepository, regionRepository);

        return finder.getResult();
    }

    public DataRow updateEcoProgram(String programId, SaveRequest saveRequest) throws ValidationException, NotFoundResourceException {
        if (StringUtils.isEmpty(programId)) {
            throw new ValidationException("programId is null.");
        }

        dtoValidator.validate(saveRequest);

        Optional <EcotourismProgram> result = programRepository.findById(programId);
        if (result.isPresent()) {
            ServiceRegion serviceRegion = getServiceLocation(saveRequest.getRegion());

            EcotourismProgram dbData = result.get();
            dbData.setName(saveRequest.getName());
            dbData.setRegionCode(serviceRegion.getCode());
            dbData.setTheme(saveRequest.getTheme());
            dbData.setIntroduce(saveRequest.getIntroduce());
            dbData.setDetail(saveRequest.getDetail());
            dbData.setRegion(serviceRegion);

            programRepository.save(dbData);

            log.info("update to {}", dbData);

            return new DataRow(dbData);
        } else {
            throw new NotFoundResourceException("not found program id.");
        }
    }

    public synchronized void saveFileDataToDb() throws Exception {
        try {
            new CsvFileReader("./dump.csv", true).read(line -> {
                SaveRequest saveRequest = new SaveRequest();
                saveRequest.setName(line.get("프로그램명"));
                saveRequest.setTheme(line.get("테마별 분류"));
                saveRequest.setIntroduce(line.get("프로그램 소개"));
                saveRequest.setRegion(line.get("서비스 지역"));
                saveRequest.setDetail(line.get("프로그램 상세 소개"));

                saveNewEcoProgram(saveRequest);
            });
        } catch (Exception e) {
            log.error("fail to read source file.", e);
            throw e;
        }
    }
}
