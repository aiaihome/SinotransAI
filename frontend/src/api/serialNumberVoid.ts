import request from '@/utils/request'
import type { SerialNumberVoidRequest, SerialNumberVoidResponse } from '@/types/serialNumberVoid'

export const serialNumberVoidApi = {
  void: (data: SerialNumberVoidRequest) =>
    request.post<SerialNumberVoidResponse>('/api/v1/serial-numbers/void', data)
}
