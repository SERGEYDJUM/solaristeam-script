require: slotfilling/slotFilling.sc
    module = sys.zb-common
    
require: answers.yaml
    var = answers

require: js/getters.js
require: js/reply.js
require: js/actions.js

theme: /
    state: Start
        q!: $regex</start>
        q!: * (запусти | открой | вруби | давай (сыграем | поиграем) в) Гомоку
        a: Запускаю Гомоку...
        go!: /ResetGame
        
    state: ResetGame
        q!: * (заново | снова | по новой | сброс* | перезапус* ) *
        event!: reset_game
        script: 
            resetGame($context);
            $session.character = $rawRequest.payload.character.name || "Сбер";
        a: {{$global.answers.board_ready[$session.character]}}

        state: PlayerMoved
            intent: /Move
            script: 
                playerMove($parseTree._Row, $parseTree._Column, $context);

        state: Moving
            event: registered_move
            script:
                $session.gstate = game_state($context);
                $session.ai_move = {x: ($session.gstate.ai_move.y + 1), y: ($session.gstate.ai_move.x + 1)};
            
            if: $session.gstate.game_status != 1
                a: {{$session.ai_move.x}} {{$session.ai_move.y}}.
            
            if: $session.gstate.game_status == 1
                a: {{$global.answers.invalid_move[$session.character]}}
            elseif: $session.gstate.game_status == 2
                a: {{$global.answers.player_won[$session.character]}}
                go!: /PollBegin
            elseif: $session.gstate.game_status == 3
                a: {{$global.answers.player_lost[$session.character]}}
                go!: /PollBegin
        
        state: HelpInternal
            q: $regex</help>
            q: * (помоги | помощь | а как | правила) *
            a: {{$global.answers.help}}
        
        state: NoMove
            event: noMatch
            a: {{$global.answers.nomatch_move[$session.character]}}
            script:
                addSuggestions(["Помощь"], $context)
            
    state: PollBegin
        state: Affirmative
            q: (давай | да)
            go!: /ResetGame
        state: Negative
            q: (нет | не хочу)
            a: {{$global.answers.goodbye[$session.character]}}

    state: Fallback
        event!: noMatch
        a: Я не понимаю.
        script:
            addSuggestions(["Помощь"], $context)
            
    state: StopAssistant
        event!: STOP_ASSISTANT
        script:
            $context.response.replies = [];
            $context.response.replies.push({type: "raw", body: {"pronounceText": '.'}});
            
    state: Help
        q: $regex</help>
        q!: * (помоги | помощь | а как | правила) *
        a: {{$global.answers.help}}
