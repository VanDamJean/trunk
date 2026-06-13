import { useEffect } from "react";
import { useLocation } from "wouter";

export default function TestMode() {
  const [, setLocation] = useLocation();

  useEffect(() => {
    // 테스트 사용자로 로그인 상태 설정
    const testUser = {
      id: 999,
      openId: "test-user-999",
      name: "테스트 사용자",
      email: "test@queezy.local",
      role: "user",
    };

    // localStorage에 테스트 사용자 정보 저장
    localStorage.setItem("__test_user__", JSON.stringify(testUser));

    // 홈 화면으로 이동
    setLocation("/home");
  }, [setLocation]);

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-purple-50 to-blue-50 dark:from-slate-900 dark:to-slate-800">
      <div className="text-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary mx-auto mb-4"></div>
        <p className="text-gray-600 dark:text-gray-400">테스트 모드로 진입 중...</p>
      </div>
    </div>
  );
}
