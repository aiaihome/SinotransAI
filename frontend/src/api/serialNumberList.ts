import request from '@/utils/request'
import type { SerialNumberListQuery, SerialNumberListResponse } from '@/types/serialNumberList'

export const serialNumberListApi = {
  getSerialNumbers: (params: SerialNumberListQuery) =>
    request.get<SerialNumberListResponse>('/api/v1/serial-numbers', { params })
}
