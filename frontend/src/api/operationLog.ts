import request from '@/utils/request'
import type { OperationLogQuery, OperationLogResponse } from '@/types/operationLog'

export const operationLogApi = {
  list: (params: OperationLogQuery) =>
    request.get<OperationLogResponse>(`/api/v1/operator-logs/${params.id}/logs`, { params })
}
