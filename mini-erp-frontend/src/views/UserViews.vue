<template>
  <el-card shadow="hover">
    <template #header>
      <div style="display: flex; justify-content: space-between; align-items: center">
        <span style="font-size: 18px; font-weight: 600">使用者管理</span>
        <el-button type="primary" @click="handleAdd" style="padding: 10px 20px">
          <el-icon><Plus /></el-icon>
          新增帳號
        </el-button>
      </div>
    </template>

    <!-- 查詢使用者 -->
    <el-form :inline="true" style="margin-bottom: 20px; margin-top: 30px">
      <el-form-item label="角色">
        <el-input
          v-model="searchForm.role"
          placeholder="請輸入角色"
          clearable
          style="width: 120px"
        />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 顯示所有使用者 -->
    <el-table
      v-loading="loading"
      :data="filteredTableData"
      style="width: 100%; margin-top: 20px"
      border
      stripe
    >
      <el-table-column label="工號" prop="id" />
      <el-table-column label="帳號" prop="username" />
      <el-table-column label="角色" prop="role" />
      <el-table-column label="建立時間" prop="createdAt" :formatter="formatDate" />
      <el-table-column label="更新時間" prop="updatedAt" :formatter="formatDate" />
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button size="small" type="primary" @click="handleUpdatePassword(row)">
            更改密碼
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分頁 -->
    <div style="margin-top: 20px; display: flex; justify-content: flex-end">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 更新密碼對話框 -->
    <el-dialog
      v-model="updatePasswordDialog"
      title="更改密碼"
      @close="resetPasswordForm"
      width="400px"
    >
      <el-form
        :model="updatePasswordForm"
        :rules="updatePasswordRules"
        ref="updatePasswordFormRef"
        label-width="100px"
      >
        <el-form-item label="新密碼" prop="newPassword">
          <el-input
            type="password"
            v-model="updatePasswordForm.newPassword"
            placeholder="請輸入新密碼"
          />
        </el-form-item>

        <el-form-item label="確認新密碼" prop="confirmNewPassword">
          <el-input
            type="password"
            v-model="updatePasswordForm.confirmNewPassword"
            placeholder="請確認新密碼"
          />
        </el-form-item>

        <el-form-item>
          <el-button @click="updatePassword" type="primary" :loading="submitLoading">
            確定
          </el-button>
        </el-form-item>
      </el-form>
    </el-dialog>

    <!-- 新增帳號對話框 -->
    <el-dialog v-model="addUserDialog" title="新增帳號" @close="resetAddUserForm" width="400px">
      <el-form :model="addUserForm" :rules="addUserRules" ref="addUserFormRef" label-width="100px">
        <el-form-item label="帳號" prop="username">
          <el-input v-model="addUserForm.username" placeholder="請輸入帳號" />
        </el-form-item>

        <el-form-item label="角色" prop="role">
          <el-input v-model="addUserForm.role" placeholder="請輸入角色 (例如: ADMIN, USER)" />
        </el-form-item>

        <el-form-item label="密碼" prop="password">
          <el-input type="password" v-model="addUserForm.password" placeholder="請輸入密碼" />
        </el-form-item>

        <el-form-item label="確認密碼" prop="confirmPassword">
          <el-input
            type="password"
            v-model="addUserForm.confirmPassword"
            placeholder="請確認密碼"
          />
        </el-form-item>

        <el-form-item>
          <el-button @click="addUser" type="primary" :loading="submitLoading">確定</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import api from '@/utils/api'

// 搜尋表單
const searchForm = ref({
  username: '',
  role: '',
})

const addUserForm = ref({
  userid: '',
  username: '',
  role: '',
  password: '',
  confirmPassword: '',
})

const addUserRules = {
  username: [{ required: true, message: '請輸入帳號', trigger: 'blur' }],
  role: [{ required: true, message: '請輸入角色', trigger: 'blur' }],
  password: [{ required: true, message: '請輸入密碼', trigger: 'blur' }],
  confirmPassword: [{ required: true, message: '請確認密碼', trigger: 'blur' }],
}

const updatePasswordForm = ref({
  newPassword: '',
  confirmNewPassword: '',
})

const updatePasswordDialog = ref(false)
const updatePasswordRules = {
  newPassword: [{ required: true, message: '請輸入新密碼', trigger: 'blur' }],
  confirmNewPassword: [{ required: true, message: '請確認新密碼', trigger: 'blur' }],
}

const selectedUsername = ref('')
const users = ref([])
const addUserDialog = ref(false)
const submitLoading = ref(false)
const loading = ref(false)

// 分頁
const currentPage = ref(1)
const pageSize = ref(10)

// 過濾資料
const filteredData = computed(() => {
  let data = users.value

  if (searchForm.value.username) {
    data = data.filter((item) =>
      item.username.toLowerCase().includes(searchForm.value.username.toLowerCase()),
    )
  }

  if (searchForm.value.role) {
    data = data.filter((item) =>
      item.role?.toLowerCase().includes(searchForm.value.role.toLowerCase()),
    )
  }

  return data
})

// 分頁後的資料
const filteredTableData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredData.value.slice(start, end)
})

const total = computed(() => filteredData.value.length)

// 查詢 - 使用後端 API 查詢單一使用者
// const handleSearch = async () => {
//   if (!searchForm.value.username) {
//     ElMessage.warning('請輸入帳號查詢')
//     return
//   }

//   loading.value = true
//   try {
//     const response = await api.get(`/users/${searchForm.value.username}`)
//     users.value = [response.data]
//     currentPage.value = 1
//   } catch (error) {
//     console.error('查詢失敗:', error)
//     if (error.response?.status !== 401) {
//       ElMessage.error('查詢失敗，請確認帳號是否存在')
//     }
//   } finally {
//     loading.value = false
//   }
// }

// 重置
const handleReset = () => {
  searchForm.value = {
    username: '',
    role: '',
  }
  currentPage.value = 1
}

const loadAllUsers = async () => {
  loading.value = true
  try {
    const response = await api.get('/users')
    users.value = response.data
  } catch (error) {
    console.error('載入使用者失敗:', error)
    if (error.response?.status !== 401) {
      ElMessage.error('載入使用者資料失敗')
    }
  } finally {
    loading.value = false
  }
}

const updatePassword = async () => {
  if (updatePasswordForm.value.newPassword !== updatePasswordForm.value.confirmNewPassword) {
    ElMessage.warning('新密碼與確認新密碼不相符')
    return
  }

  submitLoading.value = true
  try {
    await api.put(`/users/${selectedUsername.value}/password`, updatePasswordForm.value.newPassword)
    ElMessage.success('密碼更新成功')
    updatePasswordDialog.value = false
    updatePasswordForm.value = {
      newPassword: '',
      confirmNewPassword: '',
    }
  } catch (error) {
    console.error('密碼更新失敗:', error)
    if (error.response?.status !== 401) {
      ElMessage.error('密碼更新失敗')
    }
  } finally {
    submitLoading.value = false
  }
}

const addUser = async () => {
  if (addUserForm.value.password !== addUserForm.value.confirmPassword) {
    ElMessage.warning('密碼與確認密碼不相符')
    return
  }

  submitLoading.value = true
  try {
    await api.post('/users/register', addUserForm.value)
    ElMessage.success('帳號新增成功')
    addUserForm.value = {
      username: '',
      role: '',
      password: '',
      confirmPassword: '',
    }
    addUserDialog.value = false
    loadAllUsers()
  } catch (error) {
    console.error('帳號新增失敗:', error)
    if (error.response?.status !== 401) {
      ElMessage.error('帳號新增失敗')
    }
  } finally {
    submitLoading.value = false
  }
}

// 格式化日期
const formatDate = (row, column, cellValue) => {
  if (cellValue === null || cellValue === undefined || cellValue === '') {
    return '無'
  }

  const d = new Date(cellValue)

  if (isNaN(d.getTime())) {
    return '無'
  }

  return d.toLocaleDateString('zh-TW', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  })
}

const resetPasswordForm = () => {
  updatePasswordForm.value = {
    newPassword: '',
    confirmNewPassword: '',
  }
}

const resetAddUserForm = () => {
  addUserForm.value = {
    username: '',
    role: '',
    password: '',
    confirmPassword: '',
  }
}

const handleAdd = () => {
  addUserDialog.value = true
}

const handleUpdatePassword = (row) => {
  selectedUsername.value = row.username
  updatePasswordDialog.value = true
}

// 分頁
const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
}

const handleCurrentChange = (val) => {
  currentPage.value = val
}

onMounted(() => {
  loadAllUsers()
})
</script>

<style scoped>
/* 可自訂樣式 */
</style>
