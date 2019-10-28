package com.ksm.kakao.service;

import com.ksm.kakao.JwtSecurityFilter;
import com.ksm.kakao.domain.UserInfo;
import com.ksm.kakao.repository.UserInfoRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import team.balam.security.jwt.JwtSecurity;

import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class LoginService {
    private static final PasswordEncoder PASSWORD_ENCODER = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    private JwtSecurity<JwtSecurityFilter.LoginInfo> jwtSecurity;
    private UserInfoRepository userInfoRepository;

    private String generateToken(String id) {
        return jwtSecurity.generateToken(new JwtSecurityFilter.LoginInfo(id));
    }

    public String signup(@NotNull String id, @NotNull String password) throws ResourceDuplicationException, ValidationException {
        if (StringUtils.isEmpty(id) || StringUtils.isEmpty(password)) {
            throw new ValidationException("id or password is empty.");
        }

        Optional<UserInfo> result = userInfoRepository.findById(id);
        if (result.isPresent()) {
            throw new ResourceDuplicationException("id already exists.");
        } else {
            UserInfo userInfo = new UserInfo();
            userInfo.setId(id);
            userInfo.setPassword(PASSWORD_ENCODER.encode(password));

            userInfoRepository.save(userInfo);

            return generateToken(id);
        }
    }

    public String signin(String id, String password) throws NotFoundResourceException, ValidationException {
        if (StringUtils.isEmpty(id) || StringUtils.isEmpty(password)) {
            throw new ValidationException("id or password is empty.");
        }

        Optional<UserInfo> result = userInfoRepository.findById(id);
        if (result.isPresent()) {
            UserInfo dbUserInfo = result.get();

            if (PASSWORD_ENCODER.matches(password, dbUserInfo.getPassword())) {
                return generateToken(id);
            } else {
                throw new NotFoundResourceException("not match password");
            }
        } else {
            throw new NotFoundResourceException("id not exists.");
        }
    }

    public String refreshToken() {
        String id = jwtSecurity.getAuthenticationInfo().getId();

        log.info("refresh jwt. id: {}", id);
        return generateToken(id);
    }
}
