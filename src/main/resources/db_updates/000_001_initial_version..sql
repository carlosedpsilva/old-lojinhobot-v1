DROP TABLE IF EXISTS `guilds`;
CREATE TABLE `guilds` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `discord_id` varchar(18) NOT NULL UNIQUE,
  `name` varchar(100) DEFAULT NULL,
  `owner` int(11) NOT NULL,
  `active` bool NOT NULL DEFAULT true,
  `banned` bool NOT NULL DEFAULT false,
  PRIMARY KEY (`id`)
);
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `discord_id` varchar(18) NOT NULL UNIQUE,
  `name` varchar(32) DEFAULT NULL,
  `commands_used` int(11) NOT NULL,
  PRIMARY KEY (`id`)
);
DROP TABLE IF EXISTS `guild_member`;
CREATE TABLE `guild_member` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL UNIQUE,
  `guild_id` int(11) NOT NULL UNIQUE,
  `join_date` TIMESTAMP DEFAULT NULL,
  PRIMARY KEY (`id`)
);
DROP TABLE IF EXISTS `ranks`;
CREATE TABLE `ranks` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code_name` varchar(32) NOT NULL UNIQUE,
  `full_name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
);
DROP TABLE IF EXISTS `role_categories_type`;
CREATE TABLE `role_categories_type` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code_name` varchar(32) NOT NULL UNIQUE,
  `full_name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
);
DROP TABLE IF EXISTS `user_economy`;
CREATE TABLE `user_economy` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL UNIQUE,
  `level` int(11) NOT NULL DEFAULT '0',
  `experience` int(11) NOT NULL DEFAULT '0',
  `current_balance` int(11) NOT NULL DEFAULT '0',
  `reputation` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
);
DROP TABLE IF EXISTS `user_data`;
CREATE TABLE `user_data` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL UNIQUE,
  `daily_streak` int(11) NOT NULL DEFAULT '0',
  `last_daily_at` TIMESTAMP DEFAULT NULL,
  `description` varchar(500) DEFAULT NULL,
  `title` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`)
);
DROP TABLE IF EXISTS `member_economy`;
CREATE TABLE `member_economy` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `member_id` int(11) NOT NULL UNIQUE DEFAULT '0',
  `experience` int(11) NOT NULL DEFAULT '0',
  `guild_points` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
);
DROP TABLE IF EXISTS `guild_roles`;
CREATE TABLE `guild_roles` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `discord_id` varchar(18) NOT NULL UNIQUE,
  `guild_id` int(11) NOT NULL,
  `role_category` int(11) NOT NULL,
  PRIMARY KEY (`id`)
);
DROP TABLE IF EXISTS `guild_role_categories`;
CREATE TABLE `guild_role_categories` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `category_type` int(11) NOT NULL UNIQUE,
  `name` varchar(64) NOT NULL,
  PRIMARY KEY (`id`)
);
DROP TABLE IF EXISTS `guild_settings`;
CREATE TABLE `guild_settings` (
  `guild_id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `config` varchar(255) NOT NULL,
  PRIMARY KEY (`guild_id`)
);
DROP TABLE IF EXISTS `user_rank`;
CREATE TABLE `user_rank` (
  `user_id` int(11) NOT NULL,
  `rank_type` int(11) NOT NULL,
  PRIMARY KEY (`user_id`, `rank_type`)
);
DROP TABLE IF EXISTS `bot_meta`;
CREATE TABLE `bot_meta` (
  `meta_name` VARCHAR(32) NOT NULL,
  `meta_value` VARCHAR(32),
  PRIMARY KEY (`meta_name`)
);