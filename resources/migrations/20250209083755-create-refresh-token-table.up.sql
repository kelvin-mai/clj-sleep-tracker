create table if not exists refresh_token (
  id text primary key default nanoid(),
  token text unique not null,
  expires_at timestamptz not null
);