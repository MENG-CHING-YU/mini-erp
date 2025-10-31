// api.js
import axios from 'axios'
import router from '@/router'
import { ElMessage } from 'element-plus'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8083',
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' },
})

api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('jwt')

    // 🔍 Debug: 檢查 token
    console.log('📤 API Request:', config.method.toUpperCase(), config.url)

    if (token) {
      config.headers.Authorization = `Bearer ${token}`
      console.log('🔑 Token 前 20 字元:', token.substring(0, 20) + '...')
      console.log('🔑 Token 長度:', token.length)
    } else {
      console.warn('⚠️ 沒有 token!')
    }

    return config
  },
  (error) => Promise.reject(error),
)

api.interceptors.response.use(
  (response) => {
    console.log('✅ API Response:', response.status, response.config.url)
    return response
  },
  (error) => {
    const status = error.response?.status
    const url = error.config?.url

    console.error('❌ API Error:', status, url)
    console.error('❌ Error Response:', error.response?.data)

    if (status === 401) {
      console.warn('⚠️ 401 Unauthorized - 清除 token 並跳轉登入')
      localStorage.removeItem('jwt')
      localStorage.removeItem('user')
      ElMessage.warning('登入已過期,請重新登入')
      router.push('/')
    } else if (status === 403) {
      ElMessage.error('沒有權限訪問此資源')
    } else if (status === 404) {
      ElMessage.error('請求的資源不存在')
    } else if (status === 500) {
      ElMessage.error('伺服器錯誤,請稍後再試')
    } else if (error.message === 'Network Error') {
      ElMessage.error('網路連線失敗,請檢查網路設定')
    } else if (error.code === 'ECONNABORTED') {
      ElMessage.error('請求超時,請稍後再試')
    }

    return Promise.reject(error)
  },
)

export default api
