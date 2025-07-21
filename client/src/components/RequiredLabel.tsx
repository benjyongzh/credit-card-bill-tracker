import { cn } from "@/lib/utils"
import * as React from "react";
import {FormLabel} from "@/components/ui/form.tsx";

type RequiredLabelProps = React.ComponentPropsWithoutRef<"label"> & {
    required?: boolean
}

export function RequiredLabel({ children, required, className, ...props }: RequiredLabelProps) {
    return (
        <FormLabel className={cn(className)} {...props}>
            {children}
            {required && <span className="text-red-500 ml-1">*</span>}
        </FormLabel>
    )
}
