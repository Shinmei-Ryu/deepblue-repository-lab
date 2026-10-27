import { Routes, Route } from 'react-router-dom'
import CaseList from './components/CaseList'
import CasePage from './components/CasePage'

export default function App() {
    return (
        <>
            <h1>DeepBlue Rescue</h1>
            <main className="layout">
                <CaseList />
                <Routes>
                    <Route path="/" element={<p>Selecciona un caso</p>} />
                    <Route path="/cases/:caseCode" element={<CasePage />} />
                </Routes>
            </main>
        </>
    )
}
