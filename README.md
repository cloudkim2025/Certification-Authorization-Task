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


