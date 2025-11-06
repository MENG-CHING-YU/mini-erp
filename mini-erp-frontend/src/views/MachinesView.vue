<template>
  <div>
    <el-card shadow="hover">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span style="font-size: 18px; font-weight: 600">機台管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增機台
          </el-button>
        </div>
      </template>

      <!-- 搜尋區域 -->
      <el-form :inline="true" style="margin-bottom: 20px">
        <el-form-item label="機台名稱">
          <el-input
            v-model="searchForm.name"
            placeholder="請輸入機台名稱"
            clearable
            style="width: 180px"
          />
        </el-form-item>
        <el-form-item label="狀態">
          <el-select
            v-model="searchForm.status"
            placeholder="請選擇"
            clearable
            style="width: 120px"
          >
            <el-option label="運行中" value="running" />
            <el-option label="已停止" value="stopped" />
            <el-option label="維護中" value="maintenance" />
          </el-select>
        </el-form-item>
        <el-form-item label="位置">
          <el-input
            v-model="searchForm.location"
            placeholder="位置"
            clearable
            style="width: 120px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜尋</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 資料表格 - 精簡顯示 -->
      <el-table
        v-loading="loading"
        :data="filteredTableData"
        border
        stripe
        style="width: 100%; min-width: 1000px"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="機台名稱" min-width="200" show-overflow-tooltip />
        <el-table-column prop="serialNumber" label="出廠編號" min-width="150" />
        <el-table-column prop="model" label="型號" min-width="120" show-overflow-tooltip />
        <el-table-column prop="status" label="狀態" min-width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="location" label="位置" min-width="150" />
        <el-table-column prop="responsiblePerson" label="負責人" min-width="120" />
        <el-table-column label="保養狀態" min-width="120">
          <template #default="{ row }">
            <el-tag v-if="isMaintenanceDue(row)" type="danger" size="small">需保養</el-tag>
            <el-tag v-else type="success" size="small">正常</el-tag>
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
      width="800px"
      @close="resetForm"
      :close-on-click-modal="false"
    >
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="機台名稱" prop="name">
              <el-input v-model="formData.name" placeholder="請輸入機台名稱" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="出廠編號" prop="serialNumber">
              <el-input
                v-model="formData.serialNumber"
                placeholder="請輸入出廠編號"
                :disabled="isEdit"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="型號" prop="model">
              <el-input v-model="formData.model" placeholder="請輸入型號" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="製造商" prop="manufacturer">
              <el-input v-model="formData.manufacturer" placeholder="請輸入製造商" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="狀態" prop="status">
              <el-select v-model="formData.status" placeholder="請選擇狀態" style="width: 100%">
                <el-option label="運行中" value="running" />
                <el-option label="已停止" value="stopped" />
                <el-option label="維護中" value="maintenance" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="位置" prop="location">
              <el-input v-model="formData.location" placeholder="請輸入位置" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="購買日期" prop="purchaseDate">
              <el-date-picker
                v-model="formData.purchaseDate"
                type="date"
                placeholder="選擇日期"
                style="width: 100%"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="保固到期日" prop="warrantyEndDate">
              <el-date-picker
                v-model="formData.warrantyEndDate"
                type="date"
                placeholder="選擇日期"
                style="width: 100%"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="購買價格" prop="price">
              <el-input v-model.number="formData.price" placeholder="請輸入金額" type="number">
                <template #prepend>NT$</template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="負責人" prop="responsiblePerson">
              <el-input v-model="formData.responsiblePerson" placeholder="請輸入負責人" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="保養週期" prop="maintenanceCycle">
              <el-input v-model.number="formData.maintenanceCycle" placeholder="天數" type="number">
                <template #append>天</template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="上次保養日期">
              <el-date-picker
                v-model="formData.lastMaintenanceDate"
                type="date"
                placeholder="選擇日期"
                style="width: 100%"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="備註說明">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="請輸入機台相關備註..."
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">確定</el-button>
      </template>
    </el-dialog>

    <!-- 詳情對話框 -->
    <el-dialog
      v-model="detailVisible"
      title="機台詳細資訊"
      width="900px"
      :close-on-click-modal="false"
    >
      <el-descriptions :column="2" border v-if="detailData">
        <el-descriptions-item label="ID">{{ detailData.id }}</el-descriptions-item>
        <el-descriptions-item label="機台名稱">{{ detailData.name }}</el-descriptions-item>
        <el-descriptions-item label="出廠編號">{{ detailData.serialNumber }}</el-descriptions-item>
        <el-descriptions-item label="型號">{{ detailData.model || '-' }}</el-descriptions-item>
        <el-descriptions-item label="製造商">{{
          detailData.manufacturer || '-'
        }}</el-descriptions-item>
        <el-descriptions-item label="狀態">
          <el-tag :type="getStatusType(detailData.status)">
            {{ getStatusText(detailData.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="放置位置">{{
          detailData.location || '-'
        }}</el-descriptions-item>
        <el-descriptions-item label="負責人">{{
          detailData.responsiblePerson || '-'
        }}</el-descriptions-item>
        <el-descriptions-item label="購買日期">{{
          formatDate(detailData.purchaseDate, true)
        }}</el-descriptions-item>
        <el-descriptions-item label="保固到期日">
          <span :class="{ 'text-danger': isWarrantyExpired(detailData.warrantyEndDate) }">
            {{ formatDate(detailData.warrantyEndDate, true) }}
            <el-tag v-if="isWarrantyExpired(detailData.warrantyEndDate)" type="danger" size="small"
              >已過保</el-tag
            >
          </span>
        </el-descriptions-item>
        <el-descriptions-item label="購買價格">
          {{ detailData.price ? `NT$ ${Number(detailData.price).toLocaleString()}` : '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="保養週期">
          {{ detailData.maintenanceCycle ? `${detailData.maintenanceCycle} 天` : '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="上次保養日期">
          {{ formatDate(detailData.lastMaintenanceDate, true) }}
        </el-descriptions-item>
        <el-descriptions-item label="下次保養日期">
          <span :class="{ 'text-danger': isMaintenanceDue(detailData) }">
            {{ formatDate(detailData.nextMaintenanceDate, true) }}
            <el-tag v-if="isMaintenanceDue(detailData)" type="danger" size="small">需保養</el-tag>
          </span>
        </el-descriptions-item>
        <el-descriptions-item label="建立時間" :span="2">
          {{ formatDate(detailData.createdAt) }}
        </el-descriptions-item>
        <el-descriptions-item label="更新時間" :span="2">
          {{ formatDate(detailData.updatedAt) }}
        </el-descriptions-item>
        <el-descriptions-item label="備註說明" :span="2">
          {{ detailData.description || '無' }}
        </el-descriptions-item>
      </el-descriptions>

      <template #footer>
        <el-button @click="detailVisible = false">關閉</el-button>
        <el-button type="primary" @click="handleEditFromDetail">編輯</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/utils/api'

// 搜尋表單
const searchForm = ref({
  name: '',
  status: '',
  location: '',
})

// 表格資料
const tableData = ref([])
const loading = ref(false)
const submitLoading = ref(false)

// 分頁
const currentPage = ref(1)
const pageSize = ref(10)

// 對話框
const dialogVisible = ref(false)
const dialogTitle = ref('新增機台')
const formRef = ref(null)
const isEdit = ref(false)
const editId = ref(null)

// 詳情對話框
const detailVisible = ref(false)
const detailData = ref(null)

// 表單資料
const formData = ref({
  name: '',
  serialNumber: '',
  model: '',
  manufacturer: '',
  status: 'running',
  location: '',
  purchaseDate: null,
  warrantyEndDate: null,
  maintenanceCycle: 30,
  lastMaintenanceDate: null,
  price: null,
  responsiblePerson: '',
  description: '',
})

// 表單驗證規則
const formRules = {
  name: [{ required: true, message: '請輸入機台名稱', trigger: 'blur' }],
  serialNumber: [{ required: true, message: '請輸入出廠編號', trigger: 'blur' }],
  status: [{ required: true, message: '請選擇狀態', trigger: 'change' }],
}

// 過濾資料
const filteredData = computed(() => {
  let data = tableData.value

  if (searchForm.value.name) {
    data = data.filter((item) =>
      item.name.toLowerCase().includes(searchForm.value.name.toLowerCase()),
    )
  }

  if (searchForm.value.status) {
    data = data.filter((item) => item.status === searchForm.value.status)
  }

  if (searchForm.value.location) {
    data = data.filter((item) =>
      item.location?.toLowerCase().includes(searchForm.value.location.toLowerCase()),
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

// 狀態類型
const getStatusType = (status) =>
  ({
    running: 'success',
    stopped: 'danger',
    maintenance: 'warning',
  })[status] || 'info'

const getStatusText = (status) => {
  const textMap = {
    running: '運行中',
    Running: '運行中',
    stopped: '已停止',
    Stopped: '已停止',
    maintenance: '維護中',
    error: '錯誤',
    Error: '錯誤',
    idle: '閒置',
    Idle: '閒置',
  }
  return textMap[status] || status
}

// 判斷是否需要保養
const isMaintenanceDue = (row) => {
  if (!row.nextMaintenanceDate) return false
  const today = new Date()
  const nextDate = new Date(row.nextMaintenanceDate)
  return nextDate <= today
}

// 判斷保固是否過期
const isWarrantyExpired = (date) => {
  if (!date) return false
  const today = new Date()
  const warrantyDate = new Date(date)
  return warrantyDate < today
}

// 格式化日期
const formatDate = (date, dateOnly = false) => {
  if (!date) return '-'
  const d = new Date(date)

  if (dateOnly) {
    return d.toLocaleDateString('zh-TW', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
    })
  }

  return d.toLocaleString('zh-TW', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  })
}

// 載入機台資料
const loadMachines = async () => {
  loading.value = true
  try {
    const response = await api.get('/machines')
    tableData.value = response.data
    console.log('✅ 載入成功:', response.data.length, '筆資料')
  } catch (error) {
    console.error('❌ 載入機台失敗:', error)
    if (error.response?.status !== 401) {
      ElMessage.error('載入機台資料失敗')
    }
  } finally {
    loading.value = false
  }
}

// 搜尋
const handleSearch = () => {
  currentPage.value = 1
}

// 重置
const handleReset = () => {
  searchForm.value = {
    name: '',
    status: '',
    location: '',
  }
  currentPage.value = 1
}

// 新增
const handleAdd = () => {
  isEdit.value = false
  editId.value = null
  dialogTitle.value = '新增機台'
  formData.value = {
    name: '',
    serialNumber: '',
    model: '',
    manufacturer: '',
    status: 'running',
    location: '',
    purchaseDate: null,
    warrantyEndDate: null,
    maintenanceCycle: 30,
    lastMaintenanceDate: null,
    price: null,
    responsiblePerson: '',
    description: '',
  }
  dialogVisible.value = true
}

// 編輯
const handleEdit = (row) => {
  isEdit.value = true
  editId.value = row.id
  dialogTitle.value = '編輯機台'
  formData.value = { ...row }
  dialogVisible.value = true
}

// 詳情
const handleDetail = (row) => {
  detailData.value = { ...row }
  detailVisible.value = true
}

// 從詳情頁轉編輯
const handleEditFromDetail = () => {
  detailVisible.value = false
  handleEdit(detailData.value)
}

// 刪除
const handleDelete = (row) => {
  ElMessageBox.confirm(`確定要刪除機台 "${row.name}" 嗎?`, '警告', {
    confirmButtonText: '確定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      try {
        await api.delete(`/machines/${row.id}`)
        ElMessage.success('刪除成功')
        loadMachines()
      } catch (error) {
        console.error('❌ 刪除失敗:', error)
        if (error.response?.status !== 401) {
          ElMessage.error('刪除失敗')
        }
      }
    })
    .catch(() => {})
}

// 提交
const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    submitLoading.value = true

    try {
      if (isEdit.value) {
        await api.put(`/machines/${editId.value}`, formData.value)
        ElMessage.success('更新成功')
      } else {
        await api.post('/machines', formData.value)
        ElMessage.success('新增成功')
      }

      dialogVisible.value = false
      loadMachines()
    } catch (error) {
      console.error('❌ 操作失敗:', error)
      if (error.response?.status !== 401) {
        ElMessage.error((isEdit.value ? '更新' : '新增') + '失敗')
      }
    } finally {
      submitLoading.value = false
    }
  })
}

// 重置表單
const resetForm = () => {
  if (formRef.value) formRef.value.resetFields()
  editId.value = null
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
  loadMachines()
})
</script>

<style scoped>
.text-danger {
  color: #f56c6c;
  font-weight: 600;
}

:deep(.el-dialog__header) {
  padding: 20px 20px 10px;
  border-bottom: 1px solid #e4e7ed;
}

:deep(.el-dialog__body) {
  padding: 20px;
}

:deep(.el-form-item) {
  margin-bottom: 18px;
}

:deep(.el-tag) {
  font-weight: 500;
}
</style>
