drop table if exists users;

create table users (
    id bigint auto_increment primary key,
    name varchar(100) not null,
    description varchar(100) not null,
    email varchar(100) not null unique,
    password varchar(100) not null,
    ultimAcces timestamp,
    dataCreated timestamp,
    dataUpdated timestamp
);