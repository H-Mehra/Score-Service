# Score-Service

Please update config.yml with appropriate details before running the service.

To compile : run `mvn clean test install` in the Score-Serice directory

To run     : run `java -jar <jarFileOfScore-ServiceProject> server <config.yml_file>`

To setup databse environment in Maria/MySql database : 
export following variables

export MYSQL_USER=\<yourUserName>
export MYSQL_PASSWD=\<yourPassWord>

To create database and tables:  run `bash setupScoreDatabase.sh`

To setup Mock data into the tables: run `bash setupMockDataScoreDatabase.sh`
