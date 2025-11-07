import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  // 新增這段配置
  define: {
    global: 'globalThis',
  },
  optimizeDeps: {
    // 將 sockjs-client 加入預打包，避免在瀏覽器直接載入 CJS 版本導致 require is not defined
    include: ['sockjs-client'],
  },
})
