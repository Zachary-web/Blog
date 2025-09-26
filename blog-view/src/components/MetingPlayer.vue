<template>
  <!-- 播放器容器 -->
  <div ref="playerContainer"></div>
</template>

<script setup>
import { onMounted, onBeforeUnmount, ref } from "vue";

const props = defineProps({
  server: { type: String, default: "netease" }, // 音乐源 (netease, tencent...)
  type: { type: String, default: "song" },     // 类型 (song, album, playlist)
  id: { type: String, required: true },        // 必须: 资源ID
  auto: { type: String, default: "" },         // 自动解析url
  theme: { type: String, default: "#25CCF7" }, // 主题色
});

const playerContainer = ref(null);
let metingEl = null;

onMounted(() => {
  // 创建 meting-js 元素
  metingEl = document.createElement("meting-js");

  // 设置属性
  metingEl.setAttribute("server", props.server);
  metingEl.setAttribute("type", props.type);
  metingEl.setAttribute("id", props.id);
  metingEl.setAttribute("theme", props.theme);

  if (props.auto) {
    metingEl.setAttribute("auto", props.auto);
  }

  // 挂载到容器
  playerContainer.value.appendChild(metingEl);
});

onBeforeUnmount(() => {
  // 手动销毁，防止 Vue 卸载时报错
  if (metingEl && metingEl.aplayer) {
    try {
      metingEl.aplayer.destroy();
    } catch (err) {
      console.warn("APlayer destroy 出错", err);
    }
  }
  if (metingEl && playerContainer.value.contains(metingEl)) {
    playerContainer.value.removeChild(metingEl);
  }
  metingEl = null;
});
</script>
