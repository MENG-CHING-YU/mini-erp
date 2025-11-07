一個基於 Spring Boot + Vue.js 的全端企業資源規劃系統範例專案

📋 專案簡介
這是一個功能完整的 Mini ERP 系統,涵蓋企業日常營運的核心模組,包含機台管理、庫存控制、訂單處理、員工管理等功能。系統採用前後端分離架構,展現現代化企業應用開發的最佳實踐。
🎯 專案特色

前後端分離架構 - Spring Boot REST API + Vue.js SPA
完整的權限控制 - JWT Token 認證機制
即時資料監控 - WebSocket + MQTT 整合 (開發中)
響應式設計 - Element Plus UI 框架
RESTful API 設計 - 標準化的 API 介面
資料快取優化 - Redis 快取機制

🚀 技術棧
後端技術

框架: Spring Boot 3.x
安全: Spring Security + JWT
資料庫: SQL Server
ORM: Spring Data JPA (Hibernate)
快取: Redis
即時通訊: WebSocket, MQTT (Eclipse Paho)
建構工具: Maven

前端技術

框架: Vue.js 3 (Composition API)
狀態管理: Pinia
路由: Vue Router
UI 框架: Element Plus
HTTP 客戶端: Axios
建構工具: Vite

📦 功能模組
✅ 已完成功能

1. 使用者認證系統

使用者登入/登出
JWT Token 認證
密碼管理
角色權限控制

2. 機台管理

機台 CRUD 操作
機台狀態監控
保養週期管理
保固到期提醒
機台搜尋與篩選

3. 員工管理

員工資料管理
部門/職位管理
員工與使用者帳號關聯
資料匯出功能

4. 產品管理

產品 CRUD 操作
產品分類
價格管理
產品搜尋

5. 庫存管理

庫存查詢
入庫/出庫操作
庫存調整
低庫存警告
庫存異動紀錄追蹤

6. 訂單管理

訂單建立與編輯
訂單狀態管理
訂單明細管理
自動庫存扣減
付款狀態追蹤

7. 儀表板

營運數據統計
低庫存警告
最近訂單列表
快捷操作入口

🚧 開發中功能 8. 機台即時監控 (80% 完成)

WebSocket 即時推送
MQTT 資料接收
Redis 快取機制
歷史數據查詢
溫度/震動警報

注意: 機台監控功能的 MQTT 資料源尚未完全整合測試

🛠️ 系統架構
mini-erp-system/
├── mini-erp-backend/ # Spring Boot 後端
│ ├── src/main/java/
│ │ └── com/example/mini_erp/
│ │ ├── config/ # 配置類 (Security, Redis, MQTT, WebSocket)
│ │ ├── controller/ # REST API 控制器
│ │ ├── dto/ # 資料傳輸物件
│ │ ├── entity/ # JPA 實體類
│ │ ├── exception/ # 異常處理
│ │ ├── jwt/ # JWT 認證
│ │ ├── repository/ # 資料存取層
│ │ ├── service/ # 業務邏輯層
│ │ └── util/ # 工具類
│ └── src/main/resources/
│ └── application.properties
│
└── mini-erp-frontend/ # Vue.js 前端
├── src/
│ ├── assets/ # 靜態資源
│ ├── components/ # Vue 元件
│ ├── router/ # 路由配置
│ ├── stores/ # Pinia 狀態管理
│ ├── utils/ # 工具函數 (API, auth)
│ └── views/ # 頁面視圖
└── package.json

🗂️ 資料庫結構
核心資料表

users - 使用者帳號
employees - 員工資料
machines - 機台資訊
machine_data - 機台監控數據
products - 產品主檔
inventory - 庫存資料
orders - 訂單主檔
order_details - 訂單明細
inventory_transactions - 庫存異動紀錄

🔐 API 文件
認證 API
POST /users/login # 使用者登入
POST /users/register # 註冊新使用者
機台管理 API
GET /machines # 查詢所有機台
GET /machines/{id} # 查詢單一機台
POST /machines # 新增機台
PUT /machines/{id} # 更新機台
DELETE /machines/{id} # 刪除機台
產品管理 API
GET /api/products # 查詢所有產品
POST /api/products # 新增產品
PUT /api/products/{id} # 更新產品
DELETE /api/products/{id} # 刪除產品
庫存管理 API
GET /api/inventory # 查詢所有庫存
PUT /api/inventory/increase/{id} # 入庫
PUT /api/inventory/decrease/{id} # 出庫
PUT /api/inventory/update/{id} # 調整庫存
訂單管理 API
GET /api/orders # 查詢所有訂單
POST /api/orders # 建立訂單
PUT /api/orders/{id}/status # 更新訂單狀態
DELETE /api/orders/{id} # 刪除訂單
