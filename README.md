## 🚀 커밋 규칙 (Commit Convention)

| **커밋 유형**      | **설명**                                                  |
|-------------------|---------------------------------------------------------|
| `feat`            | 새로운 기능 추가                                          |
| `fix`             | 버그 수정                                                 |
| `docs`            | 문서 수정                                                 |
| `style`           | 코드 formatting, 세미콜론 누락, 코드 자체의 변경이 없는 경우 |
| `refactor`        | 코드 리팩토링                                             |
| `test`            | 테스트 코드, 리팩토링 테스트 코드 추가                    |
| `chore`           | 패키지 매니저 수정, 그 외 기타 수정 ex) .gitignore         |
| `design`          | CSS 등 사용자 UI 디자인 변경                              |
| `comment`         | 필요한 주석 추가 및 변경                                   |
| `rename`          | 파일 또는 폴더 명을 수정하거나 옮기는 작업만인 경우       |
| `remove`          | 파일을 삭제하는 작업만 수행한 경우                        |
| `!BREAKING CHANGE`| 커다란 API 변경의 경우                                    |
| `!HOTFIX`         | 급하게 치명적인 버그를 고쳐야 하는 경우                   |
<P>
  ## Related issue 🛠
closed #<issue_number>
fixed #<issue_number>
resolved #<issue_number>## 작업 내용 ✏️
- 작업 내용을 간단히 작성해주세요.

## 변경 사항 📋
- [ ] 🐞 BugFix Something isn't working
- [ ] 💻 CrossBrowsing Browser compatibility
- [ ] 🌏 Deploy Deploy
- [ ] 🎨 Design Markup & styling
- [ ] 📃 Docs Documentation writing and editing (README.md, etc.)
- [ ] ✨ Feature Feature
- [ ] 🔨 Refactor Code refactoring
- [ ] ⚙️ Setting Development environment setup
- [ ] ✅ Test Test related (storybook, jest, etc.)

## 체크리스트 ✅
- [ ] PR 컨벤션에 맞게 작성했습니다.
- [ ] Commit 메시지 컨벤션을 지켰습니다.
- [ ] Issue 번호에 맞는 브랜치를 생성했습니다.
- [ ] Issue 컨벤션에 맞게 Issue를 생성했습니다.

## 🧩 기능 구현 및 역할 분담 (최종 수정: 2025.06.06)

| 기능/작업 내용 | 작업 유형 | 담당자 |
|----------------|-----------|--------|
| 프로젝트 전체 ERD 설계 | 구조 설계 | `수정` |
| 프로젝트 초기 세팅 (의존성, 기본 구조, 환경 설정 등) | 초기 환경 구성 | `재윤` |
| AWS S3 연동 및 Presigned URL 발급 | API 연동 (POST & PUT) | `재윤` |
| Qwen-VL-Max API 연동 및 이미지 URL → 텍스트 변환 | AI 모델 연동 (POST) | `유리` |
| Presigned URL 접근 제약을 해소하여 Qwen-VL 연동이 가능하도록 처리하는 Relay API 설계 및 구현 | API 설계 및 연동 (GET/POST) | `재윤` |
| Qwen-VL-Max → Qwen-2.5 VL 7B 모델 교체 및 재연동 | 모델 전환/재연동 (PATCH) | `재윤` |
| Qwen-2.5 VL 7B 모델 출력값 → Deepseek 입력 후 결과 반환 | AI 연동 로직 구현 (POST) | `수정` |
| Docker 이미지 제작 및 Deepseek 모델 배포 | 인프라 배포 (Docker) | `유리` |
| GPU 리소스 확장 및 성능 최적화 후 재배포 | 인프라 최적화 | `유리` |
| Deepseek 결과 → OpenAI API 후처리 (step-by-step 가공) | 결과 가공 (POST) | `재윤` |
| GPT가 반환한 최종 결과를 프론트엔드에 전달하는 API 연동 | 최종 API 연동 | `유리` |
| 불필요한 로직 제거 및 패키지 재편성 포함 전면 리팩토링 | 구조 리팩토링 | `유리` |
| 웹 서버 배포 | 서버 운영 | `수정` |
