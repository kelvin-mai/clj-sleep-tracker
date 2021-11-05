-- password_hash is 'password'
insert into account(username, password_hash)
values ('user', 'bcrypt+sha512$1e7a19e12ab46a3b6c81d335032305d6$12$9d03323ea6c81a00dfd5516287958eefa5d82fe82c247369');

with a as (select id from account),
     nums as (select num from generate_series(1,10) as num)
insert into sleep (account_id, sleep_date, start_time, end_time)
  select a.id
        ,(current_date - (nums.num || ' day' )::interval)::date
        ,'10:00PM'::time
        ,'8:00AM'::time
from nums cross join a
on conflict do nothing;
