# Gemsin 디자인 정리 폴더

이 폴더는 **앱/웹 구현 코드 없이**, Figma 원본 디자인을 후속 앱 개발에서 그대로 재현하기 위한 기준 자료만 보관한다.

## 포함 파일

- `frame_index_375x812.csv`
  - Figma 메타데이터에서 추출한 `375x812` 모바일 프레임 전체 인덱스(총 164행, clip/그룹 포함).
- `screen_frames.csv`
  - 실제 화면 후보로 사용 가능한 `name=Group` 프레임 목록(총 40개).
- `DESIGN_FREEZE_SPEC.md`
  - 디자인 변경 금지 원칙, 화면 구조 요약, 후속 앱 작업 전 체크리스트.
- `TASK_STATUS.md`
  - 진행한 작업/진행 중/할 일/차단 이슈를 체크박스로 관리.
- `WORKLOG.md`
  - 날짜별 작업 이력(무엇을 왜/어떻게 변경했는지) 상세 기록.
- `ANDROID_APP_TRANSITION_REPORT.md`
  - 현재 웹 목업을 Android 앱으로 전환할 때 필요한 스택/API/DB/공수/리스크 조사 보고서.
- `mock-data.js`
  - 화면 렌더링에 사용하는 JSON 형태 mock 데이터.
- `TEST_SCENARIOS.md`
  - 사용자 클릭 플로우 테스트 시나리오 체크리스트.

## 원본 소스

- Figma 파일: https://www.figma.com/design/YwNL6NSg776AyOAPuCBMsp/Gemsin---Gaming-Application-Featuring-Five-Engaging-Games--Community-?node-id=1-3&m=dev&t=7nftv8kwAPRZhJgd-1
- 기준 노드: `1:3`

## 사용 목적

- 지금 단계에서는 UI 구현을 하지 않는다.
- 이후 앱 개발 시, 이 폴더의 문서를 기준으로 **디자인 불변(색/크기/간격/배치 유지)** 원칙으로 제작한다.
