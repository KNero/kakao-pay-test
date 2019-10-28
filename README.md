## 개발환경
- Java8
- Spring framework 2.1.3.RELEASE
- JPA
- SQLite DB
- [JWT](https://github.com/KNero/jwt-security) (지원자의 라이브러리)

## 실행방법
프로젝트 폴더 하위의 `startup.sh` 실행하면 Gradle 에 의해서 Test Case 검증 후 빌드하여 실행됩니다.
(초기 파일 데이터는 저장된 상태입니다.)

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

추가 가중치: `테마, 소개, 상세(모든 항목에 포함) > 테마, 소개 > 테마, 상세 > 소개, 상세`

각 프로그램 별로 가중치를 계산하여 가장 높은 프로그램의 ID 를 반환 
(참고 소스 `com.ksm.kakao.service.EcoProgramFinder.RecommendationFinder`)

## API
signin, signup 을 제외한 모든 API 는 jwt 를 검사하기 때문에 header Authorization 을 포함해야 합니다.
```
Authorization: Bearer eyJyb2xlIjoidXNlciIsImFsZyI6IkhTMjU2In0=.eyJpZCI6InRlc3QifQ==.Oq/r00PxlUnq5DuNkX49CjE3fsA6KLJk/RcPZBlAPLI=
```
|API설명|요청 URL|input|output|
|:------|:--------------------|:----|:----|
|계정 생성|POST /api/user/signup|{<br>&nbsp;&nbsp;&nbsp;&nbsp;"ID": "test",<br>&nbsp;&nbsp;&nbsp;&nbsp;"PW": "password"<br>}|{<br>&nbsp;&nbsp;&nbsp;&nbsp;"jwt": "eyJy..."<br>}|
|로그인|POST /api/user/signin|{<br>&nbsp;&nbsp;&nbsp;&nbsp;"ID": "test",<br>&nbsp;&nbsp;&nbsp;&nbsp;"PW": "password"<br>}|{<br>&nbsp;&nbsp;&nbsp;&nbsp;"jwt": "eyJy..."<br>}|
|토큰 재발급|POST /api/user/refresh|{<br>&nbsp;&nbsp;&nbsp;&nbsp;"ID": "test",<br>&nbsp;&nbsp;&nbsp;&nbsp;"PW": "password"<br>}|{<br>"jwt": "eyJy..."<br>}|

---

|API설명|요청 URL|input|output|
|:------|:--------------------|:----|:----|
|파일 데이터 저장|POST /api/eco/dump-data|||
|지역코드로 조회|GET /api/eco|{<br>&nbsp;&nbsp;&nbsp;&nbsp;"regionCode": "reg1454"<br>}|{<br>&nbsp;&nbsp;&nbsp;&nbsp;"data": [<br>    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"id": "prg1462",<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"name": "\"속리산과 함께하는 자연나누리\"",<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"theme": "문화생태체험,",<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"region": "충청북도 보은군 속리산면 상판리 법주사로84",<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"regionCode": "reg1454",<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"introduce": "다양한 프로그램 중 1~2가지를 선택, 일정을 협의 하여 진행.",<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"detail": " 영화, 연극, 놀이공원, 동물원, 기마체험 등\r 다양한 문화적 혜택을 다문화, 저소득층 등\r 에게 제공하는 프로그램."<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}<br>&nbsp;&nbsp;&nbsp;&nbsp;]<br>}|
|프로그램 추가|POST /api/eco|{<br>&nbsp;&nbsp;&nbsp;&nbsp;"name": "test-name",<br>&nbsp;&nbsp;&nbsp;&nbsp;"theme": "test-theme",<br>&nbsp;&nbsp;&nbsp;&nbsp;"region": "경기도 구리시",<br>&nbsp;&nbsp;&nbsp;&nbsp;"introduce": "test-introduce",<br>&nbsp;&nbsp;&nbsp;&nbsp;"detail": "test-detail"<br>}|{<br>&nbsp;&nbsp;&nbsp;&nbsp;"id": "prg8368",<br>&nbsp;&nbsp;&nbsp;&nbsp;"name": "test-name",<br>&nbsp;&nbsp;&nbsp;&nbsp;"theme": "test-theme",<br>&nbsp;&nbsp;&nbsp;&nbsp;"region": "경기도 구리시",<br>&nbsp;&nbsp;&nbsp;&nbsp;"introduce": "test-introduce",<br>&nbsp;&nbsp;&nbsp;&nbsp;"detail": "test-detail"<br>}|
|프로그램 수정|PUT /api/eco/{programId}|{<br>&nbsp;&nbsp;&nbsp;&nbsp;"name": "test-name",<br>&nbsp;&nbsp;&nbsp;&nbsp;"theme": "test-theme",<br>&nbsp;&nbsp;&nbsp;&nbsp;"region": "경기도 구리시",<br>&nbsp;&nbsp;&nbsp;&nbsp;"introduce": "test-introduce",<br>&nbsp;&nbsp;&nbsp;&nbsp;"detail": "test-detail"<br>}|{<br>&nbsp;&nbsp;&nbsp;&nbsp;"id": "prg8368",<br>&nbsp;&nbsp;&nbsp;&nbsp;"name": "test-name",<br>&nbsp;&nbsp;&nbsp;&nbsp;"theme": "test-theme",<br>&nbsp;&nbsp;&nbsp;&nbsp;"region": "경기도 구리시",<br>&nbsp;&nbsp;&nbsp;&nbsp;"introduce": "test-introduce",<br>&nbsp;&nbsp;&nbsp;&nbsp;"detail": "test-detail"<br>}|

---

|API설명|요청 URL|input|output|
|:------|:--------------------|:----|:----|
|특정지역 프로그램|GET /api/eco/region-program|{<br>&nbsp;&nbsp;&nbsp;&nbsp;"region": "강원도"<br>}|{<br>&nbsp;&nbsp;&nbsp;&nbsp;"programs": [<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"prgm_name": "자연과 문화를 함께 즐기는 설악산 기행",<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"theme": "문화생태체험,자연생태체험,"<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;},<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"prgm_name": "[설악산] 설악산에서 길을 묻다(설악을 내 품에)",<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"theme": "아동·청소년 체험학습,"<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}, ...<br>&nbsp;&nbsp;&nbsp;&nbsp;],<br>&nbsp;&nbsp;&nbsp;&nbsp;"region": "reg7283"<br>}|
|프로그램 소개에 문자열 포함된 지역 검색|GET /api/eco/count-introduce-word|{<br>&nbsp;&nbsp;&nbsp;&nbsp;"keyword": "세계문화유산"<br>}|{<br>&nbsp;&nbsp;&nbsp;&nbsp;"programs": [<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"count": 2,<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"region": "경상북도 경주시"<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}<br>&nbsp;&nbsp;&nbsp;&nbsp;],<br>&nbsp;&nbsp;&nbsp;&nbsp;"keyword": "세계문화유산"<br>}|
|전체 단어 출현빈도 계산|GET /api/eco/count-all-word|{<br>&nbsp;&nbsp;&nbsp;&nbsp;"keyword": "문화"<br>}|{<br>&nbsp;&nbsp;&nbsp;&nbsp;"count": 59,<br>&nbsp;&nbsp;&nbsp;&nbsp;"keyword": "문화"<br>}|
|프로그램 추천|GET /api/eco/recommend|{<br>&nbsp;&nbsp;&nbsp;&nbsp;"region": "남해",<br>&nbsp;&nbsp;&nbsp;&nbsp;"keyword": "생태체험"<br>}|{<br>&nbsp;&nbsp;&nbsp;&nbsp;"program": "prg8304"<br>}|
