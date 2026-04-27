import request from '@/utils/request'
import type { BlankBillSegmentEditRequest, BlankBillSegmentEditResponse } from '@/types/blankBillEdit'

export const blankBillEditApi = {
  // 编辑号码段
  editSegment: (segmentId: number, data: BlankBillSegmentEditRequest) =>
    request.put<BlankBillSegmentEditResponse>(`/api/v1/blank-bill-segments/${segmentId}`, data)
}
