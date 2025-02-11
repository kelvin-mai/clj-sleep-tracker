create table sleep (
  account_id text not null references account(id),
  sleep_date date not null default now(),
  start_time time not null,
  end_time time not null,
  duration decimal generated always as (
    trunc(extract(epoch from (end_time - start_time
      + case when end_time < start_time then interval '1 day' else '0 seconds' end
    )) /3600, 2)
  ) stored,
  created_at timestamp not null default current_timestamp,
  updated_at timestamp,
  primary key (account_id, sleep_date)
);
--;;
create trigger sleep_updated_at_trigger
  before update on sleep
  for each row
  execute procedure moddatetime(updated_at);
--;;