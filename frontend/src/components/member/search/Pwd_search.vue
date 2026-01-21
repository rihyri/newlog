<template>
    <div>
        <form @submit.prevent="searchPwd">
            <div>
                <label for="member_id">ID</label>
                <input type="text" id="member_id"
                    v-model="searchData.memberId"
                    placeholder="아이디를 입력해주세요."
                >
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
                    placeholder="이메일주소"
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
                    {{ isSubmitting ? '처리중...' : '비밀번호 재설정' }}
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
    name: 'Pwd_search',
    data() {
        return {
            searchData: {
                memberId: '',
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

        async searchPwd() {
            
            if (!this.searchData.memberId.trim()) {
                alert('ID를 입력해주세요.');
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

                const response = await memberApi.searchPwd(this.searchData);

                if (response.data.success) {
                    this.$router.push({
                        path: '/pwd-result',
                        state: {
                            memberId: response.data.data.memberId,
                            success: true
                        }
                    });
                }
            } catch (error) {
                this.$router.push({
                    path: '/pwd-result',
                    state: {
                        error: true,
                        message: error.response?.data?.message || '일치하는 회원 정보가 존재하지 않습니다.'
                    }
                })
            } finally {
                this.isSubmitting = false;
            }
        }
    }    
}

</script>