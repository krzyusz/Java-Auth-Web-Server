# Java-Auth-Web-Server
Web server using three-factor authentication, built on Java Sockets

**Project consists of:**
- browser's extension - handling requests send and get for login data to server 
- mobile app for Android - user is receiving notifications when request for data is sent, then he authenticate with fingerprint
- Java web server built on sockets

## Example Usage: 
User wants to authenticate on "example-website.com" in public place, and is afraid of his login data being seen by other people. 
He uses web extension to connect with server, then server sends notification to mobile app (using Firebase) and user can authenticate using his fingerprint.
After that login data is being send from server to web extension, and user logs into "example-website.com" automaticaly. 
This method may be even faster than casually typing login data into inputs (it takes about 5 seconds to do this process using mobile app - counted with notification delay)
and it's much safer. 
