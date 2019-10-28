package com.ksm.kakao.repository;

import com.ksm.kakao.entity.EcotourismProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface ProgramRepository extends JpaRepository<EcotourismProgram, String> {
    List<EcotourismProgram> findByRegionCode(String regionCode);

    @Query(value = "with recursive tree_table (code, name, parent_code) as (" +
            "   select code, name, parent_code from service_region sr where sr.code=?1" +
            "   union all" +
            "   select sr.code, sr.name, sr.parent_code from service_region sr, tree_table tt where sr.parent_code=tt.code" +
            ")" +
            "select ep.* from tree_table tt, ecotourism_program ep where tt.code=ep.region_code", nativeQuery = true)
    List<Map<String, Object>> findByParentRegionCode(String regionCode);

    @Query(value = "select region_code, count(*) cnt from ecotourism_program where introduce like '%' || ?1 || '%' group by region_code", nativeQuery = true)
    List<Map<String, Object>> findByIntroduceContains(String keyword);
}
