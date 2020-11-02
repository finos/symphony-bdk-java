let appToken, symphonyToken, jwt
let appId = 'myapp'

let buttons = [$('#authenticate-btn'), $('#register-btn'), $('#validate-token-btn'), $('#retrieve-jwt-btn'), $('#validate-jwt-btn')]
let texts = [$('#authenticate-txt'), $('#register-txt'), $('#validate-token-txt'), $('#retrieve-jwt-txt'), $('#validate-jwt-txt')]
let holders = [$('#authenticate'), $('#register'), $('#validate-token'), $('#retrieve-jwt'), $('#validate-jwt')]
$(`#register`).hide()
$(`#validate-token`).hide()
$(`#retrieve-jwt`).hide()
$(`#validate-jwt`).hide()

let authenticate = () => {
    return $.post({
        url: '/bdk/v1/app/auth',
        success: res => {
            appToken = res["appToken"]
            $('#authenticate-txt').html(`<b>Authenticated app ${appId} with the App token:</b><br/> ${res["appToken"]}`)
            $('#authenticate-btn').attr('class', 'tempo-btn tempo-btn--disabled action')
            $('#register').show()
        }
    })
}

let register = appToken => {
    return SYMPHONY.application.register(
            {
                appId: appId,
                tokenA: appToken
            },
            ['extended-user-info'],
            ['app:controller']
    ).then(res => {
        symphonyToken = res["tokenS"]
        $('#register-txt').html(`<b>Register to Symphony backend with Symphony token:</b><br/> ${symphonyToken}`)
        $('#register-btn').attr('class', 'tempo-btn tempo-btn--disabled action')
        $('#validate-token').show()
    })
}

let validateTokens = (appToken, symphonyToken) => {
    return $.post({
        url: '/bdk/v1/app/tokens',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        data: JSON.stringify({
            appToken: appToken,
            symphonyToken: symphonyToken
        }),
        dataType: "json",
        success: () => {
            $('#validate-token-txt').html(`<b>Pair of App token and Symphony token is valid!</b>`)
            $('#validate-token-btn').attr('class', 'tempo-btn tempo-btn--disabled action')
            $('#retrieve-jwt').show()
        }
    })
}

let retrieveJwt = () => {
    const userService = SYMPHONY.services.subscribe('extended-user-info');
    return userService.getJwt().then(res => {
        jwt = res
        $('#retrieve-jwt-txt').html(`<b>Retrieve JWT from Symphony Frontend:</b><br/> ${jwt}`)
        $('#retrieve-jwt-btn').attr('class', 'tempo-btn tempo-btn--disabled action')
        $('#validate-jwt').show()
    })
}

let validateJwt = (jwt) => {
    return $.post({
        url: '/bdk/v1/app/jwt',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        data: JSON.stringify({
            jwt: jwt
        }),
        dataType: "json",
        success: res => {
            $('#validate-jwt-txt').html(`<b>JWT is valid for the user with the user id:</b> ${res['userId']}`)
            $('#validate-jwt-btn').attr('class', 'tempo-btn tempo-btn--disabled action')
            $('#reset-btn').show()
        }
    })
}

SYMPHONY.remote.hello()
        .then(data => {
            $(`#authenticate-btn`).click(() => {
                authenticate()
            })
            $(`#register-btn`).click(() => {
                register(appToken)
            })
            $(`#validate-token-btn`).click(() => {
                validateTokens(appToken, symphonyToken)
            })
            $(`#retrieve-jwt-btn`).click(() => {
                retrieveJwt()
            })
            $(`#validate-jwt-btn`).click(() => {
                validateJwt(jwt)
            })
            $(`#reset-btn`).click(() => {
                holders.forEach(holder => holder.hide())
                buttons.forEach(button => button.attr('class', 'tempo-btn tempo-btn--primary action'))
                texts.forEach(text => text.html(""))
                $('#authenticate').show()

            })
        })
