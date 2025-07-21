import ManagementPage, {type Column } from '@/components/ManagementPage'
import {FormControl, FormDescription, FormField, FormItem, FormLabel, FormMessage} from "@/components/ui/form.tsx";
import {Input} from "@/components/ui/input.tsx";
import { Checkbox } from "@/components/ui/checkbox";
import {RequiredLabel} from "@/components/RequiredLabel.tsx";
import {type Card, cardSchema, cardDefaultValues} from "@/lib/dataSchema.ts";

const columns: Column<Card>[] = [
  { key: 'cardName', header: 'Name' },
  { key: 'lastFourDigits', header: 'Last 4' },
  {
    key: 'isDefault',
    header: 'Default',
    render: (item) => item.isDefault ? 'Yes' : 'No'
  },
]

export default function CreditCardsPage() {
  return (
    <ManagementPage<Card>
      title="Credit Cards"
      endpoint="/cards"
      columns={columns}
      formSchema={cardSchema}
      defaultValues={cardDefaultValues}
      renderForm={(item, form) => {
        if (!form) return null;

        return (
          <>
              <FormField
                  control={form.control}
                  name="cardName"
                  render={({ field }) => (
                      <FormItem>
                          <RequiredLabel className="text-foreground" required={!cardSchema.shape.cardName.optional()}>Card Name</RequiredLabel>
                          <FormControl>
                              <Input className="text-accent" placeholder="Enter card name" {...field} />
                          </FormControl>
                          <FormDescription>e.g., Bank of American Frequent Flyer Card</FormDescription>
                          <FormMessage />
                      </FormItem>
                  )}
              />
              <FormField
                  control={form.control}
                  name="lastFourDigits"
                  render={({ field }) => (
                      <FormItem>

                          <RequiredLabel className="text-foreground" required={!cardSchema.shape.lastFourDigits.optional()}>Last 4 Digits</RequiredLabel>
                          <FormControl>
                              <Input
                                  type="text"
                                  inputMode="numeric"
                                  pattern="\d*"
                                  placeholder="Enter 4-digit code"
                                  maxLength={4} className="text-accent" {...field} />
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
                                  Set as default card
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
