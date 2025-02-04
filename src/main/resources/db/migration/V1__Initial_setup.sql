create table instance
(
    id          bigserial    not null,
    instance_id varchar(255) not null,
    completed   boolean      not null,
    primary key (id)
);