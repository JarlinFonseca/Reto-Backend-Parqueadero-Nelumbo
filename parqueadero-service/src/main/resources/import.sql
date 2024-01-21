INSERT INTO roles(nombre, descripcion) VALUES('ADMIN', 'Rol de ADMIN');
INSERT INTO roles(nombre, descripcion) VALUES('SOCIO', 'Rol de SOCIO');


INSERT INTO usuarios (nombre, apellido, documento_de_identidad, correo, clave, fecha_registro, rol_id) VALUES ('Admin','Admin', '545454454', 'admin@mail.com', '$2a$12$jrk350scTSD6wlmAtLfF1OeRPkVXCGtL7/33BkJwzXPaTJWYbVQeW', current_timestamp, 1);
