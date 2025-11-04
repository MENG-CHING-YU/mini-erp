<template>
  <div>
    <el-card shadow="hover">
      <template #header>
        <span style="font-size: 18px; font-weight: 600">庫存異動紀錄</span>
      </template>

      <!-- 搜尋區域 -->
      <el-form :inline="true" style="margin-bottom: 20px">
        <el-form-item label="產品ID">
          <el-input-number
            v-model="searchForm.productId"
            placeholder="請輸入產品ID"
            :controls="false"
            style="width: 150px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜尋</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 資料表格 -->
      <el-table v-loading="loading" :data="tableData" border stripe style="width: 100%">
        <el-table-column prop="transactionId" label="交易ID" width="100" />
        <el-table-column prop="productId" label="產品ID" width="100" />
        <el-table-column label="產品名稱" min-width="200">
          <template #default="{ row }">
            {{ getProductName(row.productId) }}
          </template>
        </el-table-column>
        <el-table-column prop="transactionType" label="異動類型" width="120">
          <template #default="{ row }">
            <el-tag :type="getTransactionType(row.transactionType)" size="small">
              {{ getTransactionText(row.transactionType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="quantity" label="數量" width="100">
          <template #default="{ row }">
            <span :style="{ color: row.quantity > 0 ? '#67C23A' : '#F56C6C' }">
              {{ row.quantity > 0 ? '+' : '' }}{{ row.quantity }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="orderId" label="關聯訂單" width="120">
          <template #default="{ row }">
            {{ row.orderId || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="transactionDate" label="異動時間" width="180">
          <template #default="{ row }">
            {{ formatDate(row.transactionDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="notes" label="備註" min-width="150" show-overflow-tooltip />
      </el-table>

      <!-- 分頁 -->
      <div style="margin-top: 20px; display: flex; justify-content: flex-end">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import api from '@/utils/api'

const searchForm = ref({ productId: null })
const tableData = ref([])
const products = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)

const getProductName = (productId) => {
  const product = products.value.find((p) => p.productId === productId)
  return product ? product.productName : `產品 #${productId}`
}

const getTransactionType = (type) => {
  const types = {
    INITIAL: 'info',
    IN: 'success',
    OUT: 'danger',
    UPDATE: 'warning',
  }
  return types[type] || 'info'
}

const getTransactionText = (type) => {
  const texts = {
    INITIAL: '初始化',
    IN: '入庫',
    OUT: '出庫',
    UPDATE: '調整',
  }
  return texts[type] || type
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-TW')
}

const loadTransactions = async (productId = null) => {
  loading.value = true
  try {
    let response
    if (productId) {
      response = await api.get(`/api/inventory-transactions/product/${productId}`)
      tableData.value = response.data
      total.value = response.data.length
    } else {
      // 如果沒有指定產品ID，載入所有產品的交易紀錄
      ElMessage.info('請輸入產品ID查詢交易紀錄')
      tableData.value = []
      total.value = 0
    }
    console.log('✅ 載入交易紀錄成功:', tableData.value.length, '筆')
  } catch (error) {
    console.error('❌ 載入交易紀錄失敗:', error)
    if (error.response?.status === 404) {
      ElMessage.warning('找不到該產品的交易紀錄')
      tableData.value = []
      total.value = 0
    } else {
      ElMessage.error(error.response?.data?.message || '載入交易紀錄失敗')
    }
  } finally {
    loading.value = false
  }
}

const loadProducts = async () => {
  try {
    const response = await api.get('/api/products')
    products.value = response.data
  } catch (error) {
    console.error('❌ 載入產品失敗:', error)
  }
}

const handleSearch = () => {
  if (!searchForm.value.productId) {
    ElMessage.warning('請輸入產品ID')
    return
  }
  currentPage.value = 1
  loadTransactions(searchForm.value.productId)
}

const handleReset = () => {
  searchForm.value = { productId: null }
  tableData.value = []
  total.value = 0
  currentPage.value = 1
}

onMounted(() => {
  loadProducts()
})
</script>
