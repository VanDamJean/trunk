interface ProgressSectionProps {
  progress: number;
}

export default function ProgressSection({ progress }: ProgressSectionProps) {
  return (
    <div className="bg-white">
      <p className="text-sm font-medium text-gray-700 mb-2">
        Your overall progress is <span className="text-pink-500 font-bold">{progress}%</span>
      </p>
      <div className="w-full bg-gray-200 rounded-full h-2">
        <div
          className="bg-gradient-to-r from-pink-500 to-purple-600 h-2 rounded-full transition-all duration-300"
          style={{ width: `${progress}%` }}
        ></div>
      </div>
    </div>
  );
}
