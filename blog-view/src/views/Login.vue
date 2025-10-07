<template>
  <div class="login_container">
    <div class="login_box">
      <!-- 头像 -->
      <div class="avatar_box">
        <img src="/img/avatar.jpg" alt="用户头像">
      </div>
      <!-- 登录表单 -->
      <el-form ref="loginFormRef" :model="loginForm" :rules="loginFormRules" class="login_form">
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" :prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
              v-model="loginForm.password"
              :prefix-icon="Lock"
              show-password
              @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item class="btns">
          <el-button type="primary" @click="handleLogin">登录</el-button>
          <el-button type="info" @click="resetLoginForm">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { User, Lock } from '@element-plus/icons-vue';
import { login } from "@/api/login";

// 路由实例
const router = useRouter();

// 表单引用
const loginFormRef = ref(null);

// 登录表单数据
const loginForm = reactive({
  username: 'Naccl',
  password: '11'
});

// 表单验证规则
const loginFormRules = reactive({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
});

// 重置表单
const resetLoginForm = () => {
  loginFormRef.value.resetFields();
};

// 处理登录
const handleLogin = () => {
  loginFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = await login(loginForm);
        if (res.code === 200) {
          ElMessage.success(res.msg);
          window.localStorage.setItem('adminToken', res.data.token);
          router.push('/home');
        } else {
          ElMessage.error(res.msg);
        }
      } catch (error) {
        ElMessage.error("请求失败");
      }
    }
  });
};
</script>

<style scoped>
.login_container {
  box-sizing: unset !important;
  height: 100%;
  background-color: #2b4b6b;
}

.login_box {
  width: 450px;
  height: 300px;
  background-color: #fff;
  border-radius: 3px;
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
}

.avatar_box {
  height: 130px;
  width: 130px;
  border: 1px solid #eee;
  border-radius: 50%;
  padding: 10px;
  box-shadow: 0 0 10px #ddd;
  position: absolute;
  left: 50%;
  transform: translate(-50%, -50%);
  background-color: #fff;
}

.avatar_box img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background-color: #eee;
}

.login_form {
  position: absolute;
  bottom: 0;
  width: 100%;
  padding: 0 20px;
  box-sizing: border-box;
}

.btns {
  display: flex;
  justify-content: flex-end;
}
</style>
