create table if not exists "airline"
(
    "airline_id" bigserial constraint "pk_airline" primary key,
    "name" text not null,
    "country" text not null
);

create table if not exists "airport"
(
    "airport_id" bigserial constraint "pk_airport" primary key,
    "iata_code" char(3) not null,
    "name" text not null,
    "city" text not null,
    "country" text not null
);

create table if not exists "plane_model"
(
    "plane_model_id" bigserial constraint "pk_plane_model" primary key,
    "name" text not null,
    "seats" smallint not null
);

create table if not exists "plane"
(
    "plane_id" bigserial constraint "pk_plane" primary key,
    "plane_model_id" bigint not null
        constraint "fk_plane_model" references plane_model(plane_model_id)
);

create table if not exists "flight"
(
    "flight_id" bigserial constraint "pk_flight" primary key,
    "airport_departure_id" bigint not null
        constraint "fk_flight_airport_departure" references airport(airport_id),
    "airport_arrival_id" bigint not null
        constraint "fk_flight_airport_arrival" references airport(airport_id),
    "plane_id" bigint not null
        constraint "fk_flight_plane" references plane(plane_id),
    "airline_id" bigint not null
        constraint "fk_flight_airline" references airline(airline_id),
    "duration" interval not null,
    "departure_time" timestamptz not null,
    "arrival_time" timestamptz not null
);

create table if not exists "passenger"
(
    "passenger_id" bigserial constraint "pk_passenger" primary key,
    "first_name" text not null,
    "last_name" text not null,
    "age" smallint not null
);

create table if not exists "ticket_class"
(
    "ticket_class_id" smallint constraint "pk_ticket_class" primary key,
    "name" text not null
);

create table if not exists "ticket"
(
    "passenger_id" bigint not null references passenger(passenger_id),
    "flight_id" bigint not null references flight(flight_id),
        constraint "pk_ticket" primary key (passenger_id, flight_id),
    "ticket_class_id" smallint not null
        constraint "fk_ticket_class" references ticket_class(ticket_class_id),
    "ticket_number" bigint,
    "price" numeric(10,2)
);
