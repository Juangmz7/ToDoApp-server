CREATE EXTENSION IF NOT EXISTS vector;
CREATE EXTENSION IF NOT EXISTS hstore;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS vector_store (
    id uuid DEFAULT uuid_generate_v4() PRIMARY KEY,
    content text,
    metadata json,
    embedding vector(1536)
    );

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
INSERT INTO task (id, body, is_completed, task_date, priority, user_id) VALUES
                                                                            (101, 'Planificar reunión del equipo para el viernes', FALSE, '2025-07-10', 2, 1), -- HIGH (2)
                                                                            (102, 'Enviar informe mensual de ventas', FALSE, '2025-07-15', 1, 1), -- MEDIUM (1)
                                                                            (103, 'Revisar propuesta del cliente A', FALSE, '2025-07-12', 2, 1), -- HIGH (2)
                                                                            (104, 'Programar cita con el dentista', FALSE, '2025-07-08', 0, 1), -- LOW (0)
                                                                            (105, 'Investigar nuevas herramientas de análisis', FALSE, '2025-07-20', 1, 1) -- MEDIUM (1)
    ON CONFLICT DO NOTHING;

-- Insert tasks for Bob (user_id = 2)
INSERT INTO task (id, body, is_completed, task_date, priority, user_id) VALUES
                                                                            (201, 'Configurar entorno de desarrollo para nuevo proyecto', FALSE, '2025-07-10', 2, 2), -- HIGH (2)
                                                                            (202, 'Resolver bug crítico en módulo de autenticación', FALSE, '2025-07-11', 2, 2), -- HIGH (2)
                                                                            (203, 'Escribir tests unitarios para funcionalidad X', FALSE, '2025-07-18', 1, 2), -- MEDIUM (1)
                                                                            (204, 'Actualizar dependencias del proyecto', FALSE, '2025-07-25', 0, 2), -- LOW (0)
                                                                            (205, 'Documentar API de usuarios', FALSE, '2025-07-30', 0, 2) -- LOW (0)
    ON CONFLICT DO NOTHING;

-- Insert tasks for Carol (user_id = 3)
INSERT INTO task (id, body, is_completed, task_date, priority, user_id) VALUES
                                                                            (301, 'Preparar presentación para el inversor', FALSE, '2025-07-14', 2, 3), -- HIGH (2)
                                                                            (302, 'Responder a correos electrónicos importantes', FALSE, '2025-07-09', 1, 3), -- MEDIUM (1)
                                                                            (303, 'Analizar feedback de usuarios de la última encuesta', FALSE, '2025-07-16', 1, 3), -- MEDIUM (1)
                                                                            (304, 'Organizar archivos en la nube', FALSE, '2025-07-22', 0, 3), -- LOW (0)
                                                                            (305, 'Contactar a equipo de marketing para nueva campaña', FALSE, '2025-07-11', 2, 3) -- HIGH (2)
    ON CONFLICT DO NOTHING;

-- Insert tasks for Dave (user_id = 4)
INSERT INTO task (id, body, is_completed, task_date, priority, user_id) VALUES
                                                                            (401, 'Revisar código de Pull Request #123', FALSE, '2025-07-10', 2, 4), -- HIGH (2)
                                                                            (402, 'Asistir a la reunión de stand-up diario', FALSE, '2025-07-08', 1, 4), -- MEDIUM (1)
                                                                            (403, 'Aprender nuevo framework de frontend', FALSE, '2025-08-01', 0, 4), -- LOW (0)
                                                                            (404, 'Depurar problema de rendimiento en base de datos', FALSE, '2025-07-13', 2, 4), -- HIGH (2)
                                                                            (405, 'Escribir artículo para blog técnico', FALSE, '2025-07-28', 1, 4) -- MEDIUM (1)
    ON CONFLICT DO NOTHING;

-- Insert tasks for Admin (user_id = 5)
INSERT INTO task (id, body, is_completed, task_date, priority, user_id) VALUES
                                                                            (501, 'Aprobar solicitudes de acceso de nuevos usuarios', FALSE, '2025-07-09', 2, 5), -- HIGH (2)
                                                                            (502, 'Monitorear rendimiento del servidor', FALSE, '2025-07-08', 1, 5), -- MEDIUM (1)
                                                                            (503, 'Programar copia de seguridad de la base de datos', FALSE, '2025-07-15', 2, 5), -- HIGH (2)
                                                                            (504, 'Auditar registros de seguridad', FALSE, '2025-07-20', 0, 5), -- LOW (0)
                                                                            (505, 'Actualizar políticas de usuario', FALSE, '2025-07-25', 1, 5) -- MEDIUM (1)
    ON CONFLICT DO NOTHING;