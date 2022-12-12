create table eventparticipants (
    id bigint not null,
    ending_time datetime(6) not null,
    event_id bigint not null,
    starting_time datetime(6) not null,
    user_id bigint not null,
    primary key (id)
)

create table usertimezones (
    user_id bigint not null,
    zone_id tinyblob not null,
    primary key (user_id)
)