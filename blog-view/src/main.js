import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'

// 样式资源导入（按「基础→组件」顺序）
import './assets/css/base.css'                  // 自定义基础样式
import './assets/css/icon/iconfont.css'         // 阿里图标库
import "./assets/css/typo.css";                 // 排版样式
import 'semantic-ui-css/semantic.min.css'       // Semantic UI 组件样式
import 'element-plus/theme-chalk/index.css'     // Element Plus 组件样式
import 'viewerjs/dist/viewer.css'               // 图片预览插件样式

// 插件/工具导入
import Element from 'element-plus'              // Element Plus 组件库
import Viewer from 'v-viewer'                   // 图片预览插件（v-viewer）
import './util/directive'                       // 自定义指令（如权限、防抖等）
import './util/dateTimeFormatUtils.js'          // 日期格式化工具（全局可用）

// 控制台个性化信息（可选，不影响功能）
console.log(
    '%c NBlog %c By Naccl %c https://github.com/Naccl/NBlog',
    'background:#35495e ; padding: 1px; border-radius: 3px 0 0 3px;  color: #fff',
    'background:#41b883 ; padding: 1px; border-radius: 0 3px 3px 0;  color: #000',
    'background:transparent'
)

// 1. 创建 Vue 应用实例（无多余 render 函数，避免冲突）
const app = createApp(App)

// 2. 关键配置：识别 meting-js 为原生自定义元素（解决「组件无法解析」警告）
app.config.compilerOptions.isCustomElement = (tag) => {
    return tag === 'meting-js' // 仅匹配 meting-js 标签，不影响其他组件
}

// 3. 注册插件（需在 mount 前完成，顺序不影响功能）
app.use(Element)         // 注册 Element Plus
app.use(Viewer)          // 注册图片预览插件
app.use(router)          // 注册路由
app.use(store)           // 注册 Vuex 状态管理

// 4. 全局消息提示方法（挂载到 globalProperties，组件内用 this.msgSuccess 调用）
app.config.globalProperties.msgSuccess = function(msg) {
    this.$message.success(msg)
}
app.config.globalProperties.msgError = function(msg) {
    this.$message.error(msg)
}
app.config.globalProperties.msgInfo = function(msg) {
    this.$message.info(msg)
}

// 5. 全局滚动到顶部方法（Element Plus 回到顶部组件同款算法）
const cubic = value => Math.pow(value, 3)
const easeInOutCubic = value =>
    value < 0.5 ? cubic(value * 2) / 2 : 1 - cubic((1 - value) * 2) / 2

app.config.globalProperties.scrollToTop = function() {
    const el = document.documentElement // 文档根元素（滚动容器）
    const beginTime = Date.now()
    const beginValue = el.scrollTop
    // 兼容 requestAnimationFrame（动画流畅）
    const rAF = window.requestAnimationFrame || (func => setTimeout(func, 16))

    const frameFunc = () => {
        const progress = (Date.now() - beginTime) / 500 // 500ms 完成滚动
        if (progress < 1) {
            el.scrollTop = beginValue * (1 - easeInOutCubic(progress))
            rAF(frameFunc)
        } else {
            el.scrollTop = 0 // 确保最终滚动到顶部
        }
    }
    rAF(frameFunc)
}

// 6. 关闭生产环境提示（减少控制台冗余）
app.config.productionTip = false

// 7. 挂载应用到 DOM（#app 对应 index.html 中的容器）
app.mount('#app')
