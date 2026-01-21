<template>
    <div>
        <form @submit.prevent="searchId">
            <div>
                <label for="nickname">닉네임</label>
                <input type="text" id="nickname"
                    v-model="searchData.nickname"
                    placeholder="닉네임을 입력해주세요."
                />
            </div>

            <div>
                <label for="email">이메일</label>
                <input type="text" id="email_local"
                    v-model="email.local"
                    placeholder="이메일을 입력해주세요."
                />
                <input type="text" id="email_domain"
                    v-model="email.domain"
                    :readonly="email.domainReadOnly"
                    placeholder="이메일 주소"
                />
                <select v-model="email.selected" @change="handleEmailDomainChange">
                    <option value="hanmail.net">hanmail.net</option>
                    <option value="naver.com">naver.com</option>
                    <option value="daum.net">daum.net</option>
                    <option value="nate.com">nate.com</option>
                    <option value="gmail.com">gmail.com</option>
                    <option value="korea.com">korea.com</option>
                    <option value="dreamwiz.com">dreamwiz.com</option>
                    <option value="hotmail.com">hotmail.com</option>
                    <option value="yahoo.co.kr">yahoo.co.kr</option>
                    <option value="sportal.or.kr">sportal.or.kr</option>
                    <option value="" selected>직접입력</option>
                </select>
            </div>

            <div>
                <button type="submit" class="submit_btn" :disabled="isSubmitting">
                    {{ isSubmitting ? '처리중 ... ' : '아이디 찾기' }}
                </button>
                <button type="button" @click="$router.push('/login')">
                    취소
                </button>
            </div>
        </form>
    </div>
</template>

<script>
import { memberApi } from '@/api/member';

export default {
    name: 'Id_search',
    data() {
        return {
            searchData: {
                nickname: '',
                email: ''
            },
            email: {
                local: '',
                domain: '',
                selected: '',
                domainReadOnly: false
            },
            isSubmitting: false
        };
    },

    methods: {
        handleEmailDomainChange() {
            if (this.email.selected === '') {
                this.email.domain = '';
                this.email.domainReadOnly = false;
            } else {
                this.email.domain = this.email.selected;
                this.email.domainReadOnly = true;
            }
        },

        async searchId() {

            if (!this.searchData.nickname.trim()) {
                alert('닉네임을 입력해주세요.');
                return false;
            }

            if (!this.email.local.trim() || !this.email.domain.trim()) {
                alert('이메일을 입력해주세요.');
                return false;
            }

            const fullEmail = `${this.email.local}@${this.email.domain}`;
            this.searchData.email = fullEmail;

            try {

                this.isSubmitting = true;
                const response = await memberApi.searchId(this.searchData);

                // 아이디 찾기 결과 페이지로 이동
                this.$router.push({
                    path: '/id-result',
                    state: {
                        nickname: this.searchData.nickname,
                        memberId: response.data.data.memberId
                    }
                });

            } catch (error) {
                this.$router.push({
                    path: '/id-result',
                    state: {
                        error: true,
                        message: error.response?.data?.message || '일치하는 ID가 존재하지 않습니다.'
                    }
                });
            } finally {
                this.isSubmitting = false;
            }
        }
    },
}
</script>