# ImplarioClient
Клиент для Minecraft с множеством различных дополнений.

## Компиляция
Учитывайте, что этот код - имплементация ядра, которое находится [здесь](https://github.com/ImplarioCore), и оно, вместе с его библиотеками, должно быть включено в сборку.

### Компиляция при помощи Github Desktop и IntelliJ IDEA
Создайте новый проект в IDEA, здесь он называется project.

В папку с этим проектом клонируйте репозиторий [ImplarioCore](https://github.com/ImplarioCore).

У вас получится папка с названием project/ImplarioCore.

Теперь в IDEA создайте новый пустой модуль и назовите его точно так же - ImplarioCore.

В Project Structure -> Modules -> ImplarioCore -> Sources пометьте папку src как исходники, а папку resources как ресурсы. 

Затем в том же Project Structure -> Modules перейдите во вкладку Dependencies и добавьте туда JAR-файл libs.jar, расположенный в project/ImplarioCore.


Теперь, после установки ядра, можно установить и сам клиент.

В папку с этим проектом клонируйте этот репозиторий (Будет project/ImplarioClient).

Перейдите в IDEA и создайте новый модуль (БЕЗ existing sources) и назовите его ImplarioClient.

В Project Structure -> Modules -> ImplarioClient -> Sources пометьте папку src как исходники, а папку resources как ресурсы. Затем перейдите во вкладку Dependencies и добавьте туда следующие зависимости:
* Файл libs_client.jar, находящийся в project/ImplarioClient
* Файл libs.jar, находящийся в project/ImplarioCore
* Зависимость от модуля ImplarioCore

Теперь можно собирать модуль ImplarioClient.


## Запуск
### Запуск через IntelliJ IDEA
Перейдите в меню Run -> Edit Configurations...
Создайте новую конфигурацию Application.
В поле Main class укажите главный класс ImplarioClient - Start
В поле VM options вставьте следующее:
`-Djava.library.path=/gamedata/natives/` 

Поле Program arguments заполните этим:
`--username YOUR_NAME --gameDir .\ --uuid 1a2b3c4d5e6f7d8d9d0d1a2b3c4d5e6d --userType mojang`

`YOUR_NAME` замените на свой игровой ник.
Поскольку Implario Client пока не поддерживает лицензионные сервера, UUID может быть любым.

В Working directory пропишите путь к папке project/ImplarioClient/env, в ней находятся нативные библиотеки для Windows, а также некоторые другие файлы игры, например звуки.

Use classpath of module: *ImplarioClient*

После нажатия OK можно запускать эту конфигурацию и играть!
