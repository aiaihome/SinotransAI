export interface Carrier {
  id: number
  name: string
}

export interface BlankBillSegmentRequest {
  carrierId: number
  startNumber: string
  endNumber: string
  quantity: number
  remark?: string
  entryDate: string
}

export interface BlankBillSegmentResponse {
  success: boolean
  message: string
  data: {
    segmentId: number
    serialNumbers: string[]
    quantity: number
  }
}

export interface BlankBillSegmentCheckRequest {
  carrierId: number
  startNumber: string
  endNumber: string
}

export interface BlankBillSegmentCheckResponse {
  conflict: boolean
  conflictSegments: Array<{
    segmentId: number
    startNumber: string
    endNumber: string
  }>
  message: string
}
