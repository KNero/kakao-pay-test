package com.ksm.kakao.controller;

import com.ksm.kakao.JwtSecurityFilter;
import com.ksm.kakao.controller.dto.SignRequest;
import com.ksm.kakao.repository.UserInfo;
import com.ksm.kakao.repository.UserInfoRepository;
import com.ksm.kakao.service.LoginService;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import team.balam.security.jwt.JwtSecurity;

import java.util.Optional;

public class UserApiControllerTest {
    private PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @Test
    @SuppressWarnings("unchecked")
    public void SignupTest() {
        JwtSecurity<JwtSecurityFilter.LoginInfo> jwtSecurity = Mockito.mock(JwtSecurity.class);
        UserInfoRepository repository = Mockito.mock(UserInfoRepository.class);
        Mockito.when(repository.findById(Mockito.anyString())).thenAnswer(invocation -> {
            String id = invocation.getArgument(0);

            if ("test".equals(id)) {
                return Optional.empty();
            } else {
                return Optional.of(new UserInfo());
            }
        });
        Mockito.doAnswer(invocation -> {
            UserInfo newUser = invocation.getArgument(0);

            Assert.assertEquals("test", newUser.getId());
            Assert.assertTrue(passwordEncoder.matches("password", newUser.getPassword()));

            return null;
        }).when(repository).save(Mockito.any(UserInfo.class));

        LoginService service = new LoginService(jwtSecurity, repository);
        UserApiController controller = new UserApiController(service);

        SignRequest request = new SignRequest();
        ResponseEntity responseEntity = controller.signup(request);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        request.setPassword("password");
        responseEntity = controller.signup(request);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        request.setId("test1"); // 존재하는 아이디
        responseEntity = controller.signup(request);
        Assert.assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());

        request.setId("test");
        responseEntity = controller.signup(request);
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }
}
