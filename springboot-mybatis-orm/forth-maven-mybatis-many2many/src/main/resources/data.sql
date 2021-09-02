create table tb_course(
c_id int primary key auto_increment,
c_name varchar(20),
c_credit int);

insert into tb_course(c_name,c_credit) values('Math',5);
insert into tb_course(c_name,c_credit) values('Computer',4);


create table tb_select_course(
sc_s_id int,
sc_c_id int,
sc_date date,
primary key(sc_s_id,sc_c_id),
foreign key(sc_s_id) references tb_student(s_id),
foreign key(sc_c_id) references tb_course(c_id));

insert into tb_select_course(sc_s_id,sc_c_id,sc_date) values(1,1,'2017-03-01');
insert into tb_select_course(sc_s_id,sc_c_id,sc_date) values(1,2,'2017-03-01');
insert into tb_select_course(sc_s_id,sc_c_id,sc_date) values(2,1,'2017-03-02');
insert into tb_select_course(sc_s_id,sc_c_id,sc_date) values(2,2,'2017-03-02');
