create extension if not exists "uuid-ossp";

create table account (
  id uuid not null primary key default uuid_generate_v4(),
  username text unique not null,
  password_hash text not null
);

create table sleep (
  id uuid not null primary key default uuid_generate_v4(),
  account_id uuid not null,
  sleep_date date unique not null default now(),
  start_time time not null,
  end_time time not null,
  duration decimal generated always as
    (trunc(extract(epoch from (end_time - start_time + '24:00'))/3600, 2)) stored,
  constraint fk_account foreign key(account_id) references account(id)
);
