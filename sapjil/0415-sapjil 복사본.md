오늘의 삽질 역사 (2026-04-15)
목표
Gemma 4 26B A4B 무검열 버전 로컬에서 돌리기
(어제 397B 실패 후 현실적인 목표로 하향 조정)

시도 1: mlx-community 4bit
PLE 아키텍처 잘못 양자화된 버전 → 패스

시도 2: FakeRockert543 8bit → Jiunsong 8bit으로 변경
pip install mlx-vlm → externally-managed-environment 에러
venv 만들어서 해결: python3 -m venv ~/mlxenv
모델 28.6GB 다운 완료
python -m mlx_vlm.generate → 57 tok/s 성공

시도 3: 서버 + Cursor 연결
mlx_vlm.server 실행 성공
Cursor Override Base URL 설정 → 400 에러 (계속 실패)
원인: Cursor 요청 포맷 비호환

시도 4: Continue.dev 연결
config.yaml에 openai provider로 추가
서버 200 OK 오는데 응답 렌더링 안 됨
disableStreaming: true 해봤으나 무효
원인: mlx_vlm 스트리밍 포맷 ≠ Continue 기대값

시도 5: Gradio Chat UI
pip install 'mlx-vlm[ui]'
python -m mlx_vlm.chat_ui → http://localhost:7860 성공!
58 tok/s, 28GB RAM, 이미지 입력 가능

결론
어제: 397B, 807GB 원본, SSD 부족으로 포기
오늘: 26B A4B, 28.6GB, 58 tok/s로 쾌적하게 성공

교훈
MoE 활성 파라미터(4B)랑 저장 크기(28GB)는 다른 개념
mlx_vlm.server는 OpenAI 스트리밍 호환 안 됨 → Chat UI 쓸 것
Continue 연동은 나중에 mlx_lm.server로 재도전


------추가-----
## 시도 6: Continue Agent로 테트리스 만들기
- mlx_lm.server로 교체 후 Continue 연동 성공 (스트리밍 정상)
- Agent 모드로 테트리스 요청
- **결과: 무한루프** - "그만" 해도 계속 python3 src/game.py 날림
- 테트리스 만들다가 갑자기 FastAPI + SQLAlchemy 짜고 있었음
- Llama-3-70B라고 자기소개, 클라우드 요금제 50회/4시간 지어냄

## 시도 7: Cursor Agent 연결
- Override Base URL: http://localhost:8080/v1
- **결과: 400 에러** - Cursor 요청 포맷 비호환, 끝내 실패

## 최종 결론
- **쓸만한 용도**: Gradio Chat UI로 무검열 대화, 긴 문서 분석, 프라이버시 민감한 작업
- **못 쓰는 용도**: 코딩 에이전트 (지시 추적 능력 부족, 환각 심함)
- **코딩은 그냥 Claude**

## 오늘의 교훈
1. MoE 4B 활성 = 속도↑, RAM은 여전히 모델 크기만큼 필요
2. 58 tok/s로 빠르지만 에이전트 루프 탈출 못 함
3. 로컬 모델은 "도구"지 "에이전트"가 아님
4. 무검열+프라이버시+오프라인 = 로컬의 존재 이유

26B MoE 로컬 실행 성공 (58 tok/s)
에이전트는 Claude가 답 이라는 걸 몸으로 배움
다음 목표: OpenClaw + API 키
