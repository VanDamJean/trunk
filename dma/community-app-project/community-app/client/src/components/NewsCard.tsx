interface NewsCardProps {
  title: string;
  gradient: string;
  onClick?: () => void;
}

export default function NewsCard({ title, gradient, onClick }: NewsCardProps) {
  return (
    <button
      type="button"
      onClick={onClick}
      className={`flex-shrink-0 w-40 h-32 rounded-2xl bg-gradient-to-br ${gradient} p-4 flex items-end justify-start text-white shadow-lg hover:shadow-xl transition-all hover:scale-105 cursor-pointer text-left`}
    >
      <p className="text-sm font-semibold leading-tight">{title}</p>
    </button>
  );
}
