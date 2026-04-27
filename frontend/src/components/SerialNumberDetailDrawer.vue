<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import { serialNumberDetailApi } from '@/api/serialNumberDetail'
import type { SerialNumberDetail, SerialNumberLog } from '@/types/serialNumberDetail'
import { ElMessage } from 'element-plus'

const props = withDefaults(defineProps<{ id: number; visible?: boolean }>(), { visible: true })
const emit = defineEmits(['close'])

const detail = ref<SerialNumberDetail | null>(null)
const logs = ref<SerialNumberLog[]>([])
const loading = ref(false)

async function loadDetail() {
  if (!props.id) return
  loading.value = true
  try {
    const { data } = await serialNumberDetailApi.getDetail(props.id)
    detail.value = data
    const logsRes = await serialNumberDetailApi.getLogs(props.id)
    logs.value = logsRes.data
  } catch (e: any) {
    ElMessage.error(e?.message || '加载失败')
  } finally {
    loading.value = false
  }
}

watch(() => props.visible, v => {
  if (v) loadDetail()
})

onMounted(() => {
loadDetail()
})
</script>

<template>
  <el-drawer :model-value="props.visible" title="序列号详情" size="480px" @close="() => emit('close')">
    <el-skeleton v-if="loading" :rows="6" animated />
    <div v-else-if="detail">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="ID">{{ detail.id }}</el-descriptions-item>
        <el-descriptions-item label="提单号">{{ detail.billNumber }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag v-if="detail.status === 'NEW'" type="info">新生成</el-tag>
          <el-tag v-else-if="detail.status === 'USED'" type="success">已使用</el-tag>
          <el-tag v-else-if="detail.status === 'RECOVERED'" type="warning">已回收</el-tag>
          <el-tag v-else-if="detail.status === 'VOIDED'" type="danger">已作废</el-tag>
          <el-tag v-else-if="detail.status === 'ARCHIVED'" type="default">已归档</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="入库日期">{{ detail.entryDate }}</el-descriptions-item>
        <el-descriptions-item label="备注">{{ detail.remark }}</el-descriptions-item>
      </el-descriptions>
      <h4 style="margin: 24px 0 8px;">操作时间线</h4>
      <el-timeline>
        <el-timeline-item v-for="log in logs" :key="log.id" :timestamp="log.operateTime" :color="log.action === '作废' ? '#F53F3F' : undefined">
          <div>
            <b>{{ log.action }}</b> - {{ log.operator }}
            <div v-if="log.remark" style="color: #8C8C8C; font-size: 12px; margin-top: 2px;">{{ log.remark }}</div>
          </div>
        </el-timeline-item>
      </el-timeline>
    </div>
    <div v-else style="text-align: center; color: #8C8C8C; margin-top: 40px;">暂无详情</div>
  </el-drawer>
</template>

<style scoped>
</style>
