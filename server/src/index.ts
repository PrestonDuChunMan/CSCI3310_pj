import "dotenv/config";
import express from "express";
import db from "./db";
import { AddressInfo } from "net";
import { verifyInteger, verifyPostBody } from "./util";

const app = express();
app.use(express.json()); // middleware for parsing post body json

// get all events at a court (no description)
app.get("/api/court/:court/events", (req, res) => {
	const court = verifyInteger(req.params.court, "court", res);
	if (court === undefined) return;
	const rows = db.prepare<[number], { id: number, title: string, time: number }>("SELECT id, title, time FROM events WHERE court = ?").all(court);
	res.json({ success: true, data: rows });
});

// get a specific event
app.get("/api/court/:court/event/:event", (req, res) => {
	const court = verifyInteger(req.params.court, "court", res);
	if (court === undefined) return;
	const event = verifyInteger(req.params.event, "event", res);
	if (event === undefined) return;
	const row = db.prepare<[number, number], { id: number, court: number, title: string, description: string, time: number }>("SELECT * FROM events WHERE court = ? AND id = ?").get(court, event);
	if (!row) {
		res.status(404);
		res.json({ success: false, error: `Event ${event} doesn't exist for court ${court}` });
	} else res.json({ success: true, data: row });
});

// create a new event
// remember to send this post request with header "Content-Type: application/json"
app.post("/api/court/:court/event", (req, res) => {
	const court = verifyInteger(req.params.court, "court", res);
	if (court === undefined) return;
	if (!verifyPostBody(req.body, ["title", "description", "time"], res)) return;
	const time = verifyInteger(req.body.time, "time", res);
	if (time === undefined) return;
	db.prepare<[number, string, string, number], unknown>("INSERT INTO events (court, title, description, time) VALUES (?, ?, ?, ?)").run(court, req.body.title, req.body.description, time);
	res.json({ success: true });
});

const server = app.listen(process.env.PORT || 3000, () => console.log(`Listening at port ${(server.address() as AddressInfo).port}`));