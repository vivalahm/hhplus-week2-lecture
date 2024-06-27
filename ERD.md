## ERD

```mermaid
erDiagram
    LECTURE {
        Long id PK "기본키"
        String title "강의 제목, 비어있을 수 없음"
        LocalDateTime openDate "강의 시작 날짜 및 시간"
        Integer maxAttendees "최대 참석자 수, 최소 1"
    }
    LECTURE_HISTORY {
        Long id PK "기본키"
        Long user_id FK "외래키, User 엔티티 참조"
        Long lecture_id FK "외래키, Lecture 엔티티 참조"
        LocalDateTime applyDate "신청 날짜 및 시간"
        Boolean isApplied "신청 여부"
    }
    USER {
        Long id PK "기본키"
        String name "사용자 이름, 비어있을 수 없음"
    }

    LECTURE ||--o{ LECTURE_HISTORY : "lecture_id"
    USER ||--o{ LECTURE_HISTORY : "user_id"
```
	1.	USER 엔터티
		id: 각 유저를 고유하게 식별할 수 있는 기본 키(Primary Key)입니다.
		name: 유저의 이름을 저장합니다.
		설계 당위성: USER 엔터티는 시스템을 사용하는 개인을 나타내며, 기본적인 유저 정보를 관리합니다. 기본 키는 유저를 고유하게 식별하기 위해 필요합니다.
	2.	LECTURE 엔터티
		id: 각 강의를 고유하게 식별할 수 있는 기본 키입니다.
		title: 강의의 제목을 저장합니다.
		openDate: 강의가 시작되는 날짜를 저장합니다.
		maxAttendees: 강의에 참가할 수 있는 최대 참가자 수를 나타냅니다.
	3.	LECTURE_HISTORY 엔터티
		id: 각 강의 신청 내역을 고유하게 식별할 수 있는 기본 키입니다.
		userId: 신청한 유저를 참조하는 외래 키(Foreign Key)입니다.
		lectureId: 신청한 강의를 참조하는 외래 키입니다.
		applyDate: 유저가 강의에 신청한 날짜를 저장합니다.
		isApplied: 신청 상태를 나타내는 불리언 값입니다.
	4.	엔터티 간의 관계
		USER와 LECTURE_HISTORY: 유저는 여러 강의에 신청할 수 있으며, 이는 일대다 관계로 나타납니다. 유저는 LECTURE_HISTORY 엔터티를 통해 강의에 신청합니다.
		LECTURE와 LECTURE_HISTORY: 하나의 강의는 여러 유저에 의해 신청될 수 있으며, 이는 일대다 관계로 나타납니다. 각 강의는 LECTURE_HISTORY 엔터티를 통해 유저와 연결됩니다.
		설계 당위성: 이 관계는 실제 유저와 강의 간의 상호작용을 정확하게 반영합니다. LECTURE_HISTORY 엔터티를 통해 유저와 강의 간의 다대다 관계를 효과적으로 관리할 수 있습니다. 이는 데이터의 중복을 최소화하고, 유저와 강의 간의 연결을 효율적으로 유지할 수 있게 합니다.
