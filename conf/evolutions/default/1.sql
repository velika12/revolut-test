# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table account (
  id                            bigint not null,
  balance                       decimal(38),
  constraint pk_account primary key (id)
);
create sequence account_seq;

create table transaction (
  id                            bigint not null,
  amount                        decimal(38),
  sender_id                     bigint,
  receiver_id                   bigint,
  constraint pk_transaction primary key (id)
);
create sequence transaction_seq;

alter table transaction add constraint fk_transaction_sender_id foreign key (sender_id) references account (id) on delete restrict on update restrict;
create index ix_transaction_sender_id on transaction (sender_id);

alter table transaction add constraint fk_transaction_receiver_id foreign key (receiver_id) references account (id) on delete restrict on update restrict;
create index ix_transaction_receiver_id on transaction (receiver_id);


# --- !Downs

alter table transaction drop constraint if exists fk_transaction_sender_id;
drop index if exists ix_transaction_sender_id;

alter table transaction drop constraint if exists fk_transaction_receiver_id;
drop index if exists ix_transaction_receiver_id;

drop table if exists account;
drop sequence if exists account_seq;

drop table if exists transaction;
drop sequence if exists transaction_seq;

