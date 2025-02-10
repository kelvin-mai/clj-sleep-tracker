create table if not exists refresh_tokens (
  id text primary key default nanoid(),
  token text unique not null,
  expires_at timestamptz not null
);