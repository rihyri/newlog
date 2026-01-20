import { createRouter, createWebHistory } from 'vue-router';
import Home from '@/components/Home.vue';
import Login from '@/components/member/join/Login.vue';
import Join from '@/components/member/join/Join.vue';
import Id_search from '@/components/member/search/Id_search.vue';
import Id_result from '@/components/member/search/Id_result.vue';

const routes = [
    {
        path: '/',
        name: 'Home',
        component: Home,
        // meta: { requiresAuth : true }
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

export default router;