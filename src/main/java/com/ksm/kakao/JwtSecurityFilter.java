package com.ksm.kakao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import team.balam.security.jwt.AuthToken;
import team.balam.security.jwt.JwtFilter;
import team.balam.security.jwt.JwtSecurity;

import javax.servlet.FilterConfig;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class JwtSecurityFilter extends JwtFilter<JwtSecurityFilter.LoginInfo> {
    private static final String JWT_SECRET_KEY = "0651b92344b34c95a4d871f9db2e4d38";
    private static final long TOKE_EXPIRED_MS = TimeUnit.HOURS.toMillis(3);

    @Bean
    public JwtSecurity<LoginInfo> jwtFilter() {
        return jwtSecurity;
    }

    @Override
    protected JwtSecurity<LoginInfo> build(JwtSecurity.Builder<LoginInfo> builder, FilterConfig filterConfig) {
        return builder
                .setSecretKey(JWT_SECRET_KEY)
                .setPackages("com.ksm.kakao.controller")
                .setAuthTokenConverter(data ->
                        AuthToken.builder()
                                .info(data.toMap())
                                .role("user")
                                .expirationTime(new Date(System.currentTimeMillis() + TOKE_EXPIRED_MS))
                                .build())
                .setObjectConverter(LoginInfo::of)
                .build();
    }

    @Getter
    @AllArgsConstructor
    public static class LoginInfo {
        private String id;

        private Map<String, Object> toMap() {
            HashMap<String, Object> info = new HashMap<>();
            info.put("id", id);

            return info;
        }

        private static LoginInfo of(AuthToken authToken) {
            Map<String, Object> info = authToken.getInfo();
            return new LoginInfo((String) info.get("id"));
        }
    }
}
