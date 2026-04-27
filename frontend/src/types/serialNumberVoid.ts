export interface SerialNumberVoidRequest {
  ids: number[]
  reason: string
  remark?: string
}

export interface SerialNumberVoidResponse {
  success: boolean
  message: string
  data: {
    voidedIds: number[]
    count: number
  }
}
