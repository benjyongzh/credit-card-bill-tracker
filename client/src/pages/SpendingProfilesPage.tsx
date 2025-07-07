import ManagementPage, {type Column } from '@/components/ManagementPage'

interface Profile {
  id: string
  name: string
}

const columns: Column<Profile>[] = [
  { key: 'name', header: 'Name' },
]

export default function SpendingProfilesPage() {
  return (
    <ManagementPage<Profile>
      title="Spending Profiles"
      endpoint="/spending-profiles"
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
