import {useEffect, useState} from 'react'
import { formatNumber } from 'accounting-js'
import {
  bankAccountApi,
  billPaymentApi,
  billingCycleApi,
  creditCardApi,
  expenseApi,
  expenseSummaryApi,
  userApi,
} from '@/lib/api'
import type {
  BillingCycle,
  BillPayment,
  Card,
  BankAccount,
  Expense,
  ExpenseSummary,
} from '@/lib/dataSchema'
import dayjs from 'dayjs'
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer,
} from 'recharts'
import {
  Table,
  TableHeader,
  TableBody,
  TableRow,
  TableHead,
  TableCell,
} from '@/components/ui/table'

interface UnpaidPerCard {
  cardId: string
  cardName: string
  amount: number
}

interface SpendPerAccount {
  accountId: string
  accountName: string
  amount: number
}

interface CycleChartDatum {
  label: string
  expenses: number
  payments: number
}

export default function DashboardPage() {
  const [unpaid, setUnpaid] = useState<UnpaidPerCard[]>([])
  const [accountTotals, setAccountTotals] = useState<SpendPerAccount[]>([])
  const [cycleData, setCycleData] = useState<CycleChartDatum[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    const load = async () => {
      const userRes = await userApi.getProfile()
      const userId = userRes.data.id as string

      const [cardsRes, accountsRes, summariesRes, paymentsRes, expensesRes, cyclesRes] =
        await Promise.all([
          creditCardApi.getAll(),
          bankAccountApi.getAll(),
          expenseSummaryApi.getFromUser(userId),
          billPaymentApi.getAll(),
          expenseApi.getAll(),
          billingCycleApi.getAll(),
        ])

      const cards = cardsRes.data as Card[]
      const accounts = accountsRes.data as BankAccount[]
      const summaries = summariesRes.data as ExpenseSummary[]
      const payments = paymentsRes.data as BillPayment[]
      const expenses = expensesRes.data as Expense[]
      const cycles = cyclesRes.data as BillingCycle[]

      // --- unpaid per card ---
      const unpaidMap: Record<string, UnpaidPerCard> = {}
      for (const card of cards) {
        unpaidMap[card.id] = { cardId: card.id, cardName: card.cardName, amount: 0 }
      }
      for (const s of summaries) {
        if (s.toType === 'CARD' && unpaidMap[s.toId]) {
          unpaidMap[s.toId].amount += s.totalExpense
        }
      }
      for (const p of payments) {
        if (p.toCardId) {
          const entry = unpaidMap[p.toCardId]
          if (entry) entry.amount -= p.amount
        }
      }
      setUnpaid(Object.values(unpaidMap))

      // --- spend per account ---
      const accountMap: Record<string, SpendPerAccount> = {}
      for (const a of accounts) {
        accountMap[a.id] = { accountId: a.id, accountName: a.name, amount: 0 }
      }
      for (const s of summaries) {
        const entry = accountMap[s.fromAccountId]
        if (entry) entry.amount += s.totalExpense
      }
      setAccountTotals(Object.values(accountMap))

      // --- cycle chart data ---
      const expenseGroup: Record<string, number> = {}
      for (const e of expenses) {
        const key = dayjs(e.date).format('YYYY-MM')
        expenseGroup[key] = (expenseGroup[key] || 0) + e.amount
      }

      const cycleArr: CycleChartDatum[] = cycles
        .sort((a, b) => dayjs(a.completedDate).unix() - dayjs(b.completedDate).unix())
        .map((c) => {
          const key = dayjs(c.completedDate).format('YYYY-MM')
          const paymentsTotal = payments
            .filter((p) => p.billingCycleId === c.id)
            .reduce((sum, bp) => sum + bp.amount, 0)
          return {
            label: c.label,
            expenses: expenseGroup[key] || 0,
            payments: paymentsTotal,
          }
        })
      setCycleData(cycleArr)
    }

    setLoading(true)
    load()
      .catch(() => setError('Failed to load dashboard data'))
      .finally(() => setLoading(false))
  }, [])

  return (
    <div className="flex flex-col gap-8 max-w-6xl mx-auto">
      <h1 className="page-title">Dashboard</h1>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
        <div>
          <h2 className="font-bold mb-2 text-foreground">Unpaid Expenses per Card</h2>
          <Table className="text-left">
            <TableHeader className="bg-muted">
              <TableRow>
                <TableHead>Card</TableHead>
                <TableHead>Unpaid</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody className="bg-muted/30">
              {loading ? (
                <TableRow>
                  <TableCell colSpan={2} className="text-center">
                    Loading...
                  </TableCell>
                </TableRow>
              ) : error ? (
                <TableRow>
                  <TableCell colSpan={2} className="text-destructive text-center">
                    {error}
                  </TableCell>
                </TableRow>
              ) : unpaid.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={2} className="text-center">
                    Nothing to show.
                  </TableCell>
                </TableRow>
              ) : (
                unpaid.map((u) => (
                  <TableRow key={u.cardId}>
                    <TableCell>{u.cardName}</TableCell>
                    <TableCell>{formatNumber(u.amount, { precision: 2 })}</TableCell>
                  </TableRow>
                ))
              )}
            </TableBody>
          </Table>
        </div>

        <div>
          <h2 className="font-bold mb-2 text-foreground">Historical Spend by Account</h2>
          <Table className="text-left">
            <TableHeader className="bg-muted">
              <TableRow>
                <TableHead>Account</TableHead>
                <TableHead>Total Spent</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody className="bg-muted/30">
              {loading ? (
                <TableRow>
                  <TableCell colSpan={2} className="text-center">
                    Loading...
                  </TableCell>
                </TableRow>
              ) : error ? (
                <TableRow>
                  <TableCell colSpan={2} className="text-destructive text-center">
                    {error}
                  </TableCell>
                </TableRow>
              ) : accountTotals.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={2} className="text-center">
                    Nothing to show.
                  </TableCell>
                </TableRow>
              ) : (
                accountTotals.map((a) => (
                  <TableRow key={a.accountId}>
                    <TableCell>{a.accountName}</TableCell>
                    <TableCell>{formatNumber(a.amount, { precision: 2 })}</TableCell>
                  </TableRow>
                ))
              )}
            </TableBody>
          </Table>
        </div>
      </div>

      <div className="w-full h-72">
        <h2 className="font-bold mb-2 text-foreground">Expenses vs Payments</h2>
        {loading ? (
          <p className="text-foreground flex justify-center">Loading...</p>
        ) : error ? (
          <p className="text-destructive flex justify-center">{error}</p>
        ) : cycleData.length === 0 ? (
          <p className="text-foreground flex justify-center">Nothing to show.</p>
        ) : (
          <ResponsiveContainer width="100%" height="100%">
            <BarChart data={cycleData} margin={{ top: 20, right: 30, bottom: 5, left: 0 }}>
              <XAxis dataKey="label" />
              <YAxis />
              <Tooltip />
              <Bar dataKey="expenses" stackId="total" fill="var(--chart-2)" name="Expenses" />
              <Bar dataKey="payments" stackId="total" fill="var(--chart-1)" name="Paid" />
            </BarChart>
          </ResponsiveContainer>
        )}
      </div>
    </div>
  )
}
