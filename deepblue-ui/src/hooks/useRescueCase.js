import { useQuery } from '@tanstack/react-query'
import { getCaseByCode } from '../api/rescueCases'

export function useRescueCase(caseCode) {
    return useQuery({
        queryKey: ['rescue-cases', 'detail', caseCode],
        queryFn: () => getCaseByCode(caseCode),
    })
}