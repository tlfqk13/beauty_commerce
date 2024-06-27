drop table if exists wishlist;
drop table if exists survey;
drop table if exists product;
drop table if exists review;
drop table if exists member;

create table member (
                        member_id bigint not null auto_increment,
                        email varchar(255) not null,
                        kakao_id varchar(255),
                        nickname varchar(255),
                        password varchar(255) not null,
                        phone_number varchar(255),
                        shop_by_member_id varchar(255),
                        primary key (member_id)
);

create table product (
                         product_id bigint not null auto_increment,
                         product_name varchar(255),
                         primary key (product_id)
);

create table review (
                        review_id bigint not null auto_increment,
                        comment varchar(255),
                        order_number varchar(255),
                        shop_by_member_id varchar(255),
                        tag varchar(255),
                        primary key (review_id)
);

create table survey (
                        survey_id bigint not null auto_increment,
                        preference varchar(255),
                        skin_trouble varchar(255),
                        skin_type varchar(255),
                        member_id bigint,
                        primary key (survey_id)
);

create table wishlist (
                          wishlist_id bigint not null auto_increment,
                          member_id bigint,
                          product_id bigint,
                          primary key (wishlist_id)
);

alter table survey
    add constraint FK8jxem4c3k9lo8nj4ebempero9
        foreign key (member_id)
            references member (member_id);

alter table wishlist
    add constraint FKr9m487rorwstnl1r1ib9r5pds
        foreign key (member_id)
            references member (member_id);

alter table wishlist
    add constraint FKqchevbfw5wq0f4uqacns02rp7
        foreign key (product_id)
            references product (product_id);