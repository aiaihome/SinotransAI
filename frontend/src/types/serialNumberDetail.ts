export interface SerialNumberDetail {
  id: number
  billNumber: string
  status: string
  entryDate: string
  remark: string
  history: SerialNumberLog[]
}

export interface SerialNumberLog {
  id: number
  action: string
  operator: string
  operateTime: string
  beforeSnapshot?: any
  afterSnapshot?: any
  remark?: string
}
