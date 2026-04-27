<script setup lang="ts">
import { ref } from 'vue'
import { serialNumberVoidExportApi } from '@/api/serialNumberVoidExport'
import { ElMessage } from 'element-plus'

const props = defineProps<{
  filterParams?: Record<string, any>
  disabled?: boolean
}>()

const exporting = ref(false)

async function handleExport() {
  exporting.value = true
  try {
    const params = props.filterParams || {}
    const { data } = await serialNumberVoidExportApi.export(params)
    if (data && data.url) {
      window.open(data.url, '_blank')
      ElMessage.success('导出成功')
    } else {
      ElMessage.error(data?.message || '导出失败')
    }
  } catch (e: any) {
    ElMessage.error(e?.message || '导出失败')
  } finally {
    exporting.value = false
  }
}
</script>

<template>
  <el-button type="primary" :loading="exporting" :disabled="props.disabled || exporting" @click="handleExport">
    <template #icon>
      <el-icon><i-ep-download /></el-icon>
    </template>
    {{ exporting ? '导出中...' : '导出作废清单' }}
  </el-button>
</template>

<style scoped>
</style>
