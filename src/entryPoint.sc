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
                $session.LastGameState = game_state($context);
            if: $session.LastGameState.playerTurn == true
                a: Хороший ход!
            else:
                a: Эй, это мой ход!

    state: Fallback
        event!: noMatch
        a: Я не понимаю

