# DB Migrator with Spring Boot

Simple and Fast Mongo to Postgres Migration tool based on Spring-data projects.

**Just by register DB connection information to application.yml, adding Legacy DB Entities and Migration DB Entities, and enter localhost:8080, Migrator Automatically Migrate Legacy DB Data to Migration DB!** 

Entities are automatically Analyzed, Migrator create Virtual MongoRepository and JpaRepository, and register them to Spring IoC. Spring ODM and ORM read, convert, and save the Legacy Data to Migration DB by using those registered Repository interfaces.

It will be evolved into a General Purpose Migration Tool!
<br>

스프링부트 기반의 간단하고 빠른 MongoDB to Postgres 마이그레이션 툴입니다.

**DB 연결 값들을 application.yml에 등록, Legacy DB Entity와 Migration DB Entity를 추가하고, localhost:8080에 접속하면 자동으로 Migration 됩니다!**

Migrator는 자동으로 코드를 분석해 각각에 대한 Repository를 생성 및 스프링 IoC에 등록하며, 스프링 ORM과 ODM은 이렇게 생성된 Repository interface를 통해 데이터를 읽어와 변환하고, 저장해줍니다.

이를 점점 발전시켜 범용 마이그레이션 툴로 만드는 것이 목표입니다!

## Contributor

### 최영우 ([cyw320712](https://github.com/cyw320712))


## Usage

Usage is simple. Just register DB connection information to application.yml, and add entity information to domain/legacy and domain/migration respectively. However, you must follow the rules below for each. 

사용법은 간단합니다. 그냥 application.yml에 DB 연결 정보를 등록하고, domain/legacy와 domain/migration에 각각 entity 정보를 추가해주면 됩니다. 다만 각각에 대해서는 아래의 규칙을 따라야 합니다.

### 1. Register DB Connection information

src/main/resources/application.yml을 봅시다.

```yaml
spring:
  mongodb:
    host: localhost
    port: 27018
    base-package: fromdb
    username: root
    password: root
  postgres:
    host: localhost
    port: 5433
    dbname: todb
    dbschema: public
    username: root
    password: root
  jpa:
#    show-sql: true
    hibernate:
      ddl-auto: create
```

해당 yml 파일에서 mongodb는 legacy DB이고, postgres는 Migration DB입니다. 각각에 대해서 Connection 정보를 알맞게 기입해주면, DB Config 파일이 알아서 DB와의 연결 후 필요한 정보들을 bean에 등록합니다.

### 2. Entity Creation Rule

먼저 Legacy Entity와 Migration Entity를 생성하는 규칙을 알아봅시다. 일단 Legacy Entity와 Migration Entity를 제대로 만들기만 하면 Entity에 해당하는 Repository는 Migrator가 자동으로 생성해서 Bean에 등록하고, 사용하게 됩니다.

Repository 튜닝은 아직 지원하지 않습니다. 하지만 utils/EntityRepositoryFactoryPostProcessor를 분석해 ByteBuddy 부분을 수정하면 쉽게 method를 추가할 수 있습니다.
<br>
<br>

**Legacy Entity Creation Rule**
```java
...

@Getter
@Entity
@Document(collection = "User")
public class LegacyExample implements BaseLegacyEntity {

    @Id
    private String id;

    @Indexed
    private Long userId;

    private String email;

    private String name;

    private String type;

    private Date regDate;

    private int coin;

    @Override
    public Object convert() {
        MigrationUser migrationUser = new MigrationUser(userId, email, name, type, coin);

        return migrationUser;
    }
}

```

위 코드에서 볼 수 있듯, 모든 Legacy Entity는 BaseLegacyEntity의 구현체여야 하며, /domain/legacy 폴더에 위치해야 합니다.

또한, BaseLegacyEntity에 포함된 convert() 메서드를 반드시 구현해줘야 하며, 만약 해당 LegacyEntity에서 MigrationEntity로 수정할 때 바꾸고 싶은 값이 있다면 해당 메서드 내부에서 수정해주시면 됩니다.

주의할 점은 EntityManager에서 인식할 수 있도록 @Entity annotation을 반드시 붙여줘야한다는 것입니다. 이로 인해 Migration DB에 Legacy- Entity가 생기는 문제가 발생 중이며 수정 예정입니다.

ex)
```java
public class LegacyExample implements BaseLegacyEntity {

    ...

    @Override
    public Object convert() {
        String newName = "converted" + name;
        int newCoin = coin + 400; // migration으로 인한 서버 정지 보상
        MigrationUser migrationUser = new MigrationUser(userId, email, newName, type, newCoin);
        return migrationUser;
    }
}
```
<br>
<br>

**Migration Entity Creation Rule**
```java
...

@NoArgsConstructor
@Entity
@Table(name = "user", schema = "public")
public class MigrationExample extends BaseTimeEntity {

    @Id
    private Long userId;

    private String email;

    private String name;

    private String type;

    private int coin;

    @Builder
    public MigrationExample(Long userId, String email, String name, String type, int coin){
        super();

        this.userId = userId;
        this.email = email;
        this.name = name;
        this.type = type;
        this.coin = coin;
    }
}

```

Migration Entity는 BaseTimeEntity의 확장이어야 합니다. 또한 Migration Entity에는 반드시 생성자가 있어야 합니다. 생성자는 BaseTimeEntity를 포함해야 합니다. regDate와 modDate가 따로 없을 시 migration하는 날짜가 자동으로 저장되게 됩니다.

이렇게 migration하고자 하는 모든 Entity들에 대해서 legacy와 migration entity를 생성해줍니다. (당연히 사용하실 땐 LegacyExample과 MigrationExample은 삭제하셔야 합니다.)


### 3. DB Migrator 실행

SpringBoot 앱을 실행해 DB connection이 문제 없이 이루어졌음이 확인되고, Application도 문제 없이 실행된 것을 확인해줍니다. 정상적으로 실행됐다면, http://localhost:8080으로 접속해 자동으로 migration 되는 것을 기다려줍니다.

## Structure

Firstly, it's simple project structure would look like this:

먼저 간단한 프로젝트 구조는 다음과 같습니다:

java<br>
&nbsp;├─┬com.dbmigrator.DBMigrator. <br>
&nbsp;│&nbsp;&nbsp;&nbsp;├─┬config/ <br>
&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;├── LegacyDBConfig <br>
&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;└── MigrationDBConfig <br>
&nbsp;│&nbsp;&nbsp;&nbsp;├─┬controller/ <br>
&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;└── DefaultController <br>
&nbsp;│&nbsp;&nbsp;&nbsp;├─┬domain/ <br>
&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;├─┬ **common/**<br>
&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;├── BaseLegacyEntity <br>
&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;└── BaseTimeEntity <br>
&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;├─┬ **legacy/**<br>
&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;├── LegacyEntity1 <br>
&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;├── LegacyEntity2 <br>
&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;└── ...LegacyEntities <br>
&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;└─┬ **migration/** <br>
&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;├── MigrationEntity1 <br>
&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;├── MigrationEntity2 <br>
&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;└── ...MigrationEntities <br>
&nbsp;│&nbsp;&nbsp;&nbsp;├─┬service/ <br>
&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;├── Migrator <br>
&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;└── MigratorService <br>
&nbsp;│&nbsp;&nbsp;&nbsp;├─┬utils/ <br>
&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;└── EntityRepositoryFactoryPostProcessor <br>
&nbsp;│&nbsp;&nbsp;&nbsp;├──DbMigratorApplication <br>
&nbsp;│&nbsp;&nbsp;&nbsp;└──**InitDb (Testing DB Script)** <br>
resources<br>
&nbsp;└─**application.yml**<br>

Nextly, modify application.yml file according to Your Database setting. After that, **Add** the Entity objects of existing DB (Legacy DB) to the domain/legacy directory and **Add** the Entity objects of migration target DB (Migration DB) to the domain/migration DB directory.

`WARNING` : I recommend that connect Testdb first, then check that the connection and conversion has no problem using the testing db script before processing.

Activate InitDb scripts, it will create testUserEntity in LegacyDB. By using this, you can check whether each db connection is fine or not. Please check that porting test data is complete.

Below diagram describes entire process simply.

자신의 DB에 맞게 application.yml을 먼저 수정합니다. 이후 기존의 데이터가 있는 DB(Legacy DB)의 Entity 객체들을 Legacy 폴더에 추가하고, 데이터를 전송할 DB(Migration DB)의 Entity 객체를 Migration 폴더에 추가합니다.

`WARNING` : 먼저 Testdb와 연결한 후, testing db script를 활용해 연결이 완료됐음을 확인하고 진행하시는 것을 권장합니다.

InitDb 스크립트를 활용하면 LegacyDB에 TestUser를 생성합니다. 이후, legacyExample과 migrationExample을 활용해 migration db에 포팅된 데이터가 생성되는 것을 확인하실 수 있습니다. 테스트 데이터가 원활히 포팅되는 것을 먼저 확인하고 진행해주시기 바랍니다.

아래 그림은 전체 프로세스를 설명합니다.

![](/src/images/Whole%20diagram.png)