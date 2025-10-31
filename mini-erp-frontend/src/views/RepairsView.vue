<template>
  <div>
    <el-card shadow="hover">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span style="font-size: 18px; font-weight: 600">維修管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增維修單
          </el-button>
        </div>
      </template>

      <!-- 搜尋區域 -->
      <el-form :inline="true" style="margin-bottom: 20px">
        <el-form-item label="維修單號">
          <el-input v-model="searchForm.ticketNo" placeholder="請輸入維修單號" clearable />
        </el-form-item>
        <el-form-item label="狀態">
          <el-select v-model="searchForm.status" placeholder="請選擇狀態" clearable>
            <el-option label="待處理" value="pending" />
            <el-option label="處理中" value="processing" />
            <el-option label="已完成" value="completed" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜尋</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 資料表格 -->
      <el-table :data="tableData" border stripe style="width: 100%">
        <el-table-column prop="ticketNo" label="維修單號" width="150" />
        <el-table-column prop="machineName" label="機台名稱" width="150" />
        <el-table-column prop="issueDescription" label="問題描述" min-width="200" />
        <el-table-column prop="status" label="狀態" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="priority" label="優先級" width="100">
          <template #default="{ row }">
            <el-tag :type="getPriorityType(row.priority)" size="small">
              {{ getPriorityText(row.priority) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="technician" label="維修人員" width="120" />
        <el-table-column prop="createdAt" label="建立時間" width="180" />
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="handleView(row)">查看</el-button>
            <el-button size="small" type="success" @click="handleProcess(row)">處理</el-button>
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

// 搜尋表單
const searchForm = ref({
  ticketNo: '',
  status: '',
})

// 表格資料
const tableData = ref([
  {
    ticketNo: 'R20240001',
    machineName: '機台A',
    issueDescription: '機台異常震動，需要檢查馬達',
    status: 'pending',
    priority: 'high',
    technician: '張三',
    createdAt: '2024-10-20 09:30:00',
  },
  {
    ticketNo: 'R20240002',
    machineName: '機台B',
    issueDescription: '溫度過高警報',
    status: 'processing',
    priority: 'medium',
    technician: '李四',
    createdAt: '2024-10-21 10:15:00',
  },
  {
    ticketNo: 'R20240003',
    machineName: '機台C',
    issueDescription: '定期保養維護',
    status: 'completed',
    priority: 'low',
    technician: '王五',
    createdAt: '2024-10-22 14:20:00',
  },
])

// 分頁
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(3)

// 狀態類型
const getStatusType = (status) => {
  const typeMap = {
    pending: 'warning',
    processing: 'primary',
    completed: 'success',
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    pending: '待處理',
    processing: '處理中',
    completed: '已完成',
  }
  return textMap[status] || '未知'
}

const getPriorityType = (priority) => {
  const typeMap = {
    high: 'danger',
    medium: 'warning',
    low: 'info',
  }
  return typeMap[priority] || 'info'
}

const getPriorityText = (priority) => {
  const textMap = {
    high: '高',
    medium: '中',
    low: '低',
  }
  return textMap[priority] || '未知'
}

// 方法
const handleSearch = () => {
  ElMessage.success('搜尋功能開發中')
}

const handleReset = () => {
  searchForm.value.ticketNo = ''
  searchForm.value.status = ''
  ElMessage.info('已重置搜尋條件')
}

const handleAdd = () => {
  ElMessage.info('新增維修單功能開發中')
}

const handleView = (row) => {
  ElMessage.info(`查看維修單: ${row.ticketNo}`)
}

const handleProcess = (row) => {
  ElMessage.info(`處理維修單: ${row.ticketNo}`)
}

const handleSizeChange = (val) => {
  pageSize.value = val
}

const handleCurrentChange = (val) => {
  currentPage.value = val
}

onMounted(() => {
  // 載入資料
})
</script>

<style scoped>
/* 可自訂樣式 */
</style>
