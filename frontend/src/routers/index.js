import { createRouter, createWebHistory } from 'vue-router';
import Home from '@/components/Home.vue';
import Login from '@/components/member/join/Login.vue';
import Join from '@/components/member/join/Join.vue';
import Id_search from '@/components/member/search/Id_search.vue';
import Id_result from '@/components/member/search/Id_result.vue';
import Pwd_search from '@/components/member/search/Pwd_search.vue';
import Pwd_result from '@/components/member/search/Pwd_result.vue';
import Mypage from '@/components/member/mypage/Mypage.vue';
import NewsList from '@/components/news/NewsList.vue';
import NewsView from '@/components/news/NewsView.vue';
import CustomList from '@/components/custom-news/CustomList.vue';

const routes = [
    {
        path: '/',
        name: 'Home',
        component: Home,
    },
    {
        path: '/login',
        name: 'Login',
        component: Login
    },
    {
        path: '/join',
        name: 'Join',
        component: Join
    },
    {
        path: '/id-search',
        name: 'Id_search',
        component: Id_search
    },
    {
        path: '/id-result',
        name: 'Id_result',
        component: Id_result
    },
    {
        path: '/pwd-search',
        name: 'Pwd_search',
        component: Pwd_search
    },
    {
        path: '/pwd-result',
        name: 'Pwd_result',
        component: Pwd_result
    }, 
    {
        path: '/mypage',
        name: 'Mypage',
        component: Mypage,
        meta: { requiresAuth : true }
    }, 
    {
        path: '/news-list',
        name: 'NewsList',
        component: NewsList,
        mata: { requiresAuth : true }
    },
    {
        path: '/news-view/:id',
        name: 'NewsView',
        component: NewsView,
        meata: { requiresAuth : true }
    },
    {
        path: '/custom-news/list',
        name: 'CustomList',
        component: CustomList,
        meta: { requiresAuth : true }
    }
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

// 인증 체크
router.beforeEach((to, from, next) => {
    const token = localStorage.getItem('accessToken');

    if (to.meta.requiresAuth && !token) {
        next('/login');
    } else {
        next();
    }
})

router.beforeEach((to, _, next) => {
    const accessToken = localStorage.getItem("accessToken");
    const memberId = localStorage.getItem("memberId");
    const nickname = localStorage.getItem('nickname');

    const isAuthenticated = Boolean(accessToken && memberId && nickname);
    
    if (!isAuthenticated && to.name !== "Login" && to.name !== "Join" && to.name !== "Home") {
        return next({ name: "Login" });
    } else if (isAuthenticated && (to.name === "Login" || to.name === "Join")) {
        return next({ name: "Home" });
    } else {
        return next();
    }
});

export default router;