package com.ksm.kakao.controller;

import com.ksm.kakao.TestHelper;
import com.ksm.kakao.controller.dto.SaveRequest;
import com.ksm.kakao.controller.dto.SearchRequest;
import com.ksm.kakao.repository.EcotourismProgram;
import com.ksm.kakao.repository.ProgramRepository;
import com.ksm.kakao.repository.RegionRepository;
import com.ksm.kakao.service.DataRow;
import com.ksm.kakao.service.DtoValidator;
import com.ksm.kakao.service.EcoProgramService;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public class ApiEcoControllerTest {
    @Test
    public void saveResponseTest() {
        RegionRepository regionRepository = Mockito.mock(RegionRepository.class);
        ProgramRepository programRepository = Mockito.mock(ProgramRepository.class);
        EcoProgramService service = new EcoProgramService(regionRepository, programRepository, new DtoValidator());

        EcoApiController controller = new EcoApiController(service);

        SaveRequest saveRequest = new SaveRequest();
        ResponseEntity responseEntity = controller.save(saveRequest);
        //필수값 name, location 모두 null
        Assert.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        saveRequest.setName("test-name");
        responseEntity = controller.save(saveRequest);
        //필수값 location 모두 null
        Assert.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        saveRequest.setRegion("test-location");
        responseEntity = controller.save(saveRequest);
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        DataRow savedDto = (DataRow) responseEntity.getBody();
        Assert.assertNotNull(savedDto);
    }

    @Test
    public void saveTest() {
        SaveRequest saveRequest = new SaveRequest();
        saveRequest.setName("test-name");
        saveRequest.setTheme("test-theme");
        saveRequest.setRegion("test-location");
        saveRequest.setIntroduce("test-introduce");
        saveRequest.setDetail("detail-test");

        RegionRepository regionRepository = Mockito.mock(RegionRepository.class);
        ProgramRepository programRepository = Mockito.mock(ProgramRepository.class);
        Mockito.doAnswer(invocation -> {
            EcotourismProgram param = invocation.getArgument(0);

            checkEqualsRequestAndDto(saveRequest, param); //request 의 값이 dto 로 잘 전달되었는지 검사.
            checkProgramId(param.getId()); // id 생성 검사
            checkValidLocationCode(param.getRegionCode()); // location code 생성검

            return null;
        }).when(programRepository).save(Mockito.any(EcotourismProgram.class));

        EcoProgramService service = new EcoProgramService(regionRepository, programRepository, new DtoValidator());
        EcoApiController controller = new EcoApiController(service);

        ResponseEntity responseEntity = controller.save(saveRequest);
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        DataRow response = (DataRow) responseEntity.getBody();
        Assert.assertNotNull(response); // 응답 생성 유무 검사

        checkProgramId(response.getId()); // 응답에 새로 생성된 ID 포함 검사
        checkValidLocationCode(response.getRegionCode()); // 응답에 location code 포함 검사
        checkEqualsRequestAndResponse(saveRequest, response);
    }

    public static void checkValidLocationCode(String regionCode) {
        Assert.assertNotNull(regionCode);
        Assert.assertEquals(7, regionCode.length());
        Assert.assertTrue(regionCode.startsWith("reg"));
    }

    private void checkEqualsRequestAndDto(SaveRequest request, EcotourismProgram dto) {
        Assert.assertEquals(request.getName(), dto.getName());
        Assert.assertEquals(request.getTheme(), dto.getTheme());
        Assert.assertEquals(request.getIntroduce(), dto.getIntroduce());
        Assert.assertEquals(request.getDetail(), dto.getDetail());
        Assert.assertEquals(request.getRegion(), dto.getRegion().getName());
    }

    private void checkProgramId(String programId) {
        Assert.assertNotNull(programId);
        Assert.assertEquals(7, programId.length());
        Assert.assertTrue(programId.startsWith("prg"));
    }

    private void checkEqualsRequestAndResponse(SaveRequest request, DataRow response) {
        Assert.assertEquals(request.getName(), response.getName());
        Assert.assertEquals(request.getTheme(), response.getTheme());
        Assert.assertEquals(request.getIntroduce(), response.getIntroduce());
        Assert.assertEquals(request.getDetail(), response.getDetail());
        Assert.assertEquals(request.getRegion(), response.getRegion());
    }

    @Test
    public void updateResponseTest() {
        RegionRepository regionRepository = Mockito.mock(RegionRepository.class);
        ProgramRepository programRepository = TestHelper.createDemoProgramRepository();

        EcoProgramService service = new EcoProgramService(regionRepository, programRepository, new DtoValidator());
        EcoApiController controller = new EcoApiController(service);

        SaveRequest saveRequest = new SaveRequest();
        ResponseEntity responseEntity = controller.update(null, saveRequest);
        // id, name, region 모두 null
        Assert.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        responseEntity = controller.update("test-id", saveRequest);
        // name, region is null
        Assert.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        saveRequest.setName("test-name");
        responseEntity = controller.update("test-id", saveRequest);
        // region is null
        Assert.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        saveRequest.setRegion("test-location");
        responseEntity = controller.update("test-id", saveRequest);
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        DataRow savedDto = (DataRow) responseEntity.getBody();
        Assert.assertNotNull(savedDto);

        // 존재하지 않는 id 전달
        responseEntity = controller.update("not-found-id", saveRequest);
        Assert.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void updateTest() {
        SaveRequest saveRequest = new SaveRequest();
        saveRequest.setName("changed-test-name");
        saveRequest.setTheme("changed-test-theme");
        saveRequest.setRegion("changed-test-location");
        saveRequest.setIntroduce("changed-test-introduce");
        saveRequest.setDetail("changed-test-detail");

        RegionRepository regionRepository = Mockito.mock(RegionRepository.class);
        ProgramRepository programRepository = TestHelper.createDemoProgramRepository();
        Mockito.doAnswer(invocation -> {
            EcotourismProgram dto = invocation.getArgument(0);

            checkEqualsRequestAndDto(saveRequest, dto); // 변경된 값이 save 로 전달되는지 검사

            return null;
        }).when(programRepository).save(Mockito.any(EcotourismProgram.class));

        EcoProgramService service = new EcoProgramService(regionRepository, programRepository, new DtoValidator());
        EcoApiController controller = new EcoApiController(service);

        ResponseEntity responseEntity = controller.update("test-id", saveRequest);
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        DataRow updatedDto = (DataRow) responseEntity.getBody();
        Assert.assertNotNull(updatedDto); // 응답 생성 유무 검사

        checkEqualsRequestAndResponse(saveRequest, updatedDto); // 변경된 값이 client 로 정상 전달 검
        checkValidLocationCode(updatedDto.getRegionCode());
    }



    @Test
    public void findByRegionCodeTest() {
        RegionRepository regionRepository = Mockito.mock(RegionRepository.class);
        ProgramRepository programRepository = TestHelper.createDemoProgramRepository();
        EcoProgramService service = new EcoProgramService(regionRepository, programRepository, new DtoValidator());
        EcoApiController controller = new EcoApiController(service);

        SearchRequest request = new SearchRequest();
        ResponseEntity responseEntity = controller.search(request);
        // regionCode is empty
        Assert.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        request.setRegionCode("reg2222");
        responseEntity = controller.search(request);
        Map result = (Map) responseEntity.getBody();
        Assert.assertNotNull(result);

        List dataList = (List) result.get("data");
        Assert.assertEquals(0, dataList.size());

        request.setRegionCode("reg1234");
        responseEntity = controller.search(request);
        result = (Map) responseEntity.getBody();
        Assert.assertNotNull(result);

        dataList = (List) result.get("data");
        Assert.assertNotEquals(0, dataList.size());
    }

    @Test
    public void findByRegion() {
        RegionRepository regionRepository = TestHelper.createDemoRegionRepository();
        ProgramRepository programRepository = TestHelper.createDemoProgramRepository();

        EcoProgramService service = new EcoProgramService(regionRepository, programRepository, new DtoValidator());
        EcoApiController controller = new EcoApiController(service);

        SearchRequest request = new SearchRequest();
        ResponseEntity responseEntity = controller.searchRegionProgram(request);
        // region is empty
        Assert.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        request.setRegion("송파구");
        responseEntity = controller.searchRegionProgram(request);
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        Map response = (Map) responseEntity.getBody();
        Assert.assertNotNull(response);
        Assert.assertEquals("reg1234", response.get("region"));
        Assert.assertEquals(2, ((List) response.get("programs")).size());
    }

    @Test
    public void getIntroKeywordCountTest() {
        RegionRepository regionRepository = TestHelper.createDemoRegionRepository();
        ProgramRepository programRepository = TestHelper.createDemoProgramRepository();

        EcoProgramService service = new EcoProgramService(regionRepository, programRepository, new DtoValidator());
        EcoApiController controller = new EcoApiController(service);

        SearchRequest request = new SearchRequest();

        ResponseEntity responseEntity = controller.countIntroduceWord(request);
        // keyword is empty
        Assert.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        request.setKeyword("문화유산");
        responseEntity = controller.countIntroduceWord(request);
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        Map body = (Map) responseEntity.getBody();
        Assert.assertNotNull(body);
        Assert.assertEquals(request.getKeyword(), body.get("keyword"));
        Assert.assertEquals(1, ((List) body.get("programs")).size());

        Map program = (Map) ((List) body.get("programs")).get(0);
        Assert.assertEquals(2, program.get("count"));
        Assert.assertEquals("서울시 송파구", program.get("region"));
    }

    @Test
    public void getAllKeywordCountTest() {
        RegionRepository regionRepository = TestHelper.createDemoRegionRepository();
        ProgramRepository programRepository = TestHelper.createDemoProgramRepository();

        EcoProgramService service = new EcoProgramService(regionRepository, programRepository, new DtoValidator());
        EcoApiController controller = new EcoApiController(service);

        SearchRequest request = new SearchRequest();

        ResponseEntity responseEntity = controller.countAllWord(request);
        // keyword is empty
        Assert.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        request.setKeyword("test");
        responseEntity = controller.countAllWord(request);
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        Map body = (Map) responseEntity.getBody();
        Assert.assertNotNull(body);
        Assert.assertEquals(request.getKeyword(), body.get("keyword"));
        Assert.assertEquals(1, body.get("count"));
    }

    @Test
    public void getRecommendation() {
        RegionRepository regionRepository = TestHelper.createDemoRegionRepository();
        ProgramRepository programRepository = TestHelper.createDemoProgramRepository();

        EcoProgramService service = new EcoProgramService(regionRepository, programRepository, new DtoValidator());
        EcoApiController controller = new EcoApiController(service);

        SearchRequest request = new SearchRequest();

        ResponseEntity responseEntity = controller.recommend(request);
        // region, keyword is empty
        Assert.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        request.setKeyword("test");
        // region is empty
        responseEntity = controller.recommend(request);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        request.setRegion("송파");
        responseEntity = controller.recommend(request);
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        Map body = (Map) responseEntity.getBody();
        Assert.assertNotNull(body);
        /*
        TestHelper.createDemoProgramRepository 의 2개의 dto 중
        dbDto2 는 test 라는 단어가 전혀 포함되어 있지 않기 때문에
        가중치가 적용되지 않아 test 단어가 포함된 test-id 가 나온다.
         */
        Assert.assertEquals("test-id", body.get("program"));
    }
}
