import { useState } from "react";
import { Bell, Info, LifeBuoy, Menu, Settings } from "lucide-react";
import { toast } from "sonner";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { Sheet, SheetContent, SheetHeader, SheetTitle, SheetTrigger } from "@/components/ui/sheet";
import { Switch } from "@/components/ui/switch";
import { Label } from "@/components/ui/label";
import { useTheme } from "@/contexts/ThemeContext";

interface AppScreenHeaderProps {
  title: string;
}

export default function AppScreenHeader({ title }: AppScreenHeaderProps) {
  const { theme, toggleTheme, switchable } = useTheme();
  const [pushOn, setPushOn] = useState(true);

  return (
    <div className="bg-white border-b border-gray-200 px-4 py-3 flex items-center justify-between sticky top-0 z-10 dark:bg-gray-950 dark:border-gray-800">
      <DropdownMenu>
        <DropdownMenuTrigger asChild>
          <button
            type="button"
            className="p-2 hover:bg-gray-100 rounded-lg transition dark:hover:bg-gray-800"
            aria-label="메뉴"
          >
            <Menu size={24} className="text-gray-800 dark:text-gray-100" />
          </button>
        </DropdownMenuTrigger>
        <DropdownMenuContent align="start" className="w-52">
          <DropdownMenuItem
            onSelect={() => toast.info("공지사항", { description: "표시할 새 공지가 없습니다." })}
          >
            <Bell className="mr-2 size-4" />
            공지사항
          </DropdownMenuItem>
          <DropdownMenuItem
            onSelect={() => toast.message("고객센터", { description: "평일 09:00–18:00 (데모 문구)" })}
          >
            <LifeBuoy className="mr-2 size-4" />
            고객센터
          </DropdownMenuItem>
          <DropdownMenuSeparator />
          <DropdownMenuItem
            onSelect={() =>
              toast.message("Community App", { description: "UI 데모 빌드 · v1.0" })
            }
          >
            <Info className="mr-2 size-4" />
            이 앱 정보
          </DropdownMenuItem>
        </DropdownMenuContent>
      </DropdownMenu>
      <h1 className="text-lg font-semibold text-gray-800 dark:text-gray-100">{title}</h1>
      <Sheet>
        <SheetTrigger asChild>
          <button
            type="button"
            className="p-2 hover:bg-gray-100 rounded-lg transition dark:hover:bg-gray-800"
            aria-label="설정"
          >
            <Settings size={24} className="text-gray-800 dark:text-gray-100" />
          </button>
        </SheetTrigger>
        <SheetContent side="right" className="bg-white dark:bg-gray-950">
          <SheetHeader>
            <SheetTitle>설정</SheetTitle>
          </SheetHeader>
          <div className="flex flex-col gap-6 px-2 py-4">
            <div className="flex items-center justify-between gap-4">
              <Label htmlFor="push-toggle">푸시 알림</Label>
              <Switch
                id="push-toggle"
                checked={pushOn}
                onCheckedChange={(v) => {
                  setPushOn(v);
                  toast.message(v ? "알림이 켜졌습니다." : "알림이 꺼졌습니다.");
                }}
              />
            </div>
            {switchable && toggleTheme ? (
              <div className="flex items-center justify-between gap-4">
                <Label htmlFor="theme-toggle">다크 모드</Label>
                <Switch
                  id="theme-toggle"
                  checked={theme === "dark"}
                  onCheckedChange={(wantDark) => {
                    if (wantDark !== (theme === "dark")) {
                      toggleTheme();
                    }
                  }}
                />
              </div>
            ) : null}
          </div>
        </SheetContent>
      </Sheet>
    </div>
  );
}
