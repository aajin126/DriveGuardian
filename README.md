<a name="readme-top"></a>
<h1 align="center">Drive Guardian</h1> 

<h3 align="center">Monitoring for all drivers.</h3>

<p align="center">
  <img width="200" height="200" src="https://github.com/aajin126/DriveGuardian/assets/122766068/4a55ab06-4c46-43df-b0d5-e5962e4a708c" alt="App Logo">
</p>


## Overview
### Team Member
##### 이하진, 박지현, 이지원
### Primary Objective
##### 졸음 운전이나 운전 중 부주의에 취약한 화물차 운전자 및 택배 운전자를 대상으로 실시간 탐지 속도를 유지하면서도 높은 접근성을 가지도록 운전자 모니터링 서비스를 앱으로 제공하는 것.

## Installation

1. Clone the repository
    ```http
     https://github.com/aajin126/DriveGuardian.git
    ```
2. Android Studio에서 파일을 Open한다. 

3. Android device나 emulator를 사용하여 build 후 run한다. 

OR--
1. (Apk 주소)를 Android phone에 다운로드 한다. 
2. Remember to "Allowing app installs from Unknown Sources in Android"

## Features

- **Pose Detection AI Model:** 운전자의 자리 이탈과 전방 미주시를 감지하기 위해 사용한다. 
- **Object Detection AI Model:** 운전자 흡연과 하품을 감지하기 위해 yolov5로 training한 결과 모델을 사용한다. 
- **Landmark Detection AI Model:** 눈 깜박임과 눈 크기를 감지하기 위해 Landmark Detection 모델을 사용한다.
- **Visual Notifications:** 부주의나 졸린상태가 감지되면 시각적 알림을 화면에 팝업으로 띄운다. 
- **Voice Notifications:** 부주의나 졸린상태가 감지되면 각 상태에 맞는 알람을 음성으로 제시한다. 
- **Google Map:** Drive Guardian을 사용하면서 Google Map으로 길찾기 기능까지 제공한다.

#### Using Open Source : MLkit의 Pose Estimation, Landmark Detection을 Open Source로 사용하였고, yolov5 모델을 사용하였습니다. 

## Background
 <img width="900" height="400" src="https://github.com/aajin126/DriveGuardian/assets/122766068/e5a5629d-94b8-453e-aa5f-4c8ecfb3fc63" alt="Background">


