interface TagBadgeProps {
  label: string;
  color: "teal" | "pink" | "purple";
  onClick?: () => void;
}

export default function TagBadge({ label, color, onClick }: TagBadgeProps) {
  const colorClasses = {
    teal: "bg-teal-100 text-teal-700 dark:bg-teal-900/40 dark:text-teal-200",
    pink: "bg-pink-100 text-pink-700 dark:bg-pink-900/40 dark:text-pink-200",
    purple: "bg-purple-100 text-purple-700 dark:bg-purple-900/40 dark:text-purple-200",
  };

  return (
    <button
      type="button"
      onClick={onClick}
      className={`px-3 py-1 rounded-full text-xs font-medium ${colorClasses[color]} ${
        onClick ? "cursor-pointer hover:opacity-90 transition" : "cursor-default"
      }`}
    >
      {label}
    </button>
  );
}
