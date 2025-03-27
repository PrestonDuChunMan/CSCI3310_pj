import Database from "better-sqlite3";
import { mkdirSync, readdirSync, readFileSync } from "fs";

mkdirSync("runtime", { recursive: true });

const db = new Database("runtime/basketball.db");

// check if db is init
const row = db.prepare<[string], { name: string }>("SELECT name FROM sqlite_master WHERE type = 'table' AND name = ?").get("schema_version");
if (!row?.name) {
	// db is not init. run 00-init.sql
	const sql = readFileSync("migrations/00-init.sql", "utf8");
	db.exec(sql);
} else {
	// run migrations
	const row = db.prepare<[], { version: number }>("SELECT version FROM schema_version ORDER BY version DESC LIMIT 1").get();
	const version = row?.version || 0;
	for (const file of readdirSync("migrations").sort()) {
		const ver = parseInt(file);
		if (ver > version) {
			const sql = readFileSync(`migrations/${file}`, "utf8");
			db.exec(sql);
			// update schema version
			db.prepare<[number], unknown>("INSERT INTO schema_version (version) VALUES (?)").run(ver);
		}
	}
}

export default db;