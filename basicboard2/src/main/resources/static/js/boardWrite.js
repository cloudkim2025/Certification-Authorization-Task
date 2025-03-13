let selectedFile = null; // 선택된 파일을 저장할 변수

$(document).ready(() => {
    checkToken(); // 로그인된 사용자인지 확인
    setupAjax(); // AJAX 요청 설정 (토큰 포함)

    // 사용자 정보를 가져와서 입력 필드에 자동 입력
    getUserInfo().then((userInfo) => {
        console.log('user info :: ', userInfo);
        $('#hiddenUserName').val(userInfo.userName); // 숨겨진 입력 필드에 사용자 이름 저장
        $('#hiddenUserId').val(userInfo.userId); // 숨겨진 입력 필드에 사용자 ID 저장
        $('#userId').val(userInfo.userId); // 사용자 ID 입력 필드에 자동 입력
    }).catch((error) => {
        console.error('Error get user info : ', error);
    });

    // 파일 선택 시, selectedFile 변수에 저장하고 UI 업데이트
    $('#file').on('change', (event) => {
        selectedFile = event.target.files[0]; // 선택된 파일 저장
        updateFileList(); // 파일 목록 갱신
    });

    // 게시글 등록 버튼 클릭 이벤트
    $('#submitBtn').on('click', (event) => {
        event.preventDefault(); // 기본 이벤트 차단 (폼 제출 방지)

        let formData = new FormData($('#writeForm')[0]); // 폼 데이터 객체 생성

        // 기존 주석 유지
        // for (let [key, value] of formData.entries()) {
        //     if (value instanceof File) {
        //         console.log('Key:', key);
        //         console.log('Name:', value.name);
        //         console.log('Size:', value.size);
        //         console.log('Type:', value.type);
        //     } else {
        //         console.log(key + ': ' + value);
        //     }
        // }

        $.ajax({
            type: 'POST',
            url: '/api/board', // 게시글 등록 API 호출
            data: formData,
            contentType: false, // 기본 Content-Type 설정 제거 (자동 처리)
            processData: false, // 데이터 변환 방지 (파일 포함 요청 처리)
            success: () => {
                alert('게시글이 성공적으로 등록되었습니다!');
                window.location.href = '/'; // 게시글 등록 후 메인 페이지로 이동
            },
            error: (error) => {
                console.log('오류발생 : ', error);
                alert('게시글 등록 중 오류가 발생했습니다.');
            }
        });
    });
});

// 파일 목록 UI 업데이트 함수
let updateFileList = () => {
    $('#fileList').empty(); // 기존 목록 비우기

    if (selectedFile) {
        $('#fileList').append(`
            <li>
                ${selectedFile.name} <button type="button" class="remove-btn">X</button>
            </li>
        `);

        // X 버튼 클릭 시 선택된 파일 제거
        $('.remove-btn').on('click', function() {
            selectedFile = null; // 선택된 파일 제거
            $('#file').val(''); // 파일 input 초기화
            updateFileList(); // 파일 목록 갱신
        });
    }
}
