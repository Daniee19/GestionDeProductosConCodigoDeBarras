```sql
Create database tiendita;
use tiendita;

create table codigo_barra (
id_barra integer primary key not null auto_increment,
nombre varchar(30) not null,
tipo varchar(20) not null
);

create table producto (
id integer primary key not null auto_increment,
id_barra integer,
nombre varchar(60) not null,
precio decimal(8,2) not null,
cantidad integer not null
);

alter table producto add constraint id_barra 
foreign key (id_barra) 
references codigo_barra(id_barra); 
```
