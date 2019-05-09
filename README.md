# Implario
Модифицируемое ядро для игры Minecraft.
В отличие от Forge, Implario не является обёрткой, Implario - и есть ядро игры.

## Для людей

* Перейдите во вкладку '[Релизы](https://github.com/DelfikPro/Implario/releases)' и скачайте последний доступный файл с расширением `.rar`.
* Распакуйте скачанный архив в какую-нибудь папку.
* Запустите `launcher.vbs` или `console.bat`. Последний отобразит консоль с логами.


## Для программистов
Проект разрабатывается в IntelliJ IDEA, поэтому рекомендуем использовать именно её.

### Установка
* В IDEA зайдите в меню `File` | `New...` | `Project from version control` | `Git`
* В URL укажите `https://github.com/DelfikPro/Implario`
* Нажмите ОК.

В Меню `Build` | `Build Atrifacts...` есть клиент и сервер.

### Запуск из IDEA
* Создайте новую конфигурацию с типом Application.
* Заполните её как показано на скриншоте для клиента:

![](https://i.imgur.com/UnPgv65.png)

* Для сервера достаточно просто установить Working Directory на `server/env`
