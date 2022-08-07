# Slurp Plugin

# Structure (TODO)
1. Player joins the server
2. Player is able to execute the /joinsession <short> command
3. Player joins a session, websocket connection is opened (for the session, read only data)
4. A new player is added tot the session, saving the created uuid and matching it to minecraft uuids
5. The player is able to trigger drinking events / drinking buddie stuff.


Map: Minecraft UUID -> Slurp UUID\
All Slurp requests use the Slurp UUID (this is unique for every player for every session)

