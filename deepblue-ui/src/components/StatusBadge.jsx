const COLORS = {
    ADMITTED: '#1565c0',
    UNDER_EVALUATION: '#ef6c00',
    IN_REHABILITATION: '#6a1b9a',
    READY_FOR_RELEASE: '#2e7d32',
    RELEASED: '#00838f',
    CLOSED: '#546e7a',
}

export default function StatusBadge({ status }) {
    return (
        <span className="badge" style={{ background: COLORS[status] ?? '#777' }}>
      {status}
    </span>
    )
}