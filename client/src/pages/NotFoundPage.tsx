export default function NotFoundPage() {
  return (
    <>
      <div className="flex flex-col gap-4 max-w-6xl mx-auto">
        <h1 className="hidden sm:flex text-2xl text-foreground font-bold">404 - Not Found</h1>
        <p className="text-foreground">The requested resource could not be found.</p>
      </div>
    </>
  )
}
