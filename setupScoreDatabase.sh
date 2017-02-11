#!/bin/bash

user='root'
passwd='root'

mysql -u "$user" --password="$passwd" -e "CREATE DATABASE score; create table score.countryList(countryids bigint not null unique);create table score.hotelList(hotelids bigint not null unique);"
