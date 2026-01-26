<template>
    <div class="title_wrap">
        <div class="join_wrap">
            <h2>회원가입</h2>
            <div class="essential_txt">
                <span><b>*</b>필수입력사항</span>
            </div>
            <hr />
            <form @submit.prevent="handleSubmit">
                <!-- 아이디 -->
                <div class="join_input">
                    <label for="member_id">아이디<b>*</b></label>
                    <div class="input_wrap">
                        <span class="error-message" v-if="errors.memberId">{{ errors.memberId }}</span>
                        <span class="success-message" v-if="idCheckMessage">{{ idCheckMessage }}</span>
                        <input type="text" id="member_id"
                            v-model="formData.memberId" 
                            :class="{ 'error': errors.memberId && showErrors }" 
                            @blur="onFieldBlur('memberId')"
                            placeholder="4~20자 사이"    
                        />
                        </div>
                    <button 
                        type="button" 
                        @click="checkIdDuplicate"
                        :disabled="!formData.memberId"
                    >아이디 중복확인</button>
                </div>

                <!-- 비밀번호 -->
                <div class="join_input">
                    <label for="password">유저 비밀번호<b>*</b></label>
                    <div class="input_wrap">
                        <span class="error-message" v-if="errors.memberPw && showErrors">{{ errors.memberPw }}</span>
                        <input type="password" id="member_pw"
                            v-model="formData.memberPw"
                            :class="{ 'error': errors.memberPw && showErrors }"
                            @blur="onFieldBlur('memberPw')"
                            placeholder="영문, 숫자, 특수문자 포함 8~20자"
                        />
                    </div>
                </div>

                <!-- 비밀번호 확인 -->
                <div class="join_input">
                    <label for="member_pw_chk">비밀번호 확인<b>*</b></label>
                    <div class="input_wrap">
                        <span class="error-message" v-if="errors.memberPwChk && showErrors">{{ errors.memberPwChk }}</span>
                        <input 
                            type="password" 
                            id="member_pw_chk" 
                            v-model="formData.memberPwChk"
                            :class="{ 'error': errors.memberPwChk && showErrors }"
                            @blur="onFieldBlur('memberPwChk')"
                        />
                    </div>
                </div>

                <!-- 닉네임 -->
                <div class="join_input">
                    <label for="nickname">닉네임<b>*</b></label>
                    <div class="input_wrap">
                        <span class="error-message" v-if="errors.nickname && showErrors">{{ errors.nickname }}</span>
                        <span class="success-message" v-if="nicknameChecked">{{ nicknameCheckMessage }}</span>
                        <input type="text" id="nickname" 
                            v-model="formData.nickname"
                            :class="{ 'error': errors.nickname && showErrors }"
                            @blur="onFieldBlur('nickname')"    
                            placeholder="2~18자 사이"
                            />
                    </div>
                    <button type="button" @click="checkNicknameDuplicate"
                        :disabled="!formData.nickname">
                        닉네임 중복 확인
                    </button>
                </div>

                <!-- 이메일 -->
                <div class="join_input">
                    <label for="email">e-mail<b>*</b></label>
                    <div class="email_wrap">
                        <span class="error-message" v-if="errors.email && showErrors">{{ errors.email }}</span>
                        <span class="success-message" v-if="emailChecked">{{ emailCheckMessage }}</span>
                        <div>
                            <input type="text" id="email1" 
                                v-model="email.local" 
                                :class="{ 'error': errors.email && showErrors }"
                                @blur="onEmailBlur" 
                                placeholder="이메일" /> @
                            <input type="text" id="email2" 
                                v-model="email.domain" 
                                :readonly="email.domainReadonly" 
                                placeholder="이메일 주소"
                                @blur="onEmailBlur"/>
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
                    </div>
                    <button type="button" 
                        @click="checkEmailDuplicate" 
                        :disabled="!email.local || !email.domain">
                        이메일 중복 확인
                    </button>
                </div>

                <!-- submit 버튼 -->
                <div class="btn-div">
                    <button type="submit" class="submit-btn" :disabled="isSubmitting">
                        {{ isSubmitting ? '처리중 ...' : '회원가입' }}
                    </button>
                    <button type="button" class="cancel-btn" @click="$router.push('/login')">
                        취소
                    </button>
                </div>
            </form>
        </div>
    </div>
</template>

<script>
import { memberApi } from '@/api/member';

export default {
    name: 'Join',
    data() {
        return {
            formData: {
                memberId: '',
                memberPw: '',
                memberPwChk: '',
                nickname: '',
                email: ''
            },
            email: {
                local: '',
                domain: '',
                selected: '',
                domainReadonly: false
            },
            errors: {
                memberId: '',
                memberPw: '',
                memberPwChk: '',
                nickname: '',
                email: ''
            },
            idChecked: false,
            idCheckMessage: '',
            nicknameChecked: false,
            nicknameCheckMessage: '',
            emailChecked: false,
            emailCheckMessage: '',
            isSubmitting: false,
            showErrors: false 
        };
    },

    methods: {
        onFieldBlur(fieldName) {
            if (!this.showErrors) return;

            const validationMap = {
                memberId: this.validateMemberId,
                memberPw: this.validatePassword,
                memberPwChk: this.validatePasswordCheck,
                nickname: this.validateNickname,
                email: this.validateEmail
            };

            const validationMethod = validationMap[fieldName];
            if (validationMethod) {
                validationMethod.call(this);
            }
        },
        
        onEmailBlur() {
            if (this.showErrors) {
                this.validateEmail();
            }
        },

        // 아이디 유효성 검사
        validateMemberId() {
            const memberId = this.formData.memberId.trim();

            if (!memberId) {
                this.errors.memberId = '아이디를 입력해주세요.';
                this.idChecked = false;
                return false;
            }

            if (memberId.length < 4 || memberId.length > 20) {
                this.errors.memberId = '아이디는 4~20자 사이여야 합니다.';
                this.idChecked = false;
                return false;
            }

            const pattern = /^[a-zA-Z0-9]+$/;
            if (!pattern.test(memberId)) {
                this.errors.memberId = '아이디는 영문자와 숫자만 가능합니다.';
                this.idChecked = false;
                return false;
            }

            this.errors.memberId = '';
            return true;
        },

        // 아이디 중복 확인
        async checkIdDuplicate() {
            const memberId = this.formData.memberId.trim();

            if (!memberId) {
                alert('아이디를 입력해주세요.');
                return false;
            }

            if (memberId.length < 4 || memberId.length > 20) {
                alert('아이디는 4~20자 사이여야 합니다.');
                return false;
            }

            const pattern = /^[a-zA-Z0-9]+$/;
            if (!pattern.test(memberId)) {
                alert('아이디는 영문자와 숫자만 가능합니다.');
                return false;
            }

            try {
                const response = await memberApi.checkMemberId(this.formData.memberId);

                if (response.data.data) {
                    this.errors.memberId = response.data.message;
                    this.idChecked = false;
                    this.idCheckMessage = '';
                } else {
                    this.idChecked = true;
                    this.idCheckMessage = response.data.message;
                    this.errors.memberId = '';
                }

                this.showErrors = true;

            } catch (error) {
                alert('아이디 중복 확인 중 오류가 발생했습니다.');
            }
        },

        // 비밀번호 유효성 검사
        validatePassword() {
            const password = this.formData.memberPw;

            if (!password) {
                this.errors.memberPw = '비밀번호를 입력해 주세요.';
                return false;
            }

            if (password.length < 8 || password.length > 20) {
                this.errors.memberPw = '비밀번호는 8~20자 사이여야 합니다.';
                return false;
            }

            const pattern = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&^])[A-Za-z\d@$!%*#?&^]+$/;
            if (!pattern.test(password)) {
                this.errors.memberPw = '비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다.';
                return false;
            }

            this.errors.memberPw = '';
            return true;
        },

        // 비밀번호 확인 검사
        validatePasswordCheck() {
            if (!this.formData.memberPwChk) {
                this.errors.memberPwChk = '비밀번호 확인을 입력해주세요.';
                return false;
            }

            if (this.formData.memberPw !== this.formData.memberPwChk) {
                this.errors.memberPwChk = '비밀번호가 일치하지 않습니다.';
                return false;
            }

            this.errors.memberPwChk = '';
            return true;
        },

        // 닉네임 유효성 검사
        validateNickname() {
            const nickname = this.formData.nickname.trim(); 

            if (!nickname) {
                this.errors.nickname = '닉네임을 입력해주세요.';
                this.nicknameChecked = false;
                return false;
            }

            if (nickname.length < 2 || nickname.length > 18) {
                this.errors.nickname = '닉네임은 2~18자 사이여야 합니다.';
                this.nicknameChecked = false;
                return false;
            }

            this.errors.nickname = '';
            return true;
        },

        // 닉네임 중복 확인
        async checkNicknameDuplicate() {
            
            const nickname = this.formData.nickname.trim();

            if (!nickname) {
                alert('닉네임을 입력해주세요.');
                return false;
            }

            if (nickname.length < 2 || nickname.length > 18) {
                alert('닉네임은 2~18자 사이여야 합니다.');
                return false;
            }

            try {
                const response = await memberApi.checkNickname(this.formData.nickname);

                if (response.data.data) {
                    this.errors.nickname = response.data.message;
                    this.nicknameChecked = false;
                    this.nicknameCheckMessage = '';
                } else {
                    this.nicknameChecked = true;
                    this.nicknameCheckMessage = response.data.message;
                    this.errors.nickname = '';
                }

                this.showErrors = true;

            } catch (error) {
                alert('닉네임 중복 확인 중 오류가 발생했습니다.');
            }
        },
        
        // 이메일 도메인 선택
        handleEmailDomainChange() {
            if (this.email.selected === '') {
                this.email.domain = '';
                this.email.domainReadonly = false;
            } else {
                this.email.domain = this.email.selected;
                this.email.domainReadonly = true;
            }
            if (this.showErrors) {
                this.validateEmail();
            }
        },

        // 이메일 유효성 검사
        validateEmail() {
            const fullEmail = `${this.email.local}@${this.email.domain}`;
            this.formData.email = fullEmail;

            const pattern = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
            if (!pattern.test(fullEmail)) {
                this.errors.email = '올바른 이메일 형식이 아닙니다.';
                this.emailChecked = false;
                return false;
            }

            this.errors.email = '';
            return true;
        },

        // 이메일 중복 확인
        async checkEmailDuplicate() {
            const fullEmail = `${this.email.local}@${this.email.domain}`;

            if (!this.email.local.trim() || !this.email.domain.trim()) {
                alert('이메일을 입력해주세요.');
                return false;
            }

            try {
                const response = await memberApi.checkEmail(fullEmail);

                if (response.data.data) {
                    this.errors.email = response.data.message;
                    this.emailChecked = false;
                    this.emailCheckMessage = '';
                } else {
                    this.emailChecked = true;
                    this.emailCheckMessage = response.data.message;
                    this.errors.email = '';
                }

                this.showErrors = true;

            } catch (error) {
                alert('이메일 중복 확인 중 오류가 발생했습니다.');
            }
        },

        // 전체 유효성 검사
        validateAll() {
            this.showErrors = true;

            const isIdValid = this.validateMemberId();

            const isPasswordValid = this.validatePassword();

            const isPasswordCheckValid = this.validatePasswordCheck();

            const isNicknameValid = this.validateNickname();

            const isEmailValid = this.validateEmail();

            if (!this.idChecked) {
                alert('아이디 중복 확인을 해주세요.');
                return false;
            }

            if (!this.nicknameChecked) {
                alert('닉네임 중복 확인을 해주세요.');
                return false;
            }

            if (!this.emailChecked) {
                alert('이메일 중복 확인을 해주세요.');
                return false;
            }

            const allValid = isIdValid && isPasswordValid && isPasswordCheckValid && isNicknameValid && isEmailValid;
            
            return allValid;
        },

        async handleSubmit() {
            if (!this.validateAll()) return;

            this.isSubmitting = true;

            try {
                const response = await memberApi.join(this.formData);

                if (response.data.success) {
                    alert(response.data.message);
                    this.$router.push('/login');
                } else {
                    alert(response.data.message);
                }
            } catch (error) {
                if (error.response && error.response.data) {
                    alert(error.response.data.message || '회원가입 중 오류가 발생하였습니다.');
                } else {
                    alert('회원가입 중 오류가 발생했습니다.');
                }
            } finally {
                this.isSubmitting = false;
            }
        }
    },
    watch: {
        // 아이디 변경 시 중복확인 초기화
        'formData.memberId'() {
            this.idChecked = false;
            this.idCheckMessage = '';
        },
    
        // 닉네임 변경 시 중복확인 초기화
        'formData.nickname'() {
            this.nicknameChecked = false;
            this.nicknameCheckMessage = '';
        },

        // 이메일 변경 시 중복확인 초기화
        'email.local'() {
            this.emailChecked = false;
            this.emailCheckMessage = '';
        },

        'email.domain'() {
            this.emailChecked = false;
            this.emailCheckMessage = '';
        }
    }
}
</script>