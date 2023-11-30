--liquibase formatted sql
--changeset a.gordeeva:create-user

CREATE TABLE provider
(
    id bigint PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
    name varchar(40) NOT NULL,
    surname varchar(40) NOT NULL,
    status boolean NOT NULL
);