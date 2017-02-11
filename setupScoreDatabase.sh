#!/bin/bash

mysql -u "$MYSQL_USER" --password="$MYSQL_PASSWD" -e "CREATE DATABASE score; create table score.countryList(countryids bigint not null unique);create table score.hotelList(hotelids bigint not null unique);"
