<template>
    <div class="title_wrap">
        <div class="news_detail">
            <h2>뉴스 상세보기</h2>

            <div v-if="loading">로딩 중...</div>
            <div v-else-if="error">{{ error }}</div>
            <div v-else>
                <h3>{{ news.title }}</h3>
                <div v-if="news.categoryDisplayName">
                    <span class="category">{{ news.categoryDisplayName }}</span>
                </div>
                <span>발행일: {{ formatDate(news.pubDate) }}</span>
                <div v-html="news.description"></div>
                
                <div class="news_status">
                    <div>
                        <span>👁️</span>
                        <span>{{ news.viewCount }}</span>
                    </div>
                    <button
                        @click="handleLike"
                        :class="{ liked : isLiked }"
                    >
                        <span>{{ isLiked ? '❤️' : '🤍' }}</span>
                        <span>{{ news.likeCount }}</span>
                    </button>
                    <div>
                        <span>💬</span>
                        <span>{{ comments.length }}</span>
                    </div>
                </div>

                <a :href="news.originalLink">원문 보기</a>
            </div>

            <div>
                <h4>댓글 {{ comments.length }}개</h4>

                <div class="comment_form">
                    <textarea
                        v-model="newComment"
                        placeholder="뉴스에 대한 의견을 달아주세요."
                        rows="5"
                        @keyup.ctrl.enter="submitComment"
                    ></textarea>
                    <button
                        @click="submitComment"
                        :disabled="!newComment.trim()"
                    >댓글 작성</button>
                </div>

                <div class="comment_list">
                    <div
                        v-for="comment in comments" :key="comment.commentNo"
                    >
                        <div class="comment_header">
                            <span>{{ comment.memberName }}</span>
                            <span>{{ formatDateTime(comment.createdAt) }}</span>
                        </div>

                        <div v-if="editingCommentNo === comment.commentNo">
                            <textarea
                                v-model="editingContent"
                                rows="5"
                            ></textarea>
                            <div>
                                <button @click="saveComment(comment.commentNo)">저장</button>
                                <button @click="cancelEdit">취소</button>
                            </div>
                        </div>

                        <div v-else>
                            <p>{{ comment.content }}</p> 
                            <div v-if="comment.isAuthor">
                                <button
                                    @click="startEdit(comment)"
                                >
                                    수정
                                </button>
                                <button
                                    @click="deleteComment(comment.commentNo)"
                                >
                                    삭제
                                </button>
                            </div>
                        </div>    
                    </div>

                    <div v-if="comments.length === 0">
                        첫 댓글을 완성해보세요!
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import { newsApi } from '@/api/news';

export default {
    name: 'NewsView',

    data() {
        return {
            news: null,
            loading: true,
            error: null,
            isLiked: false,
            comments: [],
            newComment: '',
            editingCommentNo: null,
            editingContent: ''
        };
    },

    async created() {
        await this.fetchNewsDetail();
        await this.fetchLikeStatus();
        await this.fetchComments();
    },

    methods: {
        async fetchNewsDetail() {
            const newsNo = this.$route.params.id;

            if (!newsNo) {
                this.error = '뉴스 번호가 없습니다.';
                this.loading = false;
                return;
            }

            try {
                const response = await newsApi.newsView(newsNo);
                
                if (response.data && response.data.data) {
                    this.news = response.data.data;
                } 

                // console.log('뉴스 상세 정보: ', this.news);

            } catch (error) {
                // console.log('뉴스 상세보기 오류: ', error);
                this.error = '뉴스를 불러오는데 실패했습니다.';
            } finally {
                this.loading = false;
            }
        },

        // 좋아요 상태 조회
        async fetchLikeStatus() {
            try {
                const newsNo = this.$route.params.id;
                const response = await newsApi.getLikeStatus(newsNo);

                if (response.data && response.data.data) {
                    this.isLiked = response.data.data.isLiked;
                }
            } catch (error) {
                console.error('좋아요 상태 조회 오류: ', error);
            }
        },

        // 좋아요 토글
        async handleLike() {
            try {
                const newsNo = this.$route.params.id;
                const response = await newsApi.toggleLike(newsNo);

                if (response.data && response.data.data) {
                    this.isLiked = response.data.data.isLiked;

                    if (this.isLiked) {
                        this.news.likeCount++;
                    } else {
                        this.news.likeCount--;
                    }
                }
            } catch (error) {
                console.error('좋아요 처리 오류: ', error);
                alert('좋아요 처리에 실패했습니다.');
            }
        },

        // 댓글 목록 조회
        async fetchComments() {
            try {
                const newsNo = this.$route.params.id;
                const response = await newsApi.getComment(newsNo);

                if (response.data && response.data.data) {
                    this.comments = response.data.data;
                }

                console.log('댓글 목록 확인: ' + this.comments);

            } catch (error) {
                console.error('댓글 조회 오류: ', error);
            }
        },

        // 댓글 작성
        async submitComment() {
            if (!this.newComment.trim()) {
                alert('댓글 내용을 입력해주세요.');
                return;
            }

            try {
                const newsNo = this.$route.params.id;
                const response = await newsApi.createComment(newsNo, this.newComment);

                if (response.data && response.data.data) {
                    this.comments.unshift(response.data.data);
                    this.newComment = '';
                    alert('댓글이 작성되었습니다.');
                }
            } catch (error) {
                console.error('댓글 작성 오류: ', error);

                if (error.reponse && error.response.status === 401) {
                    alert('로그인이 필요합니다.');
                    this.$router.push('/login');
                } else {
                    alert('댓글 작성에 실패했습니다.');
                }
            }
        },

        // 댓글 수정 시작
        startEdit(comment) {
            this.editingCommentNo = comment.commentNo;
            this.editingContent = comment.content;
        },

        // 댓글 수정 취소
        cancelEdit() {
            this.editingCommentNo = null;
            this.editingContent = '';
        },

        // 댓글 수정 저장
        async saveComment(commentNo) {

            if (!this.editingContent.trim()) {
                alert('댓글 내용을 입력해주세요.');
                return;
            }

            try {
                const response = await newsApi.updateComment(commentNo, this.editingContent);

                if (response.data && response.data.data) {
                    const index = this.comments.findIndex(c => c.commentNo === commentNo);
                    if (index !== -1) {
                        this.comments[index] = response.data.data;
                    }

                    this.cancelEdit();
                    alert('댓글이 수정되었습니다.');
                }
            } catch (error) {
                console.error('댓글 수정 오류: ', error);
                alert('댓글 수정에 실패하였습니다.');
            }
        },

        // 댓글 삭제
        async deleteComment(commentNo) {
            
            if (!confirm('댓글을 삭제하시겠습니까?')) {
                return false;
            }

            try {
                await newsApi.deleteComment(commentNo);

                this.comments = this.comments.filter(c => c.commentNo !== commentNo);
                alert('댓글이 삭제되었습니다.');
            } catch (error) {
                console.error('댓글 삭제 오류: ', error);
                alert('댓글 삭제에 실패했습니다.');
            }
        },

        formatDate(date) {
            if (!date) return '';
            return new Date(date).toLocaleDateString('ko-KR');
        },

        formatDateTime(date) {
            if (!date) return '';
            const d = new Date(date);
            return d.toLocaleDateString('ko-KR') + ' ' + 
                   d.toLocaleTimeString('ko-KR', { hour: '2-digit', minute: '2-digit' });
        }
    }
}
</script>