package com.ksm.kakao.service.search;

import com.ksm.kakao.repository.EcotourismProgram;
import com.ksm.kakao.repository.ProgramRepository;
import com.ksm.kakao.repository.RegionRepository;
import com.ksm.kakao.service.DataRow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import javax.validation.ValidationException;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EcoProgramFinder {
    private EcoProgramFinder() {
    }

    public static Finder createRegionFinder(@NotEmpty String region) throws IllegalArgumentException {
        if (StringUtils.isEmpty(region)) {
            throw new IllegalArgumentException("region is empty");
        }

        return new RegionFinder(region);
    }

    public static Finder createRegionCodeFinder(@NotEmpty String regionCode) throws IllegalArgumentException {
        if (StringUtils.isEmpty(regionCode)) {
            throw new IllegalArgumentException("regionCode is empty");
        }

        return new RegionCodeFinder(regionCode);
    }

    public static Finder createIntroduceCountFinder(@NotEmpty String keyword) throws IllegalArgumentException {
        if (StringUtils.isEmpty(keyword)) {
            throw new IllegalArgumentException("keyword is empty");
        }

        return new IntroKeywordCountFinder(keyword);
    }

    public static Finder createAllCountFinder(@NotEmpty String keyword) throws IllegalArgumentException {
        if (StringUtils.isEmpty(keyword)) {
            throw new IllegalArgumentException("keyword is empty");
        }

        return new AllKeywordCountFounder(keyword);
    }

    public static Finder createRecommendFinder(@NotEmpty String region, @NotEmpty String keyword) {
        if (StringUtils.isEmpty(region)) {
            throw new ValidationException("region is empty.");
        } else if (StringUtils.isEmpty(keyword)) {
            throw new ValidationException("keyword is empty.");
        }

        return new RecommendationFinder(region, keyword);
    }

    private static int countMatches(String data, String keyword) {
        final int keywordLength = keyword.length();
        int count = 0;

        int offset = 0;
        while ((offset = data.indexOf(keyword, offset)) > -1) {
            ++count;
            offset += keywordLength;
        }

        return count;
    }

    private static class RegionCodeFinder implements Finder {
        private String regionCode;

        private List<EcotourismProgram> resultList = new ArrayList<>();

        private RegionCodeFinder(String regionCode) {
            this.regionCode = regionCode;
        }

        @Override
        public void find(ProgramRepository programRepository, RegionRepository regionRepository) {
            resultList = programRepository.findByRegionCode(regionCode);
        }

        @Override
        public Map<String, Object> getResult() {
            HashMap<String, Object> response = new HashMap<>();
            ArrayList<DataRow> list = new ArrayList<>();
            response.put("data", list);

            resultList.forEach(d -> list.add(new DataRow(d)));

            return response;
        }
    }

    private static class RegionFinder implements Finder {
        private String region;

        private List<Map<String, String>> resultList = new ArrayList<>();
        private String searchRegionCode;

        private RegionFinder(String region) {
            this.region = region;
        }

        @Override
        public void find(ProgramRepository programRepository, RegionRepository regionRepository) {
            regionRepository.findByName(region).ifPresent(region -> {
                searchRegionCode = region.getCode();

                List<Map<String, Object>> result = programRepository.findByParentRegionCode(searchRegionCode);
                result.forEach(data -> {
                    HashMap<String, String> row = new HashMap<>();
                    row.put("prgm_name", (String) data.get("name"));
                    row.put("theme", (String) data.get("theme"));
                    resultList.add(row);
                });
            });
        }

        @Override
        public Map<String, Object> getResult() {
            HashMap<String, Object> response = new HashMap<>();
            response.put("region", searchRegionCode);
            response.put("programs", resultList);
            return response;
        }
    }

    private static class IntroKeywordCountFinder implements Finder {
        private String keyword;
        private List<Map<String, Object>> resultList = new ArrayList<>();

        private IntroKeywordCountFinder(String keyword) {
            this.keyword = keyword;
        }

        @Override
        public void find(ProgramRepository programRepository, RegionRepository regionRepository) {
            List<Map<String, Object>> result = programRepository.findByIntroduceContains(keyword);
            result.forEach(data -> {
                String regionCode = (String) data.get("region_code");
                int count = (Integer) data.get("cnt");

                regionRepository.findById(regionCode).ifPresent(region -> {
                    HashMap<String, Object> row = new HashMap<>();
                    row.put("region", region.getFullAddress());
                    row.put("count", count);
                    resultList.add(row);
                });
            });
        }

        @Override
        public Map<String, Object> getResult() {
            HashMap<String, Object> response = new HashMap<>();
            response.put("keyword", keyword);
            response.put("programs", resultList);
            return response;
        }
    }

    private static class AllKeywordCountFounder implements Finder {
        private String keyword;
        private int count;

        private AllKeywordCountFounder(String keyword) {
            this.keyword = keyword;
        }

        @Override
        public void find(ProgramRepository programRepository, RegionRepository regionRepository) {
            final int pageSize = 10;
            Pageable pageRequest = PageRequest.of(0, pageSize);

            while (true) {
                Page<EcotourismProgram> list = programRepository.findAll(pageRequest);
                for (EcotourismProgram row : list) {
                    count += countMatches(row.getDetail(), keyword);
                }

                if (list.getContent().size() < pageSize) {
                    return;
                } else {
                    pageRequest = pageRequest.next();
                }
            }
        }

        @Override
        public Map<String, Object> getResult() {
            HashMap<String, Object> response = new HashMap<>();
            response.put("keyword", keyword);
            response.put("count", count);
            return response;
        }
    }

    @Slf4j
    private static class RecommendationFinder implements Finder {
        private String region;
        private String keyword;

        private float maxScore;
        private String programId;

        private RecommendationFinder(String region, String keyword) {
            this.region = region;
            this.keyword = keyword;
        }

        @Override
        public void find(ProgramRepository programRepository, RegionRepository regionRepository) {
            regionRepository.findByNameContains(region).forEach(serviceRegion ->
                    programRepository.findByParentRegionCode(serviceRegion.getCode()).forEach(data -> {
                        String name = (String) data.get("name");
                        String theme = (String) data.get("theme");
                        String intro = (String) data.get("introduce");
                        String detail = (String) data.get("detail");

                        int themeCount = countMatches(theme, keyword);
                        int introCount = countMatches(intro, keyword);
                        int detailCount = countMatches(detail, keyword);
                        float score = calculateScore(themeCount, introCount, detailCount);

                        log.info("[{}] keyword count. theme: {}, intro: {}, detail: {}, score: {}", name, themeCount, introCount, detailCount, score);

                        if (maxScore < score) {
                            maxScore = score;
                            programId = (String) data.get("id");

                            log.info("recommendation program id: {}, name: {}, score: {}", programId, name, maxScore);
                        }
                    }));
        }

        private float calculateScore(int themeCount, int introCount, int detailCount) {
            // 테마, 소개, 상세 별 가중치가 별도로 정의된다.
            float themeWeight = 1.8f;
            float introWeight = 1.5f;
            float detailWeight = 1f;
            float alpha = 1f;

            // 만약 테마, 소개, 상세에서 중복적으로 나올 경우 부가적인 가중치를 더 준다.
            if (themeCount > 0 && introCount > 0 && detailCount > 0) {
                alpha = 2f;
            } else if (themeCount > 0 && introCount > 0) {
                alpha = 1.5f;
            } else if (themeCount > 0 && detailCount > 0) {
                alpha = 1.8f;
            } else if (introCount > 0 && detailCount > 0) {
                alpha = 1.3f;
            }

            return ((themeCount * themeWeight) + (introCount * introWeight) + (detailCount * detailWeight)) * alpha;
        }

        @Override
        public Map<String, Object> getResult() {
            HashMap<String, Object> response = new HashMap<>();
            response.put("program", programId);
            return response;
        }
    }
}
