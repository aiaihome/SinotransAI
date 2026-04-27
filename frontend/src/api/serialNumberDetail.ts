import request from '@/utils/request'
import type { SerialNumberDetail, SerialNumberLog } from '@/types/serialNumberDetail'

export const serialNumberDetailApi = {
  getDetail: (id: number) =>
    request.get<SerialNumberDetail>(`/api/v1/serial-numbers/${id}`),
  getLogs: (id: number) =>
    request.get<SerialNumberLog[]>(`/api/v1/serial-numbers/${id}/logs`)
}
