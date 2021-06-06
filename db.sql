create table if not exists post
(
	id          INTEGER constraint post_pk primary key autoincrement,
	create_date TEXT not null,
	update_date TEXT,
	title       TEXT not null,
	shortcut    TEXT not null,
	language    TEXT not null
)