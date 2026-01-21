import axios from 'axios';

const API_BASE_URL = 'http://localhost:8088/api/member';

const apiClient = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json'
    }
});

apiClient.interceptors.request.use(
    config => {
        const token = localStorage.getItem('accessToken');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    error => {
        return Promise.reject(error);
    }
);

apiClient.interceptors.response.use(
    response => response,
    error => {
        if (error.response?.status === 401) {
            // 토근 만료 또는 인증 실패
            localStorage.removeItem('accessToken');
            localStorage.removeItem('memberId');
            localStorage.removeItem('nickname');
            window.location.href = '/login';
        }
        return Promise.reject(error);
    }
);

export const memberApi = {
    
    // 회원가입
    join(userData) {
        return apiClient.post('/join', userData);
    },

    // 로그인
    login(credentials) {
        return apiClient.post('/login', credentials);
    },

    // 아이디 중복 확인
    checkMemberId(memberId) {
        return apiClient.get(`/check-id/${memberId}`);
    },

    // 닉네임 중복 확인
    checkNickname(nickname) {
        return apiClient.get(`/check-nickname/${nickname}`);
    },

    // 이메일 중복 확인
    checkEmail(email) {
        return apiClient.get(`/check-email/${email}`);
    },

    // 아이디 찾기
    searchId(userData) {
        return apiClient.post('/search-id', userData);
    },

    // 비밀번호 재설정 전 유저 정보 확인
    searchPwd(userData) {
        return apiClient.post('/search-pwd', userData);
    },

    // 비밀번호 재설정 후 화면
    resultPwd(userData) {
        return apiClient.post('/result-pwd', userData);
    },

    // 마이페이지
    mypage(memberId) {
        return apiClient.post('/mypage', memberId);
    },

    // 마이페이지 업데이트
    mypageUpdate(userData) {
        return apiClient.post('/mypage-update', userData);
    }
}