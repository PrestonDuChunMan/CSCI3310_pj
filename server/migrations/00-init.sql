/* This init file should be able to create the latest version of the database */

/* Create the initial table */
CREATE TABLE events (
	id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	court INTEGER NOT NULL,
	title TEXT NOT NULL,
	description TEXT NOT NULL,
	time INTEGER NOT NULL
);

/* Create the schema version table for migration */
CREATE TABLE IF NOT EXISTS schema_version (
	version INTEGER PRIMARY KEY,
	applied_on DATETIME DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO schema_version (version) VALUES (1);