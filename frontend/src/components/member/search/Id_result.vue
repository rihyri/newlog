<template>
    <div>
        <div v-if="!error && memberId">
            <h2>아이디 찾기 결과</h2>
            <p>회원님의 아이디는 <strong>{{ memberId }} 입니다.</strong></p>
            <button @click="$router.push('/login')">로그인 하기</button>
        </div>

        <div v-else>
            <h2>아이디 찾기 결과</h2>
            <p>{{ message || '정보와 일치하는 ID가 존재하지 않습니다.' }}</p>
            <button @click="$router.push('/id-search')">아이디 찾기</button>
        </div>
    </div>
</template>

<script>
export default {
    name: 'Id_result',
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
            this.nickname = state.nickname || null;
            this.error = state.error || null;
            this.message = state.message || null;
        } else {
            this.$router.push('/id-search');
        }
    }
}
</script>