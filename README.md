## 회고

### 1. Controller, Service, Repository는 각각 어떤 역할을 맡나요?

`Controller`는 요청과 응답을 담당하고, `Service`는 비즈니스 로직을 처리하며, `Repository`는 데이터베이스 접근을 담당합니다.

### 2. `@Service`를 붙이지 않으면 서버가 뜨지 않는 이유는 무엇인가요?

`@Service`는 해당 클래스를 Spring Bean으로 등록하는 역할을 합니다. 다른 Bean에서 해당 Service를 의존성 주입받고 있는데 `@Service`가 없다면 Spring Container에서 해당 객체를 찾을 수 없어 의존성 주입에 실패하고 애플리케이션 실행이 실패할 수 있습니다.

### 3. `@Transactional(readOnly = true)`는 무슨 뜻이며, 저장하는 메서드에 붙이면 왜 안 되나요?

`readOnly = true`는 조회 전용 트랜잭션임을 의미하며, JPA가 일부 변경 감지 작업을 줄이는 등의 최적화를 할 수 있습니다. 저장이나 수정이 필요한 메서드에서는 데이터 변경이 필요하므로 일반 `@Transactional`을 사용해야 합니다.

### 4. `@NotBlank`, `@NotNull`, `@NotEmpty`는 각각 어떤 값을 걸러내나요?

`@NotNull`은 `null`만 제한하고, `@NotEmpty`는 `null`과 빈 값을 제한합니다. `@NotBlank`는 문자열에 사용하며 `null`, 빈 문자열, 공백 문자열까지 제한합니다.

### 5. 엔티티를 그대로 응답하지 않고 DTO로 바꿔서 응답하는 이유는 무엇인가요?

Entity를 그대로 반환하면 DB 구조가 API에 노출되고, 비밀번호 같은 민감한 정보가 노출될 위험이 있습니다. 또한 Entity 변경이 API 스펙 변경으로 이어지는 강한 결합 문제가 있기 때문에 DTO를 이용해서 필요한 데이터만 전달합니다.

### 6. 이름 변경에서 `save()`를 호출하지 않았는데 DB에 반영되는 이유는 무엇인가요?

트랜잭션 안에서 Entity의 값이 변경되면 트랜잭션 종료 시점에 JPA가 변경 사항을 감지하는 더티 체킹을 수행하고 자동으로 UPDATE SQL을 실행하기 때문에 별도로 `save()`를 호출하지 않아도 됩니다.
