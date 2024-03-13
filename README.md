# REST api прокси

Тестовое задание для стажировки от VK. Реализация REST API проксирующего запросы к https://jsonplaceholder.typicode.com/.


## Функциональность

- [ ] Обработчики (GET, POST, PUT, DELETE) запросов к /posts, /users, /albums
- [ ] Аутентификация и авторизация пользователей с различными уровнями доступа
- [ ] Аудит действий клиента (в консоль и в базу данных)
- [ ] inmemory кэш для уменьшения числа запросов к https://jsonplaceholder.typicode.com/

## Технологии

- Spring Web
- Spring Boot
- Spring Data JPA
- Hibernate Validator
- Maven 
- PostgreSQL
- Lombok
- Guava

## Инструкции по запуску

1. Клонируйте репозиторий https://github.com/NadirCianid/vk_internship
2. Перейдите в директорию проекта
3. Установите зависимости, соберите и запустите проект с помощью Maven
4. Подключите базу данных, указав необходимые данные в src/main/resources/config/application.properties
5. Запустите файл src/main/java/com/nadir/vk_internship/VkInternshipApplication.java



## Использование

Для использования API выполните запросы к соответствующим эндпоинтам, используя HTTP методы (GET, POST, PUT, DELETE) и передавая необходимые параметры. Для доступа к эндпоинтам необходимо создать пользователя(й). Полномочия на это имеет только пользователь с ролью ROLE_ADMIN, поэтому его нужно создать вручную в базе данных (после запуска приложения).
   
Примеры: 
 - localhost:4500/api/auth/addUser?login=postsEditor&pswd=editor&roleId=6
 - localhost:4500/api/auth?login=postsEditor&pswd=editor
 - localhost:4500/api/users
 - localhost:4500/api/posts/100

## Автор

Автор: [Денис Романенко](https://github.com/NadirCianid)


