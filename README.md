## Database
```sql
Create database tiendita;
use tiendita;

create table producto (
idProducto integer primary key not null auto_increment,
codigoBarra varchar(20) not null unique,
nombre varchar(60)  null,
precio decimal(8,2)  null default 0,
stock integer null default 0,
marca varchar(50) null,
contenido varchar(20) null,
igvAplicable tinyint(1) not null,
imagenUrl varchar(200) null
);

create table usuario (
idUsuario integer primary key not null auto_increment,
nombre varchar(40) not null,
apellido varchar(40)  not null,
nombreUsuario varchar(20) not null,
contrasenia blob,
rol varchar(20) not null
);

create table venta(
idVenta integer primary key not null auto_increment,
idUsuario integer not null, foreign key(idUsuario) REFERENCES usuario(idUsuario) ON DELETE CASCADE ON UPDATE CASCADE,
fechaVenta timestamp default current_timestamp,
impTotSnIgv decimal(8,2) not null default 0,
igvTotal decimal(6,2) not null default 0,
impTotCnIgv decimal(8,2) not null default 0,
descTot decimal(6,2) not null default 0,
total decimal(8,2) not null default 0,
tipoVenta enum('Boleta', 'Factura') default 'Boleta',
metPago enum('Tarjeta', 'Efectivo', 'Yape','Plin','Transacción', 'Fiado') not null default 'Efectivo',
observacion varchar(250) null
);

create table detalle_venta(
idDetalle integer primary key not null auto_increment,
idVenta integer not null, foreign key(idVenta) REFERENCES venta(idVenta) ON DELETE CASCADE ON UPDATE CASCADE,
idProducto integer not null, foreign key(idProducto) REFERENCES producto(idProducto) ON DELETE CASCADE ON UPDATE CASCADE, 
cant decimal(6,2) not null default 0,
impSnIgv decimal(8,2) not null default 0,
igv decimal(6,2) not null default 0,
impCnIgv decimal(8,2) not null default 0,
descProdu decimal(6,2) not null default 0,
subtotCnDesc decimal(8,2) not null default 0
);

```
## References
### Project Data
![image](https://github.com/user-attachments/assets/cc4e79c0-06fc-438f-94c9-d0495c1a62a9)
<br/>
This project uses data from [Open Food Facts](https://world.openfoodfacts.org), an open database of food products.  
The data is available under the [Open Database License (ODbL)](https://opendatacommons.org/licenses/odbl/).  
### Icons
<a target="_blank" href="https://icons8.com/icon/32244/barcode-reader">Escáner de código de barras 2 Filled</a> icono de <a target="_blank" href="https://icons8.com">Icons8</a> </br>
<a target="_blank" href="https://icons8.com/icon/6483/multiply">X</a> icono de <a target="_blank" href="https://icons8.com">Icons8</a></br>
<a target="_blank" href="https://icons8.com/icon/85458/minus">Menos</a> icono de <a target="_blank" href="https://icons8.com">Icons8</a></br>
<a target="_blank" href="https://icons8.com/icon/38033/full-screen">Full Screen</a> icono de <a target="_blank" href="https://icons8.com">Icons8</a></br>
<a target="_blank" href="https://icons8.com/icon/10421/pdf">PDF</a> icono de <a target="_blank" href="https://icons8.com">Icons8</a></br>
<a target="_blank" href="https://icons8.com/icon/11594/microsoft-excel">MS Excel</a> icono de <a target="_blank" href="https://icons8.com">Icons8</a>
