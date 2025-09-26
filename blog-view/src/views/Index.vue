<template>
  <div class="site">
    <!--顶部导航-->
    <Nav :blogName="siteInfo.blogName" :categoryList="categoryList"/>
    <!--首页大图 只在首页且pc端时显示-->
    <div class="m-mobile-hide">
      <Header v-if="route.name === 'home'"/>
    </div>

    <div class="main">
      <div class="m-padded-tb-big">
        <div class="ui container">
          <div class="ui stackable grid">
            <!--左侧-->
            <div class="three wide column m-mobile-hide">
              <Introduction :class="{'m-display-none': focusMode}"/>
            </div>
            <!--中间-->
            <div class="ten wide column">
              <router-view v-slot="{ Component }">
                <keep-alive include="Home">
                  <component :is="Component"/>
                </keep-alive>
              </router-view>
            </div>
            <!--右侧-->
            <div class="three wide column m-mobile-hide">
              <RandomBlog :randomBlogList="randomBlogList" :class="{'m-display-none': focusMode}"/>
              <Tags :tagList="tagList" :class="{'m-display-none': focusMode}"/>
              <!--只在文章页面显示目录-->
              <Tocbot v-if="route.name === 'blog'"/>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!--私密文章密码对话框-->
    <BlogPasswordDialog/>

    <!--APlayer-->
    <div class="m-mobile-hide">
      <MyAPlayer/>
    </div>
    <!--回到顶部-->
    <el-backtop style="box-shadow: none;background: none;z-index: 9999;">
      <img src="/img/paper-plane.png" style="width: 40px;height: 40px;" alt="回到顶部">
    </el-backtop>
    <!--底部footer-->
    <Footer :siteInfo="siteInfo" :badges="badges" :newBlogList="newBlogList" :hitokoto="hitokoto"/>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useStore } from 'vuex'
import { getHitokoto, getSite } from '@/api/index'
import Nav from "@/components/index/Nav.vue";
import Header from "@/components/index/Header.vue";
import Footer from "@/components/index/Footer.vue";
import Introduction from "@/components/sidebar/Introduction.vue";
import Tags from "@/components/sidebar/Tags.vue";
import RandomBlog from "@/components/sidebar/RandomBlog.vue";
import MyAPlayer from "@/components/index/MyAPlayer.vue";
import Tocbot from "@/components/sidebar/Tocbot.vue";
import BlogPasswordDialog from "@/components/index/BlogPasswordDialog.vue";
import { SAVE_CLIENT_SIZE, SAVE_INTRODUCTION, SAVE_SITE_INFO, RESTORE_COMMENT_FORM } from "@/store/mutations-types";

// 状态定义
const siteInfo = ref({ blogName: '' })
const categoryList = ref([])
const tagList = ref([])
const randomBlogList = ref([])
const badges = ref([])
const newBlogList = ref([])
const hitokoto = ref({})

// 路由和store
const route = useRoute()
const store = useStore()

// 计算属性
const focusMode = computed(() => store.state.focusMode)

// 路由变化时滚动到顶部
watch(
    () => route.path,
    () => {
      scrollToTop()
    }
)

// 方法定义
const getSiteData = async () => {
  try {
    const res = await getSite()
    if (res.code === 200) {
      siteInfo.value = res.data.siteInfo
      badges.value = res.data.badges
      newBlogList.value = res.data.newBlogList
      categoryList.value = res.data.categoryList
      tagList.value = res.data.tagList
      randomBlogList.value = res.data.randomBlogList

      store.commit(SAVE_SITE_INFO, siteInfo.value)
      store.commit(SAVE_INTRODUCTION, res.data.introduction)
      document.title = route.meta.title + siteInfo.value.webTitleSuffix
    }
  } catch (error) {
    console.error('获取站点信息失败:', error)
  }
  console.log(siteInfo.value,11111)
}

const getHitokotoData = async () => {
  try {
    const res = await getHitokoto()
    hitokoto.value = res
  } catch (error) {
    console.error('获取一言失败:', error)
  }
}

const scrollToTop = () => {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

// 生命周期
onMounted(() => {
  // 初始化数据
  getSiteData()
  getHitokotoData()
  // 定时1分钟重新请求（根据需求调整周期）
  setInterval(getSiteData, 60 * 1000)

  // 从localStorage恢复之前的评论信息
  store.commit(RESTORE_COMMENT_FORM)

  // 保存可视窗口大小
  const saveClientSize = () => {
    store.commit(SAVE_CLIENT_SIZE, {
      clientHeight: document.body.clientHeight,
      clientWidth: document.body.clientWidth
    })
  }

  saveClientSize()

  // 窗口大小变化监听
  window.addEventListener('resize', saveClientSize)

  // 清理函数
  return () => {
    window.removeEventListener('resize', saveClientSize)
  }
})
</script>

<style scoped>
.site {
  display: flex;
  min-height: 100vh; /* 没有元素时，也把页面撑开至100% */
  flex-direction: column;
}

.main {
  margin-top: 40px;
  flex: 1;
}

.main .ui.container {
  width: 1400px !important;
  margin-left: auto !important;
  margin-right: auto !important;
}

.ui.grid .three.column {
  padding: 0;
}

.ui.grid .ten.column {
  padding-top: 0;
}

.m-display-none {
  display: none !important;
}
</style>
