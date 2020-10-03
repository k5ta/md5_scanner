# MD5 Scanner

Программа должна рекурсивно просканировать директорию на предмет наличия файлов указанного расширения, посчитать md5 от содержимого файлов и сохранить эту информацию (путь к файлу, md5) в БД.

После начального сканирования, программа должна следить за добавлением новых файлов/директорий и пополнять БД информацией о новых файлах.

## Настройка

В файле *application.conf* указываются следующие параметры:
* *database.connection_url* - url для подключения к БД
* *scanner.directory* - путь к директории на ФС
* *scanner.extensions* - cписок расширений файлов
* *scanner.max_threads* - макс. количество потоков сканирования ФС
* *scanner.timeout* - задержка между проверкой новых файлов

## Сборка и запуск

Сборка:
> sbt assembly

Запуск:
> java -Dconfig.file=application.conf -Dlog4j.configurationFile=log4j2.xml -jar MD5Scanner-assembly-1.0.jar

## База данных

В качестве базы данных выбрана PostgreSQL.

Запросы:
* создание таблицы:
>CREATE TABLE public.processed_files
(
    path character varying COLLATE pg_catalog."default" NOT NULL,
    hash character varying COLLATE pg_catalog."default",
    "timestamp" bigint,
    CONSTRAINT pk PRIMARY KEY (path)
)

* листинг просканированных файлов в порядке их добавления на ФС:
> SELECT * FROM public.processed_files ORDER BY timestamp ASC 

* поиск по md5:
>SELECT * FROM public.processed_files WHERE hash = 'file_md5_hash'

* поиск по пути к файлу:
> SELECT * FROM public.processed_files WHERE path = 'path_to_file'

(в вышеприведенных запросах можно вместо * указывать через запятую только необходимые поля).

## TODO

Что хотелось бы улучшить (при наличии времени):
* Покрыть тестами (прямо must have, так как на данный момент все грустно)
* Потенциально улучшить лоигрование
* Возможно улучшить работу в многопоточном режиме