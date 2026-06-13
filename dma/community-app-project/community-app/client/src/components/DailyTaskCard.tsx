interface DailyTaskCardProps {
  label: string;
  count: number;
  color: string;
  onClick?: () => void;
}

export default function DailyTaskCard({ label, count, color, onClick }: DailyTaskCardProps) {
  return (
    <button
      type="button"
      onClick={onClick}
      className="bg-gray-50 rounded-xl p-4 text-center hover:bg-gray-100 transition cursor-pointer dark:bg-gray-900 dark:hover:bg-gray-800"
    >
      <p className="text-xs text-gray-600 mb-2 font-medium">{label}</p>
      <p className="text-3xl font-bold mb-2" style={{ color }}>
        {count}
      </p>
      <div className="w-2 h-2 rounded-full mx-auto" style={{ backgroundColor: color }}></div>
    </button>
  );
}
