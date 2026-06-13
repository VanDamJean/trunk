interface TagBadgeProps {
  label: string;
  color: "teal" | "pink" | "purple";
}

export default function TagBadge({ label, color }: TagBadgeProps) {
  const colorClasses = {
    teal: "bg-teal-100 text-teal-700",
    pink: "bg-pink-100 text-pink-700",
    purple: "bg-purple-100 text-purple-700",
  };

  return (
    <span className={`px-3 py-1 rounded-full text-xs font-medium ${colorClasses[color]}`}>
      {label}
    </span>
  );
}
