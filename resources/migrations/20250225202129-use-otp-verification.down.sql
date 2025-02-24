alter table account drop column otp_secret;
--;;
alter table account add column verification_code uuid default uuid_generate_v4();
--;;
alter table account add column verification_code_expiration timestamptz default now() + interval '1 day';
--;;