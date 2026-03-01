# 🧠 mATH+API  
### Multimodal AI-powered Math Problem Solving Platform  

> 덕성여자대학교 캡스톤 프로젝트  
> 한이음 ICT 공모전 장려상 후보작  

---

## 🚀 Project Overview

mATH+API는 멀티모달 AI 모델을 활용하여  
수학 문제 이미지와 사용자 풀이 과정을 동시에 인식하고  
단계별 풀이를 자동 생성 및 시각화하는 수학 자동 문제풀이 서비스입니다.

---

## 🖼 Service Preview

<img width="1206" height="764" alt="image" src="https://github.com/user-attachments/assets/8f9602ca-2dd6-4d91-8286-d3f02553aeee" /> 
<img width="882" height="782" alt="image" src="https://github.com/user-attachments/assets/59d51b65-bf27-4222-8f44-f986c9119ad3" />

---

## 🏗 Architecture

<p align="center">
<img width="1206" height="1027" alt="image" src="https://github.com/user-attachments/assets/adeeeeb8-e318-4d3f-b03e-7441af55ece5" />
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
