<template>
  <div>
    <!-- 内容区域 -->
    <BlogItem :blogList="blogList" v-viewer />
    <!-- 分页组件 -->
    <Pagination :getBlogList="getBlogList" :totalPage="totalPage" />
  </div>
</template>

<script setup>
import { defineProps } from 'vue'
import BlogItem from "./BlogItem";
import Pagination from "./Pagination";

// 直接定义props，不赋值给变量（因为模板中直接使用属性名，无需通过变量访问）
defineProps({
  getBlogList: {
    type: Function,
    required: true
  },
  blogList: {
    type: Array,
    required: true
  },
  totalPage: {
    type: Number,
    required: true
  }
})
</script>

<style scoped>
</style>
