import request from '@/utils/request'

export const serialNumberVoidExportApi = {
  export: (params: Record<string, any>) =>
    request.get<{ url: string }>('/api/v1/serial-numbers/void/export', { params })
}
