# --- Sample dataset

# --- !Ups

insert into account (id,balance) values (10,5000);
insert into account (id,balance) values (20,1000);

# --- !Downs

delete from account;