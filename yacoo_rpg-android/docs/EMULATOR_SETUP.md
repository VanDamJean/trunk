# Yacoo RPG — 에뮬레이터 설정 가이드

실기기(USB 연결) 없이 Android 에뮬레이터로 Yacoo RPG를 실행하고 테스트하는 절차를 다룹니다.

---

## 사전 요구항

| 도구 | 버전 |
|---|---|
| Android Studio | 최신 Stable (Meerkat 이상) |
| JDK | Android Studio 내장 JBR (21) |
| Android SDK | compileSdk 35, minSdk 26 |
| 디스크 여유 | AVD 이미지 포함 약 10GB |

터미널 빌드를 위해 `JAVA_HOME`을 Android Studio 내장 JBR로 설정:

```bash
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
export PATH="$JAVA_HOME/bin:$PATH"
```

Gradle wrapper 실행 권한 확인:

```bash
cd yacoo_rpg-android
chmod +x gradlew
```

`gradle.properties`에 JVM 힙이 이미 8192m로 설정되어 있어 OOM 이슈는 드뭅니다.

---

## 1. AVD 생성 절차

1. Android Studio → **Tools → Device Manager** (또는 환영 화면 우측의 ⋮ 메뉴).
2. **"Create Device"** 클릭.
3. **Hardware** 선택:
   - 권장: **Pixel 7** (또는 Pixel 6). 해상도 1080×2400, 6.3".
   - Play Store 포함 이미지를 고르면 앱 설치가 수월합니다.
4. **System Image** 선택:
   - 권장: **API 34 (Android 14), x86_64, API Level 34**.
   - API 35도 호환되지만 일부 플랫폼 버그 회피를 위해 34 추천.
   - `minSdk = 26`이므로 API 26+ 어느 것이든 동작하지만, 최신 API에서 Compose 안정성이 더 좋습니다.
   - 처음 선택 시 다운로드가 필요할 수 있음 (약 1.5GB).
5. **AVD Name** 입력 (예: `Pixel 7 API 34`), **Finish**.

> ARM 이미지(Apple Silicon)는 자동으로 arm64로 매핑되므로 x86_64와 혼용하지 마세요. Apple Silicon(M1/M2/M3)은 ARM 시스템 이미지를 권장합니다.

---

## 2. 빌드 및 설치

에뮬레이터가 켜진 상태에서:

```bash
# Debug APK만 빌드
./gradlew assembleDebug
# → app/build/outputs/apk/debug/app-debug.apk

# 에뮬에 자동 설치 (에뮬이 실행 중이어야 함)
./gradlew installDebug
```

또는 Android Studio의 **Run ▶** 버튼으로 빌드+설치+실행을 한 번에 처리할 수 있습니다.

설치 확인:

```bash
adb devices                                    # 에뮬 표시되어야 함
adb shell pm list packages | grep yacoo        # com.yacoo.rpg 표시
```

---

## 3. 에뮬레이터 실행 흐름

1. **Device Manager → AVD 우측 ▶** 클릭해 에뮬 부팅 (약 20-40초).
2. 앱이 자동 설치되지 않았다면 `./gradlew installDebug` 실행.
3. 에뮬 런처에서 **"Yacoo RPG"** 아이콘 탭 → 게임 시작.
4. 기본 흐름: Home → Start Combat → 전투 → Result → Claim Reward → ...

---

## 4. 빠른 피드백 루프 팁

### Apply Changes (리붓 없이 코드 변경 적용)
Android Studio에서 코드 수정 후 **Run** 대신 **Apply Changes ⚡** (또는 `Ctrl+Alt+F10` / `Cmd+Ctrl+R`) 사용하면 앱 재시작 없이 UI 변경 사항이 반영됩니다. Compose는 특히 이 방식에 최적화되어 있습니다.

### Compose `@Preview` 활용 (빌드/에뮬 불필요)
각 Screen에 `@Preview` 함수가 있으면 Android Studio 편집기 우측 패널에서 UI를 렌더링합니다. 에뮬 부팅이나 빌드 없이 레이아웃/컬러 검증 가능.

### Logcat으로 디버그
```bash
adb logcat -s "YacooRPG"        # 태그 필터
adb logcat | grep com.yacoo     # 패키지 필터
```
Android Studio의 **Logcat** 패널에서 `package:com.yacoo.rpg` 필터를 적용하면 같은 결과를 GUI로 볼 수 있습니다.

### 데이터 빠른 리셋
```bash
adb shell pm clear com.yacoo.rpg
```
DataStore에 저장된 진행 상황을 초기화합니다 (코인, 장비, 런 상태 전부 리셋).

---

## 5. 문제 해결 (Troubleshooting)

| 증상 | 원인 / 해결 |
|---|---|
| `No connected devices` | 에뮬이 실행 중인지 확인. `adb devices`로 목록 확인. 에뮬 재부팅. |
| 빌드 OOM (`java.lang.OutOfMemoryError`) | `gradle.properties`의 `org.gradle.jvmargs=-Xmx8192m` 확인 (기본값 설정됨). |
| `INSTALL_FAILED_OLDER_SDK` | 에뮬 API가 26 미만. API 26+ AVD로 새로 만들 것. |
| `INSTALL_FAILED_VERIFICATION_FAILURE` | APK 서명/검증 실패. 에뮬에서 "Install via USB" 또는 "Disable verify apps over USB" 옵션 확인. |
| DataStore 데이터가 리셋 안 됨 | `adb shell pm clear com.yacoo.rpg` 실행 또는 에뮬 설정 → Apps → Yacoo RPG → Storage → Clear Data. |
| 에뮬이 느림 | **Settings → Advanced**에서 **Hardware GLES** 활성화, RAM 2048MB 이상, VM Heap 512MB 이상. Cold boot 시도. |
| 진동이 안 울림 | 일부 에뮬은 `VIBRATE` 하드웨어를 미지원. 실기기에서만 확인 가능. 이슈 아님. |
| Compose UI가 깨져 보임 | 에뮬 Density/해상도가 기대와 다름. Pixel 7 (420dpi) 기준으로 디자인됨. |

---

## 6. 에뮬에서 테스트 가능한 것 / 불가능한 것

### 가능
- **전체 게임 플로우** — Home, Combat, Equipment, Upgrade, Result, Run Map, Reward Pick, Run Result.
- **DataStore 영속성** — 에뮬을 리붓해도 진행 상황 유지됨.
- **화면 회전 동작** — `AndroidManifest.xml`에서 `screenOrientation="portrait"`이라 회전 무시됨 (세로 고정).
- **Compose 애니메이션** — 주사위 굴리기, 몬스터 등장, HP 바 등.
- **백그라운드 전환** — Home 키로 나갔다 돌아와도 상태 보존.

### 불가능 / 제약
- **진동(Haptic)** — 일부 에뮬 하드웨어 미지원. `HapticManager`의 `safeVibrate`가 try/catch로 무시하므로 크래치는 안 남음.
- **실제 오디오(SoundPool)** — `SoundManager`가 현재 stub이라 어디서든 재생 안 됨.
- **푸시 알림 / 백그라운드 서비스** — 이 앱엔 해당 기능 없음.
- **카메라, 센서** — 사용 안 함.

---

## 7. CI 자동화 (선택)

에뮬을 헤드리스로 돌려 UI 테스트 자동화하려면:

```bash
# AVD 헤드리스 부팅
adb -d emulator -avd "Pixel_7_API_34" -no-window -no-audio -no-boot-anim &

# 부팅 대기
adb wait-for-device shell 'while [[ -z $(getprop sys.boot_completed) ]]; do sleep 1; done;'

# Compose UI 테스트 실행
./gradlew connectedAndroidTest
```

GitHub Actions에서는 `reactivecircus/android-emulator-runner` 액션으로 동일한 작업을 수행할 수 있습니다.

---

## 참고

- 빌드/테스트 절차 일반: [`docs/08-ANDROID-PORT.md`](./08-ANDROID-PORT.md)
- Compose UI 테스트 코드: `app/src/androidTest/java/com/yacoo/rpg/`
- 단위 테스트: `app/src/test/java/com/yacoo/rpg/game/`
