function StatCard({ title, value, subtitle, icon }) {
    return (
        <div className="rounded-xl border border-slate-800 bg-slate-900 p-5 transition hover:border-slate-700">

            <div className="flex items-start justify-between">

                <div>
                    <p className="text-sm text-slate-400">
                        {title}
                    </p>

                    <h2 className="mt-2 text-3xl font-bold text-white">
                        {value}
                    </h2>

                    <p className="mt-1 text-xs text-slate-500">
                        {subtitle}
                    </p>
                </div>

                <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-cyan-500/10 text-xl text-cyan-400">
                    {icon}
                </div>

            </div>

        </div>
    )
}

export default StatCard