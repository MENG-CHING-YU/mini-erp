<template>
  <el-container style="height: 100vh; justify-content: center; align-items: center; display: flex">
    <el-card class="login-card" shadow="hover">
      <h2 style="text-align: center; margin-bottom: 20px">ERP 登入</h2>

      <el-form :model="form" ref="loginForm" label-position="top">
        <el-form-item
          label="帳號"
          prop="username"
          :rules="[{ required: true, message: '請輸入帳號', trigger: 'blur' }]"
        >
          <el-input v-model="form.username" placeholder="帳號" />
        </el-form-item>

        <el-form-item
          label="密碼"
          prop="password"
          :rules="[{ required: true, message: '請輸入密碼', trigger: 'blur' }]"
        >
          <el-input v-model="form.password" type="password" placeholder="密碼" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" style="width: 100%" @click="handleLogin">登入</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </el-container>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const auth = useAuthStore()

const form = reactive({
  username: '',
  password: '',
})
const loginForm = ref(null)

const handleLogin = () => {
  console.log('=== handleLogin 被觸發 ===')

  loginForm.value.validate(async (valid, fields) => {
    console.log('驗證結果:', valid)
    console.log('驗證欄位:', fields)

    if (!valid) {
      console.log('驗證失敗')
      return
    }

    console.log('開始呼叫 API...')
    const success = await auth.login(form.username, form.password)
    console.log('API 回應:', success)

    if (success) {
      console.log('登入成功，跳轉中...')
      router.push('/home/dashboard')
    } else {
      ElMessage.error('登入失敗')
    }
  })
}

// 組件掛載時測試
console.log('LoginView 組件已載入')
</script>

<style scoped>
.login-card {
  width: 400px;
  padding: 30px;
}
</style>
