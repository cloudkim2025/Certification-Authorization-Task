$(document).ready(() => {

    $('#signin').click(() => {
        // 로그인 입력 필드에서 사용자 입력 값 가져오기
        let userId = $('#user_id').val();
        let password = $('#password').val();

        // 서버로 보낼 데이터 객체 생성
        let formData = {
            username: userId, // 서버에서 username 필드로 받으므로 변수명 매칭
            password: password
        };

        // AJAX 요청 실행 (로그인 API 호출)
        $.ajax({
            type: 'POST', // HTTP 요청 방식 (POST)
            url: '/login', // 로그인 API 엔드포인트
            data: JSON.stringify(formData), // 데이터를 JSON 형식으로 변환하여 전송
            contentType: 'application/json; charset=utf-8', // HTTP 요청의 콘텐츠 타입 설정
            dataType: 'json', // 서버에서 반환할 데이터 타입 (JSON)
            success: (response) => {
                // 로그인 성공 시 실행할 로직
                alert('로그인이 성공했습니다.');
                console.log(response); // 서버 응답 데이터 콘솔 출력

                // 액세스 토큰을 Local Storage에 저장 (추후 API 요청 시 사용)
                localStorage.setItem('accessToken', response.token);

                // 로그인 성공 후 메인 페이지로 이동
                window.location.href = '/';
            },
            error: (error) => {
                // 로그인 실패 시 실행할 로직
                console.log('오류 발생 : ', error);
                alert('로그인 중 오류가 발생했습니다.');
            }
        });

    });

});
