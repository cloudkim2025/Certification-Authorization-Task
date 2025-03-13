$(document).ready(() => {

    $('#signup').click((event) => {
        event.preventDefault(); // 폼 기본 제출 동작을 막음 (페이지 새로고침 방지)

        // 입력된 회원 가입 정보를 가져옴
        let userId = $('#user_id').val();
        let password = $('#password').val();
        let userName = $('#user_name').val();
        let role = $('#role').val();

        // 서버로 전송할 데이터 객체 생성
        let formData = {
            userId : userId,
            password : password,
            userName : userName,
            role : role
        };

        console.log('formData :: ', formData); // 콘솔에 데이터 출력 (디버깅용)

        // AJAX 요청 실행 (회원가입 API 호출)
        $.ajax({
            type: 'POST', // HTTP 요청 방식 (POST)
            url: '/join', // 백엔드 회원가입 API 엔드포인트
            data: JSON.stringify(formData), // 데이터를 JSON 형식으로 변환하여 전송
            contentType: 'application/json; charset=utf-8', // HTTP 요청의 콘텐츠 타입 설정
            dataType: 'json', // 서버에서 반환할 데이터 타입 (JSON)
            success: (response) => {
                // 회원가입 성공 시 실행할 로직
                alert('회원가입이 성공했습니다.\n로그인해주세요.');

                if (response.successed) {
                    // 회원가입 성공 후 로그인 페이지로 이동
                    window.location.href = '/member/login';
                }
            },
            error: (error) => {
                // 회원가입 실패 시 실행할 로직
                console.log('오류 발생 : ', error);
                alert('회원가입 중 오류가 발생했습니다.');
            }
        });

    });

});
