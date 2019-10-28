package com.ksm.kakao.repository;

import com.ksm.kakao.domain.ServiceRegion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RegionRepository extends JpaRepository<ServiceRegion, String> {
    Optional<ServiceRegion> findByName(String name);

    List<ServiceRegion> findByNameContains(String name);
}
