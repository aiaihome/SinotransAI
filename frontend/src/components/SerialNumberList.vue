<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { serialNumberListApi } from '@/api/serialNumberList'
import type { SerialNumberListQuery, SerialNumberListItem } from '@/types/serialNumberList'
import { ElMessage } from 'element-plus'

import SerialNumberRecoverDialog from '@/components/SerialNumberRecoverDialog.vue'
import SerialNumberVoidDialog from '@/components/SerialNumberVoidDialog.vue'

const router = useRouter()
const route = useRoute()

function handleRowClick(row: SerialNumberListItem) {
  if (row.id) {
    router.push({ name: 'SerialNumberDetail', params: { id: row.id } })
  }
}

const query = ref<SerialNumberListQuery>({
  segmentId: undefined,
  page: 1,
  size: 20
})
const loading = ref(false)
const total = ref(0)
const serialNumbers = ref<SerialNumberListItem[]>([])

async function loadList() {
  loading.value = true
  try {
    const { data } = await serialNumberListApi.getSerialNumbers(query.value)
    serialNumbers.value = data.serialNumbers
    total.value = data.total
  } catch (e: any) {
    ElMessage.error(e?.message || '加载失败')
  } finally {
    loading.value = false
  }
}

function handlePageChange(page: number) {
  query.value.page = page
  loadList()
}
function handleSizeChange(size: number) {
  query.value.size = size
  query.value.page = 1
  loadList()
}
// 操作日志按钮点击事件
function onLogClick(id: number) {
  router.push({ name: 'OperationLogList', query: { id } })
}
// 回收、作废弹窗控制
const recoverDialogVisible = ref(false)
const voidDialogVisible = ref(false)
const recoverIds = ref<number[]>([])
const voidIds = ref<number[]>([])

function onRecoverClick(row: SerialNumberListItem) {
  recoverIds.value = row.id ? [row.id] : []
  recoverDialogVisible.value = true
}

function onVoidClick(row: SerialNumberListItem) {
  voidIds.value = row.id ? [row.id] : []
  voidDialogVisible.value = true
}
onMounted(() => {
  // 如果路由参数有id，则作为segmentId过滤
  if (route.params.id) {
    query.value.segmentId = Number(route.params.id)
  }
  loadList()
})
</script>

<template>
  <el-card shadow="never" style="margin: 24px;">
    <div style="font-size: 20px; font-weight: bold; margin-bottom: 18px;">
      段ID为 {{ query.segmentId ?? '-' }} 的序列号管理列表
    </div>
    <div style="margin-bottom: 16px; display: flex; align-items: center; gap: 12px;">
      <el-input v-model="query.billNumber" placeholder="提单号" style="width: 140px" clearable />
      <el-select v-model="query.status" placeholder="状态" style="width: 120px" clearable>
        <el-option label="新生成" value="NEW" />
        <el-option label="已使用" value="USED" />
        <el-option label="已回收" value="RECOVERED" />
        <el-option label="已作废" value="VOIDED" />
        <el-option label="已归档" value="ARCHIVED" />
      </el-select>
      <el-button type="primary" @click="() => { query.page = 1; loadList() }">查询</el-button>
      <el-button @click="() => { Object.assign(query, { billNumber: '', status: '', page: 1 }); loadList() }">重置</el-button>
    </div>
    <el-table :data="serialNumbers" v-loading="loading" style="width: 100%" empty-text="暂无数据" @row-click="handleRowClick">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="billNumber" label="提单号" min-width="120" />
      <el-table-column prop="status" label="状态" min-width="100">
        <template #default="scope">
          <el-tag v-if="scope.row.status === 'NEW'" type="info">新生成</el-tag>
          <el-tag v-else-if="scope.row.status === 'USED'" type="success">已使用</el-tag>
          <el-tag v-else-if="scope.row.status === 'RECOVERED'" type="warning">已回收</el-tag>
          <el-tag v-else-if="scope.row.status === 'VOIDED'" type="danger">已作废</el-tag>
          <el-tag v-else-if="scope.row.status === 'ARCHIVED'" type="default">已归档</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="entryDate" label="入库日期" width="120" />
      <el-table-column prop="remark" label="备注" min-width="160" />
      <el-table-column label="操作" fixed="right" width="200">
        <template #default="scope">
          <el-button type="primary" size="small" @click.stop="onLogClick(scope.row.id)">操作日志</el-button>
          <el-button type="warning" size="small" style="margin-left: 6px" @click.stop="onRecoverClick(scope.row)">回收</el-button>
          <el-button type="danger" size="small" style="margin-left: 6px" @click.stop="onVoidClick(scope.row)">作废</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div style="margin-top: 16px; text-align: right;">
      <el-pagination
        v-model:current-page="query.page"
        v-model:page-size="query.size"
        :total="total"
        layout="total, prev, pager, next, sizes"
        :page-sizes="[10, 20, 50, 100]"
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
      />
    </div>
  </el-card>

  <SerialNumberRecoverDialog
    v-if="recoverDialogVisible"
    :visible="recoverDialogVisible"
    :ids="recoverIds"
    @close="recoverDialogVisible = false"
    @success="() => { recoverDialogVisible = false; loadList() }"
  />
  <SerialNumberVoidDialog
    v-if="voidDialogVisible"
    :visible="voidDialogVisible"
    :ids="voidIds"
    @close="voidDialogVisible = false"
    @success="() => { voidDialogVisible = false; loadList() }"
  />
</template>

<style scoped>
</style>
