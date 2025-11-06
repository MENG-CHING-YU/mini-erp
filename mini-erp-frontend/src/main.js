// 如果 global 未定義，將其設置為 window
if (typeof global === 'undefined') {
  var global = window
}

// 引入樣式
import './assets/main.css'

// 引入必要的庫
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

// 創建 Vue 應用並安裝插件
const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(ElementPlus)

// 將 Vue 應用掛載到 DOM
app.mount('#app')
