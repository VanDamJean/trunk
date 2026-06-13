export default function DeviceStatusRow() {
  return (
    <div className="bg-white px-4 py-2 flex items-center justify-between text-xs text-gray-600 border-b border-gray-100 dark:bg-gray-950 dark:border-gray-800 dark:text-gray-400">
      <span className="font-medium">16:05</span>
      <div className="flex gap-1">
        <span>📶</span>
        <span>🔋</span>
      </div>
    </div>
  );
}
