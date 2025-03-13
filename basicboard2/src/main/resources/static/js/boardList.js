$(document).ready(() => {
    checkToken(); // 로그인된 사용자인지 확인
    setupAjax(); // AJAX 요청 설정 (토큰 포함)

    // 사용자 정보를 가져와서 환영 메시지 및 숨겨진 필드에 저장
    getUserInfo().then((userInfo) => {
        $('#welcome-message').text(userInfo.userName + '님 환영합니다!'); // 화면에 사용자 환영 메시지 표시
        $('#hiddenUserId').val(userInfo.userId); // 숨겨진 필드에 사용자 ID 저장
        $('#hiddenUserName').val(userInfo.userName); // 숨겨진 필드에 사용자 이름 저장
    }).catch((error) => {
        console.error('board list user info error : ', error);
    });

    getBoards(); // 게시글 목록 불러오기
});

/**
 * ✅ 게시판 목록을 가져오는 함수
 * - 페이지네이션 기능을 포함하여 다음/이전 페이지 이동 가능
 */
let getBoards = () => {
    let currentPage = 1; // 현재 페이지 (기본값: 1)
    const pageSize = 10; // 한 페이지에 보여줄 게시글 개수

    loadBoard(currentPage, pageSize); // 초기 게시글 로드

    // 다음 페이지 버튼 클릭 이벤트
    $('#nextPage').on('click', () => {
        currentPage++; // 페이지 증가
        loadBoard(currentPage, pageSize);
    });

    // 이전 페이지 버튼 클릭 이벤트
    $('#prevPage').on('click', () => {
        if (currentPage > 1) {
            currentPage--; // 페이지 감소 (최소 1페이지)
            loadBoard(currentPage, pageSize);
        }
    });
}

/**
 * ✅ 특정 페이지의 게시글 목록을 서버에서 불러오는 함수
 * @param {number} page - 요청할 페이지 번호
 * @param {number} size - 페이지당 게시글 개수
 */
let loadBoard = (page, size) => {
    $.ajax({
        type: 'GET',
        url: '/api/board', // 게시글 목록 API 호출
        data: {
            page: page,
            size: size
        },
        success: (response) => {
            console.log('loadBoard : ', response);
            $('#boardContent').empty(); // 기존 게시글 목록 초기화

            if (response.articles.length <= 0) {
                // 게시글이 없는 경우 메시지 출력
                $('#boardContent').append(
                    `<tr>
                        <td colspan="4" style="text-align: center;">글이 존재하지 않습니다.</td>
                    </tr>`
                );
            } else {
                // 서버에서 받은 게시글 데이터를 HTML에 추가
                response.articles.forEach((article) => {
                    $('#boardContent').append(
                        `
                            <tr>
                                <td>${article.id}</td>
                                <td><a href="/detail?id=${article.id}">${article.title}</a></td>
                                <td>${article.userId}</td>
                                <td>${article.created.replace('T', ' ')}</td>
                            </tr>
                    `
                    );
                });
            }

            // 페이지 정보 업데이트
            $('#pageInfo').text(page);

            // 이전/다음 페이지 버튼 상태 설정
            $('#prevPage').prop('disabled', page === 1); // 첫 페이지에서는 이전 버튼 비활성화
            $('#nextPage').prop('disabled', response.last); // 마지막 페이지에서는 다음 버튼 비활성화
        },
        error: (error) => {
            console.error('board list error :: ', error);
        }
    });
}

/**
 * ✅ 로그아웃 처리 함수
 * - 서버에 로그아웃 요청을 보내고, 토큰을 삭제한 후 로그인 페이지로 이동
 */
let logout = () => {
    $.ajax({
        type: 'POST',
        url: '/logout', // 로그아웃 API 호출
        success: () => {
            alert('로그아웃이 성공했습니다.');
            localStorage.removeItem('accessToken'); // 로컬 스토리지에서 토큰 삭제
            window.location.href = '/member/login'; // 로그인 페이지로 이동
        },
        error: (error) => {
            console.log('오류발생 : ', error);
            alert('로그아웃 중 오류가 발생했습니다.');
        }
    });
}
