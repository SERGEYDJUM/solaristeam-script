function get_request(context) {
    if (context && context.request)
        return context.request.rawRequest;
    return {}
}

function get_server_action(request){
    if (request &&
        request.payload && 
        request.payload.data &&
        request.payload.data.server_action){
            return request.payload.data.server_action;
        }
    return {};
}

function get_screen(request){
    if (request &&
        request.payload &&
        request.payload.meta &&
        request.payload.meta.current_app &&
        request.payload.meta.current_app.state){
        return request.payload.meta.current_app.state.screen;
    }
    return "";
}

function get_game_state(request){
if (request &&
        request.payload &&
        request.payload.meta &&
        request.payload.meta.current_app &&
        request.payload.meta.current_app.state){
        return request.payload.meta.current_app.state.game_state;
    }
    return null;
}

function getCharacterName($request) {
    try {
        // Possible names: Сбер, Афина, Джой
        return $request.rawRequest.payload.character.name;
    } catch (e) {
        if ($request.channelType === "chatwidget") {
            return "Сбер";
        }
        throw e.message;
    }
}

function game_state(context) {
    return get_game_state(get_request(context))
}