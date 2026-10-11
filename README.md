# Spring Boot Backend Framework Practice Assignment

## 1. 프로젝트 소개

본 프로젝트는 백엔드 프레임워크 실습 과제로, Spring Boot를 활용하여 RESTful API를 구현한 프로젝트입니다.

수업에서 제공된 Spring Boot 실습 코드를 기반으로 상품 관리 API를 확장하였으며, HTTP 메소드별 API 구현, 미들웨어, 다양한 HTTP 응답 코드 및 표준화된 JSON 응답 형식을 적용하였습니다.

### 개발 환경

- Java 21
- Spring Boot 3.5.7
- Gradle
- IntelliJ IDEA
- Spring Web
- Spring Validation
- Windows
- PowerShell 및 curl을 활용한 API 테스트

## 2. 주요 구현 기능

- GET, POST, PUT, DELETE 메소드별 2개 이상의 API 구현
- 상품 등록, 조회, 검색, 수정 및 삭제
- HTTP 요청 로깅 인터셉터 적용
- HTTP 상태 코드 200, 201, 400, 404, 500, 503 활용
- 공통 JSON 응답 형식 적용
- 메모리 기반 상품 데이터 저장
- 서버 오류 상태 코드 재현용 테스트 API 구현

## 3. API 명세

기본 API 경로: `/api/v3/items`

| HTTP Method | Endpoint | Description | Success Code |
|---|---|---|---|
| GET | `/api/v3/items` | 전체 상품 조회 | 200 |
| GET | `/api/v3/items/{id}` | 특정 상품 조회 | 200 |
| GET | `/api/v3/items/search` | 상품 이름 검색 및 페이징 | 200 |
| POST | `/api/v3/items` | 상품 등록 | 201 |
| POST | `/api/v3/items/with-header` | HTTP 헤더를 포함한 상품 등록 | 201 |
| PUT | `/api/v3/items/{id}` | 상품 이름 및 가격 수정 | 200 |
| PUT | `/api/v3/items/{id}/price` | 상품 가격 수정 | 200 |
| DELETE | `/api/v3/items/{id}` | 특정 상품 삭제 | 200 |
| DELETE | `/api/v3/items` | 전체 상품 삭제 | 200 |

총 9개의 상품 관리 API를 구현하였으며, GET 3개, POST 2개, PUT 2개, DELETE 2개로 구성됩니다.

## 4. Middleware 구현

Spring MVC의 `HandlerInterceptor`를 활용하여 요청 로깅 미들웨어를 구현하였습니다.

`LoggingInterceptor.java`에서 다음 기능을 수행합니다.

- HTTP 요청 메소드 출력
- 요청 URL 및 쿼리 문자열 출력
- 요청 완료 시 HTTP 응답 상태 코드 출력

`WebConfig.java`에서 `/api/**` 경로에 인터셉터를 등록하였습니다.

로그 출력 예시:

```text
[LoggingInterceptor] 요청 시작: GET /api/v3/items
[LoggingInterceptor] 요청 완료, status=200
```

## 5. HTTP 응답 코드

| Category | HTTP Code | Description |
|---|---|---|
| 2xx | 200 OK | 정상적인 조회, 수정 및 삭제 |
| 2xx | 201 Created | 상품 생성 성공 |
| 4xx | 400 Bad Request | 잘못된 상품 정보 또는 검색 조건 |
| 4xx | 404 Not Found | 존재하지 않는 상품 |
| 5xx | 500 Internal Server Error | 서버 내부 오류 응답 재현 |
| 5xx | 503 Service Unavailable | 서비스 이용 불가 응답 재현 |

500 및 503 응답은 실제 서버 장애를 유발하는 방식이 아니라 테스트 전용 API에서 의도적으로 해당 HTTP 상태 코드를 반환하는 방식으로 구현하였습니다.

테스트 API:

- `GET /api/test/error/500`
- `GET /api/test/error/503`

## 6. 표준 응답 형식

모든 직접 구현한 상품 관리 API는 `ApiResponse<T>` 클래스를 사용해 JSON 응답 형식을 통일하였습니다.

### 성공 응답 예시

```json
{
  "status": "success",
  "data": {
    "id": 1,
    "name": "Notebook",
    "price": 15000
  }
}
```

### 오류 응답 예시

```json
{
  "status": "error",
  "data": {
    "message": "상품을 찾을 수 없습니다."
  }
}
```

## 7. 실행 방법

### 프로젝트 실행

프로젝트 루트 디렉터리에서 다음 명령어를 실행합니다.

Windows:

```powershell
.\gradlew.bat bootRun
```

정상 실행 후 다음 주소에서 API를 확인할 수 있습니다.

```text
http://localhost:8080/api/v3/items
```

### GET 요청 테스트

```powershell
curl.exe -i http://localhost:8080/api/v3/items
```

### POST 요청 테스트

```powershell
$body = '{"name":"Notebook","price":15000}'
$body | Set-Content -Encoding utf8 test-create.json

curl.exe -i -X POST "http://localhost:8080/api/v3/items" -H "Content-Type: application/json" --data-binary "@test-create.json"
```

상품 정보는 서버 메모리에 저장되므로 서버 재시작 시 초기화됩니다. 별도의 데이터베이스 설치는 필요하지 않습니다.

## 8. 테스트 결과

| Test ID | Test Description | Expected Code | Result |
|---|---|---|---|
| T01 | GET 전체 조회 및 인터셉터 | 200 | PASS |
| T02 | 서버 내부 오류 응답 재현 | 500 | PASS |
| T03 | 서비스 이용 불가 응답 재현 | 503 | PASS |
| T04 | POST 상품 등록 | 201 | PASS |
| T05 | POST 헤더 포함 상품 등록 | 201 | PASS |
| T06 | PUT 상품 정보 수정 | 200 | PASS |
| T07 | PUT 상품 가격 수정 | 200 | PASS |
| T08 | DELETE 특정 상품 삭제 | 200 | PASS |
| T09 | DELETE 전체 상품 삭제 | 200 | PASS |
| T10 | GET 상품 검색 | 200 | PASS |
| T11 | GET 존재하지 않는 상품 조회 | 404 | PASS |
| T12 | POST 잘못된 상품 정보 | 400 | PASS |
| T13 | PUT 존재하지 않는 상품 수정 | 404 | PASS |
| T14 | GET 특정 상품 조회 | 200 | PASS |
| T15 | GET 검색 조건 오류 | 400 | PASS |

총 15개의 테스트를 진행하였습니다.

실행 및 테스트 화면 캡처는 `screenshots/` 디렉터리에 저장하였습니다.

## 9. 프로젝트 구조

```text
src/main/java/com/example/ksl/wsd2025/
├── Wsd2025Application.java
├── api/
│   ├── dto/
│   │   └── ItemDto.java
│   ├── request/
│   │   ├── ItemCreateRequest.java
│   │   └── ItemUpdateRequest.java
│   ├── response/
│   │   └── ApiResponse.java
│   ├── error/
│   │   └── ErrorTestController.java
│   ├── v1/
│   │   └── ItemController.java
│   ├── v2/
│   │   └── ItemController2.java
│   └── v3/
│       └── ItemController3.java
└── config/
    ├── LoggingInterceptor.java
    └── WebConfig.java
```

## 10. 참고 자료

- 수업에서 제공된 Spring Boot 실습 영상
- [JBNU-KSL-25-02 Spring Boot Tutorial](https://github.com/JBNU-KSL-25-02/springboot-tutorial)
- [Spring Boot 공식 문서](https://docs.spring.io/spring-boot/index.html)

## 11. 결론

본 실습을 통해 Spring Boot의 REST API 개발 과정과 HTTP 메소드별 요청 처리 방법을 학습하였습니다.

또한 Spring MVC 인터셉터를 활용한 요청 로깅, 다양한 HTTP 응답 코드 적용 및 공통 JSON 응답 형식 구현을 통해 백엔드 API의 기본 구조를 이해할 수 있었습니다.