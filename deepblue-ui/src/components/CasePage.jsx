import { useParams } from 'react-router-dom'
import { useRescueCase } from '../hooks/useRescueCase'
import CaseDetail from './CaseDetail'
import ErrorMessage from './ErrorMessage'

export default function CasePage() {
    const { caseCode } = useParams()
    const { data, isPending, isError, error } = useRescueCase(caseCode)

    if (isPending) return <p>Cargando...</p>
    if (isError) return <ErrorMessage error={error} />

    return <CaseDetail caseItem={data} onChanged={() => {}} />
}