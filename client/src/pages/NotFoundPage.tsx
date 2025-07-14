import AppLayout from '@/components/AppLayout'

export default function NotFoundPage() {
  return (
    <AppLayout>
      <div className="space-y-2">
        <h1 className="text-2xl font-bold">404 - Not Found</h1>
        <p>The requested resource could not be found.</p>
      </div>
    </AppLayout>
  )
}
