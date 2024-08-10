CREATE DATABASE proyectoFinal;
USE proyectoFinal;

/* TABLA USUARIOS */
CREATE TABLE usuarios
(id_usuario INT auto_increment PRIMARY KEY NOT NULL,
nombre_usuario VARCHAR(30) NOT NULL,
apellido_usuario VARCHAR(40) NOT NULL,
ci_usuario VARCHAR(10) UNIQUE NOT NULL,
genero_usuario ENUM('masculino', 'femenino') NOT NULL,
tipo_usuario ENUM('usuario', 'administrador') NOT NULL,
password_usuario VARCHAR(20) NOT NULL);

/* TABLA LIBROS */
CREATE TABLE libros
(id_libro INT auto_increment PRIMARY KEY NOT NULL,
titulo_libro VARCHAR(100) NOT NULL,
nombre_autor_libro VARCHAR(30) NULL,
apellido_autor_libro VARCHAR(40) NULL,
genero_libro VARCHAR(30) NOT NULL,
descripcion_libro TEXT NOT NULL,
anio_publicacion DATE NOT NULL,
historial_creacion_libro timestamp DEFAULT CURRENT_TIMESTAMP,
historial_edicion_libro timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP);

ALTER TABLE libros ADD COLUMN archivo LONGBLOB NULL;

ALTER TABLE libros ADD COLUMN extension_archivo VARCHAR(10);

/* TABLA DESCARGAS */
CREATE TABLE descargas
(fk_id_usuario INT,
fk_id_libro INT,
PRIMARY KEY (fk_id_usuario, fk_id_libro),
fecha_descarga TIMESTAMP DEFAULT CURRENT_TIMESTAMP);

ALTER TABLE descargas
ADD CONSTRAINT fk_id_user
FOREIGN KEY (fk_id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE;
ALTER TABLE descargas
ADD CONSTRAINT fk_id_book
FOREIGN KEY (fk_id_libro) REFERENCES libros(id_libro) ON DELETE CASCADE;


