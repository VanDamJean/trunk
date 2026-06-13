# 오늘의 삽질 역사 (2026-04-14)

## 목표
Qwen3.5-397B-A17B 로컬에서 flash-moe로 빠르게 돌리기
(유튜브 "맥북에서 초대형 4000억 AI 돌려봤습니다" 영상 보고 따라하기)

---

## 시도 1: LM Studio 0.4.11
- IQ3_XXS GGUF (142GB) 받아놓은 상태
- Load 설정: GPU Offload, MoE CPU layers, Keep Model in Memory OFF 등 다 만져봄
- **결과: 실패** - "Not enough resources" 빨간 에러바가 절대 안 사라짐
- 이유: 메모리 추정 139GB > 64GB RAM, LM Studio가 로드 자체를 막음

## 시도 2: ollama
- `ollama create qwen397b -f ./Modelfile` → "invalid model name" (경로 문제)
- Modelfile 경로 수정 후 → "success" 떴지만
- `ollama run qwen397b` → `qwen3next: layer 0 missing attn_qkv/attn_gate projections`
- 0.20.5 → 0.20.6 업데이트 해도 동일 에러
- **결과: 실패** - ollama가 qwen35moe 아키텍처 미지원

## 시도 3: llama.cpp (brew install)
- `brew install llama.cpp`
- `llama-server -m [모델경로] --n-gpu-layers 0 -c 4096`
- `load_tensors: mmap = true` 뜨면서 드디어 로딩 성공!
- **속도: 1.36 t/s** (637토큰 = 8분 10초)
- GPU 레이어 올려봄 → 오히려 0.48 t/s로 더 느려짐
- 이유: M3 Max는 GPU/CPU 동일 메모리 공유, GPU 레이어가 mmap 캐시 공간을 빼앗음
- **결론: 돌아가긴 하나 실용 불가**

## 중간 발견: Flash-MoE
- 유튜브 댓글에서 `danveloper/flash-moe` GitHub 발굴
- 순수 C + Metal 셰이더로 만든 397B 전용 추론 엔진
- 내장 SSD 17.5 GB/s 활용, 4.36 t/s 달성
- 테스트 머신: M3 Max 48GB (우리랑 비슷한 스펙!)

## 시도 4: Flash-MoE 빌드
- `git clone https://github.com/danveloper/flash-moe`
- `cd metal_infer && make`
- **에러 1**: `main.m:263 MTLMathModeFast` SDK 버전 문제
- main.m 263번 줄 주석처리 → 또 에러
- **에러 2**: `infer.m:1023` 동일 에러
- infer.m 1023번 줄도 주석처리
- **빌드 성공!** (warnings만 남음)

## 시도 5: safetensors 다운로드 (대재앙의 시작)
- flash-moe는 GGUF 아님, safetensors 원본 필요
- `hf download Qwen/Qwen3.5-397B-A17B --local-dir ~/qwen397b-sf`
- 처음 예상: "209GB겠지" (flash-moe README의 최종 크기만 봄)
- 실제 진행: 68GB → 412GB → 550GB → 618GB → 687GB → 807GB...
- 속도는 215~500MB/s로 쾌적 (기가인터넷 병렬 다운 효과)
- SSD: 994GB 중 930GB 사용됨, 여유 64GB까지 줄어듦
- 중간에 LM Studio GGUF 142GB 삭제로 공간 확보
- 앱 115GB → 54GB로 정리
- **결국 포기**: safetensors 807GB + repack 출력 209GB = 1TB 초과 불가능

## 최종 확인
- 허깅페이스 공식: `totalFileSize: 806,796,544,792 bytes = 807GB`
- flash-moe README의 209GB는 4-bit repack 후 최종 크기였음
- 원본 BF16 safetensors가 807GB인 건 어디에도 안 써있었음

---

## 결론
- **M3 Max 64GB + 1TB SSD = 397B flash-moe 불가**
- 필요 스펙: 최소 2TB SSD (safetensors 807GB + packed 209GB + 여유)
- 실용 속도(4.36 t/s)는 클라우드 대비 여전히 느림
- **커서 + Claude Sonnet = 압도적 가성비**

## 교훈
1. flash-moe의 "209GB"는 처리 후 크기, 원본은 807GB
2. MoE 활성 파라미터(17B)와 저장 크기(807GB)는 완전히 다른 개념
3. 유튜브 테크 영상은 최적 조건만 보여줌
4. M3 Max 400GB/s 대역폭은 충분하나 SSD 용량이 병목
5. llama.cpp에서 GPU 레이어 올리면 unified memory에서 역효과 가능