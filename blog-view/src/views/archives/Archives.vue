<template>
  <div>
    <div class="ui top attached segment" style="text-align: center">
      <h2 class="m-text-500">文章归档</h2>
      <p>好! 目前共计 {{ count }} 篇日志。 继续努力。</p>
    </div>
    <div class="ui attached segment">
      <div class="timeline">
        <!-- 循环渲染每年的归档分组：添加id用于锚点定位 -->
        <div
            :class="colorObj[index % 5]"
            v-for="(value, key, index) in blogMap"
            :key="key"
            :id="key"
        >
        <div class="tl-header">
          <a class="ui large label m-text-500">{{ key }}</a>
        </div>
        <!-- 循环渲染当年的每篇文章 -->
        <div class="tl-item" v-for="blog in value" :key="blog.id">
          <div class="tl-wrap">
            <span class="tl-date">{{ blog.day }}</span>
            <a href="javascript:;" @click.prevent="toBlog(blog)">
              <div class="ui left pointing label tl-title">{{ blog.title }}</div>
            </a>
          </div>
        </div>
      </div>

      <!-- 固定的 Hello World 归档头 -->
      <div class="tl-header">
        <a class="ui black large label m-text-500">Hello World!</a>
      </div>
    </div>
  </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import { useRoute } from 'vue-router'; // 新增：用于锚点定位
import { useStore } from 'vuex';
import { getArchives } from '@/api/archives';

// 响应式数据定义
const blogMap = ref({}); // 按年月分组的博客数据（key: 2025年09月，value: 文章数组）
const count = ref(0);    // 博客总数量

// 非响应式样式映射
const colorObj = {
  0: 'tl-blue',
  1: 'tl-dark',
  2: 'tl-green',
  3: 'tl-purple',
  4: 'tl-red',
};

// 路由与Store实例
const route = useRoute(); // 新增：获取当前路由（用于监听hash）
const store = useStore();

// 获取归档数据（原有逻辑不变）
const getArchivesData = async () => {
  try {
    const res = await getArchives();
    if (res.code === 200) {
      blogMap.value = res.data.blogMap;
      count.value = res.data.count;
      console.log('归档接口返回的数据：', res.data);
    } else {
      console.error('接口错误提示：', res.msg);
    }
  } catch (error) {
    console.error('获取归档失败：', error);
  }
};

// 跳转到文章详情页（原有逻辑不变）
const toBlog = (blog) => {
  store.dispatch('goBlogPage', blog);
};

// 新增：锚点定位函数（点击侧边栏年份后，滚动到对应分区）
const scrollToAnchor = () => {
  if (route.hash) { // 若路由有hash（如 #2025年09月）
    // 解码hash（处理“年/月”等特殊字符）
    const targetId = decodeURIComponent(route.hash.slice(1));
    const targetElement = document.getElementById(targetId);
    if (targetElement) {
      // 平滑滚动到目标位置（顶部留20px间距，避免被导航栏遮挡）
      targetElement.scrollIntoView({
        behavior: 'smooth',
        block: 'start'
      });
    }
  }
};

// 生命周期（新增：挂载时执行锚点定位）
onMounted(() => {
  getArchivesData();
  scrollToAnchor(); // 页面初始加载时有hash，直接定位
});

// 新增：监听路由hash变化（侧边栏点击跳转时触发）
watch(
    () => route.hash,
    () => {
      scrollToAnchor();
    }
);
</script>

<style scoped>
.timeline {
  margin: 20px 0;
}

.tl-header {
  width: 12em;
  text-align: center;
}

.tl-date {
  position: relative;
  top: 10px;
  display: block;
  float: left;
  width: 4.5em;
  margin-left: -7.5em;
  text-align: right;
}

.tl-wrap {
  padding: 15px 0 15px 20px;
  margin-left: 6em;
  border-style: solid;
  border-width: 0 0 0 4px;
}

.tl-wrap:before {
  position: relative;
  top: 15px;
  float: left;
  width: 10px;
  height: 10px;
  margin-left: -27px;
  background: #fff;
  border-color: inherit;
  border-style: solid;
  border-width: 3px;
  border-radius: 50%;
  content: '';
  box-shadow: 0 0 0 4px #fff; /* 合法值，无错误 */
}

/* 修复：background: 0 0 不是合法值，改为 transparent（透明） */
.tl-wrap:hover:before {
  background: transparent;
  border-color: #fff;
}

.tl-title {
  margin-left: 0 !important;
  letter-spacing: 0.3px !important;
  font-size: 14px !important;
  font-weight: 500 !important;
  padding: 12px 15px !important;
}

/* 以下样式均保持原有不变 */
.tl-blue .tl-header a,
.tl-blue .tl-item .tl-title {
  background: #23b7e5 !important;
  color: #fff !important;
}

.tl-blue .tl-item .tl-wrap {
  border-color: #23b7e5;
}

.tl-dark .tl-header a,
.tl-dark .tl-item .tl-title {
  background: #3a3f51 !important;
  color: #fff !important;
}

.tl-dark .tl-item .tl-wrap {
  border-color: #3a3f51;
}

.tl-green .tl-header a,
.tl-green .tl-item .tl-title {
  background: #27c24c !important;
  color: #fff !important;
}

.tl-green .tl-item .tl-wrap {
  border-color: #27c24c;
}

.tl-purple .tl-header a,
.tl-purple .tl-item .tl-title {
  background: #7266ba !important;
  color: #fff !important;
}

.tl-purple .tl-item .tl-wrap {
  border-color: #7266ba;
}

.tl-red .tl-header a,
.tl-red .tl-item .tl-title {
  background: #f05050 !important;
  color: #fff !important;
}

.tl-red .tl-item .tl-wrap {
  border-color: #f05050;
}
</style>
