import ManagementPage, {type Column } from '@/components/ManagementPage'
import {FormControl, FormField, FormItem, FormLabel, FormMessage} from "@/components/ui/form.tsx";
import {Input} from "@/components/ui/input.tsx";
import { z } from "zod";
import { Checkbox } from "@/components/ui/checkbox";

interface Card {
  id: string
  cardName: string
  lastFourDigits: string
  isDefault: boolean
}

const cardSchema = z.object({
  cardName: z.string()
    .min(2, { message: "Card name must be at least 2 characters" })
    .max(50, { message: "Card name must be at most 50 characters" }),
  lastFourDigits: z.string()
    .length(4, { message: "Must be exactly 4 digits" })
    .regex(/^\d+$/, { message: "Must contain only digits" }),
  isDefault: z.boolean().default(false)
});

const defaultValues = {
  cardName: "",
  lastFourDigits: "",
  isDefault: false
};

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
      defaultValues={defaultValues}
      renderForm={(item, form) => {
        if (!form) return null;

        return (
          <>
              <FormField
                  control={form.control}
                  name="cardName"
                  render={({ field }) => (
                      <FormItem>
                          <FormLabel className="text-foreground">Card Name</FormLabel>
                          <FormControl>
                              <Input className="text-accent" {...field} />
                          </FormControl>
                          <FormMessage />
                      </FormItem>
                  )}
              />
              <FormField
                  control={form.control}
                  name="lastFourDigits"
                  render={({ field }) => (
                      <FormItem>
                          <FormLabel className="text-foreground">Last 4 Digits</FormLabel>
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
