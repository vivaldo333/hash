Test project "hash" for the hashing and dehashing mobile

Шаги запуска системи сервера
1) скачать и запистить Cassandra 3.11.4 локально
2) создать keyspace "hash" и таблицу "mobile" в Cassandra, с помощью выполнения скрипта в resource/cassandra_schema.cql
3) использовать скомпилированный .jar файл проекта "hash" для запуска приложения

Описание API:
1) GET localhost:8080/mobile/{mobileNumber}
Request header должен включать "user" и "password"
Responce body будет включать текстовое значение hash-а даного моб. телефона
Пример,
Configurations: salt = "AAA111", algorithm = "SHA1"
GET http://localhost:8080/mobile/380000000858
Response: 3a3c4f0db74b775dc85b1eb4ca78bc0ffb9d1d94

2) GET localhost:8080/hash/{hash}
Request header должен включать пользователя: "user" и пароль: "password"
Responce body будет включать текстовое значение моб. телефона
Пример,
Configurations: salt = "AAA111", algorithm = "SHA1"
GET http://localhost:8080/hash/3a3c4f0db74b775dc85b1eb4ca78bc0ffb9d1d94
Response: 380000000858

Улучшение вашего решения:
1) перевести загрузку информации в БД на BATCH APPLY;
2) выгружать инфо с БД в псевдо-хэш-мапу с порционно (pagination);
3) сделать асинхронным процесс добавления нового номера телефона в БД и псевдо-хэш-мапу Map;
3) сконфигурировать параметры Cassandra (time-out и другое);
4) сконфигурировать akka http;
5) сконфигурировать JVM (java_opt);


Architecture:

incoming mobile:
-> generateHash
    -> check hash on existing in Map
        -> exists
            -> checking that values ara equals (incomeMobile == savedMobile)
                -> equals 	-> return hash
                -> not equals 	-> throw InconsistencyHashException
        -> not exists
            -> return hash
            -> put in Cassandra mobile
            -> get from Cassandra mobile
            -> put in Map hash and mobile


incoming hash:
-> check that hash exists in Map
	-> exists
		-> return mobile from Map
	-> not exists
		-> throw MobileNotFound