export function Footer() {
  return (
    <footer className="border-t border-gray-100 py-10 px-6 md:px-12">
      <div className="max-w-container mx-auto flex flex-col md:flex-row items-start md:items-center justify-between gap-6">
        <div className="display-serif text-2xl">KANBAN</div>
        <div className="flex items-center gap-6 text-sm text-gray-600">
          <a href="#" className="hover:text-ink transition-colors">Features</a>
          <a href="#" className="hover:text-ink transition-colors">About</a>
          <a href="#" className="hover:text-ink transition-colors">Contact</a>
        </div>
        <div className="flex items-center gap-4 text-xs text-gray-400 mono">
          <span>© 2026</span>
          <a href="#" className="hover:text-ink transition-colors">Terms</a>
          <a href="#" className="hover:text-ink transition-colors">Privacy</a>
        </div>
      </div>
    </footer>
  )
}
