import AppScreenHeader from "@/components/AppScreenHeader";
import DeviceStatusRow from "@/components/DeviceStatusRow";

interface PlaceholderPageProps {
  title: string;
  subtitle?: string;
}

export default function PlaceholderPage({
  title,
  subtitle = "이 화면은 데모용 플레이스홀더입니다. 하단 탭으로 다른 메뉴를 눌러 보세요.",
}: PlaceholderPageProps) {
  return (
    <div className="flex-1 flex flex-col bg-white min-h-0 dark:bg-gray-950">
      <AppScreenHeader title={title} />
      <DeviceStatusRow />
      <div className="flex-1 overflow-y-auto px-4 py-8 pb-28 lg:pb-8">
        <p className="text-sm text-gray-600 leading-relaxed dark:text-gray-400">{subtitle}</p>
      </div>
    </div>
  );
}
