<template>
    <div>
      <div class="main-wrap">
        <Header></Header>
        <router-view></router-view>
      </div>
      <Footer></Footer>
    </div>
</template>

<script>
  import Header from './components/inc/Header.vue'
  import Footer from './components/inc/Footer.vue'
  import './css/main.css'

  export default {
    name: 'App',
    components: {
      Header,
      Footer
    }
  }
</script>