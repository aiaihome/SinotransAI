<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'
import { blankBillEditApi } from '@/api/blankBillEdit'
import type { BlankBillSegmentEditRequest } from '@/types/blankBillEdit'

const props = defineProps<{
  segmentId: number
  remark: string
  entryDate: string
  startNumber: string
  endNumber: string
  editable?: boolean
  onSuccess?: (data: any) => void
}>()

const emit = defineEmits(['success', 'close'])

const form = ref({
  remark: props.remark,
  entryDate: props.entryDate
})
const saving = ref(false)
const errors = ref<{ [k: string]: string }>({})

function validate() {
  errors.value = {}
  if (!form.value.entryDate) errors.value.entryDate = '请选择入库日期'
  if (form.value.remark && form.value.remark.length > 200) errors.value.remark = '备注不能超过200字'
  return Object.keys(errors.value).length === 0
}

async function handleSave() {
  if (!validate()) return
  saving.value = true
  try {
    const req: BlankBillSegmentEditRequest = {
      remark: form.value.remark,
      entryDate: form.value.entryDate
    }
    const { data } = await blankBillEditApi.editSegment(props.segmentId, req)
    if (data.success) {
      ElMessage.success('编辑成功')
      emit('success', data.data)
    } else {
      ElMessage.error(data.message || '编辑失败')
    }
  } catch (e: any) {
    ElMessage.error(e?.message || '编辑失败')
  } finally {
    saving.value = false
  }
}

function handleClose() {
  emit('close')
}
</script>

<template>
  <el-dialog
    :model-value="true"
    title="编辑空白提单号码段"
    width="480px"
    :close-on-click-modal="false"
    :close-on-press-escape="true"
    :show-close="true"
    @close="handleClose"
    class="blank-bill-edit-dialog"
  >
    <el-form :model="form" label-width="100px" class="blank-bill-edit-form">
      <el-form-item label="起始号">
        <el-input v-model="props.startNumber" readonly style="width: 200px" />
      </el-form-item>
      <el-form-item label="结束号">
        <el-input v-model="props.endNumber" readonly style="width: 200px" />
      </el-form-item>
      <el-form-item label="入库日期 *" :error="errors.entryDate">
        <el-date-picker
          v-model="form.entryDate"
          type="date"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          :disabled-date="date => date > dayjs()"
          style="width: 200px"
        />
      </el-form-item>
      <el-form-item label="备注" :error="errors.remark">
        <el-input
          v-model="form.remark"
          type="textarea"
          :rows="3"
          maxlength="200"
          placeholder="请输入备注（可选）"
          style="width: 100%"
        />
      </el-form-item>
    </el-form>
    <div class="dialog-footer">
      <el-button @click="handleClose" :disabled="saving">取消</el-button>
      <el-button type="primary" @click="handleSave" :loading="saving" :disabled="saving" style="margin-left: 12px;">{{ saving ? '保存中...' : '保存' }}</el-button>
    </div>
  </el-dialog>
</template>

<style scoped>
.blank-bill-edit-dialog {
}
.blank-bill-edit-form {
  background: #fff;
  padding: 16px 0;
}
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
