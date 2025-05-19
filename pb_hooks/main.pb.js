onRecordAfterCreateSuccess((e) => {
    const result = new DynamicModel({
        "id": "",
        "username": "",
        "giveable": 0,
        "taken": 0,
        "received": 0,
    })

    const playerId = e.record.get("player")

    $app.logger().info("Player ID: " + playerId)

    $app.db()
        .select("player.id as id", "player.username as username")
        .andSelect("COALESCE(SUM(CASE WHEN entry.giveable = 1 THEN entry.units ELSE 0 END), 0) AS giveable")
        .andSelect("COALESCE(SUM(CASE WHEN entry.giveable = FALSE AND entry.units > 0 THEN entry.units ELSE 0 END), 0) AS received")
        .andSelect("COALESCE(SUM(CASE WHEN entry.giveable = FALSE AND entry.units < 0 THEN entry.units ELSE 0 END), 0) AS taken")
        .from("players player")
        .leftJoin("entries entry", $dbx.exp("player.id = entry.player"))
        .where($dbx.exp("player.id = {:playerId}", { playerId: playerId }))
        .one(result)

    $app.logger().info("Record created")

    const message = new SubscriptionMessage({
        name: "player_entries",
        data: JSON.stringify(result),
    });

    $app.logger().info("Message created")

    // retrieve all clients (clients id indexed map)
    const clients = $app.subscriptionsBroker().clients()

    for (let clientId in clients) {
        if (clients[clientId].hasSubscription("player_entries")) {
            clients[clientId].send(message)
        }
    }

    $app.logger().info("Message sent to clients")

    e.next();
}, "entries");
