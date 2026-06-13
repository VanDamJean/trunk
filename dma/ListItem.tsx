import { ChevronRight } from "lucide-react";
import { ReactNode } from "react";

interface ListItemProps {
  icon: ReactNode;
  title: string;
  description: string;
}

export default function ListItem({ icon, title, description }: ListItemProps) {
  return (
    <div className="bg-gray-50 rounded-xl p-4 flex gap-3 hover:bg-gray-100 transition cursor-pointer group">
      <div className="flex-shrink-0 pt-1">{icon}</div>
      <div className="flex-1 min-w-0">
        <h3 className="text-sm font-semibold text-gray-800 mb-1">{title}</h3>
        <p className="text-xs text-gray-600 line-clamp-2">{description}</p>
      </div>
      <ChevronRight size={20} className="text-gray-400 flex-shrink-0 group-hover:text-gray-600 transition" />
    </div>
  );
}
