-- Insert roles
INSERT INTO roles (id, name) VALUES (1, 'ROLE_USER')
    ON CONFLICT (id) DO NOTHING;

INSERT INTO roles (id, name) VALUES (2, 'ROLE_ADMIN')
    ON CONFLICT (id) DO NOTHING;

-- Insert users
INSERT INTO users (id, user_name, password, email) VALUES
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
