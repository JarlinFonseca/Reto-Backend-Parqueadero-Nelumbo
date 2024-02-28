INSERT INTO roles(nombre, descripcion) VALUES('ADMIN', 'Rol de ADMIN');
INSERT INTO roles(nombre, descripcion) VALUES('SOCIO', 'Rol de SOCIO');


INSERT INTO usuarios (nombre, apellido, documento_de_identidad, correo, clave, fecha_registro, rol_id) VALUES ('Admin','Admin', '545454454', 'admin@mail.com', '$2a$10$z7Rr9.jvu2dp1.DWbrQLU.ZHFWolOARlRgfC8WnWDl0uohqG72lR2', current_timestamp, 1);
