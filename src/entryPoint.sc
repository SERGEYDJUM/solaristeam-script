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
        q!: (запусти | открой | вруби) Гомоку
        a: Запускаю Гомоку...
        go!: /ResetGame
        
    state: ResetGame
        q!: * (заново | снова | по новой | сброс* | перезапус* ) *
        event!: reset_game
        script: 
            resetGame($context);
            $session.character = get_character($context);
        a: {{$global.answers.board_ready[$session.character]}}

        state: Moving
            intent: /Move
            event: registered_move
            script:
                if ($parseTree._Row && $parseTree._Column) {
                    playerMove($parseTree._Row, $parseTree._Column, $context);
                }
                $session.gstate = game_state($context);
                
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
        event: noMatch
        a: Я не понимаю.
        script:
            addSuggestions(["Помощь"], $context)
            
    state: Help
        q: $regex</help>
        q!: * (помоги | помощь | а как | правила) *
        a: {{$global.answers.help}}
