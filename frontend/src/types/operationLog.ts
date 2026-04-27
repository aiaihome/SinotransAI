export interface OperationLogQuery {
  page: number
  size: number
  keyword?: string
  type?: string
  startDate?: string
  endDate?: string
}

export interface OperationLogItem {
  id: number
  type: string
  operator: string
  content: string
  detail: string
  before: string
  after: string
  createdAt: string
}

export interface OperationLogResponse {
  total: number
  items: OperationLogItem[]
}
