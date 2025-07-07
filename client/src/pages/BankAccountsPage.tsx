import ManagementPage, { Column } from '@/components/ManagementPage'

interface Account {
  id: string
  name: string
}

const columns: Column<Account>[] = [
  { key: 'name', header: 'Name' },
]

export default function BankAccountsPage() {
  return (
    <ManagementPage<Account>
      title="Bank Accounts"
      endpoint="/accounts"
      columns={columns}
      renderForm={(item) => (
        <div>
          <label className="block mb-1" htmlFor="name">Name</label>
          <input
            id="name"
            name="name"
            defaultValue={item?.name}
            className="border p-2 rounded w-full"
          />
        </div>
      )}
    />
  )
}
