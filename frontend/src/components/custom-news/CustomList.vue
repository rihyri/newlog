<template>
    <div class="title_wrap">
        <h1>커스텀 뉴스 화면</h1>
        
        <div>
            <LoadingVar v-if="loading" />

            <ErrorVar v-else-if="error" :message="error" />
        
            <div v-else>
                <div v-if="newsList.length === 0">
                    표시할 뉴스가 없습니다.
                </div>

                <div v-else>
                    <div
                        v-for="news in newsList"
                        :key="news.newsno"
                    >
                        <router-link :to="{ name: 'NewsView', params: {id: news.newsNo }}">
                            <h3>{{ truncateText(news.title, 20) }}</h3>
                            <div v-if="news.categoryDisplayName">
                                {{ news.categoryDisplayName }}
                            </div>
                            <p>{{ formatDate(news.pubDate) }}</p>
                        </router-link>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import { customNewsApi } from '@/api/customNews';
import { ref, onMounted } from 'vue';
import { truncateText } from '@/utils/stringUtil';
import ErrorVar from '../loading/ErrorVar.vue';
import LoadingVar from '../loading/LoadingVar.vue';

export default {
    name: 'CustomList', 
    components: {
        ErrorVar,
        LoadingVar
    },
    setup() {
        const newsList = ref([]);
        const loading = ref(false);
        const error = ref(null);

        const loadNewsList = async() => {
            loading.value = true;
            error.value = null;

            try {
                const response = await customNewsApi.customNewsList();
                newsList.value = response.data.data || [];
            } catch (err) {
                console.error('맞춤 뉴스 로딩 실패: ', err);
                error.value = '뉴스 목록을 불러오는데 실패했습니다.';
            } finally {
                loading.value = false;
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
            newsList,
            loading,
            error,
            loadNewsList,
            truncateText,
            formatDate
        }
    }
}
</script>
