<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { blankBillListApi } from '@/api/blankBillList'
import type { BlankBillSegmentListQuery, BlankBillSegmentListItem } from '@/types/blankBillList'
import { ElMessage } from 'element-plus'
import BlankBillSegmentDialog from '@/components/BlankBillSegmentDialog.vue'

const router = useRouter()
const query = ref<BlankBillSegmentListQuery>({
  page: 1,
  size: 20
})
const loading = ref(false)
const total = ref(0)
const segments = ref<BlankBillSegmentListItem[]>([])
const dialogVisible = ref(false)

async function loadList() {
  loading.value = true
  try {
    const { data } = await blankBillListApi.getSegments(query.value)
    console.log('加载列表数据', data)
    segments.value = data.segments
    segments.value = data.segments.map(item => ({ ...item }))
    console.log('列表数据', segments.value)
    total.value = data.total
        // 强制刷新表格
    await nextTick()
    // 触发一次数据变化
    segments.value = [...segments.value]

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

function openDialog() {
  dialogVisible.value = true
}

async function handleCreateSuccess() {
  dialogVisible.value = false

  Object.assign(query.value, {
    startNumber: '',
    endNumber: '',
    page: 1
  })

  await nextTick()
  await loadList()
}

function handleDialogClose() {
  dialogVisible.value = false
}

onMounted(() => {
  loadList()
})

function handleViewBlankBill(row: BlankBillSegmentListItem) {
  // 跳转到序列号列表页，带segmentId参数
  router.push({ name: 'SerialNumberList', params: { id: row.segmentId } })
}

function getCarrierDisplayName(carrierValue: string | number | undefined) {
  const carrierMap: Record<string, string> = {
    '1': '中远海运',
    '2': '马士基',
    '3': '海洋网联'
  }

  if (carrierValue === undefined || carrierValue === null || carrierValue === '') {
    return '-'
  }

  return carrierMap[String(carrierValue)] ?? String(carrierValue)
}
</script>

<template>
  <el-card shadow="never" style="margin: 24px; min-height: 300px;">
    <div style="font-size: 20px; font-weight: bold; margin-bottom: 18px;">号码段管理列表</div>
    <div style="margin-bottom: 16px; display: flex; align-items: center; gap: 12px;">
      <el-button type="primary" @click="openDialog">新建空白提单入库</el-button>
      <!-- 筛选条件可扩展：船司、起始号、结束号、入库日期等 -->
      <el-input v-model="query.startNumber" placeholder="起始号" style="width: 140px" clearable />
      <el-input v-model="query.endNumber" placeholder="结束号" style="width: 140px" clearable />
      <el-button type="primary" @click="() => { query.page = 1; loadList() }">查询</el-button>
      <el-button @click="() => { Object.assign(query, { startNumber: '', endNumber: '', page: 1 }); loadList() }">重置</el-button>
    </div>

    <el-table :data="segments" v-loading="loading" style="width: 100%" :key="segments.length" empty-text="暂无数据">
      <el-table-column prop="segmentId" label="ID" width="80" />
      <el-table-column label="船司" min-width="120">
        <template #default="scope">
          {{ getCarrierDisplayName(scope.row.carrierName) }}
        </template>
      </el-table-column>
      <el-table-column prop="startNumber" label="起始号" min-width="120" />
      <el-table-column prop="endNumber" label="结束号" min-width="120" />
      <el-table-column prop="quantity" label="数量" width="100" />
      <el-table-column prop="remark" label="备注" min-width="160" />
      <el-table-column prop="entryDate" label="入库日期" width="120" />
      <el-table-column prop="archived" label="归档" width="80">
        <template #default="scope">
          <el-tag v-if="scope?.row?.archived" type="info">已归档</el-tag>
          <el-tag v-else-if="scope?.row" type="success">有效</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="140">
        <template #default="scope">
          <el-button type="primary" size="small" @click="handleViewBlankBill(scope.row)">查看空白提单</el-button>
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

    <BlankBillSegmentDialog
      v-if="dialogVisible"
      :visible="dialogVisible"
      @update:visible="val => dialogVisible = val"
      @success="handleCreateSuccess"
      @close="handleDialogClose"
    />
  </el-card>
</template>

<style scoped>
</style>
