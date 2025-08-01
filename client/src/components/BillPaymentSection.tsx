import { useMemo } from 'react'
import dayjs from 'dayjs'
import {
  ColumnDef,
  getCoreRowModel,
  useReactTable,
} from '@tanstack/react-table'
import EditableTable from './EditableTable'
import { Button } from './ui/button'
import { useBillPayments } from '@/hooks/useBillPayments'
import type { BillPayment, Card, BankAccount } from '@/lib/dataSchema'

interface Props {
  accounts: BankAccount[]
  cards: Card[]
}

export default function BillPaymentSection({ accounts, cards }: Props) {
  const { payments, addPayment, removePayment, updatePayment } = useBillPayments()

  const columns = useMemo<ColumnDef<BillPayment>[]>(
    () => [
      {
        id: 'date',
        header: 'Date',
        cell: ({ row }) => (
          <input
            type="date"
            className="border px-1"
            value={row.original.date}
            onChange={(e) => updatePayment(row.original.id, { date: e.target.value })}
          />
        ),
      },
      {
        id: 'from',
        header: 'From Account',
        cell: ({ row }) => (
          <select
            className="border px-1 text-foreground"
            value={row.original.fromAccountId}
            onChange={(e) => updatePayment(row.original.id, { fromAccountId: e.target.value })}
          >
            {accounts.map((a) => (
              <option key={a.id} value={a.id}>
                {a.name}
              </option>
            ))}
          </select>
        ),
      },
      {
        id: 'to',
        header: 'To Card',
        cell: ({ row }) => (
          <select
            className="border px-1 text-foreground"
            value={row.original.toCardId ?? ''}
            onChange={(e) => updatePayment(row.original.id, { toCardId: e.target.value })}
          >
            <option value="">--</option>
            {cards.map((c) => (
              <option key={c.id} value={c.id}>
                {c.cardName}
              </option>
            ))}
          </select>
        ),
      },
      {
        id: 'amount',
        header: 'Amount',
        cell: ({ row }) => (
          <input
            type="number"
            className="w-24 border px-1"
            value={row.original.amount}
            onChange={(e) => updatePayment(row.original.id, { amount: parseFloat(e.target.value) })}
          />
        ),
      },
    ],
    [accounts, cards],
  )

  const table = useReactTable({
    data: payments,
    columns,
    getCoreRowModel: getCoreRowModel(),
  })

  return (
    <div className="flex flex-col gap-2">
      <EditableTable
        table={table}
        renderRowAction={(row) => (
          <Button variant="destructive" size="sm" onClick={() => removePayment(row.original.id)}>
            Delete
          </Button>
        )}
      />
      <div className="mt-2">
        <Button
          size="sm"
          onClick={() =>
            addPayment({
              id: Math.random().toString(),
              amount: 0,
              date: dayjs().format('YYYY-MM-DD'),
              billingCycleId: '',
              fromAccountId: accounts[0]?.id || '',
              toCardId: cards[0]?.id || null,
              toAccountId: null,
            })
          }
        >
          Add Payment
        </Button>
      </div>
    </div>
  )
}
