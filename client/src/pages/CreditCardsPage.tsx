import ManagementPage, {type Column } from '@/components/ManagementPage'

interface Card {
  id: string
  cardName: string
  lastFourDigits: string
}

const columns: Column<Card>[] = [
  { key: 'cardName', header: 'Name' },
  { key: 'lastFourDigits', header: 'Last 4' },
]

export default function CreditCardsPage() {
  return (
    <ManagementPage<Card>
      title="Credit Cards"
      endpoint="/cards"
      columns={columns}
      renderForm={(item) => (
        <>
          <div>
            <label className="block mb-1" htmlFor="cardName">Name</label>
            <input
              id="cardName"
              name="cardName"
              defaultValue={item?.cardName}
              className="border p-2 rounded w-full"
            />
          </div>
          <div>
            <label className="block mb-1" htmlFor="lastFourDigits">Last 4</label>
            <input
              id="lastFourDigits"
              name="lastFourDigits"
              defaultValue={item?.lastFourDigits}
              className="border p-2 rounded w-full"
            />
          </div>
        </>
      )}
    />
  )
}
