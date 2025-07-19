import { useAuth } from '@/hooks/useAuth'
import { Button } from "@/components/ui/button"
import {Input} from "@/components/ui/input.tsx";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod"
import { useForm } from "react-hook-form";
import Modal from "@/components/Modal.tsx";
import * as test from "node:test";

const formSchema = z.object({
  username: z.string()
      .min(2, {message: "Must be at least 2 characters"})
      .max(20, {message: "Must be at most 20 characters"}),
  password: z.string()
      .min(2, { message: "Must be at least 2 characters"})
      .max(20, {message: "Must be at most 20 characters"}),
})

export default function LoginPage() {
  const { login } = useAuth()

  const googleLogin = () => {
    const base = import.meta.env.VITE_API_BASE_URL.replace(/\/api$/, '')
    window.location.href = `${base}${import.meta.env.VITE_OAUTH_LOGIN_CALLBACK_URL}`
  }

  // 1. Define your form.
  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {
        username: "",
        password: "",
    },
  })

  // 2. Define a submit handler.
  function onSubmit(values: z.infer<typeof formSchema>) {
    // Do something with the form values.
    // ✅ This will be type-safe and validated.
    console.log(values)
    // try {
    //   await login(values.username, values.password)
    // } catch (err) {
    //   setError('Login failed')
    // }
  }

  function onRegister() {
        // Do something with the form values.
        // ✅ This will be type-safe and validated.
        console.log("onRegister")
        // () => window.location.href = '/api/auth/logout'
    }

  return (
    <div className="flex flex-col items-center justify-center min-h-screen p-10 max-w-md mx-auto">
      <Form {...form}>
          <h1 className="text-primary-foreground text-2xl font-bold md:self-start mb-1">Credit Card Bill Tracker</h1>
        <h2 className="text-primary-foreground text-lg font-bold md:self-start mb-6">Login</h2>
        <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6 w-full">
          <FormField
              control={form.control}
              name="username"
              render={({ field }) => (
                  <FormItem>
                    <FormLabel className="text-primary-foreground">Username</FormLabel>
                    <FormControl>
                      <Input className="text-accent" placeholder="Enter your username" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
              )}
          />
          <FormField
              control={form.control}
              name="password"
              render={({ field }) => (
                  <FormItem>
                    <FormLabel className="text-primary-foreground">Password</FormLabel>
                    <FormControl>
                      <Input className="text-accent" placeholder="Enter your password" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
              )}
          />
            <Button className="mt-6 w-full">
                Login
            </Button>
        </form>
      </Form>
        <div className="flex flex-col justify-between w-full mt-8 gap-6">
            <Button variant="secondary" onClick={googleLogin} className="bg-accent w-full">
                Sign in with Google
            </Button>
            <Button variant="link" onClick={onRegister} className="text-primary-foreground underline cursor-pointer -mt-1 pt-0">Dont have an account? Register</Button>
            <Modal title="testModal" triggerLabel="test here" triggerClassName="text-primary-foreground underline cursor-pointer -mt-1 pt-0" contentClassName="text-primary-foreground" onOpen={() => console.log("test modal is open")} >
                <div>
                    <p>
                        Lorem ipsum dolor sit amet, consectetur adipiscing elit.
                        Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas.
                        Vestibulum tortor quam, feugiat vitae, ultricies eget, tempor sit amet, ante.
                        Donec eu libero sit amet quam egestas semper.
                        Aenean ultricies mi vitae est. Mauris placerat eleifend leo.
                        Quisque sit amet est et sapien ullamcorper pharetra.
                        Vestibulum erat wisi, condimentum sed, commodo vitae, ornare sit amet, wisi.
                    </p>
                </div>
            </Modal>
        </div>
    </div>
  )
}
