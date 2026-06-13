import { toast } from "sonner";
import { User, Flag, Heart } from "lucide-react";
import ReportCard from "@/components/ReportCard";
import TagBadge from "@/components/TagBadge";
import AppScreenHeader from "@/components/AppScreenHeader";
import DeviceStatusRow from "@/components/DeviceStatusRow";
import type { NavTabId } from "@/components/BottomNav";

interface ProfilePageProps {
  onNavigate: (page: NavTabId) => void;
  /** 데스크톱 2열 레이아웃에 넣을 때 하단 탭 여백 생략 */
  embeddedDesktop?: boolean;
}

export default function ProfilePage({ onNavigate, embeddedDesktop }: ProfilePageProps) {
  const reports = [
    { icon: User, title: "Astro psychological report", description: "Some short description of this type of report" },
    { icon: Flag, title: "Monthly prediction report", description: "Some short description of this type of report" },
    { icon: Flag, title: "Daily Prediction", description: "Some short description of this type of report" },
    { icon: Heart, title: "Love report", description: "Some short description of this type of report" },
  ];

  const bottomPad = embeddedDesktop ? "pb-8" : "pb-28 lg:pb-8";

  return (
    <div className="flex-1 flex flex-col bg-white min-h-0 dark:bg-gray-950 lg:min-h-auto">
      <AppScreenHeader title="Profile" />
      <DeviceStatusRow />
      <div className={`flex-1 overflow-y-auto ${bottomPad}`}>
        <div className="px-4 py-6 space-y-6">
          <div className="flex flex-col items-center text-center">
            <button
              type="button"
              className="w-24 h-24 rounded-full bg-gradient-to-br from-blue-500 to-purple-600 flex items-center justify-center text-white text-4xl font-bold mb-3 relative shadow-lg hover:opacity-95 transition"
              onClick={() => toast.message("프로필 사진", { description: "갤러리에서 사진을 고른다고 가정합니다." })}
              aria-label="프로필 사진 변경"
            >
              👤
              <span className="absolute bottom-0 right-0 w-6 h-6 bg-red-500 rounded-full border-2 border-white flex items-center justify-center text-xs font-bold">
                ✓
              </span>
            </button>
            <h2 className="text-xl font-bold text-gray-800 dark:text-gray-100">Angelica Jackson</h2>
            <p className="text-sm text-gray-600 mb-2 dark:text-gray-400">Analyzer</p>
            <button
              type="button"
              className="text-sm text-purple-600 font-medium hover:text-purple-700 transition"
              onClick={() => {
                if (!embeddedDesktop) {
                  onNavigate("settings");
                }
                toast.success("프로필 편집", {
                  description: embeddedDesktop
                    ? "데스크톱 레이아웃에서는 우측 상단 설정 시트를 사용해 보세요."
                    : "설정 탭으로 이동했습니다.",
                });
              }}
            >
              Change profile
            </button>
          </div>

          <div>
            <h3 className="text-sm font-semibold text-gray-800 mb-3 dark:text-gray-100">Strong side:</h3>
            <div className="flex flex-wrap gap-2">
              <TagBadge
                label="Analytics"
                color="teal"
                onClick={() => toast.message("태그", { description: "Analytics" })}
              />
              <TagBadge
                label="Perfectionism"
                color="teal"
                onClick={() => toast.message("태그", { description: "Perfectionism" })}
              />
              <TagBadge
                label="Analytics"
                color="teal"
                onClick={() => toast.message("태그", { description: "Analytics" })}
              />
            </div>
          </div>

          <div>
            <h3 className="text-sm font-semibold text-gray-800 mb-3 dark:text-gray-100">Weak side:</h3>
            <div className="flex flex-wrap gap-2">
              <TagBadge
                label="Perfectionism"
                color="pink"
                onClick={() => toast.message("태그", { description: "Perfectionism" })}
              />
              <TagBadge
                label="Analytics"
                color="pink"
                onClick={() => toast.message("태그", { description: "Analytics" })}
              />
            </div>
          </div>

          <div>
            <h3 className="text-sm font-semibold text-gray-800 mb-3 dark:text-gray-100">My Reports:</h3>
            <div className="grid grid-cols-2 gap-3">
              {reports.map((report, index) => (
                <ReportCard
                  key={index}
                  icon={report.icon}
                  title={report.title}
                  description={report.description}
                  onClick={() =>
                    toast.info(report.title, { description: "리포트 뷰어는 데모에서 토스트로 대체합니다." })
                  }
                />
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
