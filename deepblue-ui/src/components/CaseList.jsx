import { useState } from 'react'
import { useRescueCases } from '../hooks/useRescueCases'
import { useNavigate, useMatch } from 'react-router-dom'
import ErrorMessage from './ErrorMessage'

export const STATUSES = [
    'ADMITTED',
    'UNDER_EVALUATION',
    'IN_REHABILITATION',
    'READY_FOR_RELEASE',
    'RELEASED',
    'CLOSED',
]

export default function CaseList() {
    const [status, setStatus] = useState('ADMITTED')
    const navigate = useNavigate()
    const match = useMatch('/cases/:caseCode')
    const selectedCode = match?.params.caseCode
    const { data, isPending, isError, error } = useRescueCases(status)

    return (
        <aside>
            <h2>Casos de rescate</h2>

            <label>
                Estado
                <select value={status} onChange={(e) => setStatus(e.target.value)}>
                    {STATUSES.map((s) => (
                        <option key={s} value={s}>
                            {s}
                        </option>
                    ))}
                </select>
            </label>

            {isPending && <p>Cargando...</p>}
            {isError && <ErrorMessage error={error} />}
            {data && data.length === 0 && <p>No hay casos con este estado</p>}

            {data?.map((c) => (
                <button
                    key={c.caseCode}
                    className={`case-item ${selectedCode === c.caseCode ? 'active' : ''}`}
                    onClick={() => navigate(`/cases/${c.caseCode}`)}
                >
                    <strong>{c.caseCode}</strong> — {c.rescueLocation}
                    <br />
                    <small>{c.rescueDate}</small>
                </button>
            ))}
        </aside>
    )
}
