<template>
  <div>
    <el-card shadow="hover">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span style="font-size: 18px; font-weight: 600">
            <el-icon><Monitor /></el-icon>
            機台即時監控
          </span>
          <div>
            <el-tag :type="wsConnected ? 'success' : 'danger'" size="large">
              <el-icon v-if="wsConnected"><Connection /></el-icon>
              <el-icon v-else><Close /></el-icon>
              {{ wsConnected ? '已連線' : '未連線' }}
            </el-tag>
            <el-button
              type="primary"
              @click="loadRealtimeData"
              :loading="loading"
              style="margin-left: 10px"
            >
              <el-icon><Refresh /></el-icon>
              重新整理
            </el-button>
          </div>
        </div>
      </template>

      <!-- 統計卡片 -->
      <el-row :gutter="20" style="margin-bottom: 20px">
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div
              class="stat-icon"
              style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)"
            >
              <el-icon :size="30"><Monitor /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ machineData.length }}</div>
              <div class="stat-label">機台總數</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div
              class="stat-icon"
              style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)"
            >
              <el-icon :size="30"><CircleCheck /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ normalMachines }}</div>
              <div class="stat-label">正常運行</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div
              class="stat-icon"
              style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)"
            >
              <el-icon :size="30"><Warning /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ warningMachines }}</div>
              <div class="stat-label">警告</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div
              class="stat-icon"
              style="background: linear-gradient(135deg, #fa709a 0%, #fee140 100%)"
            >
              <el-icon :size="30"><CircleClose /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ criticalMachines }}</div>
              <div class="stat-label">嚴重</div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 機台列表 -->
      <el-table :data="machineData" border stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="machineId" label="機台ID" width="100" />
        <el-table-column prop="machineName" label="機台名稱" min-width="150" />
        <el-table-column prop="status" label="運行狀態" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="temperature" label="溫度 (°C)" width="120">
          <template #default="{ row }">
            <span :style="{ color: getTemperatureColor(row.temperature) }">
              {{ row.temperature ? row.temperature.toFixed(1) : '-' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="speed" label="轉速" width="100">
          <template #default="{ row }">
            {{ row.speed ? row.speed.toFixed(0) : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="powerConsumption" label="功耗 (kW)" width="120">
          <template #default="{ row }">
            {{ row.powerConsumption ? row.powerConsumption.toFixed(2) : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="vibration" label="震動值" width="100">
          <template #default="{ row }">
            {{ row.vibration ? row.vibration.toFixed(2) : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="alertLevel" label="警報" width="100">
          <template #default="{ row }">
            <el-tag :type="getAlertType(row.alertLevel)" size="small">
              {{ getAlertText(row.alertLevel) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="recordedAt" label="更新時間" width="180">
          <template #default="{ row }">
            {{ formatDate(row.recordedAt) }}
          </template>
        </el-table-column>
        <el-table-column prop="message" label="訊息" min-width="150" show-overflow-tooltip />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" plain @click="viewHistory(row)">
              歷史
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 歷史數據對話框 -->
    <el-dialog v-model="historyDialogVisible" title="機台歷史數據" width="90%" top="5vh">
      <div style="margin-bottom: 20px">
        <el-date-picker
          v-model="dateRange"
          type="datetimerange"
          range-separator="至"
          start-placeholder="開始時間"
          end-placeholder="結束時間"
          style="margin-right: 10px"
        />
        <el-button type="primary" @click="loadHistoryData">查詢</el-button>
      </div>

      <el-table :data="historyData" border stripe style="width: 100%" max-height="500">
        <el-table-column prop="temperature" label="溫度 (°C)" width="120" />
        <el-table-column prop="status" label="狀態" width="100" />
        <el-table-column prop="speed" label="轉速" width="100" />
        <el-table-column prop="powerConsumption" label="功耗 (kW)" width="120" />
        <el-table-column prop="vibration" label="震動值" width="100" />
        <el-table-column prop="recordedAt" label="記錄時間" width="180">
          <template #default="{ row }">
            {{ formatDate(row.recordedAt) }}
          </template>
        </el-table-column>
        <el-table-column prop="message" label="訊息" min-width="200" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Monitor,
  Connection,
  Close,
  Refresh,
  CircleCheck,
  Warning,
  CircleClose,
} from '@element-plus/icons-vue'
import api from '@/utils/api'
import SockJS from 'sockjs-client'
import { Client } from '@stomp/stompjs'

const machineData = ref([])
const loading = ref(false)
const wsConnected = ref(false)
const historyDialogVisible = ref(false)
const historyData = ref([])
const dateRange = ref([])
const selectedMachineId = ref(null)

let stompClient = null

// 統計數據
const normalMachines = computed(
  () => machineData.value.filter((m) => m.alertLevel === 'normal').length,
)
const warningMachines = computed(
  () => machineData.value.filter((m) => m.alertLevel === 'warning').length,
)
const criticalMachines = computed(
  () => machineData.value.filter((m) => m.alertLevel === 'critical').length,
)

// 載入即時數據
const loadRealtimeData = async () => {
  loading.value = true
  try {
    const response = await api.get('/api/machines/realtime')
    machineData.value = response.data
    console.log('✅ 載入即時數據成功:', response.data.length, '筆')
  } catch (error) {
    console.error('❌ 載入即時數據失敗:', error)
    ElMessage.error('載入即時數據失敗')
  } finally {
    loading.value = false
  }
}

// 連接 WebSocket
const connectWebSocket = () => {
  const socket = new SockJS('http://localhost:8083/ws')

  stompClient = new Client({
    webSocketFactory: () => socket,
    reconnectDelay: 5000,
    heartbeatIncoming: 4000,
    heartbeatOutgoing: 4000,

    onConnect: () => {
      console.log('✅ WebSocket 已連線')
      wsConnected.value = true

      // 訂閱機台數據主題
      stompClient.subscribe('/topic/machine-data', (message) => {
        const data = JSON.parse(message.body)
        console.log('📡 收到即時數據:', data)
        updateMachineData(data)
      })
    },

    onDisconnect: () => {
      console.log('❌ WebSocket 已斷線')
      wsConnected.value = false
    },

    onStompError: (frame) => {
      console.error('❌ STOMP 錯誤:', frame)
      wsConnected.value = false
    },
  })

  stompClient.activate()
}

// 更新機台數據
const updateMachineData = (newData) => {
  const index = machineData.value.findIndex((m) => m.machineId === newData.machineId)
  if (index !== -1) {
    machineData.value[index] = newData
  } else {
    machineData.value.push(newData)
  }
}

// 查看歷史數據
const viewHistory = (row) => {
  selectedMachineId.value = row.machineId
  // 預設查詢最近24小時
  const end = new Date()
  const start = new Date(end.getTime() - 24 * 60 * 60 * 1000)
  dateRange.value = [start, end]
  historyDialogVisible.value = true
  loadHistoryData()
}

// 載入歷史數據
const loadHistoryData = async () => {
  if (!dateRange.value || dateRange.value.length !== 2) {
    ElMessage.warning('請選擇時間範圍')
    return
  }

  try {
    const [start, end] = dateRange.value
    const response = await api.get(`/api/machines/${selectedMachineId.value}/history`, {
      params: {
        start: start.toISOString(),
        end: end.toISOString(),
      },
    })
    historyData.value = response.data
  } catch (error) {
    console.error('❌ 載入歷史數據失敗:', error)
    ElMessage.error('載入歷史數據失敗')
  }
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-TW')
}

// 狀態類型
const getStatusType = (status) => {
  const types = {
    running: 'success',
    stopped: 'info',
    warning: 'warning',
    error: 'danger',
  }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = {
    running: '運行中',
    stopped: '已停止',
    warning: '警告',
    error: '錯誤',
    unknown: '未知',
  }
  return texts[status] || status
}

// 警報類型
const getAlertType = (level) => {
  const types = {
    normal: 'success',
    warning: 'warning',
    critical: 'danger',
  }
  return types[level] || 'info'
}

const getAlertText = (level) => {
  const texts = {
    normal: '正常',
    warning: '警告',
    critical: '嚴重',
  }
  return texts[level] || level
}

// 溫度顏色
const getTemperatureColor = (temp) => {
  if (!temp) return '#909399'
  if (temp >= 90) return '#F56C6C'
  if (temp >= 70) return '#E6A23C'
  return '#67C23A'
}

onMounted(() => {
  loadRealtimeData()
  connectWebSocket()
})

onUnmounted(() => {
  if (stompClient) {
    stompClient.deactivate()
  }
})
</script>

<style scoped>
.stat-card {
  position: relative;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-icon {
  position: absolute;
  right: 20px;
  top: 50%;
  transform: translateY(-50%);
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  opacity: 0.9;
}

.stat-content {
  padding: 10px 0;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}
</style>
