<template>
    <div>
        <div v-if="!error && memberId">
            <h2>비밀번호 재설정</h2>
            <p>입력하신 메일로 새로운 비밀번호가 전송됐습니다. 마이페이지에서 비밀번호를 변경해주세요.</p>
            <button @click="$router.push('/login')">로그인 하기</button>
        </div>

        <div v-else>
            <h2>비밀번호 재설정</h2>
            <p>{{ message || '정보와 일치하는 회원정보가 존재하지 않습니다.' }}</p>
            <button @click="$router.push('/id-search')">아이디 찾기</button>
        </div>

    </div>
</template>

<script>

export default {
    name: 'Pwd_result',
    data() {
        return {
            memberId: null,
            error: false,
            message: ''
        };
    },

    created() {
        const state = history.state;

        if (state) {
            this.memberId = state.memberId || null;
            this.error = state.error || null;
            this.message = state.message || null; 
        } else {
            this.$router.push('/pwd-search');
        }
    }
}

</script>