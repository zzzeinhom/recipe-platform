-- Grant explicit privileges to rp_user on the recipe_platform database
-- The user is created by MySQL during initialization
ALTER USER 'rp_user'@'%' IDENTIFIED WITH mysql_native_password BY 'rp_password';
ALTER USER 'rp_user'@'localhost' IDENTIFIED WITH mysql_native_password BY 'rp_password';
GRANT ALL PRIVILEGES ON recipe_platform.* TO 'rp_user'@'%';
GRANT ALL PRIVILEGES ON recipe_platform.* TO 'rp_user'@'localhost';
FLUSH PRIVILEGES;

