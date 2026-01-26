<template>
    <header>
        <div class="header-img">
            <a href="/">
                <img src="../../img/newslog_logo.png" alt="로고">
            </a>
        </div>
        <div class="header_wrap">
            <span class="welcome_txt" v-if="isLoggedIn">
                {{  nickname  }}님 환영합니다! <i class="fa-solid fa-dove"></i>
            </span>
            <div class="main-menu">
                <ul>
                    <li class="sub-li"><a href="/">메인화면</a></li>
                    <li class="sub-li"><a href="/news-list">뉴스</a></li>
                    
                    <!-- 로그인 -->
                    <li class="member-li" v-if="isLoggedIn">
                        <a href="/mypage">마이페이지</a>
                    </li>
                    <li class="member-li" v-if="isLoggedIn">
                        <a @click="handleLogout">로그아웃</a>
                    </li>
                    <!-- 로그아웃 -->
                    <li class="member-li" v-if="!isLoggedIn">
                        <a href="/login">로그인</a>
                    </li>
                    <li class="member-li" v-if="!isLoggedIn">
                        <a href="/join">회원가입</a>
                    </li>
                </ul>
            </div>
        </div>
    </header>
</template>

<script>
    export default {
        name: 'Header',
        data() {
            return {
                isLoggedIn: false,
                nickname: ''
            };
        },

        methods: {
            checkLoginStatus() {
                const token = localStorage.getItem('accessToken');
                const storedNickname = localStorage.getItem('nickname');

                this.isLoggedIn = !!token;
                this.nickname = storedNickname || '';
            },

            handleLogout() {
                if (confirm('로그아웃 하시겠습니까?')) {
                    localStorage.removeItem('accessToken');
                    localStorage.removeItem('memberId');
                    localStorage.removeItem('nickname');

                    this.checkLoginStatus();

                    this.$router.push('/login');
                }
            }
        },
        mounted() {
            this.checkLoginStatus();

            window.addEventListener('loginStateChanged', this.checkLoginStatus);
        },
        beforeUnmount() {
            window.removeEventListener('loginStateChanged', this.checkLoginStatus);
        }
    }
</script>