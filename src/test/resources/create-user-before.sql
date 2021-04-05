delete from user_roles;
delete from users;

insert into users(id, username, password, active) values
(1, 'user', 'select password from usr where username='user';', true),
(2, 'mike', 'select password from usr where username='user';', true);

insert into user_role(user_id, roles) values
(1, 'ADMIN'), (1, 'USER'),
(2, 'USER');
