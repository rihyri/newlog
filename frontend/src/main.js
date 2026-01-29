import { createApp } from 'vue'
import App from './App.vue'
import router from './routers'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'
import { createPinia } from 'pinia';
import LoadingVar from './components/loading/LoadingVar.vue';
import ErrorVar from './components/loading/ErrorVar.vue';

const app = createApp(App);
const pinia = createPinia();

pinia.use(piniaPluginPersistedstate);

app.component('LoadingVar', LoadingVar);
app.component('ErrorVar', ErrorVar);

app.use(pinia);
app.use(router);
app.mount('#app');