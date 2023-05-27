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
        go!: /ResetGame
        
    state: ResetGame
        a: Поле готово. Предлагаю вам сделать первый ход

        state:
            intent: /Move
            script:
                $session.LastGameState = "win";
            if $session.LastGameState == "win"
                a: Молодец!
            else
                a: Плохо!

    state: Fallback
        event!: noMatch
        a: Я не понимаю

