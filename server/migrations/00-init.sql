/* This init file should be able to create the latest version of the database */

/* Create the initial table */
CREATE TABLE events (
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	court INTEGER,
	title TEXT,
	description TEXT
);

/* Create the schema version table for migration */
CREATE TABLE IF NOT EXISTS schema_version (
	version INTEGER PRIMARY KEY,
	applied_on DATETIME DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO schema_version (version) VALUES (0);