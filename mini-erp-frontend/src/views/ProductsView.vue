<template>
  <div>
    <el-card shadow="hover">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span style="font-size: 18px; font-weight: 600">產品管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增產品
          </el-button>
        </div>
      </template>

      <!-- 搜尋區域 -->
      <el-form :inline="true" style="margin-bottom: 20px">
        <el-form-item label="產品名稱">
          <el-input
            v-model="searchForm.name"
            placeholder="請輸入產品名稱"
            clearable
            style="width: 180px"
          />
        </el-form-item>
        <el-form-item label="分類">
          <el-input
            v-model="searchForm.category"
            placeholder="請輸入分類"
            clearable
            style="width: 150px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜尋</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 資料表格 -->
      <el-table v-loading="loading" :data="filteredTableData" border stripe style="width: 100%">
        <el-table-column prop="productId" label="ID" width="80" />
        <el-table-column
          prop="productName"
          label="產品名稱"
          min-width="200"
          show-overflow-tooltip
        />
        <el-table-column
          prop="productDescription"
          label="描述"
          min-width="250"
          show-overflow-tooltip
        />
        <el-table-column prop="unitPrice" label="單價" width="120">
          <template #default="{ row }"> NT$ {{ Number(row.unitPrice).toLocaleString() }} </template>
        </el-table-column>
        <el-table-column prop="category" label="分類" width="120" />
        <el-table-column label="操作" fixed="right" width="180">
          <template #default="{ row }">
            <el-button size="small" type="primary" plain @click="handleEdit(row)"> 編輯 </el-button>
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

    <!-- 新增/編輯對話框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="resetForm"
      :close-on-click-modal="false"
    >
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="產品名稱" prop="productName">
          <el-input v-model="formData.productName" placeholder="請輸入產品名稱" />
        </el-form-item>
        <el-form-item label="產品描述">
          <el-input
            v-model="formData.productDescription"
            type="textarea"
            :rows="3"
            placeholder="請輸入產品描述"
          />
        </el-form-item>
        <el-form-item label="單價" prop="unitPrice">
          <el-input v-model.number="formData.unitPrice" placeholder="請輸入單價" type="number">
            <template #prepend>NT$</template>
          </el-input>
        </el-form-item>
        <el-form-item label="分類">
          <el-input v-model="formData.category" placeholder="請輸入分類" />
        </el-form-item>
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
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/utils/api'

const searchForm = ref({ name: '', category: '' })
const tableData = ref([])
const loading = ref(false)
const submitLoading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const dialogVisible = ref(false)
const dialogTitle = ref('新增產品')
const formRef = ref(null)
const isEdit = ref(false)
const editId = ref(null)

const formData = ref({
  productName: '',
  productDescription: '',
  unitPrice: null,
  category: '',
})

const formRules = {
  productName: [{ required: true, message: '請輸入產品名稱', trigger: 'blur' }],
  unitPrice: [
    { required: true, message: '請輸入單價', trigger: 'blur' },
    { type: 'number', message: '單價必須為數字', trigger: 'blur' },
  ],
}

const filteredData = computed(() => {
  let data = tableData.value
  if (searchForm.value.name) {
    data = data.filter((item) =>
      item.productName.toLowerCase().includes(searchForm.value.name.toLowerCase()),
    )
  }
  if (searchForm.value.category) {
    data = data.filter(
      (item) =>
        item.category &&
        item.category.toLowerCase().includes(searchForm.value.category.toLowerCase()),
    )
  }
  return data
})

const filteredTableData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return filteredData.value.slice(start, start + pageSize.value)
})

const total = computed(() => filteredData.value.length)

const loadProducts = async () => {
  loading.value = true
  try {
    const response = await api.get('/api/products')
    tableData.value = response.data
    console.log('✅ 載入產品成功:', response.data.length, '筆')
  } catch (error) {
    console.error('❌ 載入產品失敗:', error)
    ElMessage.error(error.response?.data?.message || '載入產品失敗')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
}

const handleReset = () => {
  searchForm.value = { name: '', category: '' }
  currentPage.value = 1
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增產品'
  formData.value = {
    productName: '',
    productDescription: '',
    unitPrice: null,
    category: '',
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  editId.value = row.productId
  dialogTitle.value = '編輯產品'
  formData.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`確定要刪除產品 "${row.productName}" 嗎?`, '警告', {
    confirmButtonText: '確定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      try {
        await api.delete(`/api/products/${row.productId}`)
        ElMessage.success('刪除成功')
        loadProducts()
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '刪除失敗')
      }
    })
    .catch(() => {})
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitLoading.value = true
    try {
      if (isEdit.value) {
        await api.put(`/api/products/${editId.value}`, formData.value)
        ElMessage.success('更新成功')
      } else {
        await api.post('/api/products', formData.value)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      loadProducts()
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
})
</script>
