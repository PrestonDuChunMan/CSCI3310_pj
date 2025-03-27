import { Response } from "express";

export function verifyInteger(val: string, name: string, res: Response) {
	const parsed = parseInt(val);
	if (isNaN(parsed)) {
		res.status(400);
		res.json({ success: false, error: `"${name}" is not an integer` });
		return undefined;
	}
	return parsed;
}

export function verifyPostBody(body: any, keys: string[], res: Response) {
	for (const key of keys) {
		if (body[key] === undefined) {
			res.status(400);
			res.json({ success: false, error: "Missing title or description" });
			return false;
		}
	}
	return true;
}