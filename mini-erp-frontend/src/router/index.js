// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '@/views/LoginView.vue'
import HomeView from '@/views/HomeView.vue'
import DashboardView from '@/views/Dashboard.vue'
import MachinesView from '@/views/MachinesView.vue'
import RepairsView from '@/views/RepairsView.vue'
import EmployeesView from '@/views/EmployeesView.vue'
import UserView from '@/views/UserViews.vue'
import InventoryView from '@/views/InventoryView.vue'
import OrdersView from '@/views/OrdersView.vue'
import ProductsView from '@/views/ProductsView.vue'
import TransactionsView from '@/views/TransactionsView.vue'
import MachineRealtimeView from '@/views/MachineRealtimeView.vue'
const routes = [
  {
    path: '/',
    component: LoginView,
    meta: { title: '登入' },
  },
  {
    path: '/home',
    component: HomeView,
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        component: DashboardView,
        meta: { title: '儀表板' },
      },
      {
        path: 'machines',
        component: MachinesView,
        meta: { title: '機台管理' },
      },
      {
        path: 'repairs',
        component: RepairsView,
        meta: { title: '維修管理' },
      },
      {
        path: 'employees',
        component: EmployeesView,
        meta: { title: '員工管理', requiresAuth: true },
      },
      {
        path: 'users',
        component: UserView,
        meta: { title: '用戶管理', requiresAuth: true },
      },
      {
        path: 'inventory',
        component: InventoryView,
        meta: { title: '庫存管理', requiresAuth: true },
      },
      {
        path: 'orders',
        component: OrdersView,
        meta: { title: '訂單管理', requiresAuth: true },
      },
      {
        path: 'products',
        component: ProductsView,
        meta: { title: '產品管理', requiresAuth: true },
      },
      {
        path: 'transactions',
        component: TransactionsView,
        meta: { title: '庫存異動', requiresAuth: true },
      },
      {
        path: 'realtime',
        component: MachineRealtimeView,
        meta: { title: '即時監控', requiresAuth: true },
      },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// 路由守衛
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('jwt')

  console.log('🚦 路由守衛:', {
    to: to.path,
    from: from.path,
    hasToken: !!token,
    requiresAuth: to.meta.requiresAuth,
  })

  // 需要認證的頁面
  if (to.meta.requiresAuth && !token) {
    console.warn('⚠️ 需要登入，跳轉到登入頁')
    next('/')
  }
  // 已登入但訪問登入頁，跳轉到首頁
  else if (to.path === '/' && token) {
    console.log('✅ 已登入，跳轉到首頁')
    next('/home/dashboard')
  }
  // 正常通過
  else {
    next()
  }
})

// 路由後處理（設定頁面標題）
router.afterEach((to) => {
  document.title = to.meta.title ? `${to.meta.title} - ERP 系統` : 'ERP 系統'
})

export default router
