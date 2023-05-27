function playerMove(row, column, context) {
    addAction({
        type: "player_move",
        move: {x: column, y: row}
    }, context);
}

function resetGame(context) {
    addAction({type: "reset_game"}, context);
}