<template>
    <header>
        <div>
            <h1>NEWSLOG</h1>
        </div>
        <div>
            <ul>
                <li><a href="/">홈</a></li>
                <li><a href="/news-list">뉴스</a></li>
                
                <!-- 로그인 -->
                <div v-if="isLoggedIn">
                    <span>{{  nickname  }}님 환영합니다!</span>
                    <li>
                        <a href="/mypage">마이페이지</a>
                    </li>
                    <li>
                        <a @click="handleLogout">로그아웃</a>
                    </li>
                </div>

                <!-- 로그아웃 -->
                <div v-else>
                    <li>
                        <a href="/login">로그인</a>
                    </li>
                    <li>
                        <a href="/join">회원가입</a>
                    </li>
                </div>
            </ul>
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