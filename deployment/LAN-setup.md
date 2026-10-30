# Running the CBT Exam Server on a Local LAN

This reproduces the flow you saw at the exam centre: candidate PCs open a browser,
hit `Win+R` → type the server's IP → land on the login page.

## 1. One machine acts as the server

Pick one PC on the LAN (or a router-connected machine) to run:
- MySQL (the database)
- Apache Tomcat (running this `exam-server.war`)

Find its local IP with `ipconfig` (Windows) — e.g. `192.168.1.50`.

## 2. Install prerequisites on the server machine

- JDK 17+
- Apache Tomcat 10.x (Jakarta EE 10 / Servlet 6.0 compatible)
- MySQL 8+
- Maven (only needed to build the WAR; not needed on exam-day machine)

## 3. Build the WAR

```
cd exam-server
mvn clean package
```

This produces `target/exam-server.war`.

## 4. Deploy to Tomcat

Copy `exam-server.war` into Tomcat's `webapps/` folder, or rename it to `ROOT.war`
if you want the app to be reachable at `http://<server-ip>:8080/` directly
(matching the "type an IP and land on login" experience).

Start Tomcat. By default it listens on port 8080.

## 5. Open the firewall port

On the server machine, allow inbound TCP 8080 (or 80 if you front it with a
reverse proxy) so other LAN PCs can reach it:

```
netsh advfirewall firewall add rule name="CBT Exam Server" dir=in action=allow protocol=TCP localport=8080
```

## 6. On each exam PC

Open a browser and go to:

```
http://192.168.1.50:8080/
```

(or whatever the server's actual LAN IP is). This lands on `login.jsp`.

## 7. Database setup

On the server machine, run the scripts in `database/`:

```
mysql -u root -p < database/schema.sql
mysql -u root -p < database/seed_data.sql
```

Update `exam-server/src/main/resources/db.properties` with the real MySQL
credentials before building.

## Notes on matching the original behavior

- The exam end time is computed and stored server-side at first login
  (`exam_session.server_end_time`), so clients cannot extend time by changing
  their system clock.
- `ExamTimeFilter` re-checks the server clock on every request to `exam.jsp`,
  `api/answer`, and `api/submit` — if time is up, it force-closes the session
  and redirects to the result page even if the candidate's browser JS timer
  was tampered with.
- Answers are saved to the database on every click (`api/answer`), not just at
  final submit, so a crashed/restarted browser does not lose progress.
