import axios from 'axios';

const API_BASE_URL = 'http://localhost:8088/api/news';

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
            localStorage.removeItem('acessToken');
            localStorage.removeItem('memberId');
            localStorage.removeItem('nickname');
            window.location.href = '/login';
        }
        return Promise.reject(error);
    }
);

export const newsApi = {

    // 전체 뉴스 목록
    newsList(page = 0, size = 10) {
        return apiClient.get('/list', {
            params: { page, size }
        });
    },

    // 카테고리별 뉴스 목록
    newsByCategory(categoryName, page = 0, size = 10) {
        return apiClient.get(`/category`, {
            params: { name: categoryName, page, size }
        });
    },

    // 뉴스 상세보기
    newsView(newsNo) {
        return apiClient.get(`/view/${newsNo}`);
    }
}