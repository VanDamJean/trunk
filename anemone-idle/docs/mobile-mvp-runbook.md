# Capacitor 모바일 MVP 실행 가이드

이 문서는 현재 Vite 웹앱을 그대로 `Capacitor` 껍데기에 싣는 Android+iOS MVP용 실행 가이드입니다. 게임 UI/로직을 네이티브로 다시 만들지 않고, `dist/` 빌드 결과를 네이티브 프로젝트에 동기화하는 방식입니다.

## 현재 설정

- 앱 ID: `com.bloopoffice.anemoneidle`
- 앱 이름: `Anemone Idle Bloop Office`
- 웹 산출물: `dist/`
- Capacitor 설정 파일: `capacitor.config.json`
- Android 프로젝트: `android/`
- iOS 프로젝트: `ios/`가 있어야 하지만, 현재 로컬 환경은 CocoaPods 부재로 생성이 막혀 있습니다.

## 사전 준비

공통:

```bash
node --version
npm --version
npm install
```

Android:

```bash
java -version
./android/gradlew --version
```

Android Studio와 Android SDK가 필요합니다. `java`가 없으면 Gradle sync/build가 막힙니다.

iOS:

```bash
xcodebuild -version
pod --version
```

Xcode가 필요하고, 이 프로젝트의 현재 Capacitor iOS 생성 단계는 `pod` 명령도 요구합니다. `pod --version`이 실패하면 `npx cap add ios`가 `ios/` 생성 전에 중단됩니다.

## 웹 개발/검증

```bash
npm run dev
npm test
npm run build
node scripts/validate-progression-content.mjs
node scripts/balance-report.mjs --scenario chapter1-active
```

현재 기준 `balance-report`는 모바일 작업 전부터 실패하는 기존 밸런스 이슈가 있습니다. 모바일 MVP 작업에서는 게임 밸런스/콘텐츠를 고치지 않고, 이 실패를 별도 증거로 남깁니다.

## Capacitor 동기화

웹 파일을 바꾼 뒤 네이티브 프로젝트에 반영하려면 다음을 실행합니다.

```bash
npm run mobile:sync
```

이 스크립트는 내부적으로 `npm run build && cap sync`를 실행합니다.

## Android 실행 흐름

Android 프로젝트가 없을 때만 1회 생성합니다.

```bash
npm run build
npx cap add android
npm run mobile:sync
```

Android Studio로 열기:

```bash
npm run mobile:open:android
```

CLI로 실행하기:

```bash
npm run mobile:android
```

로컬 JDK/Android SDK가 없으면 `android/` 생성은 될 수 있지만 Gradle sync 또는 debug build가 실패합니다. 그 경우 `.omo/evidence/mobile-android-sdk-blocker.txt`에 어떤 명령이 없는지 남깁니다.

## iOS 실행 흐름

iOS 프로젝트가 없을 때만 1회 생성합니다.

```bash
npm run build
npx cap add ios
npm run mobile:sync
```

Xcode로 열기:

```bash
npm run mobile:open:ios
```

CLI로 실행하기:

```bash
npm run mobile:ios
```

현재 로컬 환경에서는 `pod` 명령이 없어 `npx cap add ios`가 실패합니다. 이 상태는 `.omo/evidence/mobile-ios-cocoapods-blocker.txt`에 기록되어 있으며, CocoaPods 설치 뒤 다시 `npx cap add ios`를 실행해야 합니다.

## 저장 방식과 MVP 리스크

이번 MVP에서는 기존 `localStorage` 저장을 유지합니다.

- 저장 키: `anemone-idle-save-v1`
- 브라우저/웹뷰 저장소를 지우면 세이브도 삭제됩니다.
- 앱 삭제 후 재설치하면 세이브가 남지 않는다고 봐야 합니다.
- 모바일 WebView는 저장소 압박이나 OS 정책에 따라 `localStorage`를 정리할 수 있습니다.
- 더 안정적인 네이티브 키-값 저장은 향후 `@capacitor/preferences` 이전으로 검토합니다.

MVP 범위에서는 `@capacitor/preferences`를 설치하지 않습니다.

## MVP에서 제외한 것

이번 작업은 앱 껍데기와 모바일 검증 루프까지입니다. 다음 항목은 포함하지 않습니다.

- React Native, Flutter, Kotlin UI, SwiftUI 재작성
- 스토어 출시, 서명, provisioning, TestFlight, Play Console
- AdMob 실광고/분석/푸시 알림
- 백엔드, 로그인, 클라우드 세이브
- `@capacitor/preferences` 저장소 마이그레이션
- 게임 밸런스/콘텐츠 수정

## 문제 해결

`android/` 또는 `ios/`가 이미 있으면 `npx cap add`를 다시 실행하지 말고 먼저 폴더 상태를 확인합니다. 덮어쓰기보다 `npm run mobile:sync`를 우선 사용합니다.

`npx cap add ios`가 CocoaPods 오류로 실패하면 다음을 확인합니다.

```bash
pod --version
```

명령이 없으면 CocoaPods를 설치한 뒤 다시 iOS 생성을 시도합니다.

`npm run mobile:sync` 전에 항상 `npm run build`가 성공해야 합니다. 오래된 `dist/`가 네이티브 앱에 들어가지 않도록 sync 스크립트는 빌드를 먼저 실행합니다.

## 증거 파일

모바일 작업의 검증 결과는 `.omo/evidence/`에 남깁니다.

- `task-1-*`: 변경 전 기준선
- `task-2-*`: iOS 패키지와 모바일 스크립트
- `task-3-*`: Android/iOS 플랫폼 생성과 sync
- `task-4-*`: 모바일 레이아웃 빌드/뷰포트 QA
- `task-5-*`: localStorage MVP 저장 검증
- `task-6-*`: 이 문서의 명령어/범위 검증
- `task-7-*`: 네이티브 build/open 환경 검증
- `task-8-*`: 최종 회귀와 모바일 smoke
