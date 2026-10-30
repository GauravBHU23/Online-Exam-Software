# CBT Exam Server

A JSP/Servlet-based Computer Based Test (CBT) exam system, modeled on the
login → candidate details → instructions → timed MCQ exam → auto-submit flow
used in centre-based exams (e.g. NIT Patna style CBT centres).

## Stack

- Java 17, Jakarta EE 10 (Servlet 6.0 / JSP), Tomcat 10.1.x
- Maven
- MySQL 8 in production (via HikariCP connection pool); H2 embedded DB for local dev
- Plain JS on the client (timer, AJAX answer auto-save) — no frontend framework

## Local development run (verified working setup)

Local dev uses H2 (embedded, file-based — no DB server install needed) instead
of MySQL. `db.properties` is already pointed at H2 by default, and
`DevDataInitializer` (a `@WebListener`) auto-creates the schema and seeds test
data from `src/main/resources/sql/schema_h2.sql` / `seed_data_h2.sql` on first
startup — no manual DB setup step needed for local testing.

1. Build the WAR:
   ```
   mvn clean package
   ```
2. Deploy `target/exam-server.war` to a Tomcat **10.1.x** instance (Jakarta EE 10 /
   Servlet 6.0 — the old `tomcat7-maven-plugin` / Tomcat 7 does NOT work with
   this code, since it only supports the legacy `javax.servlet.*` API).
   Easiest: rename it to `ROOT.war` and drop it in Tomcat's `webapps/` folder
   so the app is served from `/`.
3. Start Tomcat, then open **http://localhost:8080/login.jsp** (or whichever
   port your `conf/server.xml` Connector uses).
4. Login with the seeded test candidate:
   - Application No: `APP1001`
   - Password: `password123`

For production / MySQL, switch the `db.url`/`db.user`/`db.password` lines in
`db.properties` to the MySQL block (already present, commented out) and use
`database/schema.sql` + `database/seed_data.sql` instead of the `_h2` versions.

## Admin panel

Open **http://localhost:8080/admin/login.jsp** (adjust the port as above).

Default credentials (set in `src/main/resources/admin.properties` — change
before any real deployment):
- Username: `admin`
- Password: `admin123`

From the dashboard you can:
- Create a new exam (name + duration in minutes)
- Add/delete questions for an exam
- Add candidates one at a time, or bulk-import via CSV-style paste
  (`applicationNo,password,name,fatherName,centreCode,rollNo` — one per line;
  `centreCode`/`rollNo` are optional)
- View results per exam and export them as CSV

`/admin/*` is protected by `AdminAuthFilter`; unauthenticated requests are
redirected to `/admin/login.jsp`.

## Production / LAN deployment

See [`../deployment/LAN-setup.md`](../deployment/LAN-setup.md) for deploying
to Apache Tomcat and exposing it to exam-centre PCs over LAN.

## How the timer/auto-submit works

- `exam_session.server_end_time` is fixed in the database the moment the
  candidate clicks "Start Exam" (login_time + exam.duration_minutes).
- The browser JS timer (`exam.jsp`) just counts down a number the server
  gave it and calls `api/submit?auto=true` at zero.
- Every request to `exam.jsp`, `api/answer`, and `api/submit` is re-checked
  server-side by `ExamTimeFilter` against the real server clock, so a
  candidate cannot extend their time by editing the page or their system
  clock.
- Answers are saved individually via `api/answer` on every click (not only
  at final submit), so progress isn't lost if a PC restarts mid-exam.
