Anyframe Batch, release 1.0.0 (2012.11)
------------------------------------------------
http://www.anyframejava.org

Copyright 2010-2012 Samsung SDS Co., Ltd.

1. Anyframe Batch 소개

대용량 배치업무 처리를 위한 실행 엔진 및 통합 관제환경을 제공하는 프레임워크

- Anyframe Batch는 배치 어플리케이션 실행 엔진, 배치 어플리케이션 실행 통제 Agent, 배치 어플리케이션 실행 관리환경까지 엔터프라이즈 시스템에서 배치 업무처리에 필요한 모든 환경을 제공하여, 배치 운영 관리 수준을 향상시킬 수 있는 기반을 제공하며, 크게 실행 프레임워크와 운영 프레임워크로 구성됩니다. 
- Anyframe Batch 실행 프레임워크는 Java 기반의 Batch 실행을 담당하는 런타임 모듈로 배치 실행 엔진입니다.
- Anyframe Batch 운영 프레임워크는 Batch Agent와 Batch Manager로 구성됩니다. Batch Agent는 배치 작업을 통제하는 주체로 다수의 배치업무를 중앙 집중적으로 조회 & 통제합니다. Batch Manager는 배치 개발 및 관리를 지원하기 위해 Batch Agent를 구동시키는 이클립스 플러그인 형태의 도구로써 배치작업의 원격 실행, 검증, 결과 확인 및 제어 기능 등을 제공합니다. 

* Anyframe 포탈 사이트 : http://www.anyframejava.org


2. 제공 압축파일의 구조
- bin  
      - agent : 배치 어플리케이션 실행 통제 Agent
          - anyframe-batch-agent-1.0.0.war : 배치작업 수행,정지,모니터링을 위한 웹어플리케이션
          - config : 배치 에이전트가 동작하기 위한 설정파일
      - runtime : 배치 어플리케이션 실행 엔진      
          - anyframe-batch-agent-interfaces-1.0.0.jar : 실행 엔진과 통제 Agent의 인터페이스 라이브러리
          - anyframe-batch-runtime-1.0.0.jar : 배치 어플리케이션이 구동되기 위한 런타임 라이브러리
          - lib : 3rd party 라이브러리 파일
          - config : Anyframe Batch 프레임워크 기반 어플리케이션이 동작하기 위한 설정 파일
      - manager : 배치 어플리케이션 실행 관리 환경
          - anyframe-batch-manager-1.0.0.zip  : Batch Manager 이클립스 플러그인 설치 파일 (이클립스에서 Local archive 형태로 설치)
          - templates : Batch Manager에서 관리할 대상서버 정보에 대한 server_list.xml 파일을 포함.
      - install.txt : 배치 프레임워크의 각 모듈들의 설치 방법을 기술한 파일 
        
- source 

      - agent : 배치 어플리케이션 실행 통제 Agent 소스코드
          - anyframe-batch-agent-1.0.0-sources.jar
      - runtime : 배치 어플리케이션 실행 엔진 소스코드
          - anyframe-batch-agent-interfaces-1.0.0-sources.jar
          - anyframe-batch-runtime-1.0.0-sources.jar
      - manager : 배치 어플리케이션 실행 관리 환경 소스코드
          - anyframe-batch-manager-1.0.0-sources.jar
      - example : 배치 어플리케이션 샘플 소스코드         
          - anyframe-batch-sample-1.0.0.zip
        
- licenses 
      - LicenseList.txt : 사용하는 licenses를 제공 war 또는 jar 별로 정리한 텍스트파일
      - 사용하는 licenses의 구문 텍스트파일

- changelog.txt : Anyframe Batch 프레임워크의 변경이력
- readme.txt : 배포 파일 구성요소에 대한 설명
- license.txt : Apache License 파일


3. 라이센스 정책

Anyframe Batch 는 라이센스 정책으로 Apache Licence, Version 2.0 (http://www.apache.org)을 채택한다. 단, Anyframe 내에서 사용된 외부 오픈 소스의 경우 원 오픈 소스의 라이센스 정책을 따른다.
