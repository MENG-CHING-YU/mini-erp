<template>
  <el-container style="height: 100vh">
    <!-- Sidebar -->
    <el-aside
      width="250px"
      style="background-color: #304156; box-shadow: 2px 0 6px rgba(0, 0, 0, 0.1)"
    >
      <!-- Logo / 標題區域 -->
      <div
        style="
          padding: 20px;
          text-align: center;
          background-color: #263445;
          color: #fff;
          font-size: 20px;
          font-weight: bold;
          border-bottom: 1px solid #1f2d3d;
        "
      >
        ERP 系統
      </div>

      <!-- 選單 -->
      <el-menu
        :default-active="activeMenu"
        @select="handleSelect"
        class="el-menu-vertical-demo"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
        style="border: none"
      >
        <el-menu-item index="dashboard">
          <el-icon><House /></el-icon>
          <span>Dashboard</span>
        </el-menu-item>
        <el-menu-item index="machines">
          <el-icon><Setting /></el-icon>
          <span>機台管理</span>
        </el-menu-item>
        <el-menu-item index="repairs">
          <el-icon><Tools /></el-icon>
          <span>維修管理</span>
        </el-menu-item>
        <!-- 新增員工管理 -->
        <el-menu-item index="employees">
          <el-icon><SwitchButton /></el-icon>
          <span>員工管理</span>
        </el-menu-item>
        <!-- 用戶管理 -->
        <el-menu-item index="users">
          <el-icon><SwitchButton /></el-icon>
          <span>用戶管理</span>
        </el-menu-item>
      </el-menu>

      <!-- 登出按鈕 -->
      <div style="position: absolute; bottom: 20px; width: 250px; padding: 0 20px">
        <el-button type="danger" style="width: 100%" @click="handleLogout" plain>
          <el-icon><SwitchButton /></el-icon>
          <span>登出</span>
        </el-button>
      </div>
    </el-aside>

    <!-- Main Content -->
    <el-container direction="vertical">
      <!-- Header -->
      <el-header
        style="
          background-color: #fff;
          box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
          display: flex;
          align-items: center;
          justify-content: space-between;
          padding: 0 20px;
        "
      >
        <div style="font-size: 18px; font-weight: 500; color: #303133">
          {{ pageTitle }}
        </div>
        <div style="color: #606266">歡迎, {{ username }}</div>
      </el-header>

      <!-- Content -->
      <el-main style="padding: 20px; overflow-y: auto; background-color: #f0f2f5">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { House, Setting, Tools, SwitchButton } from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()
const activeMenu = ref('dashboard')
const username = ref('使用者') // 可以從 auth store 獲取

// 頁面標題映射
const pageTitles = {
  dashboard: 'Dashboard',
  machines: '機台管理',
  repairs: '維修管理',
  employees: '員工管理', // 新增員工管理
}

const pageTitle = computed(() => pageTitles[activeMenu.value] || 'ERP 系統')

// 初始化選單高亮
onMounted(() => {
  const path = route.path.split('/')[2] || 'dashboard'
  activeMenu.value = path
  // 可以從 auth store 獲取用戶名
  // username.value = auth.user?.username || '使用者'
})

// 監控 URL 變化保持選單高亮
watch(
  () => route.path,
  (newPath) => {
    const path = newPath.split('/')[2] || 'dashboard'
    activeMenu.value = path
  },
)

const handleSelect = (key) => {
  activeMenu.value = key
  router.push(`/home/${key}`)
}

const handleLogout = () => {
  ElMessageBox.confirm('確定要登出嗎?', '提示', {
    confirmButtonText: '確定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      auth.logout()
    })
    .catch(() => {
      // 取消登出
    })
}
</script>

<style scoped>
.el-aside {
  height: 100vh;
  position: relative;
}

.el-menu-item {
  height: 50px;
  line-height: 50px;
}

.el-menu-item:hover {
  background-color: #263445 !important;
}

.el-menu-item.is-active {
  background-color: #1f2d3d !important;
}
</style>
