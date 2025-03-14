// URL 쿼리 파라미터를 객체로 변환하는 함수
function getQueryParams() {
    const params = {};
    const queryString = window.location.search.substring(1);
    queryString.split("&").forEach(pair => {
        const [key, value] = pair.split("=");
        if (key) {
            params[key] = decodeURIComponent(value);
        }
    });
    return params;
}

// 페이지 로드 후 콜백 처리 함수
function processCallback() {
    const params = getQueryParams();
    console.log("Callback Parameters:", params);

    if (params.access_token) {
        // JWT 토큰을 localStorage에 저장
        localStorage.setItem("accessToken", params.access_token);
        alert("네이버 로그인이 성공했습니다.");
        // 메인 페이지로 이동
        window.location.href = "/";
    } else {
        alert("토큰 정보를 받아오지 못했습니다. 다시 로그인해주세요.");
        window.location.href = "/member/login";
    }
}

// DOMContentLoaded 이벤트 후 콜백 처리 함수 실행
document.addEventListener("DOMContentLoaded", processCallback);
