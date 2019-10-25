## 개발환경
- Java8
- Spring framework 2.1.3.RELEASE
- JPA
- SQLite DB
- [JWT](https://github.com/KNero/jwt-security) (지원자의 라이브러리)

## 실행방법
프로젝트 폴더 하위의 `startup.sh` 실행하면 Gradle 에 의해서 Test Case 검증 후 빌드하여 실행됩니다.

## 문제해결
#### *서비스 지역 컬럼에서 특정 지역에서 진행되는 프로그램명과 테마 출력 API*
- 서비스 지역이 포함된 하위의 모든 지역을 검색
- 서비스 지역의 각 부분이 모두 지역코드가 매핑되어야 한다. (출에 지역 코드를 전달하기 위해)

예) 강원도 평창군 진부면 -> 강원도(reg0001), 평창군(reg0002), 진부면(reg0003)

두 조건을 만족시키기 위해 서비스 지역을 별도의 테이블로 구성하고 tree 구조로 저장
```
service_region table

code     name  parent_code
---------------------
reg0001  강원도  null    <-- parent_node 가 null 이면 root 지역
reg0002  평창군  reg0001
reg0003  진부면  reg0002 <-- 관광정보 테이블에는 reg0003 으로 저장된다.
```

평창군으로 검색할 경우 평창군의 하위지역과 관광정보테이블을 join 하여 데이터를 찾는다.

#### *생태관광 프로그램을 추천하는 API*
입력의 region 이 포함된 모든 지역을 검색 후 결과에 포함된 프로그램을 검색한다. (상단 API 와 같은 방법)

가중치는 `테마 > 프로그램 소개 > 상세 소개` 로 적용되고 만약 각 항목에 중복적으로 나온다면 추가 가중치를 준다.
`테마, 소개, 상세(모든 항목에 포함) > 테마, 소개 > 테마, 상세 > 소개, 상세`

각 프로그램 별로 가중치를 계산하여 가장 높은 프로그램의 ID 를 반환 
(참고 소스 `com.ksm.kakao.service.EcoProgramFinder.RecommendationFinder`)

## API
signin, signup 을 제외한 모든 API 는 jwt 를 검사하기 때문에 header Authorization 을 포함해야 합니다.
```
Authorization: Bearer eyJyb2xlIjoidXNlciIsImFsZyI6IkhTMjU2In0=.eyJpZCI6InRlc3QifQ==.Oq/r00PxlUnq5DuNkX49CjE3fsA6KLJk/RcPZBlAPLI=
```
- 계정 생성 : POST /api/user/signup
- 로그인 : POST /api/user/signin
- 토큰 재발급 : POST /api/user/refresh
---
- 파일 데이터 저장: POST /api/eco/dump-data
- 지역코드로 조회: GET /api/eco
- 프로그램 추가: POST /api/eco
- 프로그램 수정: PUT /api/eco/{programId}
```
추가/수정 입력
{
	"name": "test-name1",
	"theme": "test-theme1",
	"region": "test-location1",
	"introduce": "test-introduce1",
	"detail": "test-detail1"
}
```
---
- 특정지역 프로그램, 테마 검색: GET /api/eco/region-program
- 프로그램 소개에 문자열 포함된 지역 검색:  GET /api/eco/count-introduce-word
- 전체 단어 출현빈도 계산: GET /api/eco/count-all-word
- 프로그램 추천: GET /api/eco/recommend
