export interface BlankBillSegmentListQuery {
  carrierId?: number
  startNumber?: string
  endNumber?: string
  entryDateFrom?: string
  entryDateTo?: string
  page?: number
  size?: number
  sort?: string
}

export interface BlankBillSegmentListItem {
  segmentId: number
  carrierName: string
  startNumber: string
  endNumber: string
  quantity: number
  remark: string
  entryDate: string
  archived: boolean
}

export interface BlankBillSegmentListResponse {
  total: number
  page: number
  size: number
  segments: BlankBillSegmentListItem[]
}
