<script setup lang="ts">
import { watch } from 'vue'
import { useRoute } from 'vue-router'
const route = useRoute()
import { ref, onMounted } from 'vue'
import { operationLogApi } from '@/api/operationLog'
import type { OperationLogItem, OperationLogQuery, OperationLogResponse } from '@/types/operationLog'
import { ElMessage } from 'element-plus'

const query = ref<OperationLogQuery>({
  page: 1,
  size: 20,
  keyword: '',
  type: '',
  startDate: '',
  endDate: ''
})
const loading = ref(false)
const logList = ref<OperationLogItem[]>([])
const total = ref(0)

const expandedRows = ref<number[]>([])

async function loadLogs() {
  const id = Number(route.query.id)
  if (!id) return;
  loading.value = true
  try {
    const params = { ...query.value, id }
    const { data } = await operationLogApi.list(params)
    logList.value = data
    total.value = data.total
  } catch (e: any) {
    ElMessage.error(e?.message || '日志查询失败')
  } finally {
    loading.value = false
  }
}

function handleExpand(row: OperationLogItem) {
  const idx = expandedRows.value.indexOf(row.id)
  if (idx >= 0) {
    expandedRows.value.splice(idx, 1)
  } else {
    expandedRows.value.push(row.id)
  }
}

onMounted(loadLogs)
// id变化时自动刷新
watch(() => route.query.id, () => { loadLogs() })
</script>

<template>
  <div class="operation-log-list">
    <el-form :inline="true" size="small" @submit.prevent>
      <el-form-item label="关键字">
        <el-input v-model="query.keyword" placeholder="操作人/内容/ID" clearable @keyup.enter="loadLogs" />
      </el-form-item>
      <el-form-item label="类型">
        <el-select v-model="query.type" placeholder="全部类型" clearable style="width: 120px">
          <el-option label="全部" value="" />
          <el-option label="入库" value="IN" />
          <el-option label="编辑" value="EDIT" />
          <el-option label="归档" value="ARCHIVE" />
          <el-option label="回收" value="RECOVER" />
          <el-option label="作废" value="VOID" />
        </el-select>
      </el-form-item>
      <el-form-item label="日期">
        <el-date-picker v-model="query" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD" style="width: 240px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="loadLogs" :loading="loading">查询</el-button>
      </el-form-item>
    </el-form>
    <el-table :data="logList" v-loading="loading" style="width: 100%; margin-top: 16px;" row-key="id">
      <el-table-column type="expand">
        <template #default="{ row }">
          <div style="padding: 8px 24px;">
            <div><b>详情：</b>{{ row.detail }}</div>
            <div><b>操作前：</b>{{ row.before }}</div>
            <div><b>操作后：</b>{{ row.after }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="type" label="类型" width="100" />
      <el-table-column prop="operator" label="操作人" width="120" />
      <el-table-column prop="content" label="内容" min-width="180" />
      <el-table-column prop="createdAt" label="时间" width="160" />
      <el-table-column label="操作">
        <template #default="{ row }">
          <el-button size="small" text type="primary" @click="handleExpand(row)">
            {{ expandedRows.includes(row.id) ? '收起' : '展开' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      v-model:current-page="query.page"
      v-model:page-size="query.size"
      :total="total"
      layout="total, prev, pager, next, sizes"
      :page-sizes="[10, 20, 50, 100]"
      @current-change="loadLogs"
      @size-change="loadLogs"
      style="margin-top: 16px; text-align: right;"
    />
  </div>
</template>

<style scoped>
.operation-log-list {
  padding: 20px;
  background: #fff;
  border-radius: 8px;
}
</style>
