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
    exclude: ['sockjs-client'],
  },
})
