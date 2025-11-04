<template>
  <div>
    <!-- 統計卡片 -->
    <el-row :gutter="20" style="margin-bottom: 20px">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div
            class="stat-icon"
            style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)"
          >
            <el-icon :size="40"><Box /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.totalProducts }}</div>
            <div class="stat-label">產品總數</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div
            class="stat-icon"
            style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)"
          >
            <el-icon :size="40"><Goods /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.totalStock }}</div>
            <div class="stat-label">總庫存數量</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div
            class="stat-icon"
            style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)"
          >
            <el-icon :size="40"><ShoppingCart /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.totalOrders }}</div>
            <div class="stat-label">訂單總數</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div
            class="stat-icon"
            style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)"
          >
            <el-icon :size="40"><Money /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">NT$ {{ formatMoney(stats.totalRevenue) }}</div>
            <div class="stat-label">總營收</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快捷操作 -->
    <el-card shadow="hover" style="margin-bottom: 20px">
      <template #header>
        <span style="font-size: 16px; font-weight: 600">快捷操作</span>
      </template>
      <el-row :gutter="15">
        <el-col :span="6">
          <el-button type="primary" style="width: 100%" @click="goToProducts">
            <el-icon><Plus /></el-icon>
            新增產品
          </el-button>
        </el-col>
        <el-col :span="6">
          <el-button type="success" style="width: 100%" @click="goToInventory">
            <el-icon><Upload /></el-icon>
            庫存入庫
          </el-button>
        </el-col>
        <el-col :span="6">
          <el-button type="warning" style="width: 100%" @click="goToOrders">
            <el-icon><ShoppingCart /></el-icon>
            新增訂單
          </el-button>
        </el-col>
        <el-col :span="6">
          <el-button type="info" style="width: 100%" @click="goToTransactions">
            <el-icon><List /></el-icon>
            查看異動紀錄
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 低庫存警告 -->
    <el-card shadow="hover" style="margin-bottom: 20px" v-if="lowStockItems.length > 0">
      <template #header>
        <span style="font-size: 16px; font-weight: 600; color: #e6a23c">
          <el-icon><Warning /></el-icon>
          庫存不足警告
        </span>
      </template>
      <el-table :data="lowStockItems" stripe style="width: 100%">
        <el-table-column prop="productId" label="產品ID" width="100" />
        <el-table-column label="產品名稱" min-width="200">
          <template #default="{ row }">
            {{ getProductName(row.productId) }}
          </template>
        </el-table-column>
        <el-table-column prop="stockQuantity" label="當前庫存" width="120">
          <template #default="{ row }">
            <el-tag type="danger">{{ row.stockQuantity }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button size="small" type="success" plain @click="handleRestock(row)">
              立即補貨
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 最近訂單 -->
    <el-card shadow="hover">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span style="font-size: 16px; font-weight: 600">最近訂單</span>
          <el-button type="text" @click="goToOrders">查看全部</el-button>
        </div>
      </template>
      <el-table :data="recentOrders" stripe style="width: 100%">
        <el-table-column prop="orderId" label="訂單ID" width="100" />
        <el-table-column prop="customerName" label="客戶名稱" width="150" />
        <el-table-column prop="totalAmount" label="金額" width="120">
          <template #default="{ row }">
            NT$ {{ Number(row.totalAmount).toLocaleString() }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="狀態" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="orderDate" label="訂單日期" width="180">
          <template #default="{ row }">
            {{ formatDate(row.orderDate) }}
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  Box,
  Goods,
  ShoppingCart,
  Money,
  Plus,
  Upload,
  List,
  Warning,
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import api from '@/utils/api'

const router = useRouter()

const stats = ref({
  totalProducts: 0,
  totalStock: 0,
  totalOrders: 0,
  totalRevenue: 0,
})

const lowStockItems = ref([])
const recentOrders = ref([])
const products = ref([])

const formatMoney = (amount) => {
  return Number(amount || 0).toLocaleString()
}

const getProductName = (productId) => {
  const product = products.value.find((p) => p.productId === productId)
  return product ? product.productName : `產品 #${productId}`
}

const getStatusType = (status) => {
  const types = {
    Pending: 'info',
    Processing: 'warning',
    Completed: 'success',
    Cancelled: 'danger',
  }
  return types[status] || 'info'
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-TW')
}

const loadDashboardData = async () => {
  try {
    // 載入產品
    const productsRes = await api.get('/api/products')
    products.value = productsRes.data
    stats.value.totalProducts = productsRes.data.length

    // 載入庫存
    const inventoryRes = await api.get('/api/inventory')
    stats.value.totalStock = inventoryRes.data.reduce((sum, item) => sum + item.stockQuantity, 0)
    // 找出庫存不足的項目（庫存 < 10）
    lowStockItems.value = inventoryRes.data.filter((item) => item.stockQuantity < 10)

    // 載入訂單
    const ordersRes = await api.get('/api/orders')

    // 如果 API 回傳格式是 { code, message, data: [...] }
    const orders = ordersRes.data.data || []

    stats.value.totalOrders = orders.length
    stats.value.totalRevenue = orders
      .filter((order) => order.status === 'Completed')
      .reduce((sum, order) => sum + Number(order.totalAmount), 0)
    recentOrders.value = orders.slice(0, 5)

    console.log('✅ 儀表板資料載入成功')
  } catch (error) {
    console.error('❌ 載入儀表板資料失敗:', error)
    ElMessage.error('載入儀表板資料失敗')
  }
}

const goToProducts = () => router.push('/home/products')
const goToInventory = () => router.push('/home/inventory')
const goToOrders = () => router.push('/home/orders')
const goToTransactions = () => router.push('/home/transactions')

const handleRestock = (row) => {
  router.push('/home/inventory')
  ElMessage.info(`請為產品 #${row.productId} 進行補貨`)
}

onMounted(() => {
  loadDashboardData()
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
  width: 80px;
  height: 80px;
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
  font-size: 32px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}
</style>
