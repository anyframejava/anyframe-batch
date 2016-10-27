Anyframe Batch
===
Anyframe Batch는 대용량 배치업무 처리를 위한 실행 엔진 및 통합 관제환경을 제공하는 Java 기술 기반의 프레임워크로 배치 어플리케이션 실행 엔진, 배치 어플리케이션 개발 환경, 배치 어플리케이션 통제 Agent 및 관리환경까지 엔터프라이즈 시스템에서 배치 업무처리에 필요한 모든 환경을 제공하여, 배치 운영 관리 수준을 향상시킬 수 있는 기반을 제공한다.

## 특징
#### Batch 실행 프레임워크
* Java 기반의 복잡한 배치 실행을 표준화된 구조로 효율적으로 개발할 수 있게 하는 배치 어플리케이션 실행 기반
* XML 기반의 선언적인 작업 정의
* 다양한 입출력 데이터 및 포맷에 맞는 Reader/Writer 제공
* SAM/DB 데이터 매핑 자동화
* 외부 툴 (Sort, Unload) 및 코볼, C 언어 기반 배치 어플리케이션의 유연한 연계
* 고성능 파일/DB 처리 엔진

#### Batch 운영 프레임워크
* 원격 서버의 작업 실행 및 제어
* 수행 중인 작업의 실시간 모니터링
* 배치작업에서 사용하는 리소스(File/DB) 내용 조회
* 배치 작업의 수행 로그 및 에러로그 조회
* 작업 실행 정책 및 용량 제한
* Agent 이중화를 통한 Fail-Over
* Clustering 아키텍처


## 주요 기능
#### Batch 실행 Framework
대량데이터의 일괄처리를 위한 Java기반의 표준화된 고성능 실행구조 제공
Batch 실행 프레임워크는 대량 데이터의 일괄처리를 위한 Java 기반의 표준화된 실행 구조를 제공한다.

Batch 실행 프레임워크 특징
* XML 기반의 선언적 Batch Job 정의
* DB, File(SAM, VSAM )등 다양한 Resource에 대하여 추상화된 동일한 IO 제공
* Multi-Thread 기반 동시 작업 처리 및 배치 특화된 데이터/파일 처리
* Java, Parallel, Shell 등 다양한 유형의 Step 처리 지원
* 고정길이, 가변길이, Encoding 변환 등 다양한 유형의 파일 처리
* Shell 기반의 Sort , Unload, FTP 등의 다양한 3rd Party 툴 통합 지원
* Shell 변수, Step 변수를 통한 동적 Resource 할당
* 멀티 서버에서 동기화된 Resource File Locking(동시 파일 접근 방지)
* 사용자 Profile 기반의 개별 권한 관리 (Unix 환경과 통합)


Batch 실행 환경은 관리 제어에서 실행에 필요한 리소스까지의 각 영역이 5개의 Layer로 구분되어 있으며, 이들 레이어는 각기 배치관리, 배치 에이전트, Job 제어, 스텝 실행, 자원의 역할로 구분되어 실행된다.

#### Batch 운영 Framework
효율적인 작업관리와 장애 및 에러발생 시 신속한 대응이 가능한 통합관제 환경
Batch Agent와 Batch Manager로 구성되는 Batch 운영 프레임워크는 주야간의 다량의 배치 작업을 효율적으로 관리하고, 장애 및 에러 발생시의 신속한 대응을 가능하게 하는 통합 관제 환경이다.

* Batch Agent: 원격 서버에서 배치 작업의 구동과 제어, 상태 모니터링을 수행하는 어플리케이션입니다. Resource 동기화를 보장하기 위한 Resource Locking과 서버의 효율성을 고려한 작업 실행 정책을 지원한다.
* Batch Manager: 이클립스 플러그인 형태로 제공되는 배치 개발 지원 및 관리 지원 도구로 배치작업의 원격 실행/제어, 실시간 결과 확인이 가능하다.

Batch Agent와 Batch Manager의 기능 및 주요 특징

##### 운영관리
* Batch Manager(UI)를 통해 원격 서버의 작업 실행제어(Start/Stop)
* Batch Monitor(UI)를 통해 실시간 작업 진행상태 조회(처리 건수, Cpu/Memory 사용량 등)
* Job의 상세 정보(XML) 내용 조회
* Resource Viewer를 통한 파일 내용 조회
* Log Viewer를 통한 실행로그 및 에러로그 조회

##### 안정성
* Resource Locking을 통한 작업 간 결함 방지
* 서버 사양을 고려한 최대 작업 실행 수 제한
* 작업 별 최대 메모리 사용량 제한
* 최대 파일 크기 제한으로 Disk Full 장애 방지
* 시스템 관리를 위한 작업 실행 Blocking
* 도메인 유형별 (예:개발/검증/운영)로 구분하여 Job 수행, 정지 권한설정 지원

##### 고가용성
* 24*365일 무정지 시스템
* Dual Agent 기반의 Fail-over
* Batch Agent 최대 용량 초과 시 자동 Load Balancing
* Light Weight한 Agent 아키텍쳐


## 설치 환경

Anyframe Batch은 아래와 같은 환경에서 동작 가능하도록 개발되었다.

* JDK 1.6 이상
* Batch Manager : Eclipse 3.7 (Indigo)
* Batch Agent : Unix/Linux 환경


## 리소스

Anyframe Batch에서 사용하고 있는 주요 오픈소스는 다음과 같다.
* <a href="http://hessian.caucho.com/">Hessian Binary Web Service Protocol</a> 
* <a href="http://www.jgroups.org/index.html/">JGroups - A Toolkit for Reliable Multicast Communication</a> 
* <a href="https://github.com/AS3Commons/as3commons-lang/">as3commons-lang</a> 
* <a href="http://code.google.com/p/cglib-wrappers/">cglib-wrappers</a> 
* <a href="http://support.hyperic.com/display/SIGAR/Home">Hyperic SIGAR</a> 
* <a href="http://sourceforge.net/projects/p6spy/">p6spy</a> 
* <a href="http://poi.apache.org/">Apache POI</a> 
* <a href="http://wiki.eclipse.org/index.php/Rich_Client_Platform">Eclipse Rich Client Platform (RCP)</a> 
* <a href="http://sourceforge.net/projects/fnr/">Feed'n Read</a> 
* <a href="http://asm.objectweb.org">AspectJ</a> 
* <a href="http://poi.apache.org/">ASM 3.3</a> 
* <a href="http://www.codehaus.org/">Codehaus - Sonar</a> 

