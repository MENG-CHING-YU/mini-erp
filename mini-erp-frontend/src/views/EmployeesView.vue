<template>
  <div>
    <el-card shadow="hover">
      <!-- 標題與新增按鈕 -->
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span style="font-size: 18px; font-weight: 600">員工管理</span>
          <div>
            <el-button type="primary" @click="handleAdd" style="margin-right: 10px"
              >新增員工</el-button
            >
            <el-button type="success" @click="handleExport">導出資料</el-button>
          </div>
        </div>
      </template>

      <!-- 搜尋區域 -->
      <el-form :inline="true" style="margin-bottom: 20px">
        <el-form-item label="姓名">
          <el-input
            v-model="searchForm.fullName"
            placeholder="請輸入姓名"
            clearable
            style="width: 180px"
          />
        </el-form-item>
        <el-form-item label="部門">
          <el-input
            v-model="searchForm.department"
            placeholder="請輸入部門"
            clearable
            style="width: 150px"
          />
        </el-form-item>
        <el-form-item label="職位">
          <el-input
            v-model="searchForm.position"
            placeholder="請輸入職位"
            clearable
            style="width: 150px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜尋</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table v-loading="loading" :data="filteredTableData" border stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="fullName" label="姓名" min-width="150" />
        <el-table-column prop="department" label="部門" min-width="120" />
        <el-table-column prop="position" label="職位" min-width="120" />
        <el-table-column prop="hireDate" label="入職日期" min-width="150">
          <template #default="{ row }">
            {{ formatDate(row.hireDate, true) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" min-width="180">
          <template #default="{ row }">
            <el-button size="small" type="info" plain @click="handleDetail(row)">詳情</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">刪除</el-button>
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
    </el-card>

    <!-- 新增/編輯對話框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      :close-on-click-modal="false"
      @close="resetForm"
    >
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="姓名" prop="fullName">
          <el-input v-model="formData.fullName"></el-input>
        </el-form-item>
        <el-form-item label="Email" prop="email">
          <el-input v-model="formData.email"></el-input>
        </el-form-item>

        <!-- 工號 改為下拉選單 -->
        <el-form-item label="工號" prop="userId">
          <template v-if="isEdit">
            <el-input v-model="formData.userId" readonly></el-input>
          </template>
          <template v-else>
            <el-select v-model="formData.userId" placeholder="請選擇工號" clearable>
              <el-option
                v-for="user in unassignedUsers"
                :key="user.id"
                :label="user.userId"
                :value="user.id"
              ></el-option>
            </el-select>
          </template>
        </el-form-item>

        <el-form-item label="部門" prop="department">
          <el-input v-model="formData.department"></el-input>
        </el-form-item>
        <el-form-item label="職位" prop="position">
          <el-input v-model="formData.position"></el-input>
        </el-form-item>
        <el-form-item label="入職日期" prop="hireDate">
          <el-date-picker
            v-model="formData.hireDate"
            type="date"
            placeholder="選擇日期"
            style="width: 100%"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">保存</el-button>
      </template>
    </el-dialog>

    <!-- 詳情對話框 -->
    <el-dialog
      v-model="detailVisible"
      title="員工詳細資訊"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-descriptions :column="2" border v-if="detailData">
        <el-descriptions-item label="ID">{{ detailData.id }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ detailData.fullName }}</el-descriptions-item>
        <el-descriptions-item label="Email">{{ detailData.email || '-' }}</el-descriptions-item>
        <el-descriptions-item label="工號">{{ detailData.userId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="部門">{{ detailData.department || '-' }}</el-descriptions-item>
        <el-descriptions-item label="職位">{{ detailData.position || '-' }}</el-descriptions-item>
        <el-descriptions-item label="入職日期">{{
          formatDate(detailData.hireDate, true)
        }}</el-descriptions-item>
        <el-descriptions-item label="建立時間">{{
          formatDate(detailData.createdAt)
        }}</el-descriptions-item>
        <el-descriptions-item label="更新時間">{{
          formatDate(detailData.updatedAt)
        }}</el-descriptions-item>
      </el-descriptions>

      <template #footer>
        <el-button @click="detailVisible = false">關閉</el-button>
        <el-button type="primary" @click="handleEditFromDetail">編輯</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { saveAs } from 'file-saver'
import * as XLSX from 'xlsx'
import {
  getAllEmployees,
  getEmployeeById,
  saveEmployee as apiSave,
  deleteEmployee as apiDelete,
  getUnassignedUsers,
} from '@/utils/employeeApi'

const employees = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增員工')
const detailVisible = ref(false)
const detailData = ref(null)
const formRef = ref(null)
const submitLoading = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const loading = ref(false)

const unassignedUsers = ref([])

const currentPage = ref(1)
const pageSize = ref(10)

const searchForm = ref({
  fullName: '',
  department: '',
  position: '',
})

const formData = ref({
  id: null,
  fullName: '',
  email: '',
  userId: '',
  department: '',
  position: '',
  hireDate: null,
})

const formRules = {
  fullName: [{ required: true, message: '請輸入姓名', trigger: 'blur' }],
  email: [{ type: 'email', message: 'Email 格式不正確', trigger: 'blur' }],
  userId: [{ required: true, message: '請選擇工號', trigger: 'blur' }],
}

const loadEmployees = async () => {
  loading.value = true
  try {
    const res = await getAllEmployees()
    employees.value = res.data
  } catch (err) {
    console.error(err)
    ElMessage.error('載入員工失敗')
  } finally {
    loading.value = false
  }
}

const loadUnassignedUsers = async () => {
  try {
    const res = await getUnassignedUsers()
    unassignedUsers.value = res.data
  } catch (err) {
    console.error(err)
    ElMessage.error('載入未指派使用者失敗')
  }
}

const filteredData = computed(() => {
  let data = employees.value
  if (searchForm.value.fullName) {
    data = data.filter((e) =>
      e.fullName.toLowerCase().includes(searchForm.value.fullName.toLowerCase()),
    )
  }
  if (searchForm.value.department) {
    data = data.filter((e) =>
      e.department?.toLowerCase().includes(searchForm.value.department.toLowerCase()),
    )
  }
  if (searchForm.value.position) {
    data = data.filter((e) =>
      e.position?.toLowerCase().includes(searchForm.value.position.toLowerCase()),
    )
  }
  return data
})

const filteredTableData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return filteredData.value.slice(start, start + pageSize.value)
})

const total = computed(() => filteredData.value.length)

const handleSearch = () => {
  currentPage.value = 1
}

const handleReset = () => {
  searchForm.value = { fullName: '', department: '', position: '' }
  currentPage.value = 1
}

const handleAdd = () => {
  isEdit.value = false
  editId.value = null
  dialogTitle.value = '新增員工'
  formData.value = {
    id: null,
    fullName: '',
    email: '',
    userId: '',
    department: '',
    position: '',
    hireDate: null,
  }
  dialogVisible.value = true
  nextTick(() => formRef.value?.clearValidate())
}

const handleEdit = (row) => {
  isEdit.value = true
  editId.value = row.id
  dialogTitle.value = '編輯員工'
  formData.value = { ...row }
  dialogVisible.value = true
  nextTick(() => formRef.value?.clearValidate())
}

const handleEditFromDetail = () => {
  detailVisible.value = false
  handleEdit(detailData.value)
}

const handleDetail = async (row) => {
  try {
    const res = await getEmployeeById(row.id)
    detailData.value = res.data
    detailVisible.value = true
  } catch (err) {
    console.error(err)
    ElMessage.error('載入詳情失敗')
  }
}

const handleDelete = async (row) => {
  ElMessageBox.confirm(`確定要刪除 ${row.fullName} 嗎？`, '警告', {
    type: 'warning',
    confirmButtonText: '確定',
    cancelButtonText: '取消',
  })
    .then(async () => {
      try {
        await apiDelete(row.id)
        ElMessage.success('刪除成功')
        loadEmployees()
      } catch (err) {
        console.error(err)
        ElMessage.error('刪除失敗')
      }
    })
    .catch(() => {})
}

const handleExport = () => {
  const worksheet = XLSX.utils.json_to_sheet(filteredData.value)
  const workbook = XLSX.utils.book_new()
  XLSX.utils.book_append_sheet(workbook, worksheet, '員工資料')

  const excelBuffer = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' })
  const data = new Blob([excelBuffer], { type: 'application/octet-stream' })
  saveAs(data, '員工資料.xlsx')
}

const handleSubmit = async () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    submitLoading.value = true
    try {
      await apiSave(formData.value)
      ElMessage.success('保存成功')
      dialogVisible.value = false
      loadEmployees()
    } catch (err) {
      console.error(err)
      ElMessage.error('保存失敗')
    } finally {
      submitLoading.value = false
    }
  })
}

const resetForm = () => {
  formRef.value?.resetFields()
  editId.value = null
}

const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
}
const handleCurrentChange = (val) => {
  currentPage.value = val
}

const formatDate = (date, dateOnly = false) => {
  if (!date) return '-'
  const d = new Date(date)
  if (dateOnly) {
    return d.toLocaleDateString('zh-TW', { year: 'numeric', month: '2-digit', day: '2-digit' })
  }
  return d.toLocaleString('zh-TW', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  })
}

onMounted(() => {
  loadEmployees()
  loadUnassignedUsers()
})
</script>

<style scoped>
.text-danger {
  color: #f56c6c;
  font-weight: 600;
}
:deep(.el-form-item) {
  margin-bottom: 18px;
}
:deep(.el-dialog__header) {
  padding: 20px 20px 10px;
  border-bottom: 1px solid #e4e7ed;
}
:deep(.el-dialog__body) {
  padding: 20px;
}
</style>
