create table if not exists account (
  id text not null primary key default nanoid(),
  email citext not null unique,
  password text not null,
  created_at timestamptz not null default now(),
  updated_at timestamptz
);
--;;
create trigger account_updated_at_trigger
  before update on account
  for each row
  execute procedure moddatetime(updated_at);
--;;