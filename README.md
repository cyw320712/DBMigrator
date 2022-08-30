# DB Migrator with Spring Boot

Simple and Fast Mongo to Postgres Migration tool based on Spring-data projects. Just by adding Entities for Legacy DB, Entities are automatically Analyzed, Converted and Saved through Spring ODM and ORM.

It will be evolved into a General Purpose Migration Tool!<br>
(Actually, migrations for Mongo To any RDBs can still be done with simple modification.)

스프링부트 기반의 간단하고 빠른 MongoDB to Postgres 마이그레이션 툴입니다. Legacy DB의 Entity와 Migration DB의 Entity를 추가해주면, 스프링 ORM과 ODM은 이를 자동으로 분석해서 변환하고, 저장해줍니다.

이를 점점 발전시켜 범용 마이그레이션 툴로 만드는 것이 목표입니다!
(사실, 지금도 Mongo To (JPA가 지원하는)대부분의 RDB로의 마이그레이션은 작은 수정으로만으로도 가능합니다!)

## Contributor

### 최영우 ([cyw320712](https://github.com/cyw320712))


## Usage

Firstly should understand the Simple Project Structure

먼저 간단한 파일 구조를 이해해야 합니다.

java<br>
&nbsp;├─┬com.dbmigrator.DBMigrator. <br>
&nbsp;│&nbsp;&nbsp;&nbsp;├─┬config/ <br>
&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;├── LegacyDBConfig <br>
&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;└── MigrationDBConfig <br>
&nbsp;│&nbsp;&nbsp;&nbsp;├─┬controller/ <br>
&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;└── DefaultController <br>
&nbsp;│&nbsp;&nbsp;&nbsp;├─┬domain/ <br>
&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;├─┬ **legacy/**<br>
&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;├── BaseLegacyEntity <br>
&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;├── LegacyEntity1 <br>
&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;├── LegacyEntity2 <br>
&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;└── ...LegacyEntities <br>
&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;└─┬ **migration/** <br>
&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;├── BaseTimeEntity <br>
&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;├── MigrationEntity1 <br>
&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;├── MigrationEntity2 <br>
&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;└── ...MigrationEntities <br>
&nbsp;│&nbsp;&nbsp;&nbsp;├─┬service/ <br>
&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;├── Migrator <br>
&nbsp;│&nbsp;&nbsp;&nbsp;│&nbsp;&nbsp;&nbsp;└── MigratorService <br>
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

