require: slotfilling/slotFilling.sc
    module = sys.zb-common

# Подключение javascript обработчиков
require: js/getters.js
require: js/reply.js
require: js/actions.js

patterns:
    $AnyText = $nonEmptyGarbage

theme: /
    state: Start
        # При запуске приложения с кнопки прилетит сообщение /start.
        q!: $regex</start>
        # При запуске приложения с голоса прилетит сказанная фраза.
        q!: (запусти | открой | вруби) Гомоку
        q!: Давай заново
        go!: /ResetGame
        
    state: ResetGame
        a: Поле готово. Предлагаю вам сделать первый ход
        script:
            resetGame($context)
            
        state:
            intent: /Move
            script:
                playerMove($parseTree._Row, $parseTree._Column, $context);
                var game_state = get_game_state(get_request($context));
            if: $session.LastGameState == "win"
                a: Молодец!
            else:
                a: Плохо!

    state: Fallback
        event!: noMatch
        a: Я не понимаю

