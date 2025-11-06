<template>
  <div>
    <el-card shadow="hover">
      <template #header>
        <span style="font-size: 18px; font-weight: 600">庫存異動紀錄</span>
      </template>

      <!-- 搜尋區域 -->
      <el-form :inline="true" style="margin-bottom: 20px">
        <el-form-item label="產品">
          <el-select
            v-model="searchForm.productId"
            placeholder="請選擇產品 (不選則顯示全部)"
            clearable
            filterable
            style="width: 250px"
          >
            <el-option
              v-for="product in products"
              :key="product.productId"
              :label="`${product.productName} (ID: ${product.productId})`"
              :value="product.productId"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜尋</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 資料表格 -->
      <el-table v-loading="loading" :data="paginatedData" border stripe style="width: 100%">
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
            <span :style="{ color: row.quantity > 0 ? '#67C23A' : '#F56C6C', fontWeight: 'bold' }">
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
      <div
        style="margin-top: 20px; display: flex; justify-content: space-between; align-items: center"
      >
        <el-tag type="info">共 {{ filteredData.length }} 筆異動紀錄</el-tag>
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="filteredData.length"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
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
const allTransactions = ref([]) // 儲存所有交易紀錄
const products = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(20)

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
  // 支援大小寫轉換
  const upperType = type?.toUpperCase()
  const texts = {
    INITIAL: '初始化',
    IN: '入庫',
    OUT: '出庫',
    UPDATE: '調整',
  }
  return texts[upperType] || type
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-TW')
}

// 過濾資料 (根據選擇的產品ID)
const filteredData = computed(() => {
  if (!searchForm.value.productId) {
    return allTransactions.value
  }
  return allTransactions.value.filter((t) => t.productId === searchForm.value.productId)
})

// 分頁資料
const paginatedData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredData.value.slice(start, end)
})

// 載入所有交易紀錄
const loadAllTransactions = async () => {
  loading.value = true
  try {
    // 方法1: 如果後端有提供查詢所有交易的 API (推薦)
    // const response = await api.get('/api/inventory-transactions')
    // allTransactions.value = response.data

    // 方法2: 透過所有產品逐一查詢 (臨時方案)
    const allTransactionsData = []
    for (const product of products.value) {
      try {
        const response = await api.get(`/api/inventory-transactions/product/${product.productId}`)
        if (response.data && response.data.length > 0) {
          allTransactionsData.push(...response.data)
        }
      } catch (error) {
        // 某些產品可能沒有交易紀錄，忽略404錯誤
        if (error.response?.status !== 404) {
          console.error(`載入產品 ${product.productId} 交易紀錄失敗:`, error)
        }
      }
    }

    // 依交易時間排序 (最新的在前面)
    allTransactions.value = allTransactionsData.sort(
      (a, b) => new Date(b.transactionDate) - new Date(a.transactionDate),
    )

    console.log('✅ 載入交易紀錄成功:', allTransactions.value.length, '筆')

    if (allTransactions.value.length === 0) {
      ElMessage.info('目前沒有任何庫存異動紀錄')
    }
  } catch (error) {
    console.error('❌ 載入交易紀錄失敗:', error)
    ElMessage.error('載入交易紀錄失敗')
  } finally {
    loading.value = false
  }
}

const loadProducts = async () => {
  try {
    const response = await api.get('/api/products')
    products.value = response.data
    console.log('✅ 載入產品成功:', products.value.length, '筆')
  } catch (error) {
    console.error('❌ 載入產品失敗:', error)
    ElMessage.error('載入產品清單失敗')
  }
}

const handleSearch = () => {
  currentPage.value = 1
  if (searchForm.value.productId) {
    ElMessage.success(`顯示產品 ID ${searchForm.value.productId} 的異動紀錄`)
  } else {
    ElMessage.success('顯示所有產品的異動紀錄')
  }
}

const handleReset = () => {
  searchForm.value = { productId: null }
  currentPage.value = 1
  ElMessage.info('已重置搜尋條件')
}

const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
}

const handleCurrentChange = (val) => {
  currentPage.value = val
}

onMounted(async () => {
  // 先載入產品清單
  await loadProducts()
  // 再載入所有交易紀錄
  await loadAllTransactions()
})
</script>

<style scoped>
:deep(.el-table) {
  font-size: 14px;
}

:deep(.el-table__header) {
  font-weight: 600;
}
</style>
