import request from '@/utils/request'
import type { SerialNumberRecoverRequest, SerialNumberRecoverResponse } from '@/types/serialNumberRecover'

export const serialNumberRecoverApi = {
  recover: (data: SerialNumberRecoverRequest) =>
    request.post<SerialNumberRecoverResponse>('/api/v1/serial-numbers/recover', data)
}
