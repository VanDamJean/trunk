import { useState } from "react";
import { Clock } from "lucide-react";
import { toast } from "sonner";
import NewsCard from "@/components/NewsCard";
import DailyTaskCard from "@/components/DailyTaskCard";
import ProgressSection from "@/components/ProgressSection";
import ListItem from "@/components/ListItem";
import BottomNav, { type NavTabId } from "@/components/BottomNav";
import AppScreenHeader from "@/components/AppScreenHeader";
import DeviceStatusRow from "@/components/DeviceStatusRow";
import ProfilePage from "./Profile";
import PlaceholderPage from "./PlaceholderPage";

function HomeMain() {
  return (
    <div className="flex-1 flex flex-col bg-white min-h-0 dark:bg-gray-950">
      <AppScreenHeader title="Home" />
      <DeviceStatusRow />
      <div className="flex-1 overflow-y-auto pb-28 lg:pb-8">
        <div className="px-4 py-6 space-y-6">
          <div>
            <h2 className="text-gray-500 text-sm font-medium mb-3 dark:text-gray-400">News</h2>
            <div className="flex gap-3 overflow-x-auto pb-2">
              <NewsCard
                title="Short news title will be here"
                gradient="from-purple-600 to-pink-500"
                onClick={() =>
                  toast.info("뉴스 카드", { description: "상세 뉴스 화면은 데모에서 생략되었습니다." })
                }
              />
              <NewsCard
                title="Short news title will be here"
                gradient="from-teal-400 to-cyan-500"
                onClick={() => toast.info("뉴스 카드", { description: "별도 기사 뷰로 연결될 위치입니다." })}
              />
              <NewsCard
                title="Short news title will be here"
                gradient="from-purple-500 to-indigo-600"
                onClick={() => toast.info("뉴스 카드", { description: "탭하여 피드백을 확인했습니다." })}
              />
            </div>
          </div>

          <div>
            <h2 className="text-gray-500 text-sm font-medium mb-3 dark:text-gray-400">Daily Tasks:</h2>
            <div className="grid grid-cols-3 gap-3">
              <DailyTaskCard
                label="Daily"
                count={3}
                color="#FF6B6B"
                onClick={() => toast.message("Daily", { description: "오늘 할 일 3건 (데모)" })}
              />
              <DailyTaskCard
                label="Daily deep"
                count={1}
                color="#FF6B6B"
                onClick={() => toast.message("Daily deep", { description: "심층 작업 1건 (데모)" })}
              />
              <DailyTaskCard
                label="Daily mantra"
                count={2}
                color="#51CF66"
                onClick={() => toast.message("Daily mantra", { description: "맨트라 2건 (데모)" })}
              />
            </div>
          </div>

          <button
            type="button"
            className="w-full text-left rounded-xl focus:outline-none focus-visible:ring-2 focus-visible:ring-purple-500"
            onClick={() =>
              toast.message("진행률", { description: "전체 진행률 위젯을 탭했습니다. (데모)" })
            }
          >
            <ProgressSection progress={60} />
          </button>

          <div className="space-y-3">
            <ListItem
              icon={<Clock size={20} className="text-gray-400" />}
              title="How was your day?"
              description="Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut sed odio in urna ultrices."
              onClick={() =>
                toast.success("일기 항목", { description: "오늘 하루 기록 화면으로 이동한다고 가정합니다." })
              }
            />
            <ListItem
              icon={<Clock size={20} className="text-red-500" />}
              title="Current Transit 3rd House"
              description="This is demonstrate siblings, hobbies, efforts, confidence, friends and short tr..."
              onClick={() =>
                toast.message("트랜짓", { description: "3하우스 트랜짓 상세 (데모)." })
              }
            />
          </div>
        </div>
      </div>
    </div>
  );
}

function renderMobilePage(currentPage: NavTabId, setCurrentPage: (p: NavTabId) => void) {
  switch (currentPage) {
    case "profile":
      return <ProfilePage onNavigate={setCurrentPage} />;
    case "center":
      return <PlaceholderPage title="Center" />;
    case "search":
      return <PlaceholderPage title="Search" subtitle="검색·히스토리 화면 플레이스홀더입니다." />;
    case "settings":
      return (
        <PlaceholderPage
          title="Settings"
          subtitle="앱 설정은 우측 상단 톱니바퀴 시트에서도 일부 조정할 수 있습니다."
        />
      );
    default:
      return <HomeMain />;
  }
}

export default function Home() {
  const [currentPage, setCurrentPage] = useState<NavTabId>("home");

  return (
    <div className="min-h-screen bg-white flex flex-col lg:flex-row dark:bg-gray-950">
      <div className="flex flex-col flex-1 w-full min-h-screen min-h-[100dvh] lg:hidden">
        <div className="flex-1 flex flex-col min-h-0">{renderMobilePage(currentPage, setCurrentPage)}</div>
        <BottomNav currentPage={currentPage} onNavigate={setCurrentPage} />
      </div>

      <div className="hidden lg:flex flex-1 flex-row w-full min-h-screen">
        <div className="flex-1 flex flex-col w-full lg:max-w-md lg:border-r lg:border-gray-200 dark:lg:border-gray-800">
          <HomeMain />
        </div>
        <div className="flex-1 flex flex-col w-full lg:max-w-md">
          <ProfilePage onNavigate={setCurrentPage} embeddedDesktop />
        </div>
      </div>
    </div>
  );
}
