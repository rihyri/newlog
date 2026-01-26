<template>
    <div class="title_wrap">
        <div class="mypage_wrap">
            <h2>마이페이지</h2>
            <hr />
            <form @submit.prevent="mypageUpdate">
                <table>
                    <tbody>
                        <tr>
                            <td>유저 ID</td>
                            <td>{{ memberId }}</td>
                        </tr>
                        <tr>
                            <td>현재 비밀번호</td>
                            <td>
                                <input type="password" id="member_pw"
                                    v-model="formData.memberPw"
                                    :class="{ 'error' : errors.memberPw && showErrors }"
                                    @blur="onFieldBlur('memberPw')"
                                />
                            </td>
                        </tr>
                        <tr>
                            <td>새로운 비밀번호</td>
                            <td>
                                <span class="error-message" v-if="errors.memberPw && showErrors">{{ errors.memberPw }}</span>
                                <input type="password" id="new_member_pw"
                                    v-model="formData.newMemberPw"
                                    :class="{ 'error' : errors.newMemberPw && showErrors }"
                                    @blur="onFieldBlur('newMemberPw')"
                                />
                            </td>
                        </tr>
                        <tr>
                            <td>새로운 비밀번호 확인</td>
                            <td>
                                <span class="error-message" v-if="errors.memberPwChk && showErrors">
                                    {{ errors.memberPwChk }}
                                </span>
                                <input type="password" id="member_pw_chk"
                                    v-model="formData.memberPwChk"
                                    :class="{ 'error': errors.memberPwChk && showErrors }"
                                    @blur="onFieldBlur('memberPwChk')"
                                />
                            </td>
                        </tr>
                        <tr>
                            <td>닉네임</td>
                            <td>
                                <span class="error-message" v-if="errors.nickname && showErrors">{{ errors.nickname }}</span>
                                <span class="success-message" v-if="nicknameChecked">
                                    {{ nicknameCheckMessage }}
                                </span>
                                <input type="text" id="nickname"
                                    value="userData.nickname"
                                    :class="{ 'error': errors.nickname && showErrors }"
                                    v-model="formData.nickname"
                                />
                                <button type="button" @click="checkNicknameDuplicate"
                                    :disabled="!formData.nickname"
                                >
                                    닉네임 중복 확인
                                </button>
                            </td>
                        </tr>
                        <tr>
                            <td>이메일</td>
                            <td class="email_td">
                                <span class="error-message" v-if="errors.email && showErrors">
                                    {{ errors.email }}
                                </span>
                                <span class="success-message" v-if="emailChecked">
                                    {{ emailCheckMessage }}
                                </span>
                                <input type="text" id="email1"
                                    v-model="email.local"
                                />
                                @
                                <input type="text" id="email2"
                                    v-model="email.domain"
                                    :readonly="email.domainReadOnly"
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
                                <button type="button" @click="checkEmailDuplicate"
                                    :disabled="!email.local || !email.domain"
                                >
                                    이메일 중복 확인
                                </button>
                            </td>
                        </tr>
                        <tr>
                            <td>가입일</td>
                            <td>
                                {{ formatDate(userData.createdAt) }}
                            </td>
                        </tr>
                    </tbody>
                </table>
                <div class="btn-div">
                    <button type="submit" class="submit-btn" :disabled="isSubmitting">
                        {{ isSubmitting ? '처리중...' : '수정하기' }}
                    </button>
                </div>
            </form>
        </div>
    </div>
</template>

<script>
    import { memberApi } from '@/api/member';

    export default {
        name: 'Mypage',
        data() {
            return {
                userData: {
                    nickname: '',
                    email: '',
                    createdAt: ''
                },
                formData: {
                    memberPw: '',
                    newMemberPw: '',
                    memberPwChk: '',
                    nickname: '',
                },
                email: {
                    local: '',
                    domain: '',
                    selected: '',
                    domainReadOnly: false
                },
                errors: {
                    memberPw: '',
                    newMemberPw: '',
                    memberPwChk: '',
                    nickname: '',
                    email: ''
                },
                memberId: '',
                nicknameChecked: false,
                nicknameCheckMessage: '',
                emailChecked: false,
                emailCheckMessage: '',
                isSubmitting: false,
                showErrors: false
            };
        },

        async mounted() {
            this.memberId = localStorage.getItem('memberId');
            await this.loadUserData();
        },

        methods: {
            async loadUserData() {
                try {
                    const response = await memberApi.mypage({ memberId: this.memberId })
                    const data = response.data.data;

                    this.userData = {
                        nickname: data.nickname,
                        email: data.email,
                        createdAt: data.createdAt
                    };

                    this.formData.nickname = data.nickname;
                    this.splitEmail(data.email);

                } catch (error) {
                    console.error('유저 정보 로드 실패: ', error);
                    alert('유저 정보를 불러오는데 실패했습니다.');
                }
            },

            onFieldBlur(fieldName) {
                if (!this.showErrors) return;

                const validationMap = {
                    newMemberPw: this.validateNewPassword,
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
                    this.validateEmail()
                }
            },

            splitEmail(emailAddress) {
                if (!emailAddress) return;

                const [local, domain] = emailAddress.split('@');
                this.email.local = local;
                this.email.domain = domain;

                const domainOptions = [
                    'hanmail.com', 'naver.com', 'daum.net', 'nate.com',
                    'gmail.com', 'korea.com', 'dreamwiz.com', 'hotmail.com',
                    'yahoo.co.kr', 'sportal.or.kr'
                ];

                if (domainOptions.includes(domain)) {
                    this.email.selected = domain;
                    this.email.domainReadOnly = true;
                } else {
                    this.email.selected = '';
                    this.email.domainReadOnly = false;
                }
            },

            handleEmailDomainChange() {
                if (this.email.selected === '') {
                    this.email.domain = '';
                    this.email.domainReadOnly = false;
                } else {
                    this.email.domain = this.email.selected;
                    this.email.domainReadOnly = true;
                }
            },

            formatDate(dateTime) {
                if (!dateTime) return '';
                const date = new Date(dateTime);
                
                return date.toLocaleString('ko-KR', {
                    year: 'numeric',
                    month: 'long',
                    day: 'numeric',
                    hour: '2-digit',
                    minute: '2-digit'
                });
            },

            // 새로운 비밀번호 유효성 검사
            validateNewPassword() {

                const password = this.formData.newMemberPw;

                if (!password) {
                    this.errors.newMemberPw = '';
                    return true;
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

                if (this.formData.newMemberPw !== this.formData.memberPwChk) {
                    this.errors.memberPwChk = '비밀번호가 일치하지 않습니다.';
                    return false;
                }

                this.errors.memberPwChk = '';
                return true;
            },

            // 닉네임 유효성 검사
            validateNickname() {
                
                const nickname = this.formData.nickname.trim();

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

            async mypageUpdate() {
                
                this.showErrors = true;
                this.isSubmitting = true;

                try {

                    // 현재 비밀번호 체크
                    const memberPw = this.formData.memberPw.trim();
                    if (!memberPw) {
                        alert('현재 비밀번호를 입력해주세요.');
                        return;
                    }

                    // 새 비밀번호 검증 (선택)
                    const newMemberPw = this.formData.newMemberPw.trim();
                    const memberPwChk = this.formData.memberPwChk.trim();

                    if (newMemberPw || memberPwChk) {
                        if (!newMemberPw || !memberPwChk) {
                            alert('비밀번호 변경 시, 새 비밀번호와 비밀번호 확인을 모두 입력해주세요.');
                            return;
                        }

                        if (!this.validateNewPassword()) {
                            alert('새 비밀번호 형식을 확인해주세요.');
                            return;
                        }

                        if (!this.validatePasswordCheck()) {
                            alert('새 비밀번호가 일치하지 않습니다.');
                            return;
                        }
                    }

                    // 닉네임 검증 (선택)
                    const nickname = this.formData.nickname.trim();
                    if (nickname && nickname !== this.userData.nickname) {
                        if (!this.validateNickname()) {
                            alert('닉네임 형식을 확인해주세요.');
                            return;
                        }

                        if (!this.nicknameChecked) {
                            alert('닉네임 중복 확인을 해주세요.');
                            return;
                        }
                    }

                    // 이메일 검증 (선택)
                    const fullEmail = `${this.email.local}@${this.email.domain}`;
                    const originalEmail = this.userData.email;

                    if (fullEmail !== originalEmail) {
                        if (!this.validateEmail()) {
                            alert('이메일 형식을 확인해주세요.');
                            return;
                        }

                        if (!this.emailChecked) {
                            alert('이메일 중복 확인을 해주세요.');
                            return;
                        }
                    }

                    const hasChanges = newMemberPw || (nickname && nickname !== this.usernickname) || (fullEmail !== originalEmail);

                    if (!hasChanges) {
                        alert('변경할 정보를 입력해주세요.');
                        return;
                    }

                    const updateData = {
                        memberId: this.memberId,
                        memberPw: memberPw, 
                        newMemberPw: newMemberPw || '',
                        memberPwChk: memberPwChk || '',
                        nickname: nickname !== this.userData.nickname ? nickname : '',
                        email: fullEmail !== originalEmail ? fullEmail : ''
                    }

                    const response = await memberApi.mypageUpdate(updateData);

                    if (response.data.success) {
                        alert('정보가 성공적으로 수정되었습니다.');
                        await this.loadUserData();
                        this.resetForm();
                    }
                } catch (error) {
                    console.error('마이페이지 수정 실패: ', error);
                    const errorMessage = error.response?.data?.message || '정보 수정 중 오류가 발생하였습니다.';
                    alert(errorMessage);
                } finally {
                    this.isSubmitting = false;
                }
            },

            resetForm() {
                this.formData.memberPw = '';
                this.formData.newMemberPw = '';
                this.formData.memberPwChk = '';
                this.nicknameChecked = false;
                this.nicknameCheckMessage = '';
                this.emailChecked = false;
                this.emailCheckMessage = '';
                this.showErrors = false;
            },

        },

        watch: {
                'formData.nickname'() {
                    this.nicknameChecked = false;
                    this.nicknameCheckMessage = '';
                },
                'email.local'() {
                    this.emailChecked = false;
                    this.emailCheckMessage = '';
                },
                'email.domain'() {
                    this.emailChecked = false;
                    this.emailChangeMessage = '';
                }
            }
    }
</script>