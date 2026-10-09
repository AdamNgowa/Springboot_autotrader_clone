// Shared shell for the login and register pages.
function AuthCard({ title, subtitle, children, footer }) {
  return (
    <main className="flex min-h-[calc(100dvh-3.5rem)] items-center justify-center bg-slate-50 px-4 py-10">
      <section className="w-full max-w-md border border-t-4 border-slate-300 border-t-blue-600 bg-white p-6 sm:p-8">
        <div className="mb-6">
          <h1 className="text-2xl font-bold text-slate-900">{title}</h1>

          {subtitle && (
            <p className="mt-1 text-sm text-slate-600">{subtitle}</p>
          )}
        </div>

        {children}

        {footer && (
          <p className="mt-6 border-t border-slate-200 pt-4 text-center text-sm text-slate-600">
            {footer}
          </p>
        )}
      </section>
    </main>
  );
}

export default AuthCard;
