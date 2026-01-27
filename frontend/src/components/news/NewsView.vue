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
                <div>
                    <span>발행일: {{ formatDate(news.pubDate) }}</span>
                    <span>👁️ {{ news.viewCount }} | ❤️ {{ news.likeCount }}</span>
                </div>
                <div v-html="news.description"></div>
                <a :href="news.originalLink">원문 보기</a>
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
            error: null
        };
    },

    async created() {
        await this.fetchNewsDetail();
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

                console.log('뉴스 상세 정보: ', this.news);

            } catch (error) {
                console.log('뉴스 상세보기 오류: ', error);
                this.error = '뉴스를 불러오는데 실패했습니다.';
            } finally {
                this.loading = false;
            }
        },

        formatDate(date) {
            if (!date) return '';
            return new Date(date).toLocaleDateString('ko-KR');
        }
    }
}
</script>