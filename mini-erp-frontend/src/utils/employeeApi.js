import api from './api'

// 取得所有員工（列表精簡欄位）
export const getAllEmployees = () => api.get('/employees')

// 取得單筆員工完整資料（詳情頁使用）
export const getEmployeeById = (id) => api.get(`/employees/${id}`)

// 新增或更新員工（統一使用 POST，後端判斷是否存在 id）
export const saveEmployee = (employee) => api.post('/employees', employee)

// 刪除員工
export const deleteEmployee = (id) => api.delete(`/employees/${id}`)

// 取得所有使用者（給前端選擇關聯 User）
export const getAllUsers = () => api.get('/users')

// 取得未指派為員工的使用者清單（用來顯示在下拉選單中）
export const getUnassignedUsers = () => api.get('/employees/unassigned-users')
