import ManagementPage, {type Column } from '@/components/ManagementPage'
import {FormControl, FormField, FormItem, FormLabel, FormMessage} from "@/components/ui/form.tsx";
import {Input} from "@/components/ui/input.tsx";
import { Checkbox } from "@/components/ui/checkbox";
import {type BankAccount, bankAccountDefaultValues, bankAccountSchema} from "@/lib/dataSchema.ts";
import {RequiredLabel} from "@/components/RequiredLabel.tsx";

const columns: Column<BankAccount>[] = [
  { key: 'name', header: 'Name' },
  {
    key: 'isDefault',
    header: 'Default',
    render: (item) => item.isDefault ? 'Yes' : 'No'
  },
]

export default function BankAccountsPage() {
  return (
    <ManagementPage<BankAccount>
      title="Bank Accounts"
      titleShortForm="Accounts"
      subtitle="Your bank accounts will be used to pay off credit card bills."
      endpoint="/accounts"
      columns={columns}
      formSchema={bankAccountSchema}
      defaultValues={bankAccountDefaultValues}
      renderForm={(item, form) => {
        if (!form) return null;

        return (
          <>
            <FormField
              control={form.control}
              name="name"
              render={({ field }) => (
                <FormItem>
                    <RequiredLabel className="text-foreground">Account Name</RequiredLabel>
                  <FormControl>
                    <Input className="text-accent" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="isDefault"
              render={({ field }) => (
                <FormItem className="flex items-center justify-start gap-4 px-2">
                  <FormControl>
                    <Checkbox
                      checked={field.value}
                      onCheckedChange={field.onChange}
                    />
                  </FormControl>
                  <div className="space-y-1 leading-none">
                      <RequiredLabel className="text-foreground">Set as default account</RequiredLabel>
                  </div>
                </FormItem>
              )}
            />
          </>
        );
      }}
    />
  )
}
