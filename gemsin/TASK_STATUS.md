# Gemsin Web Mock Task Status

## Done

- [x] `gemsin` 작업 폴더 구성
- [x] 디자인 고정 규칙 문서화 (`DESIGN_FREEZE_SPEC.md`)
- [x] 화면 인덱스 추출 (`frame_index_375x812.csv`, `screen_frames.csv`)
- [x] 온보딩 3장 화면 구성 및 전환
- [x] 홈 화면 목업 구성 (체크인/통계/검색/게임 카드/탭바)
- [x] 탭 4개 페이지 연결 (Leaderboard / Ticket / Friends / History)
- [x] 탭별 기본 콘텐츠 확장
- [x] 목업 액션 추가 (start game, follow, claim, pack purchase)
- [x] 탭별 인터랙션 추가
  - [x] Leaderboard: 검색/정렬/주간-월간 토글
  - [x] Friends: 이름 검색 필터
  - [x] History: today/yesterday 필터
- [x] Leaderboard/Friends/History 데이터 구조를 JSON mock 분리 (`mock-data.js`)
- [x] 홈 카드별 상세 mock 모달 추가 (카드 클릭 상세 + 모달 시작 버튼)
- [x] 테스트 시나리오 문서 추가 (`TEST_SCENARIOS.md`)
- [x] Leaderboard/Teman/History 이미지 슬롯 구조 반영
  - [x] avatar/thumb 필드 데이터 스키마 추가 (`mock-data.js`)
  - [x] 이미지 없을 때 fallback(이니셜/아이콘) 렌더링 추가
  - [x] 이미지 프레임 크기/반경/object-fit 고정 스타일 추가
- [x] Leaderboard 명예의전당(1/2/3위 포디움) 강화
  - [x] 상위 3위 아바타 중심 포디움 렌더링
  - [x] 검색/정렬 결과에 맞춰 포디움 동적 반영
  - [x] 랭킹 리스트는 포디움 제외 4위 이하로 분리 렌더링
- [x] 리더보드 미세 UI 작업
  - [x] Nasional/Provinsi/Kota 세그먼트 탭 추가
  - [x] 게임 필터 드롭다운 UI 및 선택 상태 반영
- [x] 프로필 영역 1차 목업 추가
  - [x] Profile 메인 화면 (체크인/설정 메뉴)
  - [x] Daily Checkin 상세 화면
  - [x] Edit Profile 폼 + 저장(mock) 동작

## In Progress

- [ ] Figma 원본과 폰트/버튼/간격 1:1 픽셀 매칭 2차 보정
- [ ] 온보딩 2/3 실제 카피 교체 준비(데이터화 완료, Figma 원문 수집 대기)

## Todo

- [ ] 온보딩 2/3 화면을 Figma 실제 문구/요소로 교체
- [ ] 프로필 화면(메인/체크인/편집) Figma 수치 기반 2차 정밀 보정
- [x] 탭별 mock 데이터를 로컬 저장(localStorage)로 유지
- [x] 리더보드/히스토리 빈 데이터(empty state) 화면 추가

## Blocked

- [ ] Figma MCP 호출 제한 해제 전에는 대량 자동 추출 불가

## Notes

- 현재 버전은 백엔드 없이 사용자 테스트 가능한 프론트 목업.
- 구글 로그인/서버 연동은 가정(mock) 상태로 처리.
