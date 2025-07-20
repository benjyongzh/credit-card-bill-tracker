import ManagementPage, {type Column } from '@/components/ManagementPage'
import {FormControl, FormField, FormItem, FormLabel, FormMessage} from "@/components/ui/form.tsx";
import {Input} from "@/components/ui/input.tsx";
import { z } from "zod";
import { Checkbox } from "@/components/ui/checkbox";

interface Account {
  id: string
  name: string
  isDefault: boolean
}

const accountSchema = z.object({
  name: z.string()
    .min(2, { message: "Account name must be at least 2 characters" })
    .max(50, { message: "Account name must be at most 50 characters" }),
  isDefault: z.boolean().default(false)
});

const defaultValues = {
  name: "",
  isDefault: false
};

const columns: Column<Account>[] = [
  { key: 'name', header: 'Name' },
  {
    key: 'isDefault',
    header: 'Default',
    render: (item) => item.isDefault ? 'Yes' : 'No'
  },
]

export default function BankAccountsPage() {
  return (
    <ManagementPage<Account>
      title="Bank Accounts"
      endpoint="/accounts"
      columns={columns}
      formSchema={accountSchema}
      defaultValues={defaultValues}
      renderForm={(item, form) => {
        if (!form) return null;

        return (
          <>
            <FormField
              control={form.control}
              name="name"
              render={({ field }) => (
                <FormItem>
                  <FormLabel className="text-foreground">Account Name</FormLabel>
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
                    <FormLabel className="text-foreground">
                      Set as default account
                    </FormLabel>
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
