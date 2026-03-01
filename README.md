# 🧠 mATH+API  
### Multimodal AI-powered Math Problem Solving Platform  

> 덕성여자대학교 캡스톤 프로젝트  
> 한이음 ICT 공모전 장려상
<img width="1206" height="764" alt="image" src="https://github.com/user-attachments/assets/ba23505d-064b-45aa-af64-4165f25d960c" />
---

## 🚀 Project Overview

mATH+API는 멀티모달 AI 모델을 활용하여  
수학 문제 이미지와 사용자 풀이 과정을 동시에 인식하고  
단계별 풀이를 자동 생성 및 시각화하는 수학 자동 문제풀이 서비스입니다.

---
## 🏗 Architecture

<p align="center">
  <img width="1141" height="924" alt="image" src="https://github.com/user-attachments/assets/46ccf84b-0cd2-4303-89c4-0268a352060a" />
</p>

---

## 🧩 Core Features

- 📌 문제 이미지 + 사용자 풀이 동시 입력 처리  
- 📌 Qwen-2.5 VL 7B 기반 이미지 텍스트 변환  
- 📌 Deepseek 기반 단계별 수학 추론 처리  
- 📌 GPT 후처리를 통한 풀이 정제  
- 📌 LaTeX → SVG 수식 시각화 렌더링  
- 📌 AWS S3 Presigned URL 기반 안전한 이미지 업로드  
- 📌 Relay API를 통한 모델 연동 안정화  
- 📌 카카오 소셜 로그인 지원  
- 📌 오답노트 조회 및 PDF 추출 기능

---

### 📊 Benchmark Performance
성능 지표(Evaluation Results)에 따르면, 본 프로젝트에서 채택한 **DeepSeek-R1-Distill-Qwen-1.5B** 모델은 경량 모델임에도 불구하고 강력한 수학 추론 능력을 보여줍니다.

<img width="1206" height="986" alt="image" src="https://github.com/user-attachments/assets/8b9943ad-2894-4507-9c76-46a3e569d31a" />
<img width="1049" height="847" alt="image" src="https://github.com/user-attachments/assets/9bd462f5-69ee-42bd-bb04-32ad4919bc3c" />

> **Key Insight:** Distillation 기법을 통해 1.5B 규모의 소형 모델로도 대형 상용 모델 수준의 수학적 추론 성능을 확보하여, 빠른 응답 속도와 정확도를 동시에 달성했습니다.

---

### 🛠 Prompting Strategy
안정적인 단계별 풀이 제공을 위해 다음과 같은 전략을 사용합니다.

* **Hierarchical Prompt Design:** 페르소나, 핵심 지시사항, 출력 규칙을 계층적으로 분리하여 모델의 역할(Math Reasoning Assistant)을 명확히 정의합니다.
* **Structured Output (JSON):** 추론 결과를 `step n`, `answer`, `type`, `next_step` 등의 키를 가진 JSON 객체 배열로 강제하여, 프론트엔드에서 단계별 시각화가 용이하도록 설계했습니다.
* **CoT (Chain-of-Thought):** 모델이 스스로 논리적 일관성을 유지하며 단계별 사고 과정을 전개하도록 유도하여 오답률을 최소화합니다.

---

## 🛠 Tech Stack

### Backend
`Spring Boot` `AWS EC2` `AWS S3` `MySQL` `JWT`

### AI Pipeline
`Qwen-2.5 VL 7B` `Deepseek` `OpenAI API`

### DevOps
`Docker` `GitHub Actions` `CI/CD`

### External Services
`Cloudinary` `Kakao OAuth`

---

## 🎯 Project Goals

- 멀티모달 AI 기반 수학 자동 풀이 시스템 구현  
- 학습자의 풀이 과정을 반영한 단계별 추론 제공  
- 수식 시각화를 통한 직관적 학습 지원  
- 확장 가능한 AI API 기반 구조 설계  
