<template>
  <div>
    <BlogComment />
    <Pagination />
  </div>
</template>

<script setup>
import { onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useStore } from 'vuex'
import BlogComment from './BlogComment.vue'
import Pagination from './Pagination.vue'
import {
  SET_COMMENT_QUERY_PAGE,
  SET_COMMENT_QUERY_BLOG_ID,
  SET_COMMENT_QUERY_PAGE_NUM,
  SET_PARENT_COMMENT_ID
} from '@/store/mutations-types'

// 定义组件props
const props = defineProps({
  page: {
    type: Number,
    required: true
  },
  blogId: {
    type: Number,
    required: false
  }
})

// 获取路由和状态管理实例
const route = useRoute()
const store = useStore()

// 初始化方法
const init = () => {
  // 重置评论相关状态
  store.commit(SET_PARENT_COMMENT_ID, -1)
  store.commit(SET_COMMENT_QUERY_PAGE, props.page)
  store.commit(SET_COMMENT_QUERY_BLOG_ID, props.blogId)
  store.commit(SET_COMMENT_QUERY_PAGE_NUM, 1)
  store.dispatch('getCommentList')
}

// 组件挂载时初始化
onMounted(init)

// 监听路由路径变化，重新初始化评论
watch(
    () => route.path,
    () => {
      init()
    }
)
</script>

<style scoped>
</style>
