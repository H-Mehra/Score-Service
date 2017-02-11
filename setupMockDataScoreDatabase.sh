#!/bin/bash

mysql -u "$MYSQL_USER" --password="$MYSQL_PASSWD" -e "insert into score.hotelList values (32423), (36224), (23537), (85346), (87543), (57323), (23565);insert into score.countryList values (23454), (32524), (24253), (35322), (43323), (43343), (45645), (45323);"
