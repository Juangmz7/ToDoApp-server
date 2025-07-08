-- Insert roles
INSERT INTO roles (id, name) VALUES (1, 'ROLE_USER')
    ON CONFLICT (id) DO NOTHING;

INSERT INTO roles (id, name) VALUES (2, 'ROLE_ADMIN')
    ON CONFLICT (id) DO NOTHING;

-- Insert users
INSERT INTO users (id, username, password, email) VALUES
                                                       (1, 'alice', '$2a$12$pvtrgSurgsu/w.UPpFv4n.KMs8EnaeG/Yp0EJdxmwycCLFQUq8bOG', 'alice@example.com'),
                                                       (2, 'bob',   '$2a$12$pvtrgSurgsu/w.UPpFv4n.KMs8EnaeG/Yp0EJdxmwycCLFQUq8bOG', 'bob@example.com'),
                                                       (3, 'carol', '$2a$12$pvtrgSurgsu/w.UPpFv4n.KMs8EnaeG/Yp0EJdxmwycCLFQUq8bOG', 'carol@example.com'),
                                                       (4, 'dave',  '$2a$12$pvtrgSurgsu/w.UPpFv4n.KMs8EnaeG/Yp0EJdxmwycCLFQUq8bOG', 'dave@example.com'),
                                                       (5, 'admin', '$2a$12$pvtrgSurgsu/w.UPpFv4n.KMs8EnaeG/Yp0EJdxmwycCLFQUq8bOG', 'admin@example.com')
    ON CONFLICT (id) DO NOTHING;

-- Insert user-role relations
INSERT INTO users_roles (user_id, roles_id) VALUES
                                                (1, 1),
                                                (2, 1),
                                                (3, 1),
                                                (4, 1),
                                                (5, 1),
                                                (5, 2)
    ON CONFLICT DO NOTHING;


-- Insert tasks for Alice (user_id = 1)
INSERT INTO task (id, body, is_completed, task_date, priority, user_id)VALUES
                                                                               (101, 'Planificar reunión del equipo para el viernes', FALSE, '2025-07-10', 'HIGH', 1),
                                                                               (102, 'Enviar informe mensual de ventas', FALSE, '2025-07-15', 'MEDIUM', 1),
                                                                               (103, 'Revisar propuesta del cliente A', FALSE, '2025-07-12', 'HIGH', 1),
                                                                               (104, 'Programar cita con el dentista', FALSE, '2025-07-08', 'LOW', 1),
                                                                               (105, 'Investigar nuevas herramientas de análisis', FALSE, '2025-07-20', 'MEDIUM', 1)
    ON CONFLICT DO NOTHING;
-- Insert tasks for Bob (user_id = 2)
INSERT INTO task (id, body, is_completed, task_date, priority, user_id) VALUES
                                                                               (201, 'Configurar entorno de desarrollo para nuevo proyecto', FALSE, '2025-07-10', 'HIGH', 2),
                                                                               (202, 'Resolver bug crítico en módulo de autenticación', FALSE, '2025-07-11', 'HIGH', 2),
                                                                               (203, 'Escribir tests unitarios para funcionalidad X', FALSE, '2025-07-18', 'MEDIUM', 2),
                                                                               (204, 'Actualizar dependencias del proyecto', FALSE, '2025-07-25', 'LOW', 2),
                                                                               (205, 'Documentar API de usuarios', FALSE, '2025-07-30', 'LOW', 2)
ON CONFLICT DO NOTHING;
-- Insert tasks for Carol (user_id = 3)
INSERT INTO task (id, body, is_completed, task_date, priority, user_id) VALUES
                                                                               (301, 'Preparar presentación para el inversor', FALSE, '2025-07-14', 'HIGH', 3),
                                                                               (302, 'Responder a correos electrónicos importantes', FALSE, '2025-07-09', 'MEDIUM', 3),
                                                                               (303, 'Analizar feedback de usuarios de la última encuesta', FALSE, '2025-07-16', 'MEDIUM', 3),
                                                                               (304, 'Organizar archivos en la nube', FALSE, '2025-07-22', 'LOW', 3),
                                                                               (305, 'Contactar a equipo de marketing para nueva campaña', FALSE, '2025-07-11', 'HIGH', 3)
ON CONFLICT DO NOTHING;
-- Insert tasks for Dave (user_id = 4)
INSERT INTO task (id, body, is_completed, task_date, priority, user_id) VALUES
                                                                               (401, 'Revisar código de Pull Request #123', FALSE, '2025-07-10', 'HIGH', 4),
                                                                               (402, 'Asistir a la reunión de stand-up diario', FALSE, '2025-07-08', 'MEDIUM', 4),
                                                                               (403, 'Aprender nuevo framework de frontend', FALSE, '2025-08-01', 'LOW', 4),
                                                                               (404, 'Depurar problema de rendimiento en base de datos', FALSE, '2025-07-13', 'HIGH', 4),
                                                                               (405, 'Escribir artículo para blog técnico', FALSE, '2025-07-28', 'MEDIUM', 4)
ON CONFLICT DO NOTHING;
-- Insert tasks for Admin (user_id = 5)
INSERT INTO task (id, body, is_completed, task_date, priority, user_id) VALUES
                                                                               (501, 'Aprobar solicitudes de acceso de nuevos usuarios', FALSE, '2025-07-09', 'HIGH', 5),
                                                                               (502, 'Monitorear rendimiento del servidor', FALSE, '2025-07-08', 'MEDIUM', 5),
                                                                               (503, 'Programar copia de seguridad de la base de datos', FALSE, '2025-07-15', 'HIGH', 5),
                                                                               (504, 'Auditar registros de seguridad', FALSE, '2025-07-20', 'LOW', 5),
                                                                               (505, 'Actualizar políticas de usuario', FALSE, '2025-07-25', 'MEDIUM', 5)
ON CONFLICT DO NOTHING;