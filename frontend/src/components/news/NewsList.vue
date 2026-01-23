<template>
    <div>
        <h2>뉴스 목록</h2>
        
        <!-- 카테고리 탭 -->
        <div>
            <button
                :class="['tab', { active: selectedCategory === null}]"
                @click="selectCategory(null)"
            >전체</button>
            <button
                v-for="category in categories"
                :key="category"
                :class="['tab', { active: selectedCategory === category }]"
                @click="selectCategory(category)"
            >
                {{ category }}
            </button>
        </div>

        <div v-if="loading" class="loading">
            뉴스를 불러오는 중...
        </div>

        <div v-else-if="error" class="error">
            {{ error }}
        </div>
        
        <div v-else>
            <div v-if="newsList.length === 0" class="no-data">
                표시할 뉴시가 없습니다.
            </div>

            <div v-else class="news-grid">
                <div
                    v-for="news in newsList"
                    :key="news.newsNo"
                >
                    <div v-if="news.categoryDisplayName">
                        {{ news.categoryDisplayName }}
                    </div>
                    <h3>{{ news.title }}</h3> 
                    <p>{{ news.description }}</p>
                    <div>
                        <span>{{  formatDate(news.pubDate) }}</span>
                        <span>
                            👁️ {{ news.viewCount }} | ❤️ {{ news.likeCount }}
                        </span>
                    </div>
                </div>
            </div>

            <!-- paging -->
            <div class="pagination" v-if="totalPages > 0">
                <button
                    @click="loadPage(currentPage - 1)"
                    :disabled="currentPage === 0"
                >이전</button>
                <span>{{ currentPage + 1 }} / {{ totalPages }}</span>
                <button
                    @click="loadPage(currentPage + 1)"
                    :disabled="currentPage >= totalPages - 1"
                >
                    다음
                </button>
            </div>
        </div>
    </div>
</template>

<script>
    import { newsApi } from '@/api/news';
    import { ref, onMounted } from 'vue';

    export default {
        name: 'NewsList',
        setup() {
            const categories = ['정치', '경제', '사회', '생활/문화', 'IT/과학', '세계'];
            const selectedCategory = ref(null);
            const newsList = ref([]);
            const loading = ref(false);
            const error = ref(null);
            const currentPage = ref(0);
            const totalPages = ref(0);
            const pageSize = ref(10);

            const loadNewsList = async (page = 0) => {
                loading.value = true;
                error.value = null;

                try {
                    let response;
                    if (selectedCategory.value) {
                        response = await newsApi.newsByCategory(
                            selectedCategory.value,
                            page,
                            pageSize.value
                        );
                    } else {
                        response = await newsApi.newsList(page, pageSize.value);
                    }

                    newsList.value = response.data.data.content;
                    currentPage.value = response.data.data.number;
                    totalPages.value = response.data.data.totalPages;
                
                } catch (err) {
                    console.error('뉴스 목록 로딩 실패: ', err);
                    error.value = '뉴스 목록을 불러오는데 실패했습니다.';
                } finally {
                    loading.value = false;
                }
            };

            const selectCategory = (category) => {
                selectedCategory.value = category;
                currentPage.value = 0;
                loadNewsList(0);
            };

            const loadPage = (page) => {
                if (page >= 0 && page < totalPages.value) {
                    loadNewsList(page);
                }
            };

            const formatDate = (dateString) => {
                const date = new Date(dateString);
                const year = date.getFullYear();
                const month = String(date.getMonth() + 1).padStart(2, '0');
                const day = String(date.getDate()).padStart(2, '0');
                const hours = String(date.getHours()).padStart(2, '0');
                const minutes = String(date.getMinutes()).padStart(2, '0');

                return `${year}-${month}-${day} ${hours}:${minutes}`;
            };

            onMounted(() => {
                loadNewsList();
            });

            return {
                categories,
                selectedCategory,
                newsList,
                loading,
                error,
                currentPage,
                totalPages,
                selectCategory,
                loadPage,
                formatDate
            };
        }
    }
</script>