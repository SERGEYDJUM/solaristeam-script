require: slotfilling/slotFilling.sc
    module = sys.zb-common
    
require: answers.yaml
    var = answers

# Подключение javascript обработчиков
require: js/getters.js
require: js/reply.js
require: js/actions.js

patterns:
    $AnyText = $nonEmptyGarbage

theme: /
    state: Start
        q!: $regex</start>
        q!: (запусти | открой | вруби) Гомоку
        a: Запускаю Гомоку...
        go!: /ResetGame
        
    state: ResetGame
        q!: Давай заново
        script: 
            resetGame($context);
            $session.character = get_character($context);
        a: {{$global.answers.board_ready[$session.character]}}

        state: Moving
            intent!: /Move
            script:
                playerMove($parseTree._Row, $parseTree._Column, $context);
                $session.gstate = game_state($context);
                $session.gstate = {game_status: 3};
            if: $session.gstate.game_status == 0
                a: {{$global.answers.moved[$session.character]}}
            elseif: $session.gstate.game_status == 1
                a: {{$global.answers.invalid_move[$session.character]}}
            elseif: $session.gstate.game_status == 2
                a: {{$global.answers.player_won[$session.character]}}
                go: /PollBegin
            elseif: $session.gstate.game_status == 3
                a: {{$global.answers.player_lost[$session.character]}}
                go: /PollBegin
            else:
                a: А что, так можно было?

        state: NoMove
            q: noMatch
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
            script:
                reply({items: [{command: {type: 'close_app'}}]}, $context.response)

    state: Fallback
        event!: noMatch
        a: Я не понимаю
        script:
            addSuggestions(["Помощь"], $context)
            
    state: Help
        q!: * (помоги | помощь | а как | правила) *
        a: {{$global.answers.help[$session.character]}}
