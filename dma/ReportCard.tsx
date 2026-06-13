import { LucideIcon } from "lucide-react";

interface ReportCardProps {
  icon: LucideIcon;
  title: string;
  description: string;
}

export default function ReportCard({ icon: Icon, title, description }: ReportCardProps) {
  return (
    <div className="bg-gray-50 rounded-xl p-4 hover:bg-gray-100 transition cursor-pointer text-center hover:shadow-md">
      <div className="flex justify-center mb-3">
        <div className="w-10 h-10 bg-purple-100 rounded-lg flex items-center justify-center">
          <Icon size={20} className="text-purple-600" />
        </div>
      </div>
      <h4 className="text-xs font-semibold text-gray-800 mb-1 line-clamp-2">{title}</h4>
      <p className="text-xs text-gray-600 line-clamp-2">{description}</p>
    </div>
  );
}
