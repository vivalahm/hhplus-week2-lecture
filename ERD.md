## ERD

```mermaid
erDiagram
    USER {
        Long id PK
        String name
    }

    LECTURE {
        Long id PK
        String title
        Date openDate
        Integer maxAttendees
    }

    LECTURE_HISTORY {
        Long id PK
        Long userId FK
        Long lectureId FK
        Date applyDate
        Boolean isApplied
    }

    USER ||--o{ LECTURE_HISTORY : applies
    LECTURE ||--o{ LECTURE_HISTORY : includes
```
	1.	USER 엔터티
	•	id: 각 유저를 고유하게 식별할 수 있는 기본 키(Primary Key)입니다.
	•	name: 유저의 이름을 저장합니다.
	•	설계 당위성: USER 엔터티는 시스템을 사용하는 개인을 나타내며, 기본적인 유저 정보를 관리합니다. 기본 키는 유저를 고유하게 식별하기 위해 필요합니다.
	2.	LECTURE 엔터티
	•	id: 각 강의를 고유하게 식별할 수 있는 기본 키입니다.
	•	title: 강의의 제목을 저장합니다.
	•	openDate: 강의가 시작되는 날짜를 저장합니다.
	•	maxAttendees: 강의에 참가할 수 있는 최대 참가자 수를 나타냅니다.
	•	설계 당위성: LECTURE 엔터티는 제공되는 강의 정보를 관리합니다. 각 강의는 고유한 식별자, 제목, 시작 날짜 및 최대 참가자 수와 같은 중요한 속성을 가지고 있습니다. 이를 통해 강의의 기본 정보를 체계적으로 관리할 수 있습니다.
	3.	LECTURE_HISTORY 엔터티
	•	id: 각 강의 신청 내역을 고유하게 식별할 수 있는 기본 키입니다.
	•	userId: 신청한 유저를 참조하는 외래 키(Foreign Key)입니다.
	•	lectureId: 신청한 강의를 참조하는 외래 키입니다.
	•	applyDate: 유저가 강의에 신청한 날짜를 저장합니다.
	•	isApplied: 신청 상태를 나타내는 불리언 값입니다.
	•	설계 당위성: LECTURE_HISTORY 엔터티는 유저가 강의에 신청한 내역을 관리합니다. 외래 키를 통해 USER와 LECTURE 엔터티와의 관계를 맺어 다대다 관계를 해결합니다. 이 엔터티는 유저와 강의 간의 상호작용을 추적하며, 신청 날짜와 상태를 기록함으로써 시스템의 기능을 향상시킵니다.
	4.	엔터티 간의 관계
	•	USER와 LECTURE_HISTORY: 유저는 여러 강의에 신청할 수 있으며, 이는 일대다 관계로 나타납니다. 유저는 LECTURE_HISTORY 엔터티를 통해 강의에 신청합니다.
	•	LECTURE와 LECTURE_HISTORY: 하나의 강의는 여러 유저에 의해 신청될 수 있으며, 이는 일대다 관계로 나타납니다. 각 강의는 LECTURE_HISTORY 엔터티를 통해 유저와 연결됩니다.
	•	설계 당위성: 이 관계는 실제 유저와 강의 간의 상호작용을 정확하게 반영합니다. LECTURE_HISTORY 엔터티를 통해 유저와 강의 간의 다대다 관계를 효과적으로 관리할 수 있습니다. 이는 데이터의 중복을 최소화하고, 유저와 강의 간의 연결을 효율적으로 유지할 수 있게 합니다.

추가 고려 사항

	•	유효성 검사: 유저가 신청 가능한 최대 인원수를 초과하지 않도록 강의 신청 시 유효성 검사를 수행해야 합니다.
	•	상태 관리: isApplied 필드를 통해 신청 상태를 관리함으로써, 강의 신청/취소 등의 기능을 지원할 수 있습니다.
	•	성능 최적화: 외래 키 제약 조건을 통해 데이터의 일관성과 무결성을 유지하며, 인덱스를 활용하여 조회 성능을 최적화할 수 있습니다.