create table if not exists accounts (
  id text not null primary key default nanoid(),
  email text not null unique,
  password text not null,
  created_at timestamptz not null default now(),
  updated_at timestamptz
);
--;;
create trigger accounts_updated_at_trigger
  before update on accounts
  for each row
  execute procedure moddatetime(updated_at);
--;;