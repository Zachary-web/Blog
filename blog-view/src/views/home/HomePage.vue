<template>
  <div>
    <BlogList
        :getBlogList="getBlogList"
        :blogList="blogList"
        :totalPage="totalPage"
    />
  </div>
</template>

<script setup>
import {ref, nextTick} from 'vue'
import {useRouter} from 'vue-router'  // 移除未使用的useRoute
import {useStore} from 'vuex'
import {ElMessage} from 'element-plus'  // 导入消息提示组件
import BlogList from "@/components/blog/BlogList";
import {getBlogList as fetchBlogList} from "@/api/home";
import {SET_IS_BLOG_TO_HOME} from "../../store/mutations-types";

// 响应式数据
const blogList = ref([])
const totalPage = ref(0)
const getBlogListFinish = ref(false)

// 路由和状态管理（移除未使用的route）
const router = useRouter()
const store = useStore()

// 导航守卫
router.beforeEach((to, from, next) => {
  if (to.name === 'blogHome') {
    if (from.name !== 'blog') {
      store.commit(SET_IS_BLOG_TO_HOME, false)
      getBlogList()
    } else {
      if (!getBlogListFinish.value) {
        getBlogList()
      }
      store.commit(SET_IS_BLOG_TO_HOME, true)
    }
  }
  next()
})

// 获取博客列表方法
const getBlogList = (pageNum) => {
  fetchBlogList(pageNum).then(res => {
    if (res.code === 200) {
      blogList.value = res.data.list
      totalPage.value = res.data.totalPage
      nextTick(() => {
        window.Prism?.highlightAll()
      })
      getBlogListFinish.value = true
    } else {
      // 使用Element Plus的消息提示替代msgError
      ElMessage.error(res.msg)
    }
  }).catch(() => {
    ElMessage.error("请求失败")
  })
}
</script>

<style scoped>
</style>
