cmpe273-assignment1
===================

CMPE 273 Assignment 1



Available Services:

Req #  | URI                                                | Method        |Description
:-----:|----------------------------------------------------|---------------|-------------------------------
       | library/v1/books                                   | **GET**       |Returns all available books
       | library/v1/books/                                  | **GET**       |Returns all available books (same as above)
1      | library/v1/books                                   | **POST**      |Create new Book
1      | library/v1/books/                                  | **POST**      |Create new Book (Save as above)
2      | library/v1/books/{isbn}                            | **GET**       |Get book by ISBN
3      | library/v1/books/{isbn}                            | **DELETE**    |Delete book by ISBN
4      | library/v1/books/{isbn}                            | **PUT**       |Update a book. Available query parameteres: `title`, `publication-date`, `language`, `numPage`, and `status`
5      | library/v1/books/{isbn}/reviews                    | **POST**      |Create book review
6      | library/v1/books/{isbn}/reviews/{reviewid}         | **GET**       |View book review by review id
7      | library/v1/books/{isbn}/reviews                    | **GET**       |View all book reviews
8      | library/v1/books/{isbn}/authors/{authorid}         | **GET**       |View book author by author id
9      | library/v1/books/{isbn}/authors                    | **GET**       |View all authors


Prepare Ubuntu instance to run project
---------------------------------------
`sudo apt-get install openjdk-6-jdk maven git`

Check out the code from GitHub
------------------------------
1. `git clone https://github.com/maksimu/cmpe273-assignment1.git`

2. Build and try to run: `mvn spring-boot:run`


Run project as a service
------------------------

1. Install **supervisor** (`supervisord` command, to install `sudo apt-get install supervisor`). After installing try to start supervisor by running the following command: `sudo service supervisor start`

2. Create a file `sudo vi /etc/supervisor/conf.d/cmpe273-assignment1.conf` with the following content:
	```
	[program:cmpe273-assignment1]
	command=/usr/bin/mvn -f /home/ubuntu/cmpe273-assignment1/pom.xml spring-boot:run
	user=ubuntu
	autostart=true
	autorestart=true
	startsecs=10
	startretries=3
	stdout_logfile=/home/ubuntu/cmpe273-assignment1/log-cmpe273-assignment1-stdout.log
	stderr_logfile=/home/ubuntu/cmpe273-assignment1/log-cmpe273-assignment1-stderr.log

3. To control the application you would need to execute `supervisorctl`, which will present you with a prompt where you could start, stop, status of the app you specified in the `cmpe273-assignment1.conf` file.
	
	```
	sudo supervisorctl
	cmpe273-assignment1 RUNNING   pid 123123, uptime 1 day, 15:00:00
	supervisor> stop cmpe273-assignment1
	supervisor> start cmpe273-assignment1

	
Restart the server and try to go to `http://54.193.33.95:8080/library/v1/books`

