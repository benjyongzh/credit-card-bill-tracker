import {type ReactNode, useRef } from 'react'
import Modal from './Modal'
import { DialogClose } from '@/components/ui/dialog'
import { Button } from '@/components/ui/button'
import { Form } from "@/components/ui/form";
import {useForm} from "react-hook-form";
import { z, ZodObject, type ZodRawShape } from "zod";
import {zodResolver} from "@hookform/resolvers/zod";

interface ModalFormProps {
  title: string
  triggerLabel: string
  onSubmit: (data:  Record<string, any>) => void
  formSchema: ZodObject<ZodRawShape>; // Accepts any Zod object schema
  defaultValues?: Record<string, any>; // Optional custom default values
  children: ReactNode | ((form: any) => ReactNode)
  triggerClassName?: string
  contentClassName?: string
  onOpen?: () => void
}

export default function ModalForm({
  title,
  triggerLabel,
  onSubmit,
  formSchema,
  defaultValues,
  children,
  triggerClassName,
  onOpen,
  ...rest
}: ModalFormProps) {
  return (
    <Modal title={title} triggerLabel={triggerLabel} triggerClassName={triggerClassName} onOpen={onOpen} {...rest}>
      <FormBody onSubmit={onSubmit} formSchema={formSchema} defaultValues={defaultValues}>{children}</FormBody>
    </Modal>
  )
}

function FormBody({ onSubmit, formSchema, defaultValues, children }: {
  onSubmit: (data: z.infer<typeof formSchema>) => void;
  formSchema: ZodObject<ZodRawShape>;
  defaultValues?: Record<string, any>;
  children: ReactNode | ((form: any) => ReactNode)
}) {
  const closeRef = useRef<HTMLButtonElement>(null);

  // Build defaultValues from the schema keys if not provided
  const formDefaultValues = defaultValues || Object.fromEntries(
      Object.entries(formSchema.shape).map(([key, value]) => {
        if (value instanceof z.ZodString) return [key, ""];
        if (value instanceof z.ZodNumber) return [key, 0];
        if (value instanceof z.ZodBoolean) return [key, false];
        return [key, null]; // fallback
      })
  );

  // 1. Define your form.
  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: formDefaultValues,
  })

  // 2. Define a submit handler.
  function handleSubmit(values: z.infer<typeof formSchema>) {
    onSubmit(values);
    // Close the modal after form submission
    closeRef.current?.click();
  }

  // Create a render function that passes the form to children if they're a function
  const renderChildren = () => {
    if (typeof children === 'function') {
      return children(form);
    }
    return children;
  };

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(handleSubmit)} className="space-y-8 w-full">
        {renderChildren()}
      </form>

      <div className="flex justify-end gap-4">
        <Button type="submit">Save</Button>
        <DialogClose asChild>
          <Button variant="ghost" className="text-foreground">
            Cancel
          </Button>
        </DialogClose>
        <DialogClose ref={closeRef} className="hidden" aria-hidden="true" />
      </div>
    </Form>
  )
}
