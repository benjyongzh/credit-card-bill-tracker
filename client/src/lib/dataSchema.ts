import { z } from "zod";

export interface Card {
    id: string
    cardName: string
    lastFourDigits: string
    isDefault: boolean
}

export const cardSchema = z.object({
    cardName: z.string()
        .min(2, { message: "Card name must be at least 2 characters" })
        .max(50, { message: "Card name must be at most 50 characters" }),
    lastFourDigits: z.string()
        .length(4, { message: "Must be exactly 4 digits" })
        .regex(/^\d+$/, { message: "Must contain only digits" })
        .optional(),
    isDefault: z.boolean().default(false)
});

export const cardDefaultValues = {
    cardName: "",
    lastFourDigits: "",
    isDefault: false
};

export interface BankAccount {
    id: string
    name: string
    isDefault: boolean
}

export const bankAccountSchema = z.object({
    name: z.string()
        .min(2, { message: "Account name must be at least 2 characters" })
        .max(50, { message: "Account name must be at most 50 characters" }),
    isDefault: z.boolean().default(false)
});

export const bankAccountDefaultValues = {
    name: "",
    isDefault: false
};

export type SpendingProfileAccount = Omit<BankAccount, 'isDefault'>;

export interface SpendingProfile {
    id: string
    name: string
    bankAccounts: string[] // Array of bank account names
}

export const spendingProfileSchema = z.object({
    name: z.string()
        .min(2, { message: "Profile name must be at least 2 characters" })
        .max(50, { message: "Profile name must be at most 50 characters" }),
    bankAccounts: z.array(z.string()).default([])
});

export const spendingProfileDefaultValues = {
    name: "",
    bankAccounts: []
};