<template>
  <!-- 文章归档 -->
  <div class="ui segments m-box">
    <!-- 组件标题 -->
    <div class="ui secondary segment"><i class="calendar icon"></i> 文章归档</div>
    <!-- 内容区：使用与标签云一致的黄色segment和内边距 -->
    <div class="ui yellow segment m-padding-small">
      <!-- 年份列表 -->
      <ul class="archive-year-list">
        <li
            v-for="(articles, yearMonth) in blogMap"
            :key="yearMonth"
            class="archive-year-item"
            @click="jumpToArchiveSection(yearMonth)"
        >
          <span class="year-month">{{ yearMonth }}</span>
          <span class="article-num">({{ articles.length }}篇)</span>
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import {ref, onMounted} from 'vue';
import {useRouter} from 'vue-router';
import {getArchives} from '@/api/archives';

// 1. 响应式数据（与Archives.vue数据来源一致）
const blogMap = ref({}); // 格式：{ "2025年09月": [文章1, 文章2] }
const count = ref(0);    // 总文章数

// 2. 路由实例（用于跳转）
const router = useRouter();

// 3. 请求归档数据（复用Archives.vue的接口逻辑）
const getArchivesData = async () => {
  try {
    const res = await getArchives();
    if (res.code === 200) {
      blogMap.value = res.data.blogMap;
      count.value = res.data.count;
    } else {
      console.error('获取归档数据失败:', res.msg);
    }
  } catch (error) {
    console.error('归档接口异常:', error);
  }
};

// 4. 点击年份：跳转到Archives页面的对应分区（锚点跳转）
const jumpToArchiveSection = (yearMonth) => {
  // 路由跳转：目标页面为Archives，hash为年份（用于定位）
  router.push({
    name: 'archives', // 需确保Archives页面的路由name是"archives"
    hash: `#${encodeURIComponent(yearMonth)}` // 编码特殊字符（如“年/月”）
  });
};

// 5. 组件挂载时请求数据
onMounted(() => {
  getArchivesData();
});
</script>

<style scoped>
/* 与标签云组件保持一致的基础样式 */
.secondary.segment {
  padding: 10px;
}

.m-padding-small {
  padding: 7px;
}

/* 年份列表样式 */
.archive-year-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

/* 年份项样式：模拟标签云的点击感 */
.archive-year-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 5px 8px;
  margin: 3px 0; /* 与标签云的margin保持一致 */
  cursor: pointer;
  border-radius: 4px; /* 增加圆角，与标签风格统一 */
  transition: all 0.2s;
}

/* 悬停效果：与标签云hover体验一致 */
.archive-year-item:hover {
  background-color: rgba(0, 0, 0, 0.05);
  color: #23b7e5;
}

/* 年份文字样式 */
.year-month {
  font-size: 14px;
  font-weight: 500;
}

/* 文章数量样式：模拟标签的小徽章风格 */
.article-num {
  font-size: 12px;
  color: #888;
  background: #f5f5f5;
  padding: 2px 6px;
  border-radius: 10px;
}
</style>
