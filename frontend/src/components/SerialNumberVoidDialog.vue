<script setup lang="ts">
import { ref } from 'vue'
import { serialNumberVoidApi } from '@/api/serialNumberVoid'
import type { SerialNumberVoidRequest } from '@/types/serialNumberVoid'
import { ElMessage } from 'element-plus'

const props = defineProps<{ ids: number[]; visible: boolean }>()
const emit = defineEmits(['success', 'close'])

const form = ref({
  reason: '',
  remark: ''
})
const saving = ref(false)

const reasonOptions = [
  { label: '作废-录入错误', value: '录入错误' },
  { label: '作废-客户取消', value: '客户取消' },
  { label: '作废-其他', value: '其他' }
]

async function handleVoid() {
  if (!props.ids?.length || !form.value.reason) {
    ElMessage.warning('请选择作废原因')
    return
  }
  saving.value = true
  try {
    const req: SerialNumberVoidRequest = {
      ids: props.ids,
      reason: form.value.reason,
      remark: form.value.remark
    }
    const { data } = await serialNumberVoidApi.void(req)
    if (data.success) {
      ElMessage.success(`作废成功（${data.data.count}条）`)
      emit('success', data.data)
    } else {
      ElMessage.error(data.message || '作废失败')
    }
  } catch (e: any) {
    ElMessage.error(e?.message || '作废失败')
  } finally {
    saving.value = false
  }
}
</script>

<template>
  <el-dialog :model-value="props.visible" title="作废序列号" width="400px" @close="() => emit('close')">
    <el-form>
      <el-form-item label="作废原因" required>
        <el-select v-model="form.reason" placeholder="请选择原因" style="width: 100%">
          <el-option v-for="item in reasonOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="说明">
        <el-input v-model="form.remark" type="textarea" :rows="2" maxlength="100" placeholder="请输入说明（可选）" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="() => emit('close')" :disabled="saving">取消</el-button>
      <el-button type="danger" @click="handleVoid" :loading="saving" :disabled="saving">{{ saving ? '作废中...' : '确定作废' }}</el-button>
    </template>
  </el-dialog>
</template>

<style scoped>
</style>
