create table account (
  id serial primary key,
  username text unique not null,
  password_hash text not null
);

create table sleep (
  id serial primary key,
  account_id serial not null,
  sleep_date date not null default now(),
  start_time timestamptz not null,
  end_time timestamptz not null,
  constraint fk_account foreign key(account_id) references account(id)
);
