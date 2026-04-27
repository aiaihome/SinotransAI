export interface BlankBillSegmentEditRequest {
  remark?: string
  entryDate?: string
}

export interface BlankBillSegmentEditResponse {
  success: boolean
  message: string
  data: {
    segmentId: number
    remark: string
    entryDate: string
  }
}
