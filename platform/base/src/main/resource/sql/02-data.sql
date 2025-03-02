
DELETE FROM account;
INSERT INTO account (id, username, password, status, nickname, mobile, email, avatar, location)
VALUES ('admin', 'admin', '$2a$10$kLRsVZ4ukaPkhpqMOR7ZqOwSIFZDiWVi1O2Rbe3AmbhZN.BXfYqRa', 0, '管理员', '', '', '', 'all');


DELETE FROM oauth_client_details;
INSERT INTO oauth_client_details (client_id, resource_ids, client_secret, scope, authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, refresh_token_validity, additional_information, autoapprove) VALUES
('web_app', 'dwis', '$2a$10$vyZc1ELnqaE4it0Mt/vYhuNlioZKNo//q2nJNw5etY3944fgQHj6W', 'SUPER_ADMIN,ADMIN', 'password,refresh_token', NULL, NULL, 86400, 2592000, NULL, NULL),
('wx_app', 'dwis', '$2a$10$kuqnAZ8D4WWY306YBYG9NOXveOu9PT/f6de3EeWPZF3ytmvR3wZJa', 'ADMIN', 'password,refresh_token', NULL, NULL, 86400, 2592000, NULL, NULL),
('services', 'dwis', '$2a$10$m2UgSDE8qgXXQsm9X19B6.k/tewWJzmSqL6PGZnW1VYVzK4TZZk9y', 'SERVICE_CALL', 'client_credentials', NULL, 'SERVICE_CALL', 86400, NULL, NULL, NULL);


DELETE FROM authority;
INSERT INTO authority (id, authority_name, descritpion, parent_id) VALUES ('Administrator', 'Administrator', '管理员权限', NULL);


DELETE FROM role;
INSERT INTO role (id, role_name, remark) VALUES	('role_admin', 'ROLE_ADMIN', '管理员');

DELETE FROM account_role;
INSERT INTO account_role (id, account_id, role_id) VALUES
  ('admin', 'admin', 'role_admin');


DELETE FROM role_authority;
INSERT INTO role_authority (role_id, authority_id) VALUES ('role_admin', 'Administrator');

