let appControllerService = SYMPHONY.services.register("app:controller")
let navService
let appId = "mybot"

let authenticate = () => {
    return $.post({
        url: '/bdk/v1/app/auth',
        success: res => $.when(res)
    })
}

let register = data => {
    let appToken = data["appToken"]
    return SYMPHONY.application.register(
            {
                appId: appId,
                tokenA: data["appToken"]
            },
            ['modules', 'applications-nav', 'extended-user-info'],
            ['app:controller']
    ).then(res => {
        res["tokenA"] = appToken
        let modulesService = SYMPHONY.services.subscribe("modules")
        // dialogsService = SYMPHONY.services.subscribe("dialogs")
        navService = SYMPHONY.services.subscribe("applications-nav")
        navService.add('show-module', 'Circle Of Trust', 'app:controller')
        appControllerService.implement({
            select: id => {
                if (id === 'show-module') {
                    modulesService.show("test-app", {title: "Circle Of Trust"}, "app:controller", "https://localhost:443/app.html")

                }
            }
        })

        return $.when(res)
    })
}
SYMPHONY.remote.hello()
        .then(authenticate)
        .then(register)
