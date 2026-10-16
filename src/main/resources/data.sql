-- Datos iniciales de prueba. Spring ejecuta este archivo al arrancar,
-- despues de que Hibernate crea las tablas. Asi la API tiene datos para consultar.

-- Poliza 1: INDIVIDUAL, ACTIVA, vigencia 12 meses. Prima = 1.500.000 x 12
INSERT INTO poliza (tipo, estado, tomador, fecha_inicio, fecha_fin, meses_vigencia, canon_mensual, prima)
VALUES ('INDIVIDUAL', 'ACTIVA', 'Juan Perez', '2026-01-01', '2027-01-01', 12, 1500000.00, 18000000.00);

-- Poliza 2: COLECTIVA, ACTIVA, vigencia 6 meses. Prima = 2.000.000 x 6
INSERT INTO poliza (tipo, estado, tomador, fecha_inicio, fecha_fin, meses_vigencia, canon_mensual, prima)
VALUES ('COLECTIVA', 'ACTIVA', 'Inmobiliaria Los Andes', '2026-03-01', '2026-09-01', 6, 2000000.00, 12000000.00);

-- Poliza 3: COLECTIVA, CANCELADA. Sirve para probar que no se puede renovar.
INSERT INTO poliza (tipo, estado, tomador, fecha_inicio, fecha_fin, meses_vigencia, canon_mensual, prima)
VALUES ('COLECTIVA', 'CANCELADA', 'Administracion Edificio Central', '2025-06-01', '2026-06-01', 12, 1800000.00, 21600000.00);

-- Riesgos (inmuebles asegurados). La individual tiene 1; la colectiva activa tiene 2.
INSERT INTO riesgo (descripcion, estado, poliza_id) VALUES ('Apartamento 301 - Calle 10 # 5-20', 'ACTIVO', 1);
INSERT INTO riesgo (descripcion, estado, poliza_id) VALUES ('Apartamento 101 - Carrera 7 # 45-10', 'ACTIVO', 2);
INSERT INTO riesgo (descripcion, estado, poliza_id) VALUES ('Apartamento 102 - Carrera 7 # 45-10', 'ACTIVO', 2);
INSERT INTO riesgo (descripcion, estado, poliza_id) VALUES ('Local 5 - Avenida 68 # 20-30', 'CANCELADO', 3);
