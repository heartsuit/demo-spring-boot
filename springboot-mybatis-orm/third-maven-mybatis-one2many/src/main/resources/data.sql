create table tb_head_teacher(
ht_id int primary key auto_increment,
ht_name varchar(20),
ht_age int);

insert into tb_head_teacher(ht_name,ht_age) values('ZhangSan',40);

create table tb_student(
s_id int primary key auto_increment,
s_name varchar(20),
s_sex varchar(10),
s_age int,
s_c_id int,
foreign key(s_c_id) references tb_class(c_id));

insert into tb_student(s_name,s_sex,s_age,s_c_id) values('Tom','male',18,1);
insert into tb_student(s_name,s_sex,s_age,s_c_id) values('Jack','male',19,1);
insert into tb_student(s_name,s_sex,s_age,s_c_id) values('Rose','female',18,1);
