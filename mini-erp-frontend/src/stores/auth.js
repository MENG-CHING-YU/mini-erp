// src/stores/auth.js
import { defineStore } from 'pinia'
import api from '@/utils/api'
import router from '@/router'
import { ElMessage } from 'element-plus'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('jwt') || null,
    user: JSON.parse(localStorage.getItem('user') || 'null'),
  }),

  getters: {
    isAuthenticated: (state) => !!state.token,
  },

  actions: {
    async login(username, password) {
      try {
        console.log('🔐 開始登入...')
        const response = await api.post('/users/login', { username, password })

        console.log('📦 登入回應:', response.data)

        // 支援多種回應格式
        const token = response.data.token || response.data.accessToken
        const user = response.data.user || { username }

        if (!token) {
          console.error('❌ 後端未返回 token!')
          throw new Error('後端未返回 token')
        }

        console.log('✅ 取得 token:', token.substring(0, 20) + '...')
        console.log('✅ Token 長度:', token.length)

        // 儲存 token 和 user
        this.token = token
        this.user = user
        localStorage.setItem('jwt', token)
        localStorage.setItem('user', JSON.stringify(user))

        // 驗證儲存
        const savedToken = localStorage.getItem('jwt')
        console.log('✅ 已儲存到 localStorage:', savedToken === token)

        ElMessage.success('登入成功')
        return true
      } catch (error) {
        console.error('❌ 登入失敗:', error)
        ElMessage.error(error.response?.data?.message || '登入失敗')
        return false
      }
    },

    logout() {
      console.log('👋 登出')
      this.token = null
      this.user = null
      localStorage.removeItem('jwt')
      localStorage.removeItem('user')
      router.push('/')
      ElMessage.info('已登出')
    },

    async checkAuth() {
      const token = localStorage.getItem('jwt')
      if (!token) {
        console.warn('⚠️ 沒有 token,執行登出')
        this.logout()
        return false
      }

      try {
        console.log('🔍 驗證 token...')
        const response = await api.get('/users/me')
        this.user = response.data
        console.log('✅ Token 有效')
        return true
      } catch (error) {
        console.error('❌ Token 驗證失敗:', error)
        this.logout()
        return false
      }
    },
  },
})
