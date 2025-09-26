<template>
  <!-- 评论输入表单 -->
  <div class="form">
    <h3>
      发表评论
      <el-button
          class="m-small"
          size="mini"
          type="primary"
          @click="handleCancelReply"
          v-show="parentCommentId !== -1"
      >
        取消回复
      </el-button>
    </h3>
    <el-form
        :inline="true"
        :model="commentForm"
        :rules="formRules"
        ref="formRef"
        size="small"
    >
      <el-input
          :class="'textarea'"
          type="textarea"
          :rows="5"
          v-model="commentForm.content"
          placeholder="评论千万条，友善第一条"
          maxlength="250"
          show-word-limit
          :validate-event="false"
      ></el-input>

      <!-- emoji 输入框 -->
      <div class="el-form-item el-form-item--small emoji">
        <img src="https://fastly.jsdelivr.net/gh/Zachary-web/blog-resource/img/paopao/1.png" @click="showEmojiBox">
        <div class="mask" v-show="emojiShow" @click="hideEmojiBox"></div>
        <div class="emoji-box" v-show="emojiShow">
          <div class="emoji-title">
            <span>{{ activeEmojiTab === 0 ? 'tv_小电视' : activeEmojiTab === 1 ? '阿鲁' : '泡泡' }}</span>
          </div>
          <div class="emoji-wrap" v-show="activeEmojiTab===0">
            <div class="emoji-list" v-for="(img,index) in tvMapperData" :key="index" @click="insertEmoji(img.name)">
              <img :src="img.src" :title="img.name">
            </div>
          </div>
          <div class="emoji-wrap" v-show="activeEmojiTab===1">
            <div class="emoji-list" v-for="(img,index) in aruMapperData" :key="index" @click="insertEmoji(img.name)">
              <img :src="img.src" :title="img.name">
            </div>
          </div>
          <div class="emoji-wrap" v-show="activeEmojiTab===2">
            <div class="emoji-list" v-for="(img,index) in paopaoMapperData" :key="index" @click="insertEmoji(img.name)">
              <img :src="img.src" :title="img.name">
            </div>
          </div>
          <div class="emoji-tabs">
            <a class="tab-link" :class="{'on':activeEmojiTab===0}" @click="activeEmojiTab=0">
              <img src="https://fastly.jsdelivr.net/gh/Zachary-web/blog-resource/img/tv/1.png">
            </a>
            <a class="tab-link" :class="{'on':activeEmojiTab===1}" @click="activeEmojiTab=1">
              <img src="https://fastly.jsdelivr.net/gh/Zachary-web/blog-resource/img/aru/1.png">
            </a>
            <a class="tab-link" :class="{'on':activeEmojiTab===2}" @click="activeEmojiTab=2">
              <img src="https://fastly.jsdelivr.net/gh/Zachary-web/blog-resource/img/paopao/1.png">
            </a>
          </div>
        </div>
      </div>

      <!-- 昵称输入框 -->
      <el-form-item prop="nickname">
        <el-popover placement="bottom" trigger="focus" content="输入QQ号将自动拉取昵称和头像" width="200">
          <template #reference>
            <el-input v-model="commentForm.nickname" placeholder="昵称（必填）" :validate-event="false">
              <template #prefix>
                <el-icon><User /></el-icon>
              </template>
            </el-input>
          </template>
        </el-popover>
      </el-form-item>

      <!-- 邮箱输入框 -->
      <el-form-item prop="email">
        <el-popover placement="bottom" trigger="focus" content="用于接收回复邮件">
          <template #reference>
            <el-input v-model="commentForm.email" placeholder="邮箱（必填）" :validate-event="false">
              <template #prefix>
                <el-icon><Message /></el-icon>
              </template>
            </el-input>
          </template>
        </el-popover>
      </el-form-item>

      <!-- 网址输入框 -->
      <el-form-item>
        <el-popover placement="bottom" trigger="focus" content="可以让我参观一下吗😊">
          <template #reference>
            <el-input v-model="commentForm.website" placeholder="https://（可选）">
              <template #prefix>
                <el-icon><Location /></el-icon>
              </template>
            </el-input>
          </template>
        </el-popover>
      </el-form-item>

      <el-form-item label="订阅回复">
        <el-switch v-model="commentForm.notice"></el-switch>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" size="medium" @click="postForm">发表评论</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { useStore } from 'vuex'
import { ElNotification } from 'element-plus'
import { checkEmail, checkUrl } from "@/common/reg"
import { SET_PARENT_COMMENT_ID } from "@/store/mutations-types"
import tvMapper from '@/plugins/tvMapper.json'
import aruMapper from '@/plugins/aruMapper.json'
import paopaoMapper from '@/plugins/paopaoMapper.json'

// 引入 Element Plus 图标
import { User, Message, Location } from '@element-plus/icons-vue'

// 网址验证函数 - 提前定义，确保在formRules之前
const validateWebsite = (rule, value, callback) => {
  if (value) {
    return checkUrl(rule, value, callback)
  }
  callback()
}

// Vuex 状态管理
const store = useStore()

// 表单引用
const formRef = ref(null)

// 状态变量
const emojiShow = ref(false)
const activeEmojiTab = ref(0)
const textarea = ref(null)
const start = ref(0)
const end = ref(0)

// 从 Vuex 获取的状态
const parentCommentId = computed(() => store.state.parentCommentId)
const commentForm = computed({
  get: () => store.state.commentForm,
  set: (value) => store.state.commentForm = value
})
const commentQuery = computed(() => store.state.commentQuery)

// 表情数据
const tvMapperData = ref([])
const aruMapperData = ref([])
const paopaoMapperData = ref([])

// 表单验证规则
const formRules = reactive({
  nickname: [
    { required: true, message: '请输入评论昵称' },
    { max: 18, message: '昵称不可多于15个字符' }
  ],
  email: [
    { required: true, message: '请输入评论邮箱' },
    { validator: checkEmail }
  ],
  website: [
    { required: false },
    { validator: validateWebsite }
  ]
})

// 初始化表情数据和textarea引用
onMounted(() => {
  tvMapperData.value = tvMapper
  aruMapperData.value = aruMapper
  paopaoMapperData.value = paopaoMapper

  // 使用nextTick确保DOM渲染完成后获取textarea
  nextTick(() => {
    textarea.value = document.querySelector('.el-form textarea')
  })
})

// 显示表情框
const showEmojiBox = () => {
  if (textarea.value) {
    start.value = textarea.value.selectionStart
    end.value = textarea.value.selectionEnd
    textarea.value.focus()
    textarea.value.setSelectionRange(start.value, end.value)
  }
  emojiShow.value = !emojiShow.value
}

// 插入表情
const insertEmoji = (name) => {
  let str = commentForm.value.content || ''
  commentForm.value.content = str.substring(0, start.value) + name + str.substring(end.value)
  start.value += name.length
  end.value = start.value

  if (textarea.value) {
    textarea.value.focus()
    nextTick(() => {
      textarea.value.setSelectionRange(start.value, end.value)
    })
  }
}

// 隐藏表情框
const hideEmojiBox = () => {
  emojiShow.value = false
  if (textarea.value) {
    textarea.value.focus()
    textarea.value.setSelectionRange(start.value, end.value)
  }
}

// 取消回复
const handleCancelReply = () => {
  store.commit(SET_PARENT_COMMENT_ID, -1)
}

// 提交表单
const postForm = () => {
  const adminToken = window.localStorage.getItem('adminToken')

  if (adminToken) {
    if (!commentForm.value.content || commentForm.value.content.length > 250) {
      return ElNotification({
        title: '评论失败',
        message: '评论内容有误',
        type: 'warning'
      })
    } else {
      return store.dispatch('submitCommentForm', adminToken)
    }
  }

  const blogToken = window.localStorage.getItem(`blog${commentQuery.value.blogId}`)

  formRef.value.validate(valid => {
    if (!valid || !commentForm.value.content || commentForm.value.content.length > 250) {
      ElNotification({
        title: '评论失败',
        message: '请正确填写评论',
        type: 'warning'
      })
    } else {
      store.dispatch('submitCommentForm', blogToken ? blogToken : '')
    }
  })
}
</script>

<style>
.form {
  background: #fff;
  position: relative;
  padding: 15px;
}

.form h3 {
  margin: 5px 0 15px;
  font-weight: 500 !important;
  display: flex;
  align-items: center;
}

.form .m-small {
  margin-left: 10px;
  padding: 4px 5px;
}

.el-form .textarea {
  margin-top: 5px;
  margin-bottom: 15px;
  width: 100%;
}

.el-form textarea {
  padding: 6px 8px;
  width: 100%;
}

.el-form textarea, .el-form input {
  color: black;
}

.el-form .el-form-item__label {
  padding-right: 3px;
}

.emoji {
  margin-right: 5px;
  position: relative;
  user-select: none;
  margin-bottom: 15px;
}

.emoji > img {
  cursor: pointer;
  transition: all 0.3s ease-in-out;
  width: 24px;
  height: 24px;
}

.emoji > img:hover {
  transform: rotate(360deg);
}

.emoji-box {
  color: #222;
  overflow: visible;
  background: #fff;
  border: 1px solid #E5E9EF;
  box-shadow: 0 11px 12px 0 rgba(106, 115, 133, 0.3);
  border-radius: 8px;
  width: 340px;
  position: absolute;
  top: 40px;
  z-index: 100;
}

.emoji-box * {
  box-sizing: content-box;
}

.emoji-box .emoji-title {
  font-size: 12px;
  line-height: 16px;
  margin: 13px 15px 0;
  color: #757575;
}

.emoji-box .emoji-wrap {
  margin: 6px 11px 0 11px;
  height: 185px;
  overflow: auto;
  word-break: break-word;
}

.emoji-box .emoji-wrap .emoji-list {
  height: 33px;
  color: #111;
  border-radius: 4px;
  transition: background 0.2s;
  display: inline-block;
  outline: 0;
  cursor: pointer;
}

.emoji-box .emoji-wrap .emoji-list:hover {
  background-color: #ddd;
}

.emoji-box .emoji-wrap .emoji-list img {
  margin: 4px;
  width: 25px;
  height: 25px;
}

.emoji-box .emoji-tabs {
  position: relative;
  height: 36px;
  overflow: hidden;
  background-color: #f4f4f4;
  border-radius: 0 0 4px 4px;
}

.emoji-box .emoji-tabs .tab-link {
  cursor: pointer;
  float: left;
  padding: 7px 18px;
  width: 22px;
  height: 22px;
}

.emoji-box .emoji-tabs .tab-link.on {
  background-color: #fff;
}

.emoji-box .emoji-tabs .tab-link img {
  width: 22px;
}

.emoji-box .emoji-tabs .tab-link:hover {
  background: #e7e7e7;
}

.mask {
  pointer-events: auto;
  position: fixed;
  z-index: 99;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: rgba(0, 0, 0, 0);
}

.el-form-item {
  margin-bottom: 15px;
}
</style>
