import ManagementPage, {type Column } from '@/components/ManagementPage'
import {FormControl, FormField, FormItem, FormLabel, FormMessage} from "@/components/ui/form.tsx";
import {Input} from "@/components/ui/input.tsx";
import { useEntityList } from '@/hooks/useEntityList';
import { Dialog, DialogContent, DialogTrigger } from "@/components/ui/dialog";
import { XIcon, ChevronDownIcon } from "lucide-react";
import { Button } from "@/components/ui/button";
import {
    type SpendingProfile,
    type SpendingProfileAccount,
    spendingProfileDefaultValues,
    spendingProfileSchema
} from "@/lib/dataSchema.ts";
import {RequiredLabel} from "@/components/RequiredLabel.tsx";

const columns: Column<SpendingProfile>[] = [
  { key: 'name', header: 'Name' },
  {
    key: 'bankAccounts',
    header: 'Bank Accounts',
    render: (item) => item.bankAccounts.join(', ') || 'None'
  },
]

export default function SpendingProfilesPage() {
  const { items: accounts } = useEntityList<SpendingProfileAccount>('/accounts');

  return (
    <ManagementPage<SpendingProfile>
      title="Spending Categories"
      endpoint="/spending-profiles"
      columns={columns}
      formSchema={spendingProfileSchema}
      defaultValues={spendingProfileDefaultValues}
      renderForm={(item, form) => {
        if (!form) return null;

        // Get current bank accounts selection
        const selectedAccounts = form.watch('bankAccounts') || [];

        // Update form value when checkbox is toggled
        const handleAccountToggle = (accountName: string, checked: boolean) => {
          const currentAccounts = [...selectedAccounts];

          if (checked) {
            if (!currentAccounts.includes(accountName)) {
              currentAccounts.push(accountName);
            }
          } else {
            const index = currentAccounts.indexOf(accountName);
            if (index !== -1) {
              currentAccounts.splice(index, 1);
            }
          }

          form.setValue('bankAccounts', currentAccounts, { shouldValidate: true });
        };

        return (
          <>
            <FormField
              control={form.control}
              name="name"
              render={({ field }) => (
                <FormItem>
                    <RequiredLabel className="text-foreground">Profile Name</RequiredLabel>
                  <FormControl>
                    <Input className="text-accent" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <div className="mt-6">
                <RequiredLabel className="text-foreground block mb-2">Bank Accounts</RequiredLabel>

              {/* Display selected accounts as chips/pills */}
              <div className="flex flex-wrap gap-2 mb-2">
                {selectedAccounts.length === 0 ? (
                  <p className="text-muted-foreground text-sm">No bank accounts selected</p>
                ) : (
                  selectedAccounts.map((accountName:any, index:any) => (
                    <div
                      key={index}
                      className="flex items-center gap-1 bg-primary/10 text-primary rounded-full px-3 py-1"
                    >
                      <span className="text-sm">{accountName}</span>
                      <button
                        type="button"
                        onClick={() => handleAccountToggle(accountName, false)}
                        className="text-primary hover:text-primary/80 focus:outline-none"
                      >
                        <XIcon className="h-3 w-3" />
                      </button>
                    </div>
                  ))
                )}
              </div>

              {/* Multi-select dropdown */}
              <Dialog>
                <DialogTrigger asChild>
                  <Button
                    variant="outline"
                    className="w-full justify-between"
                    type="button"
                  >
                    <span className="text-foreground">Select Bank Accounts</span>
                    <ChevronDownIcon className="h-4 w-4" />
                  </Button>
                </DialogTrigger>
                <DialogContent className="max-w-sm">
                  <div className="max-h-[300px] overflow-auto p-1">
                    {accounts.length === 0 ? (
                      <p className="text-muted-foreground p-2">No bank accounts available</p>
                    ) : (
                      <div className="space-y-2">
                        {accounts.map(account => (
                          <div
                            key={account.id}
                            className="flex items-center justify-between p-2 hover:bg-muted/50 rounded-md cursor-pointer"
                            onClick={() => {
                              const isSelected = selectedAccounts.includes(account.name);
                              handleAccountToggle(account.name, !isSelected);
                            }}
                          >
                            <span className="text-sm font-medium">{account.name}</span>
                            {selectedAccounts.includes(account.name) && (
                              <div className="h-2 w-2 rounded-full bg-primary"></div>
                            )}
                          </div>
                        ))}
                      </div>
                    )}
                  </div>
                </DialogContent>
              </Dialog>

              <FormMessage />
            </div>
          </>
        );
      }}
    />
  )
}
