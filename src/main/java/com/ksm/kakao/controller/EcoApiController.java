package com.ksm.kakao.controller;

import com.ksm.kakao.controller.dto.SaveRequest;
import com.ksm.kakao.controller.dto.SearchRequest;
import com.ksm.kakao.service.EcoProgramService;
import com.ksm.kakao.service.NotFoundResourceException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.balam.security.jwt.access.RestAccess;

import javax.validation.ValidationException;


@RestController
@RequestMapping("/api/eco")
@AllArgsConstructor
public class EcoApiController {
    private EcoProgramService ecoProgramService;

    @PostMapping("dump-data")
    @RestAccess(uri = "/api/eco/dump-data", method = "post", allRole = true)
    public void saveDumpData() throws Exception {
        ecoProgramService.saveFileDataToDb();
    }

    @GetMapping
    @RestAccess(uri = "/api/eco", method = "get", allRole = true)
    public ResponseEntity search(@RequestBody SearchRequest searchRequest) {
        try {
            return ResponseEntity.ok(ecoProgramService.search(searchRequest));
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("region-program")
    @RestAccess(uri = "/api/eco/region-program", method = "get", allRole = true)
    public ResponseEntity searchRegionProgram(@RequestBody SearchRequest searchRequest) {
        try {
            return ResponseEntity.ok(ecoProgramService.searchRegionProgram(searchRequest));
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("count-introduce-word")
    @RestAccess(uri = "/api/eco/count-introduce-word", method = "get", allRole = true)
    public ResponseEntity countIntroduceWord(@RequestBody SearchRequest searchRequest) {
        try {
            return ResponseEntity.ok(ecoProgramService.countKeywordInIntro(searchRequest));
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("count-all-word")
    @RestAccess(uri = "/api/eco/count-all-word", method = "get", allRole = true)
    public ResponseEntity countAllWord(@RequestBody SearchRequest searchRequest) {
        try {
            return ResponseEntity.ok(ecoProgramService.countInAll(searchRequest));
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("recommend")
    @RestAccess(uri = "/api/eco/recommend", method = "get", allRole = true)
    public ResponseEntity recommend(@RequestBody SearchRequest searchRequest) {
        try {
            return ResponseEntity.ok(ecoProgramService.recommend(searchRequest));
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping
    @RestAccess(uri = "/api/eco", method = "post", allRole = true)
    public ResponseEntity save(@RequestBody SaveRequest request) {
        try {
            return ResponseEntity.ok(ecoProgramService.saveNewEcoProgram(request));
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{programId}")
    @RestAccess(uri = "/api/eco/*", method = "put", allRole = true)
    public ResponseEntity update(@PathVariable("programId") String programId, @RequestBody SaveRequest request) {
        try {
            return ResponseEntity.ok(ecoProgramService.updateEcoProgram(programId, request));
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NotFoundResourceException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
