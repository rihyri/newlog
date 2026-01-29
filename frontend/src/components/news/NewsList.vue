<template>
    <div class="list_wrap">
        <div class="news_list">
            <!-- 카테고리 탭 -->
            <div class="cate_menu">
                <!-- <button
                    :class="['tab', { active: selectedCategory === null}]"
                    @click="selectCategory(null)"
                >전체</button> -->
                <button
                    v-for="category in categories"
                    :key="category"
                    :class="['tab', { active: selectedCategory === category }]"
                    @click="selectCategory(category)"
                >
                    {{ category }}
                </button>
            </div>
            
            <LoadingVar v-if="loading" />

            <ErrorVar v-else-if="error" />
            
            <div v-else class="list_text">
                <div v-if="newsList.length === 0" class="no-data">
                    표시할 뉴스가 없습니다.
                </div>

                <div v-else class="news-grid">
                    <div
                        v-for="news in newsList"
                        :key="news.newsNo"
                        class="seperate-news"
                    >
                        <router-link :to="{ name: 'NewsView', params: {id: news.newsNo}}">
                            <div class="news_cate">
                                    <h3 class="news_title">{{ truncateText(news.title, 23) }}</h3>
                                <div class="cate-name" v-if="news.categoryDisplayName">
                                    {{ news.categoryDisplayName }}
                                </div>
                            </div>
                            <span class="date">{{  formatDate(news.pubDate) }}</span> 
                            <p class="news_content">{{ truncateText(news.description, 35) }}</p>
                            <div class="news_status">
                                
                                <p>
                                    <i>👁️</i> {{ news.viewCount }} <span>|</span> <i>❤️</i> {{ news.likeCount }}
                                </p>
                            </div>
                        </router-link>
                    </div>
                </div>
            </div>
        </div>

        <!-- paging -->
        <div class="pagination" v-if="totalPages > 0">
            <div class="paging_wrap">
                <button
                    @click="loadPage(currentPage - 1)"
                    :disabled="currentPage === 0"
                >&lt;</button>
                <span>{{ currentPage + 1 }} / {{ totalPages }}</span>
                <button
                    @click="loadPage(currentPage + 1)"
                    :disabled="currentPage >= totalPages - 1"
                >
                    &gt;
                </button>
            </div>
        </div>
    </div>
</template>

<script>
    import { newsApi } from '@/api/news';
    import { ref, onMounted } from 'vue';
    import { truncateText } from '@/utils/stringUtil';
import ErrorVar from '../loading/ErrorVar.vue';

    export default {
        name: 'NewsList',
        setup() {
            const categories = ['정치', '경제', '사회', '생활/문화', 'IT/과학', '세계'];
            const selectedCategory = ref('정치');
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
                truncateText,
                formatDate
            };
        }
    }
</script>