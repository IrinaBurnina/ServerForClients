# Клиент-Сервер. Приложение для чата.

### Курсовой проект.

1. Приложение состоит из двух частей: приложения сервера и приложения клиента.
2. Роль сервера унифицирована, ролей клиентов может быть несколько. Общение между сервером и клиентами происходит
   параллельно. Количество клиентов ограничивается физическими параметрами исполняющего оборудования.
3. Начало функционирования: запуск сервера
   ```java
   public static void main(String[] args) throws IOException {
       Settings.writeToSettings("settings.txt");
       log.log(server, "server is started. Let's go!");
       try (ServerSocket serverSocket =
       new ServerSocket(Integer.parseInt(Settings.portNumberFromFile("settings.txt")))) {
       while (true) {
           ExecutorService group = Executors.newFixedThreadPool(4);
           group.execute(forGroup(serverSocket));
           }
       }
   }


4. Запуск сервера осуществляется через файл настроек

> settings.txt

Он содержит номер порта и хост через пробел.

5. После запуска сервера в поле чата появляется служебное сообщение сервера о том, что сервер стартовал и начал свою
   работу.

````java
[Сервер #1]2023-04-21T23:09:49.121399800===server is started.Let's go!
````

6. Далее сервер ожидает подключения клиентов.
7. Подключение клиентов также осуществляется через файл настроек, через номер порта.

````java
 public static void main(String[]args)throws IOException{
        Socket clientSocket=new Socket(Settings.hostFromFile("settings.txt"),
        Integer.parseInt(Settings.portNumberFromFile("settings.txt")));
        Thread sending=new Thread(clientSendMessage(clientSocket));
        Thread getting=new Thread(clientGetMessage(clientSocket));
        sending.start();
        getting.start();
        }
````

8. Приложение клиента содержит два потока, один - на считывание данных, другой - на приём. Потоки могут работать
   параллельно друг другу.
9. Далее сервер отправляет клиенту запрос об имени, для регистрации в общем чате.

````java
What is ur name?
        Maxim
````

10. В общем чате появляются: вопрос об имени, отправленное имя и приветствие от сервера.
    ![](https://github.com/IrinaBurnina/Server-Client/pictures/Chat.PNG)
11. Сообщение с приветствием и краткой инструкцией в ответ (сообщение №3 от сервера) также поступило подключенному
    клиенту.
    ![](https://github.com/IrinaBurnina/Server-Client/pictures/ClientIsClosed.PNG)
12. В ответ клиент отправляет своё сообщение, которое сервер выводит в общий чат
    ![](https://github.com/IrinaBurnina/Server-Client/pictures/ServerPic.PNG)
    и каждому клиенту:
    ![](https://github.com/IrinaBurnina/Server-Client/pictures/Chatting.PNG).
13. После для прекращения подключения к серверу клиент вводит /exit, прощается и покидает чат.
    ![](https://github.com/IrinaBurnina/Server-Client/pictures/ExitOnOneClient.PNG)
