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
        a: Я {{$global.answers.board_ready[$session.character]}}

        state: Moving
            intent!: /Move
            script:
                playerMove($parseTree._Row, $parseTree._Column, $context);
                $session.gstate = game_state($context);
            if: $session.gstate.game_status == 0
                a: А вот и мой ход
            elseif: $session.gstate.game_status == 1
                a: Так ходить нельзя, попробуйте ещё раз
            elseif: $session.gstate.game_status == 2
                a: Поздравляю, вы победили!
                go!: /PollBegin
            elseif: $session.gstate.game_status == 3
                a: Я победил!
                go!: /PollBegin
            else:
                a: А что, так можно было?

        state: NoMove
            q: noMatch
            a: Я не понимаю. Повторите свой ход
            script:
                addSuggestions(["Помощь"], $context)
            

    state: Fallback
        event: noMatch
        a: Я не понимаю
        script:
            addSuggestions(["Помощь"], $context)
            
    state: Help
        q!: * (помоги | помощь | а как | правила) *
        a: Помоги сам себе
