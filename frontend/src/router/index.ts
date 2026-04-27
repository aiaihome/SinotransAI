import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

import BlankBillSegmentList from '@/components/BlankBillSegmentList.vue'
import SerialNumberList from '@/components/SerialNumberList.vue'
import OperationLogList from '@/components/OperationLogList.vue'
import SerialNumberDetailDrawer from '@/components/SerialNumberDetailDrawer.vue'

const routes: RouteRecordRaw[] = [
  { path: '/', name: 'Home', component: BlankBillSegmentList },
  { path: '/serial-numbers/list/:id', name: 'SerialNumberList', component: SerialNumberList },
  // 新增详情路由，适配抽屉或独立页
  { path: '/serial-number/:id', name: 'SerialNumberDetail', component: SerialNumberDetailDrawer, props: true },
  // 回收、作废弹窗页面路由（如需独立访问）
  { path: '/logs', name: 'OperationLogList', component: OperationLogList }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
