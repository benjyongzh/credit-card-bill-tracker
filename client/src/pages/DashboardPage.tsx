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
        if (p.toCardId && p.completed) {
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
          const paymentsTotal = c.billPayments.reduce(
            (sum, bp) => sum + bp.amount,
            0,
          )
          return {
            label: c.label,
            expenses: expenseGroup[key] || 0,
            payments: paymentsTotal,
          }
        })
      setCycleData(cycleArr)
    }

    load().catch(() => {
      // noop
    })
  }, [])

  return (
    <div className="flex flex-col gap-8 max-w-6xl mx-auto">
      <h1 className="page-title">Dashboard</h1>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
        <div>
          <h2 className="font-bold mb-2 text-foreground">Unpaid Expenses per Card</h2>
          <table className="w-full border text-left text-foreground">
            <thead>
              <tr>
                <th className="p-2">Card</th>
                <th className="p-2">Unpaid</th>
              </tr>
            </thead>
            <tbody>
              {unpaid.map((u) => (
                <tr key={u.cardId} className="border-t">
                  <td className="p-2">{u.cardName}</td>
                  <td className="p-2">{formatNumber(u.amount, 2)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        <div>
          <h2 className="font-bold mb-2 text-foreground">Historical Spend by Account</h2>
          <table className="w-full border text-left text-foreground">
            <thead>
              <tr>
                <th className="p-2">Account</th>
                <th className="p-2">Total Spent</th>
              </tr>
            </thead>
            <tbody>
              {accountTotals.map((a) => (
                <tr key={a.accountId} className="border-t">
                  <td className="p-2">{a.accountName}</td>
                  <td className="p-2">{formatNumber(a.amount, 2)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      <div className="w-full h-72">
        <h2 className="font-bold mb-2 text-foreground">Expenses vs Payments</h2>
        <ResponsiveContainer width="100%" height="100%">
          <BarChart data={cycleData} margin={{ top: 20, right: 30, bottom: 5, left: 0 }}>
            <XAxis dataKey="label" />
            <YAxis />
            <Tooltip />
            <Bar dataKey="expenses" stackId="total" fill="var(--chart-2)" name="Expenses" />
            <Bar dataKey="payments" stackId="total" fill="var(--chart-1)" name="Paid" />
          </BarChart>
        </ResponsiveContainer>
      </div>
    </div>
  )
}
