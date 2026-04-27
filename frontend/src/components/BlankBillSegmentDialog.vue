<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import dayjs from 'dayjs'
import { blankBillApi } from '@/api/blankBill'
import type { Carrier, BlankBillSegmentRequest, BlankBillSegmentCheckRequest, BlankBillSegmentCheckResponse } from '@/types/blankBill'
import { ElMessage } from 'element-plus'
const emit = defineEmits(['update:visible', 'success', 'close'])

const props = withDefaults(defineProps<{ visible?: boolean }>(), {
  visible: true
})
const show = computed({
  get: () => props.visible,
  set: v => emit('update:visible', v)
})

// 表单数据
const form = ref({
  carrierId: undefined as number | undefined,
  startNumber: '',
  endNumber: '',
  remark: '',
  entryDate: dayjs().format('YYYY-MM-DD')
})

const carriers = ref<Carrier[]>([])
const loading = ref(false)
const saving = ref(false)
const checkLoading = ref(false)
const errors = ref<{ [k: string]: string }>({})
const quantity = computed(() => {
  const start = Number(form.value.startNumber)
  const end = Number(form.value.endNumber)
  if (form.value.startNumber.length === 8 && form.value.endNumber.length === 8 && end > start) {
    return end - start + 1
  }
  return 0
})

// 校验逻辑
function validate() {
  errors.value = {}
  if (!form.value.carrierId) errors.value.carrierId = '请选择船司'
  if (!/^\d{8}$/.test(form.value.startNumber)) errors.value.startNumber = '请输入8位纯数字'
  if (!/^\d{8}$/.test(form.value.endNumber)) errors.value.endNumber = '请输入8位纯数字'
  if (Number(form.value.endNumber) <= Number(form.value.startNumber)) errors.value.endNumber = '结束号必须大于起始号'
  if (quantity.value > 99999) errors.value.endNumber = '单个号码段最大支持99999张'
  return Object.keys(errors.value).length === 0
}

// 船司下拉
async function loadCarriers(keyword = '') {
  const { data } = await blankBillApi.getCarriers(keyword)
  carriers.value = data.carriers

  if (!form.value.carrierId && carriers.value.length > 0) {
    const defaultCarrier = carriers.value.find(item => item.name === '马士基') ?? carriers.value[0]
    form.value.carrierId = defaultCarrier.id
  }
}

// 号码段交叉校验
async function checkSegmentCross() {
  if (!validate()) return false
  checkLoading.value = true
  try {
    const req: BlankBillSegmentCheckRequest = {
      carrierId: form.value.carrierId!,
      startNumber: form.value.startNumber,
      endNumber: form.value.endNumber
    }
    const { data } = await blankBillApi.checkSegment(req)
    if (data.conflict) {
      errors.value.endNumber = `号码段与已有号段（${data.conflictSegments.map(s => s.startNumber + '~' + s.endNumber).join('、')}）存在交叉，请修改`
      return false
    }
    return true
  } finally {
    checkLoading.value = false
  }
}

// 保存
async function handleSave() {
  if (!validate()) return
  if (!(await checkSegmentCross())) return
  saving.value = true
  try {
    const req: BlankBillSegmentRequest = {
      carrierId: form.value.carrierId!,
      startNumber: form.value.startNumber,
      endNumber: form.value.endNumber,
      quantity: quantity.value,
      remark: form.value.remark,
      entryDate: form.value.entryDate
    }
    const { data } = await blankBillApi.createSegment(req)
    console.log('保存结果-', data)
    if (data.success) {
          console.log('保存结果-', data)
        show.value = false
      ElMessage.success(`入库成功！已生成${data.quantity}条序列号`)
      emit('success', data.data)
    } else {
      ElMessage.error(data.message || '保存失败')
    }
  } catch (e: any) {
    ElMessage.error(e?.message || '保存失败')
  } finally {
    saving.value = false
  }
}

function handleClose() {
  emit('update:visible', false)
  emit('close')
}

onMounted(() => {
  loadCarriers()
})
</script>

<template>
    <el-dialog
      v-model="show"
      title="空白提单入库"
      width="500px"
    >
    <el-form :model="form" label-width="100px" class="blank-bill-form">
      <el-form-item label="船司 *" :error="errors.carrierId">
        <el-select
          v-model="form.carrierId"
          filterable
          remote
          reserve-keyword
          placeholder="请选择船司"
          :remote-method="loadCarriers"
          :loading="loading"
          style="width: 200px"
        >
          <el-option
            v-for="item in carriers"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="起始号 *" :error="errors.startNumber">
        <el-input
          v-model="form.startNumber"
          maxlength="8"
          placeholder="例：00010001"
          style="width: 200px"
          @input="val => form.startNumber = val.replace(/\D/g, '').slice(0,8)"
        />
      </el-form-item>
      <el-form-item label="结束号 *" :error="errors.endNumber">
        <el-input
          v-model="form.endNumber"
          maxlength="8"
          placeholder="例：00010099"
          style="width: 200px"
          @input="val => form.endNumber = val.replace(/\D/g, '').slice(0,8)"
        />
        <span class="quantity-label" v-if="quantity > 0">共 {{ quantity }} 张</span>
      </el-form-item>
      <el-form-item label="入库日期 *">
        <el-date-picker
          v-model="form.entryDate"
          type="date"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          :disabled-date="date => date > dayjs()"
          style="width: 200px"
        />
      </el-form-item>
      <el-form-item label="备注">
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
.blank-bill-dialog {
  /* 基础弹窗美化 */
  border-radius: 10px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.18);
  background: #fff;
  padding: 0 0 12px 0;
  min-height: 200px;
}
.blank-bill-form {
  background: #fff;
  padding: 16px 0;
}
.quantity-label {
  margin-left: 12px;
  color: #1677FF;
  font-weight: bold;
  font-size: 14px;
  line-height: 32px;
}
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
