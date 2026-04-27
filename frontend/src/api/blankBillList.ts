import request from '@/utils/request'
import type { BlankBillSegmentListQuery, BlankBillSegmentListResponse } from '@/types/blankBillList'

export const blankBillListApi = {
  getSegments: (params: BlankBillSegmentListQuery) =>
    request.get<BlankBillSegmentListResponse>('/api/v1/blank-bill-segments', { params })
}
