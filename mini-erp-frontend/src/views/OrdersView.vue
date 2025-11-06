<template>
  <div>
    <el-card shadow="hover">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span style="font-size: 18px; font-weight: 600">訂單管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增訂單
          </el-button>
        </div>
      </template>

      <!-- 搜尋區域 -->
      <el-form :inline="true" style="margin-bottom: 20px">
        <el-form-item label="客戶名稱">
          <el-input
            v-model="searchForm.customerName"
            placeholder="請輸入客戶名稱"
            clearable
            style="width: 180px"
          />
        </el-form-item>
        <el-form-item label="訂單狀態">
          <el-select
            v-model="searchForm.status"
            placeholder="請選擇"
            clearable
            style="width: 120px"
          >
            <el-option label="待處理" value="Pending" />
            <el-option label="處理中" value="Processing" />
            <el-option label="已完成" value="Completed" />
            <el-option label="已取消" value="Cancelled" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜尋</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 資料表格 -->
      <el-table v-loading="loading" :data="filteredTableData" border stripe style="width: 100%">
        <el-table-column prop="orderId" label="訂單ID" width="100" />
        <el-table-column prop="customerName" label="客戶名稱" min-width="150" />
        <el-table-column prop="customerEmail" label="客戶Email" min-width="180" />
        <el-table-column prop="totalAmount" label="總金額" width="120">
          <template #default="{ row }">
            NT$ {{ Number(row.totalAmount).toLocaleString() }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="狀態" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="paymentStatus" label="付款狀態" width="100">
          <template #default="{ row }">
            <el-tag :type="row.paymentStatus === 'Paid' ? 'success' : 'warning'" size="small">
              {{ row.paymentStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="orderDate" label="訂單日期" width="180">
          <template #default="{ row }">
            {{ formatDate(row.orderDate) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="250">
          <template #default="{ row }">
            <el-button size="small" type="info" plain @click="handleDetail(row)"> 詳情 </el-button>
            <el-button size="small" type="primary" plain @click="handleUpdateStatus(row)">
              更新狀態
            </el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">刪除</el-button>
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

    <!-- 新增訂單對話框 -->
    <el-dialog
      v-model="dialogVisible"
      title="新增訂單"
      width="700px"
      @close="resetForm"
      :close-on-click-modal="false"
    >
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="客戶名稱" prop="customerName">
              <el-input v-model="formData.customerName" placeholder="請輸入客戶名稱" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客戶Email">
              <el-input v-model="formData.customerEmail" placeholder="請輸入Email" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="配送地址">
          <el-input v-model="formData.shippingAddress" placeholder="請輸入配送地址" />
        </el-form-item>

        <el-divider content-position="left">訂單明細</el-divider>

        <el-form-item>
          <el-button type="primary" size="small" @click="handleAddDetail">新增明細</el-button>
        </el-form-item>

        <el-table :data="formData.orderDetails" border style="width: 100%">
          <el-table-column label="產品" width="200">
            <template #default="{ row, $index }">
              <el-select
                v-model="row.productId"
                placeholder="選擇產品"
                @change="onProductChange($index)"
              >
                <el-option
                  v-for="product in products"
                  :key="product.productId"
                  :label="product.productName"
                  :value="product.productId"
                />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="數量" width="120">
            <template #default="{ row }">
              <el-input-number
                v-model="row.quantity"
                :min="1"
                size="small"
                @change="calculateTotal"
              />
            </template>
          </el-table-column>
          <el-table-column label="單價" width="120">
            <template #default="{ row }">
              {{ row.unitPrice ? `NT$ ${Number(row.unitPrice).toLocaleString()}` : '-' }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80">
            <template #default="{ $index }">
              <el-button size="small" type="danger" @click="handleRemoveDetail($index)">
                刪除
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-form-item label="訂單總額" style="margin-top: 20px">
          <el-input :value="`NT$ ${Number(formData.totalAmount).toLocaleString()}`" disabled />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading"> 確定 </el-button>
      </template>
    </el-dialog>

    <!-- 更新狀態對話框 -->
    <el-dialog v-model="statusDialogVisible" title="更新訂單狀態" width="400px">
      <el-form label-width="80px">
        <el-form-item label="訂單狀態">
          <el-select v-model="currentStatus" placeholder="請選擇狀態" style="width: 100%">
            <el-option label="待處理" value="Pending" />
            <el-option label="處理中" value="Processing" />
            <el-option label="已完成" value="Completed" />
            <el-option label="已取消" value="Cancelled" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="statusDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleStatusSubmit" :loading="submitLoading">
          確定
        </el-button>
      </template>
    </el-dialog>

    <!-- 訂單詳情對話框 -->
    <el-dialog v-model="detailVisible" title="訂單詳情" width="800px">
      <el-descriptions :column="2" border v-if="detailData">
        <el-descriptions-item label="訂單ID">{{ detailData.orderId }}</el-descriptions-item>
        <el-descriptions-item label="客戶名稱">{{ detailData.customerName }}</el-descriptions-item>
        <el-descriptions-item label="客戶Email">{{
          detailData.customerEmail || '-'
        }}</el-descriptions-item>
        <el-descriptions-item label="訂單狀態">
          <el-tag :type="getStatusType(detailData.status)">{{ detailData.status }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="付款狀態">
          <el-tag :type="detailData.paymentStatus === 'Paid' ? 'success' : 'warning'">
            {{ detailData.paymentStatus }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="總金額">
          NT$ {{ Number(detailData.totalAmount).toLocaleString() }}
        </el-descriptions-item>
        <el-descriptions-item label="配送地址" :span="2">
          {{ detailData.shippingAddress || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="訂單日期">
          {{ formatDate(detailData.orderDate) }}
        </el-descriptions-item>
        <el-descriptions-item label="建立時間">
          {{ formatDate(detailData.createdAt) }}
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">關閉</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/utils/api'

const searchForm = ref({ customerName: '', status: '' })
const tableData = ref([])
const products = ref([])
const loading = ref(false)
const submitLoading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const dialogVisible = ref(false)
const statusDialogVisible = ref(false)
const detailVisible = ref(false)
const formRef = ref(null)
const currentOrderId = ref(null)
const currentStatus = ref('')
const detailData = ref(null)

const formData = ref({
  customerName: '',
  customerEmail: '',
  shippingAddress: '',
  totalAmount: 0,
  status: 'Pending',
  paymentStatus: 'Pending',
  orderDetails: [],
})

const formRules = {
  customerName: [{ required: true, message: '請輸入客戶名稱', trigger: 'blur' }],
}

const filteredData = computed(() => {
  let data = tableData.value
  if (searchForm.value.customerName) {
    data = data.filter((item) =>
      item.customerName.toLowerCase().includes(searchForm.value.customerName.toLowerCase()),
    )
  }
  if (searchForm.value.status) {
    data = data.filter((item) => item.status === searchForm.value.status)
  }
  return data
})

const filteredTableData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return filteredData.value.slice(start, start + pageSize.value)
})

const total = computed(() => filteredData.value.length)

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

const loadOrders = async () => {
  loading.value = true
  try {
    const response = await api.get('/api/orders')
    tableData.value = response.data
    console.log('✅ 載入訂單成功:', response.data.length, '筆')
  } catch (error) {
    console.error('❌ 載入訂單失敗:', error)
    ElMessage.error(error.response?.data?.message || '載入訂單失敗')
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
  searchForm.value = { customerName: '', status: '' }
  currentPage.value = 1
}

const handleAdd = () => {
  formData.value = {
    customerName: '',
    customerEmail: '',
    shippingAddress: '',
    totalAmount: 0,
    status: 'Pending',
    paymentStatus: 'Pending',
    orderDetails: [],
  }
  dialogVisible.value = true
}

const handleAddDetail = () => {
  formData.value.orderDetails.push({
    productId: null,
    quantity: 1,
    unitPrice: 0,
  })
}

const handleRemoveDetail = (index) => {
  formData.value.orderDetails.splice(index, 1)
  calculateTotal()
}

const onProductChange = (index) => {
  const detail = formData.value.orderDetails[index]
  const product = products.value.find((p) => p.productId === detail.productId)
  if (product) {
    detail.unitPrice = product.unitPrice
    calculateTotal()
  }
}

const calculateTotal = () => {
  formData.value.totalAmount = formData.value.orderDetails.reduce(
    (sum, item) => sum + (item.unitPrice || 0) * item.quantity,
    0,
  )
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return

    if (formData.value.orderDetails.length === 0) {
      ElMessage.error('請至少新增一筆訂單明細')
      return
    }

    // 檢查所有明細是否都已選擇產品
    const hasEmptyProduct = formData.value.orderDetails.some((detail) => !detail.productId)
    if (hasEmptyProduct) {
      ElMessage.error('請為所有訂單明細選擇產品')
      return
    }

    submitLoading.value = true
    try {
      console.log('📤 準備送出訂單資料:', formData.value)

      // 轉換格式以符合後端要求
      const orderData = {
        customerName: formData.value.customerName,
        customerEmail: formData.value.customerEmail,
        shippingAddress: formData.value.shippingAddress,
        totalAmount: formData.value.totalAmount,
        status: formData.value.status,
        paymentStatus: formData.value.paymentStatus,
        orderDetails: formData.value.orderDetails.map((detail) => ({
          product: {
            productId: detail.productId,
          },
          quantity: detail.quantity,
          unitPrice: detail.unitPrice,
        })),
      }

      console.log('📤 轉換後的訂單資料:', orderData)

      await api.post('/api/orders', orderData)
      ElMessage.success('新增成功')
      dialogVisible.value = false
      loadOrders()
    } catch (error) {
      console.error('❌ 新增訂單失敗:', error)
      console.error('❌ 錯誤詳情:', error.response?.data)
      ElMessage.error(error.response?.data?.message || '新增失敗')
    } finally {
      submitLoading.value = false
    }
  })
}

const handleUpdateStatus = (row) => {
  currentOrderId.value = row.orderId
  currentStatus.value = row.status
  statusDialogVisible.value = true
}

const handleStatusSubmit = async () => {
  submitLoading.value = true
  try {
    await api.put(`/api/orders/${currentOrderId.value}/status`, null, {
      params: { status: currentStatus.value },
    })
    ElMessage.success('更新成功')
    statusDialogVisible.value = false
    loadOrders()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '更新失敗')
  } finally {
    submitLoading.value = false
  }
}

const handleDetail = (row) => {
  detailData.value = row
  detailVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`確定要刪除訂單 #${row.orderId} 嗎?`, '警告', {
    confirmButtonText: '確定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      try {
        await api.delete(`/api/orders/${row.orderId}`)
        ElMessage.success('刪除成功')
        loadOrders()
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '刪除失敗')
      }
    })
    .catch(() => {})
}

const resetForm = () => {
  if (formRef.value) formRef.value.resetFields()
}

onMounted(() => {
  loadProducts()
  loadOrders()
})
</script>
