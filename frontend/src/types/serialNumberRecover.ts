export interface SerialNumberRecoverRequest {
  ids: number[]
  remark?: string
}

export interface SerialNumberRecoverResponse {
  success: boolean
  message: string
  data: {
    recoveredIds: number[]
    count: number
  }
}
