import { ReactNode } from 'react'
import AppLayout from './AppLayout'

export default function ManagementPage({ title, children }: { title: string; children: ReactNode }) {
  return (
    <AppLayout>
      <h1 className="text-2xl font-bold mb-4">{title}</h1>
      {children}
    </AppLayout>
  )
}
