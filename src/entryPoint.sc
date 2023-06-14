require: slotfilling/slotFilling.sc
    module = sys.zb-common
    
require: answers.yaml
    var = answers

require: js/getters.js
require: js/reply.js
require: js/actions.js

patterns:
    $Number = @duckling.number

theme: /
    state: Start
        q!: $regex</start>
        q!: * (запусти | открой | вруби | давай (сыграем | поиграем) в) *
        a: Запускаю Гомоку: Пять в ряд.
        go!: /ResetGame
        
    state: ResetGame
        q!: * (заново | снова | по новой | сброс* | перезапус* ) *
        event!: reset_game
        script: 
            resetGame($context);
            $session.character = get_character($request);
        a: {{$global.answers.board_ready[$session.character]}}

        state: PlayerMoved
            q: $Number::Row [; | ,] $Number::Column
            q: $Number::Row [строка | ряд] * $Number::Column [столбец | колонка]
            intent: /Move
            script: 
                playerMove($parseTree._Row, $parseTree._Column, $context);

        state: Moving
            event: registered_move
            script:
                $session.gstate = game_state($context);
                $session.ai_move = {x: ($session.gstate.ai_move.y + 1), y: ($session.gstate.ai_move.x + 1)};
            
            if: $session.gstate.game_status != 1
                a: {{$session.ai_move.x}}, {{$session.ai_move.y}}. || auto_listening = false
            
            if: $session.gstate.game_status == 1
                a: {{$global.answers.invalid_move[$session.character]}} || auto_listening = true
            elseif: $session.gstate.game_status == 2
                a: {{$global.answers.player_won[$session.character]}} || auto_listening = true
                go!: /PollBegin
            elseif: $session.gstate.game_status == 3
                a: {{$global.answers.player_lost[$session.character]}} || auto_listening = true
                go!: /PollBegin
        
        state: HelpInternal
            q: $regex</help>
            q: * (помоги | помощь | а как | правила) *
            a: {{$global.answers.help}}
        
        state: NoMove
            event: noMatch
            script:
                addSuggestions(["Помощь", "Сброс"], $context);
            a: {{$global.answers.nomatch_move[$session.character]}}
            
    state: PollBegin
        state: Affirmative
            q: (давай | да | ага)
            go!: /ResetGame
        state: Negative
            q: (нет | не хочу | не надо)
            script:
                addSuggestions(["Сброс"], $context);
            a: {{$global.answers.goodbye[$session.character]}}

    state: Fallback
        event!: noMatch
        script:
            addSuggestions(["Помощь", "Сброс"], $context);
        a: {{$global.answers.nomatch_global[$session.character]}}
        
    state: StopAssistant
        event!: STOP_ASSISTANT
        script:
            $context.response.replies = [];
            $context.response.replies.push({type: "raw", body: {"pronounceText": '.'}});
            
    state: Help
        q!: $regex</help>
        q!: * (помоги | помощь | а как | правила) *
        a: {{$global.answers.help}}
