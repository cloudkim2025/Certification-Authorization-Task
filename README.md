## "BasicBoard2 - Spring Boot JWT 인증 및 게시판 시스템"

BasicBoard2는 Spring Boot 기반의 간단한 게시판 프로젝트로, 회원 가입, 로그인, 게시글 CRUD, JWT 인증 및 보안 기능을 포함합니다.

## 기술 스택

- **Backend**: Java 21, Spring Boot 3.4.3, Spring Security, MyBatis, JWT
- **Database**: MySQL (MariaDB 호환)
- **Frontend**: HTML, CSS, JavaScript

## 회원가입 및 로그인 과정

### 1. 회원가입 (Sign-Up)

회원가입 시 사용자 정보를 데이터베이스에 저장합니다.
**POST** `/join`

```json
{
  "userId": "user1",
  "password": "password123",
  "userName": "사용자 이름"
}

```

### 2. 로그인 (Sign-In) 및 JWT 발급

로그인 요청 시 `TokenProvider`가 JWT를 생성하여 반환합니다.

**POST** `/login`

```json
{
  "username": "user1",
  "password": "password123"
}

```

**응답:**

```json
{
  "isLoggined": true,
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI...",
  "userId": "user1",
  "userName": "사용자 이름"
}

```

### 3. JWT 인증 처리 (TokenAuthenticationFilter)

모든 요청에서 `Authorization` 헤더를 확인하여 JWT를 검증합니다.

```java
@Override
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
    String token = resolveToken(request);
    if (token != null && tokenProvider.validToken(token) == 1) {
        Authentication authentication = tokenProvider.getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    filterChain.doFilter(request, response);
}

```

### 4. 토큰 갱신 (Refresh Token)

액세스 토큰이 만료되면, 클라이언트는 `refreshToken`을 이용해 새로운 액세스 토큰을 요청합니다.

**POST** `/refresh-token`

```
Cookie: refreshToken=eyJhbGciOiJIUzI1NiJ9...

```

**응답:**

```json
{
  "token": "새로운 액세스 토큰"
}

```

### 5. 로그아웃 (Logout)

로그아웃 시 클라이언트의 쿠키에서 `refreshToken`을 삭제합니다.

```java
CookieUtil.deleteCookie(request, response, "refreshToken");

```

### 6. 사용자 정보 조회

현재 로그인된 사용자의 정보를 반환합니다.

**GET** `/user/info`**응답:**

```json
{
  "id": 1,
  "userId": "user1",
  "userName": "사용자 이름",
  "role": "ROLE_USER"
}

```
![2025-03-13T02-55-29 738Z](https://github.com/user-attachments/assets/5519471f-3978-474e-913a-d6f63b726180)

![2025-03-13T03-20-41 746Z](https://github.com/user-attachments/assets/dff7beb9-366e-411f-9d98-17242c450f34)



# 게시판 기능 (Board API)

BasicBoard2의 게시판 기능은 **로그인한 사용자**가 게시글을 작성, 조회, 수정, 삭제할 수 있도록 구성되어 있습니다. 또한, **파일 업로드 및 다운로드 기능**을 제공하며, 게시글 수정 및 삭제는 **작성자 본인에게만 허용**됩니다.

### 1. 게시글 목록 조회 (페이징 포함)

로그인한 사용자는 게시판 목록을 확인할 수 있습니다.

**GET** `/api/board?page=1&size=10`

- **응답 예시:**

```json
{
  "articles": [
    {
      "id": 1,
      "title": "첫 번째 게시글",
      "content": "게시글 내용입니다.",
      "author": "user1",
      "created": "2024-03-13T10:00:00"
    }
  ],
  "last": false
}

```

### 2. 게시글 상세 조회

게시글을 클릭하면 상세 페이지에서 내용을 확인할 수 있습니다.

**GET** `/api/board/{id}`

- **응답 예시:**

```json
{
  "title": "첫 번째 게시글",
  "content": "게시글 내용입니다.",
  "userId": "user1",
  "filePath": "/uploads/sample.jpg",
  "created": "2024-03-13T10:00:00"
}

```

**작성자가 아닌 경우 수정/삭제 버튼 비활성화**
게시글 상세 페이지에서 **로그인한 사용자 ID**와 **게시글 작성자의 ID**가 다를 경우, 수정 및 삭제 버튼이 비활성화됩니다.

```jsx
if (hUserId !== response.userId) {
    $('#editBtn').prop('disabled', true);
    $('#deleteBtn').prop('disabled', true);
}

```

### 3. 게시글 작성 (파일 업로드 포함)

로그인한 사용자는 게시글을 작성할 수 있으며, 파일을 첨부할 수도 있습니다.

**POST** `/api/board`

```json
{
  "title": "새로운 게시글",
  "content": "게시글 본문",
  "hiddenUserId": "user1",
  "file": "첨부파일"
}

```

### 4. 게시글 수정

게시글 작성자는 자신의 게시글을 수정할 수 있습니다.

**PUT** `/api/board`

```json
{
  "title": "수정된 제목",
  "content": "수정된 내용",
  "hiddenUserId": "user1",
  "hiddenId": 1,
  "fileChanged": false,
  "hiddenFilePath": "/uploads/sample.jpg"
}

```

### 5. 게시글 삭제

게시글 작성자는 자신의 게시글을 삭제할 수 있습니다.

**DELETE** `/api/board/{id}`

```json
{
  "userId": "user1"
}

```

### 6. 파일 다운로드

첨부된 파일이 있을 경우, 다운로드할 수 있습니다.

**GET** `/api/board/file/download/{fileName}`

- 파일 다운로드가 수행됩니다.

### 7. 게시판 UI 동작 방식

### 로그인 후 게시판 접근

1. 사용자가 로그인하면, **환영 메시지**와 함께 **숨겨진 필드에 사용자 정보**가 저장됩니다.
2. 이후, 로그인한 사용자가 게시판을 이용할 수 있습니다.

```jsx
$(document).ready(() => {
    getUserInfo().then((userInfo) => {
        $('#welcome-message').text(userInfo.userName + '님 환영합니다!');
        $('#hiddenUserId').val(userInfo.userId);
    });
});

```

### 게시글 목록 불러오기

게시글 목록은 **페이지네이션**을 포함하여 불러올 수 있습니다.

```jsx
let getBoards = () => {
    let currentPage = 1;
    const pageSize = 10;
    loadBoard(currentPage, pageSize);

    $('#nextPage').on('click', () => {
        currentPage++;
        loadBoard(currentPage, pageSize);
    });

    $('#prevPage').on('click', () => {
        if (currentPage > 1) {
            currentPage--;
            loadBoard(currentPage, pageSize);
        }
    });
};

```

### 수정 및 삭제 버튼 활성화 조건

```jsx
if (hUserId !== response.userId) {
    $('#editBtn').prop('disabled', true);
    $('#deleteBtn').prop('disabled', true);
}

```

이와 같은 방식으로, **게시글 수정/삭제는 작성자만 가능**하도록 구현되어 있습니다.

## 게시글 조회 다이어그램

![흐름 다이어그램 drawio](https://github.com/user-attachments/assets/777c0ebd-0005-4a87-bd06-a6e29b59cf48)


### 네이버 로그인 구현

## 1. 네이버 로그인 기능 개요

네이버 로그인 기능을 추가하여 사용자들이 네이버 계정으로 간편하게 로그인할 수 있도록 구현했습니다. 네이버 로그인 후, 네이버에서 제공하는 사용자 프로필(예: id, 이메일, 닉네임 등)을 받아 내부 JWT 토큰을 생성하고, 이 정보를 이용해 게시글 작성, 조회 등 기능에서 작성자 정보를 표시하도록 했습니다.

---

## 2. 주요 수정 및 추가 사항

### 2.1. Web Security 설정

- **WebSecurityConfig.java**
    - Spring Security 설정에서 네이버 로그인 관련 URL(`/api/social/**`)과 정적 리소스(예: `/naver-callback.html`)를 인증 없이 접근할 수 있도록 `permitAll()` 설정을 추가했습니다.
    - JWT 인증 필터(`TokenAuthenticationFilter`)를 등록하여 모든 요청에 대해 JWT를 검사합니다.

### 2.2. JWT 관련 구성

- **TokenProvider.java**
    - 내부 JWT 토큰 생성 로직을 구현했습니다.
    - 네이버 로그인 시 반환받은 닉네임(별명)을 `Member` 객체의 `userName` 필드로 매핑하고, 이를 기반으로 JWT를 생성하도록 수정하였습니다.
    - 토큰에 작성자 ID, 닉네임, 역할 등의 정보를 포함시켜 API 요청 시 인증에 사용합니다.

### 2.3. JWT 인증 필터

- **TokenAuthenticationFilter.java**
    - 요청 헤더의 `Authorization` 값을 추출하여 JWT 형식(“Bearer …”)인지 확인하고, 유효한 경우 `TokenProvider`를 사용해 인증 정보를 SecurityContext에 설정합니다.
    - JWT 형식이 올바르지 않거나 없을 경우 경고 메시지를 로그에 남기고, 필요한 경우 오류 응답을 반환합니다.

### 2.4. 네이버 로그인 관련 서비스 및 DTO

- **NaverAuthService.java**
    - 네이버 OAuth 2.0을 사용하여 네이버 로그인 URL을 생성하고, 인가 코드를 통해 접근 토큰을 요청하는 메서드를 구현했습니다.
    - 네이버 API (`https://openapi.naver.com/v1/nid/me`)를 호출하여 사용자 프로필 정보를 JSON으로 받아 `NaverUserResponse` DTO에 매핑합니다.
- **NaverUserResponse.java**
    - 네이버에서 반환하는 응답 JSON을 매핑하는 DTO로, 내부 클래스 `NaverResponse`에 `id`, `email`, `nickname` 등의 필드를 포함시켰습니다.
- **SocialLoginRequestDTO.java / SocialLoginResponseDTO.java**
    - 소셜 로그인 요청 및 응답에 필요한 정보를 전달하기 위한 DTO를 생성했습니다.
    - **중요 변경 사항**: 네이버 로그인 시 네이버의 `nickname` 값을 `SocialLoginResponseDTO`에 포함하여 클라이언트에서 닉네임(별명)으로 표시되도록 했습니다.
- **SocialLoginType.java**
    - 네이버 등 다양한 소셜 로그인 타입을 열거형(enum)으로 관리합니다. (현재는 네이버만 사용)
- **SocialLoginService.java**
    - 소셜 로그인 요청을 처리하여, 네이버 로그인 시 `NaverAuthService`를 통해 사용자 정보를 가져오고, `TokenProvider`로 내부 JWT 토큰을 생성합니다.
    - 생성된 JWT 토큰과 함께 네이버의 `id`, `email`, `nickname` 정보를 `SocialLoginResponseDTO`에 담아 클라이언트로 반환합니다.
    - **핵심 변경 사항**: 네이버에서 받은 닉네임을 `member.userName`에 저장하고, 이를 JWT 및 응답 DTO에 포함시켜 모든 화면(게시글 작성, 상세, 목록)에서 별명 대신 네이버 닉네임이 표시되도록 처리했습니다.

### 2.5. 네이버 로그인 컨트롤러

- **SocialLoginController.java**
    - `/api/social/naver`: 네이버 로그인 요청 시 상태 토큰을 생성하고, 네이버 로그인 페이지로 리다이렉트합니다.
    - `/api/social/login/callback`: 네이버 로그인 콜백 엔드포인트로, 네이버에서 전달된 인가 코드와 상태 값을 검증한 후 네이버 액세스 토큰을 받아 사용자 프로필 정보를 조회합니다.
        - 조회된 정보를 기반으로 `Member` 객체를 생성하고, 내부 JWT 토큰을 발급합니다.
        - 최종적으로 JWT 토큰을 쿼리 파라미터에 포함하여 정적 콜백 페이지(예: `naver-callback.html`)로 리다이렉트합니다.

### 2.6. 네이버 콜백 페이지 및 관련 JavaScript

- **naver-callback.html**
    - 네이버 로그인 후 내부 JWT 토큰을 URL 쿼리 파라미터로 받아 로컬 스토리지에 저장하고, 메인 페이지로 리다이렉트하는 정적 HTML 페이지입니다.
    - 이 페이지는 Spring Security 필터링 대상에서 제외되어 있어 인증 없이 접근할 수 있습니다.
- **naverCallback.js**
    - `naver-callback.html`에서 실행되며, URL에서 JWT 토큰을 추출하여 `localStorage`에 저장한 후 메인 페이지로 이동합니다.
    - 개발자 도구의 콘솔 로그를 통해 JWT 토큰이 올바르게 저장되는지 확인할 수 있습니다.

### 2.7. 게시글(Article) 관련 수정 사항

- **Article.java**
    - 게시글 모델에 `nickname` 필드를 추가하여, 소셜 로그인 시 네이버에서 받아온 닉네임(별명)을 저장하도록 수정했습니다.
    - `toBoardDetailResponseDTO()` 메서드에 `nickname` 값을 포함시켜 게시글 상세 정보에 표시되도록 했습니다.
- **BoardMapper.xml**
    - 게시글 저장 시 `nickname` 컬럼을 포함시키도록 INSERT SQL을 수정했습니다.
    - 게시글 조회 시 `nickname` 컬럼을 매핑하는 ResultMap을 수정하여, DB에 저장된 닉네임(별명)이 조회될 수 있도록 했습니다.

### 2.8. 프론트엔드 수정 (게시판 목록, 상세, 글쓰기 화면)

- **boardList.js / boardDetail.js / boardWrite.js**
    - 사용자 정보 조회 API(`/user/info`)를 통해 네이버 로그인 시 내부 JWT에 포함된 사용자 정보를 가져옵니다.
    - 화면의 작성자 정보 표기를 `response.nickname` (우선 순위), `response.userName`, 또는 `response.userId` 순으로 표시하여 네이버 닉네임(별명)이 보이도록 수정했습니다.
    - 글쓰기 화면에서는 사용자 입력 필드에 기본값으로 네이버 로그인 시 반환된 닉네임을 보여주도록 설정했습니다.

---

## 3. 테스트 및 검증

- 네이버 로그인 시, 네이버 오픈 API에서 아래와 같이 응답이 반환됨을 확인했습니다.
    
    ```json
    {
        "response": {
            "id": "xHHVIL0rgAfXa03aPX-ZUQaZbt7On3xhnph6mx5Q52k",
            "email": "ygpanda@naver.com",
            "nickname": "너구리"
        }
    }
    
    ```
    
- 내부 JWT 토큰이 정상적으로 생성되어 `/user/info` API에서 사용자 정보에 네이버 닉네임(예: "너구리")가 포함되어 반환되는지 확인했습니다.
- 게시글 작성 시 네이버 로그인 사용자의 닉네임(별명)이 DB의 `nickname` 컬럼에 저장되고, 게시글 목록 및 상세 페이지에서 올바르게 표시되는지 테스트했습니다.
- 글쓰기 화면에서 작성자 입력 필드에 네이버 닉네임이 기본값으로 표시되는지 확인했습니다.

---

## 4. 문제점 및 해결 과정

- **문제점**: 네이버 로그인 후 DB의 `article` 테이블에 저장될 때, 작성자 아이디(`userId`)만 저장되고, `nickname` 컬럼이 비어 있거나 내부 JWT 토큰에 포함되지 않는 문제 발생.
- **해결 과정**:
    - 네이버 API 응답에서 `nickname`을 올바르게 추출하여 `Member` 객체의 `userName` 필드에 매핑하도록 수정.
    - 게시글 저장 시, BoardMapper의 INSERT SQL에 `nickname` 컬럼을 포함하도록 수정.
    - 프론트엔드에서 게시글 상세 및 목록 화면에 작성자 정보를 표시할 때, `response.nickname`을 우선 사용하도록 수정.
    - 최종적으로, 네이버 로그인 시 "너구리"와 같이 네이버에서 제공된 닉네임이 정상적으로 표시되는 것을 확인함.

---

## 결론

네이버 로그인 기능을 추가하면서, 백엔드와 프론트엔드 양쪽에서 다음과 같은 주요 수정 작업을 진행했습니다.

- **백엔드**:
    - 네이버 OAuth 2.0을 통한 토큰 발급 및 사용자 정보 조회 기능 구현
    - 내부 JWT 생성 시 네이버에서 받아온 `nickname`을 포함하여 생성하도록 TokenProvider와 SocialLoginService 수정
    - BoardMapper.xml에서 INSERT 및 SELECT SQL에 `nickname` 컬럼을 추가하여 DB에 저장 및 조회가 가능하도록 수정
- **프론트엔드**:
    - 사용자 정보 조회 및 글쓰기 화면에서 작성자 정보를 표시할 때, 네이버 로그인 시 받은 닉네임(별명)을 우선적으로 사용하도록 수정
    - 네이버 콜백 페이지 및 관련 JS 파일을 통해 내부 JWT 토큰을 클라이언트에 전달

이 과정을 통해 기존 프로젝트에 네이버 로그인 기능을 성공적으로 추가하고, 네이버 로그인 시 반환되는 닉네임(별명)이 올바르게 표시되도록 개선했습니다.
