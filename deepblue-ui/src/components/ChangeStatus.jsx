import { useState } from 'react'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import { changeCaseStatus } from '../api/rescueCases'

import ErrorMessage from './ErrorMessage'

const NEXT = {
    ADMITTED: 'UNDER_EVALUATION',
    UNDER_EVALUATION: 'IN_REHABILITATION',
    IN_REHABILITATION: 'READY_FOR_RELEASE',
    READY_FOR_RELEASE: 'RELEASED',
}

export default function ChangeStatus({ caseItem, onChanged }) {
    const queryClient = useQueryClient()
    const [next, setNext] = useState(caseItem.status)
    const options = [caseItem.status, NEXT[caseItem.status]].filter(Boolean)

    const mutation = useMutation({
        mutationFn: (status) => changeCaseStatus(caseItem.caseCode, status),
        onSuccess: (updatedCase) => {
            // Todas las listas por estado quedaron desactualizadas
            queryClient.invalidateQueries({ queryKey: ['rescue-cases'] })
            onChanged(updatedCase)
        },
    })

    return (
        <section>
            <h3>Cambiar estado</h3>

            <label>
                Nuevo estado
                <select value={next} onChange={(e) => setNext(e.target.value)}>
                    {options.map((s) => (
                        <option key={s} value={s}>
                            {s}
                        </option>
                    ))}
                </select>
            </label>

            <button
                disabled={mutation.isPending || next === caseItem.status}
                onClick={() => mutation.mutate(next)}
            >
                {mutation.isPending ? 'Aplicando...' : 'Aplicar'}
            </button>

            <ErrorMessage error={mutation.error} />
        </section>
    )
}
