import { Menu, Settings, User, Flag, Heart } from "lucide-react";
import ReportCard from "@/components/ReportCard";
import TagBadge from "@/components/TagBadge";

interface ProfilePageProps {
  onNavigate: (page: string) => void;
}

export default function ProfilePage({ onNavigate }: ProfilePageProps) {
  const reports = [
    { icon: User, title: "Astro psychological report", description: "Some short description of this type of report" },
    { icon: Flag, title: "Monthly prediction report", description: "Some short description of this type of report" },
    { icon: Flag, title: "Daily Prediction", description: "Some short description of this type of report" },
    { icon: Heart, title: "Love report", description: "Some short description of this type of report" },
  ];

  return (
    <div className="flex-1 flex flex-col bg-white min-h-screen lg:min-h-auto">
      {/* Header */}
      <div className="bg-white border-b border-gray-200 px-4 py-3 flex items-center justify-between sticky top-0 z-10">
        <button className="p-2 hover:bg-gray-100 rounded-lg transition">
          <Menu size={24} className="text-gray-800" />
        </button>
        <h1 className="text-lg font-semibold text-gray-800">Profile</h1>
        <button className="p-2 hover:bg-gray-100 rounded-lg transition">
          <Settings size={24} className="text-gray-800" />
        </button>
      </div>

      {/* Status Bar */}
      <div className="bg-white px-4 py-2 flex items-center justify-between text-xs text-gray-600 border-b border-gray-100">
        <span className="font-medium">16:05</span>
        <div className="flex gap-1">
          <span>📶</span>
          <span>🔋</span>
        </div>
      </div>

      {/* Main Content */}
      <div className="flex-1 overflow-y-auto pb-8">
        <div className="px-4 py-6 space-y-6">
          {/* Profile Section */}
          <div className="flex flex-col items-center text-center">
            <div className="w-24 h-24 rounded-full bg-gradient-to-br from-blue-500 to-purple-600 flex items-center justify-center text-white text-4xl font-bold mb-3 relative shadow-lg">
              👤
              <div className="absolute bottom-0 right-0 w-6 h-6 bg-red-500 rounded-full border-2 border-white flex items-center justify-center text-xs font-bold">
                ✓
              </div>
            </div>
            <h2 className="text-xl font-bold text-gray-800">Angelica Jackson</h2>
            <p className="text-sm text-gray-600 mb-2">Analyzer</p>
            <button className="text-sm text-purple-600 font-medium hover:text-purple-700 transition">
              Change profile
            </button>
          </div>

          {/* Strong Side */}
          <div>
            <h3 className="text-sm font-semibold text-gray-800 mb-3">Strong side:</h3>
            <div className="flex flex-wrap gap-2">
              <TagBadge label="Analytics" color="teal" />
              <TagBadge label="Perfectionism" color="teal" />
              <TagBadge label="Analytics" color="teal" />
            </div>
          </div>

          {/* Weak Side */}
          <div>
            <h3 className="text-sm font-semibold text-gray-800 mb-3">Weak side:</h3>
            <div className="flex flex-wrap gap-2">
              <TagBadge label="Perfectionism" color="pink" />
              <TagBadge label="Analytics" color="pink" />
            </div>
          </div>

          {/* My Reports */}
          <div>
            <h3 className="text-sm font-semibold text-gray-800 mb-3">My Reports:</h3>
            <div className="grid grid-cols-2 gap-3">
              {reports.map((report, index) => (
                <ReportCard
                  key={index}
                  icon={report.icon}
                  title={report.title}
                  description={report.description}
                />
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
