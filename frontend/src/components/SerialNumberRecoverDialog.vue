<script setup lang="ts">
import { ref } from 'vue'
import { serialNumberRecoverApi } from '@/api/serialNumberRecover'
import type { SerialNumberRecoverRequest } from '@/types/serialNumberRecover'
import { ElMessage } from 'element-plus'

const props = defineProps<{ ids: number[]; visible: boolean }>()
const emit = defineEmits(['success', 'close'])

const form = ref({
  remark: ''
})
const saving = ref(false)

async function handleRecover() {
  if (!props.ids?.length) return
  saving.value = true
  try {
    const req: SerialNumberRecoverRequest = {
      ids: props.ids,
      remark: form.value.remark
    }
    const { data } = await serialNumberRecoverApi.recover(req)
    if (data.success) {
      ElMessage.success(`回收成功（${data.data.count}条）`)
      emit('success', data.data)
    } else {
      ElMessage.error(data.message || '回收失败')
    }
  } catch (e: any) {
    ElMessage.error(e?.message || '回收失败')
  } finally {
    saving.value = false
  }
}
</script>

<template>
  <el-dialog :model-value="props.visible" title="回收序列号" width="400px" @close="() => emit('close')">
    <el-form>
      <el-form-item label="备注">
        <el-input v-model="form.remark" type="textarea" :rows="2" maxlength="100" placeholder="请输入备注（可选）" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="() => emit('close')" :disabled="saving">取消</el-button>
      <el-button type="primary" @click="handleRecover" :loading="saving" :disabled="saving">{{ saving ? '回收中...' : '确定回收' }}</el-button>
    </template>
  </el-dialog>
</template>

<style scoped>
</style>
