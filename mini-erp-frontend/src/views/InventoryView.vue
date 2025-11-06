<template>
  <div>
    <el-card shadow="hover">
      <template #header>
        <span style="font-size: 18px; font-weight: 600">庫存管理</span>
      </template>

      <!-- 搜尋區域 -->
      <el-form :inline="true" style="margin-bottom: 20px">
        <el-form-item label="產品">
          <el-select
            v-model="searchForm.productId"
            placeholder="請選擇產品"
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
      <el-table v-loading="loading" :data="filteredTableData" border stripe style="width: 100%">
        <el-table-column prop="productId" label="產品ID" width="100" />
        <el-table-column label="產品名稱" min-width="200">
          <template #default="{ row }">
            {{ getProductName(row.productId) }}
          </template>
        </el-table-column>
        <el-table-column prop="stockQuantity" label="庫存數量" width="120">
          <template #default="{ row }">
            <el-tag :type="getStockType(row.stockQuantity)">
              {{ row.stockQuantity }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastUpdated" label="最後更新" width="180">
          <template #default="{ row }">
            {{ formatDate(row.lastUpdated) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="320">
          <template #default="{ row }">
            <el-button size="small" type="success" plain @click="handleIncrease(row)">
              入庫
            </el-button>
            <el-button size="small" type="warning" plain @click="handleDecrease(row)">
              出庫
            </el-button>
            <el-button size="small" type="primary" plain @click="handleUpdate(row)">
              調整
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分頁 -->
      <div style="margin-top: 20px; display: flex; justify-content: flex-end">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next"
        />
      </div>
    </el-card>

    <!-- 庫存操作對話框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      @close="resetForm"
      :close-on-click-modal="false"
    >
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="產品">
          <el-input :value="getProductName(formData.productId)" disabled />
        </el-form-item>
        <el-form-item label="當前庫存">
          <el-input :value="formData.currentStock" disabled />
        </el-form-item>
        <el-form-item :label="operationType === 'update' ? '調整後數量' : '數量'" prop="quantity">
          <el-input v-model.number="formData.quantity" placeholder="請輸入數量" type="number">
            <template #append>件</template>
          </el-input>
        </el-form-item>
        <el-alert
          v-if="operationType === 'update'"
          title="提示：請輸入調整後的庫存總數"
          type="info"
          :closable="false"
          style="margin-top: -10px"
        />
        <el-alert
          v-if="operationType === 'increase'"
          :title="`將增加 ${formData.quantity || 0} 件，調整後庫存：${formData.currentStock + (formData.quantity || 0)} 件`"
          type="success"
          :closable="false"
          style="margin-top: 10px"
        />
        <el-alert
          v-if="operationType === 'decrease'"
          :title="`將減少 ${formData.quantity || 0} 件，調整後庫存：${formData.currentStock - (formData.quantity || 0)} 件`"
          :type="formData.currentStock - (formData.quantity || 0) < 0 ? 'error' : 'warning'"
          :closable="false"
          style="margin-top: 10px"
        />
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading"> 確定 </el-button>
      </template>
    </el-dialog>
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
const submitLoading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const dialogVisible = ref(false)
const dialogTitle = ref('庫存操作')
const formRef = ref(null)
const operationType = ref('') // 'increase', 'decrease', 'update'

const formData = ref({
  productId: null,
  currentStock: 0,
  quantity: null,
})

const formRules = {
  quantity: [
    { required: true, message: '請輸入數量', trigger: 'blur' },
    { type: 'number', message: '數量必須為數字', trigger: 'blur' },
  ],
}

const filteredData = computed(() => {
  let data = tableData.value
  if (searchForm.value.productId) {
    data = data.filter((item) => item.productId === searchForm.value.productId)
  }
  return data
})

const filteredTableData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return filteredData.value.slice(start, start + pageSize.value)
})

const total = computed(() => filteredData.value.length)

const getProductName = (productId) => {
  const product = products.value.find((p) => p.productId === productId)
  return product ? product.productName : `產品 #${productId}`
}

const getStockType = (quantity) => {
  if (quantity === 0) return 'danger'
  if (quantity < 10) return 'warning'
  return 'success'
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-TW')
}

const loadInventory = async () => {
  loading.value = true
  try {
    const response = await api.get('/api/inventory')
    tableData.value = response.data
    console.log('✅ 載入庫存成功:', response.data.length, '筆')
  } catch (error) {
    console.error('❌ 載入庫存失敗:', error)
    ElMessage.error(error.response?.data?.message || '載入庫存失敗')
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
  currentPage.value = 1
}

const handleReset = () => {
  searchForm.value = { productId: null }
  currentPage.value = 1
}

const handleIncrease = (row) => {
  operationType.value = 'increase'
  dialogTitle.value = '入庫'
  formData.value = {
    productId: row.productId,
    currentStock: row.stockQuantity,
    quantity: null,
  }
  dialogVisible.value = true
}

const handleDecrease = (row) => {
  operationType.value = 'decrease'
  dialogTitle.value = '出庫'
  formData.value = {
    productId: row.productId,
    currentStock: row.stockQuantity,
    quantity: null,
  }
  dialogVisible.value = true
}

const handleUpdate = (row) => {
  operationType.value = 'update'
  dialogTitle.value = '調整庫存'
  formData.value = {
    productId: row.productId,
    currentStock: row.stockQuantity,
    quantity: row.stockQuantity,
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return

    if (formData.value.quantity <= 0 && operationType.value !== 'update') {
      ElMessage.error('數量必須大於 0')
      return
    }

    submitLoading.value = true
    try {
      let url = ''
      let params = { quantity: formData.value.quantity }

      if (operationType.value === 'increase') {
        url = `/api/inventory/increase/${formData.value.productId}`
      } else if (operationType.value === 'decrease') {
        url = `/api/inventory/decrease/${formData.value.productId}`
      } else if (operationType.value === 'update') {
        url = `/api/inventory/update/${formData.value.productId}`
      }

      await api.put(url, null, { params })
      ElMessage.success('操作成功')
      dialogVisible.value = false
      loadInventory()
    } catch (error) {
      ElMessage.error(error.response?.data?.message || '操作失敗')
    } finally {
      submitLoading.value = false
    }
  })
}

const resetForm = () => {
  if (formRef.value) formRef.value.resetFields()
}

onMounted(() => {
  loadProducts()
  loadInventory()
})
</script>

<style scoped>
:deep(.el-alert) {
  padding: 8px 12px;
}
</style>
