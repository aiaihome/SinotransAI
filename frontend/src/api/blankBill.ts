import request from '@/utils/request'
import type { BlankBillSegmentRequest, BlankBillSegmentCheckRequest, BlankBillSegmentResponse, BlankBillSegmentCheckResponse, Carrier } from '@/types/blankBill'

export const blankBillApi = {
  // 入库
  createSegment: (data: BlankBillSegmentRequest) =>
    request.post<BlankBillSegmentResponse>('/api/v1/blank-bill-segments', data),

  // 号码段交叉校验
  checkSegment: (data: BlankBillSegmentCheckRequest) =>
    request.post<BlankBillSegmentCheckResponse>('/api/v1/blank-bill-segments/check', data),

  // 船司主数据
  getCarriers: (keyword?: string) =>
    request.get<{ carriers: Carrier[] }>('/api/v1/carriers', { params: keyword ? { keyword } : {} })
}
