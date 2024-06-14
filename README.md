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

## About Source Code
#### Training 폴더의 python code
  ##### - 하품 탐지 yolov5 training 코드
  ##### - 흡연 탐지 yolov5 training 코드
##### 그 외 나머지 코드는 전부 Android Studio에서 가동되는 App 코드입니다. 

## How To Install & Build

1. Clone the repository
    ```http
     https://github.com/aajin126/DriveGuardian.git
    ```
2. Android Studio에서 파일을 Open한다. 

3. Android device나 emulator를 사용하여 build 후 run한다. 

OR--
1. (Apk 주소)를 Android phone에 다운로드 한다. 
2. Remember to "Allowing app installs from Unknown Sources in Android"

## How To Test

1. 앱을 실행하면 HOME화면으로 연결됩니다.
2. 하단의 버튼 바에서 카메라 모양 아이콘을 터치하면 운전자 모니터링을 할 수 있는 탭으로 이동합니다.
3. 모니터링 탭에서 하단의 Start 버튼을 누르면 운전자 모니터링이 시작됩니다.
4. 최대한 얼굴을 화면에 중앙에 놓은 후 주행을 시작합니다. 
5. 모니터링 중에는 여러 인공지능 모델이 운전자의 부주의와 졸음을 감지하고 알립니다.
6. 모니터링을 멈추고 싶다면 하단의 Stop 버튼을 누릅니다. 

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
 
## About Drive Guardian
<img width="900" height="400" src="https://github.com/aajin126/DriveGuardian/assets/122766068/d0341b25-a2ef-4cd9-96a8-f069be279e16" alt="DG">

### 부주의 상태
<img width="900" height="400" src="https://github.com/aajin126/DriveGuardian/assets/122766068/bd3e6237-7e49-407c-98f8-8a143d9051ca" alt="부주의1">
<img width="900" height="400" src="https://github.com/aajin126/DriveGuardian/assets/122766068/cdc12391-b858-4263-b834-aae35a879fad" alt="부주의2">
<img width="900" height="400" src="https://github.com/aajin126/DriveGuardian/assets/122766068/0c296a4c-f3b5-4c9b-9eff-1b34def41261" alt="부주의3">

### 졸린 상태
<img width="900" height="400" src="https://github.com/aajin126/DriveGuardian/assets/122766068/8c2654b5-2d46-481a-a2d3-9f076972354a" alt="졸린1">
<img width="900" height="400" src="https://github.com/aajin126/DriveGuardian/assets/122766068/e0cfadac-406e-48f5-85af-252aeae3cf3b" alt="졸린2">

### Alert 
<img width="900" height="450" src="https://github.com/aajin126/DriveGuardian/assets/122766068/9f00db37-0cc2-448d-873e-cd7f2b128343" alt="알람">

