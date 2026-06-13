interface DailyTaskCardProps {
  label: string;
  count: number;
  color: string;
}

export default function DailyTaskCard({ label, count, color }: DailyTaskCardProps) {
  return (
    <div className="bg-gray-50 rounded-xl p-4 text-center hover:bg-gray-100 transition cursor-pointer">
      <p className="text-xs text-gray-600 mb-2 font-medium">{label}</p>
      <p className="text-3xl font-bold mb-2" style={{ color }}>
        {count}
      </p>
      <div className="w-2 h-2 rounded-full mx-auto" style={{ backgroundColor: color }}></div>
    </div>
  );
}
