alter table account add column otp_secret text;
--;;
alter table account drop column verification_code;
--;;
alter table account drop column verification_code_expiration;
--;;