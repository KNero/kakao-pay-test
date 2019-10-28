package com.ksm.kakao.repository;

import com.ksm.kakao.domain.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInfoRepository extends JpaRepository<UserInfo, String> {
}
