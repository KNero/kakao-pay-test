package com.ksm.kakao.controller;

import com.ksm.kakao.controller.dto.SignRequest;
import com.ksm.kakao.service.LoginService;
import com.ksm.kakao.service.NotFoundResourceException;
import com.ksm.kakao.service.ResourceDuplicationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.balam.security.jwt.access.RestAccess;

import javax.validation.ValidationException;
import java.util.HashMap;

@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
@Slf4j
public class UserApiController {
    private LoginService loginService;

    @PostMapping("signup")
    public ResponseEntity signup(@RequestBody SignRequest request) {
        String id = request.getId();
        String password = request.getPassword();

        try {
            String jwt = loginService.signup(id, password);

            HashMap<String, String> tokenInfo = new HashMap<>();
            tokenInfo.put("jwt", jwt);

            log.info("success signup {}", id);
            return ResponseEntity.ok(tokenInfo);
        } catch (ResourceDuplicationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("signin")
    public ResponseEntity signin(@RequestBody SignRequest request) {
        String id = request.getId();
        String password = request.getPassword();

        try {
            String jwt = loginService.signin(id, password);

            HashMap<String, String> tokenInfo = new HashMap<>();
            tokenInfo.put("jwt", jwt);

            log.info("served jwt to {}", id);
            return ResponseEntity.ok(tokenInfo);
        } catch (NotFoundResourceException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("refresh")
    @RestAccess(uri = "/api/login/refresh", method = "post", allRole = true)
    public ResponseEntity refreshToken() {
        String jwt = loginService.refreshToken();

        HashMap<String, String> tokenInfo = new HashMap<>();
        tokenInfo.put("jwt", jwt);

        return ResponseEntity.ok(tokenInfo);
    }
}
