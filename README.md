# Kafka и AOP - мини-проект по реализации совместной работы брокера сообщений Kafka и аспектно-ориентированного программирования
[![Java](https://img.shields.io/badge/-Java-F29111?style=for-the-badge&logo=java&logoColor=e38873)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/-Spring%20Boot-6AAD3D?style=for-the-badge&logo=spring-boot&logoColor=90fd87)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/-Spring%20Security-6AAD3D?style=for-the-badge&logo=spring-security&logoColor=90fd87)](https://spring.io/projects/spring-security)
[![Hibernate](https://img.shields.io/badge/-Hibernate-B6A975?style=for-the-badge&logo=hibernate&logoColor=717c88)](https://hibernate.org/)
[![Postgresql](https://img.shields.io/badge/-postgresql%20-31648C?style=for-the-badge&logo=postgresql&logoColor=FFFFFF)](https://www.postgresql.org/)
[![Liquibase](https://img.shields.io/badge/Liquibase-2a62ff?style=for-the-badge&logo=liquibase&logoColor=white)](https://www.liquibase.com/)
[![Kafka](https://img.shields.io/badge/Kafka-231F20?style=for-the-badge&logo=apache-kafka&logoColor=white)](https://kafka.apache.org/)
[![Maven](https://img.shields.io/badge/-Maven-7D2675?style=for-the-badge&logo=apache&logoColor=e38873)](https://maven.apache.org/)
[![Git](https://img.shields.io/badge/git-%23F05033.svg?style=for-the-badge&logo=git&logoColor=white)](https://git-scm.com/)
[![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)
[![MapStruct](https://img.shields.io/badge/MapStruct-d23120?style=for-the-badge&logo=&logoColor=white)](https://mapstruct.org/)

## Архитектура
Монолит. Kafka и PostgreSQL запускаются в отдельных docker-контейнерах

## Функциональность
| **Эндпоинт** | **Описание** |
| - | - |
|POST http://localhost:8080/api/auth/signup|регистрация пользователя|
|POST http://localhost:8080/api/auth/signin|аутентификация пользователя и получения access-токена|
|POST http://localhost:8080/client/register|регистрация клиента|
|GET http://localhost:8080/parse|создание клиентов из файла MOCK_DATA.json. Клиенты записываются продьюсером KafkaClientProducer в топик client_registration. Оттуда читаются консьюмером KafkaClientConsumer и записываются в БД в таблицу client. Id клиентов отправляются продьюсером KafkaClientProducer в топик client_registered|
|GET http://localhost:8080/loadAccounts|создание аккаунтов клиентов из файла MOCK_ACCOUNT_DATA.json. Аккаунты записываются в топик t1_demo_accounts продьюсером KafkaAccountProducer. Консьюмер KafkaAccountConsumer читает их из топика, записывает аккаунты в БД в таблицу account. Id аккаунтов отправляются в топик t1_demo_accounts_registered с помощью продьюсера KafkaAccountProducer|
|POST http://localhost:8080/account/register|создание аккаунта|
|GET http://localhost:8080/account/{accountId}|получение аккаунта по id|
|PUT http://localhost:8080/account/block-debit/{accountId}|блокировка дебитового аккаунта (счёта) по его id|
|GET http://localhost:8080/loadTransactions|создание транзакций из файла MOCK_TRANSACTION_DATA.json. Транзакции пишутся в топик t1_demo_client_transactions продьюсером KafkaTransactionProducer. Оттуда читаются консьюмером KafkaTransactionConsumer и транзакции и изменение баланса аккаунтов записываются в БД в таблицы transaction и account соответственно. Id успешно проведенных транзакций записываются в топик t1_demo_transactions_registered, а неуспешных транзакций в топик t1_demo_client_transaction_error продьюсером KafkaTransactionProducer. Из топика t1_demo_client_transaction_error консьюмер KafkaTransactionConsumer считывает id неуспешных транзакций и удаляет их из БД и откатывает обратно изменение баланса аккаунта.|
|POST http://localhost:8080/transaction/add|добавление транзакции|
|GET http://localhost:8080/transaction/{transactionId}|получение транзакции по id|

Для методов, помеченных в коде аннотацией @Metric, срабатывает аспект MetricAspect. Он оценивает длительность работы метода, помеченного аннотацией @Metric и в случае превышения порогового значения информация о методе (его название, параметры и длительность выполнения) записываются в топик t1_demo_metric_trace продьюсером KafkaMetricProducer

## Диаграммы базы данных
![схема БД](https://github.com/user-attachments/assets/be97952e-5075-4852-bb83-a7abe133f654)

## Как запустить и использовать
Для запуска установите и откройте программу [Docker Desktop](https://www.docker.com/products/docker-desktop/). Затем в командной строке cmd выполните следующие команды

   ```
git clone https://github.com/OsipovKonstantin/homework_kafka_aop.git
   ```
в командной строке перейдите в корень проекта. Далее поднимите докер-контейнеры Kafka и PostgreSQL с помощью команды
   ```
docker-compose up
   ```
В Intellij IDEA запустите приложение. Приложение готово к использованию! 

Оценить работу эндпоинтов можете через Insomnia или Postman. 

Сохранение данных в БД - через Intellij Idea, pgAdmin или dBeaver.

Работу продьюсеров и консьюмеров Kafka - через ПО Offset Explorer и отчасти через набор плагинов Big Data Tools + Big Data Tools Core + Kafka.
