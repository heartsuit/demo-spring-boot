create table tb_head_teacher(
ht_id int primary key auto_increment,
ht_name varchar(20),
ht_age int);

insert into tb_head_teacher(ht_name,ht_age) values('ZhangSan',40);


create table tb_class(
c_id int primary key auto_increment,
c_name varchar(20),
c_ht_id int unique,
foreign key(c_ht_id) references tb_head_teacher(ht_id));

insert into tb_class(c_name,c_ht_id) values('Class One',1);
