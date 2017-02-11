#!/bin/bash

user='root'
passwd='root'

mysql -u "$user" --password="$passwd" -e "insert into agoda.hotelList values (32423), (36224), (23537), (85346), (87543), (57323), (23565);insert into agoda.countryList values (23454), (32524), (24253), (35322), (43323), (43343), (45645), (45323);"
