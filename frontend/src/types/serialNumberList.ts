export interface SerialNumberListQuery {
  billNumber?: string
  entryDateFrom?: string
  entryDateTo?: string
  status?: string
  page?: number
  size?: number
}

export interface SerialNumberListItem {
  id: number
  billNumber: string
  status: string
  entryDate: string
  remark: string
}

export interface SerialNumberListResponse {
  total: number
  page: number
  size: number
  serialNumbers: SerialNumberListItem[]
}
