<template>
    <div class="title_wrap">
        <div class="login_wrap">
                <div class="logo_img">
                <img src="../../../img/newslog_logo.png" alt="로고">
            </div>
            <h2>로그인</h2>
            <form class="login_form" @submit.prevent="handleLogin">
                <div class="login_div">
                    <span class="login-error" v-if="errors.memberId">* {{ errors.memberId }}</span>
                    <input type="text" id="member_id" class="login_input"
                        v-model="loginData.memberId"
                        placeholder="아이디를 입력하세요."
                        :class="{ 'error': errors.memberId }"
                    />
                </div>

                <div class="login_div">
                    <span class="login-error" v-if="errors.memberPw">* {{ errors.memberPw }}</span>
                    <input
                        type="password" id="member_pw" v-model="loginData.memberPw" class="login_input"
                        placeholder="비밀번호를 입력하세요"
                        :class="{ 'error': errors.memberPw }"
                    />
                </div>

                <div class="pwd_btn"><a href="/pwd-search">비밀번호 재설정</a></div>

                <div class="btn_div">
                    <button type="submit" class="login-btn" :disabled="isLoading">
                        {{ isLoading ? '로그인중...' : '로그인' }}
                    </button>
                </div>

                <div class="login_search">
                    <div class="search_btn"><router-link to="/join">회원가입</router-link></div>
                    <span>|</span>
                    <div class="search_btn"><a href="/id-search">아이디 찾기</a></div>
                </div>
            </form>
        </div>
    </div>
</template>

<script>
    import { memberApi } from '@/api/member';

    export default {
        name: 'Login',
        data() {
            return {
                loginData: {
                    memberId: '',
                    memberPw: ''
                },
                errors: {
                    memberId: '',
                    memberPw: ''
                },
                isLoading: false
            };
        },
        methods: {
            validate() {
                let isValid = true;
                
                // 아이디 검증
                if (!this.loginData.memberId.trim()) {
                    this.errors.memberId = '아이디를 입력해주세요.';
                    isValid = false;
                } else {
                    this.errors.memberId = '';
                }

                // 비밀번호 검증 
                if (!this.loginData.memberPw) {
                    this.errors.memberPw = '비밀번호를 입력해주세요.';
                    isValid = false;
                } else {
                    this.errors.memberPw = '';
                }

                return isValid;
            },

            async handleLogin() {
                if (!this.validate()) return;

                this.isLoading = true;

                try {
                    const response = await memberApi.login(this.loginData);

                    if (response.data.success) {
                        const { accessToken, memberId, nickname } = response.data.data;
                    
                        // 토큰, 사용자 정보 localStorage 에 저장 
                        localStorage.setItem('accessToken', accessToken);
                        localStorage.setItem('memberId', memberId);
                        localStorage.setItem('nickname', nickname);

                        window.dispatchEvent(new Event('loginStateChanged'));

                        this.$router.push('/');
                    } else {
                        alert(response.data.message);
                    }
                } catch (error) {
                    if (error.response && error.response.data) {
                        alert(error.response.data.message || '로그인에 실패했습니다.');
                    } else {
                        alert('로그인 중 오류가 발생했습니다.');
                    }
                } finally {
                    this.isLoading = false;
                }
            }
        }
    }
</script>