<card accent="<#if (record?? && record.TotalResults > 0)>tempo-bg-color--green<#elseif (record?? && record.TotalResults == 0)>tempo-bg-color--yellow<#else>tempo-bg-color--red</#if>" iconSrc="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABwAAAAcCAYAAAByDd+UAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyhpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuNi1jMTQ1IDc5LjE2MzQ5OSwgMjAxOC8wOC8xMy0xNjo0MDoyMiAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENDIDIwMTkgKE1hY2ludG9zaCkiIHhtcE1NOkluc3RhbmNlSUQ9InhtcC5paWQ6MDE2MDc2Nzk2NDQ5MTFFOThEQkNEOTNDQjJFMjVDNUYiIHhtcE1NOkRvY3VtZW50SUQ9InhtcC5kaWQ6MDE2MDc2N0E2NDQ5MTFFOThEQkNEOTNDQjJFMjVDNUYiPiA8eG1wTU06RGVyaXZlZEZyb20gc3RSZWY6aW5zdGFuY2VJRD0ieG1wLmlpZDowMTYwNzY3NzY0NDkxMUU5OERCQ0Q5M0NCMkUyNUM1RiIgc3RSZWY6ZG9jdW1lbnRJRD0ieG1wLmRpZDowMTYwNzY3ODY0NDkxMUU5OERCQ0Q5M0NCMkUyNUM1RiIvPiA8L3JkZjpEZXNjcmlwdGlvbj4gPC9yZGY6UkRGPiA8L3g6eG1wbWV0YT4gPD94cGFja2V0IGVuZD0iciI/PoIrDqQAAANSSURBVHja7JZJaBNhGIbfySSTrTZprKUuXRARrODWouByES9KQbRevOjBgzdxAZeCpyoK4oaeRb0I6kHUSy8qLogLVQvVaq21tlJra9qmTbNO4vslkxjTqCnVguIPD/mXmW+f748Sj8cxmcOESR7/vkKzcrEze6+G7CGrSAHpJjdIAxk1niklK4mdtJIneSvMWm8l57P2XGQ+2UzWktXkmKEsNW6TbaTjVwoVXHifmi8kzycQrSEyl0jZVxu/r8iHH3l4fILpkUi0EIeBjBi5TvaT15keuok3sf4zQzdq4mGqSmfkVCY7MUZGj489lbXsh2P5mKmSW6QwpbCL+BIvRuNJRFGIwkwKrBY+FjH2JTORWPpstkcDgrFvhunxbzJkHkkbZCP1KYXDifKmUKtmQoXbArddRQnny6dZsanCAVs0BreDhgaiKHNpmEojzi7zoLbMjlI+K9my8XnB41Qxq9AMk1mB22lOGpRUWpfK4Wlas72AzpxbXYIrHX4094VwuKYIXQEd7cNROOlNGYX4xSjOz7T6cGRJEe73BlFRYEaTN4wqlwVBeiXOtQxGsLRYwwJG4MAjL1r6Q7RIDYmHG8kOYlUoqH0gjGX0qq7SCR9fruYLS4utqC6x4m1/GOGQDqemoK7cgYYXQ7DR0+5RHevLHAljfAyhXAjPvoRQO9OOpp4gTJZ0Q4up2LDzktE5Emlw8NBjM2GQynrp3XNvBDe7Avgc1DHAvF3jvFxTsbzUhvaRKBrf+bGOyht7AnhMg+5RwQqeOVRTwuseyrjFM/Y0hlV5IyGNpL9HqQNJh1SexMXGRUg3uq4CjbkKU+kUznctcuHOpyDuto0AkicpEFVJWi35Eq9opMazsJ6+AveJwqCEM13A8YyyzzVnuCwsDDPXAVqfMColUMmSkfkJAR9JpQT36ZhD5SdzehdhrgISBSpOe6TkkKF8p0w+/KgorB933xAhal5NSRSdIvNSjV1yd5fsJid+Uxs7SK4aZrWJV7ma90lyj2whc8h0sigP4fGMYPrJIXI03/vwaVY+5bop/Mm7l8le48KWUn5A+sZ7AWeONeQ+0XKcNRsXLr8JdP6u/zTyt6GKXDCau3j8khwmiw1l46+3//9L/3qFXwUYAO/kMMCUg2S0AAAAAElFTkSuQmCC">
    <#if record??>
        <div>
            <#if record.TotalResults == 0>
                <p>We did not find any contacts that matched your query.</p>
                <hr />
            <#else>
                <table>
                    <thead>
                        <tr>
                            <th>Name</th>
                            <th>Title</th>
                            <th>Email</th>
                            <th>Phone</th>
                            <th>Owner</th>
                            <th>Account</th>
                            <th>Last Modified</th>
                        </tr>
                    </thead>
                    <tbody>
                        <#list record.Contacts as contact>
                            <tr>
                                <td><a href="${salesforceUrl}/${contact.Id}">${contact.Name}</a></td>
                                <td>${(contact.Title)!}</td>
                                <td>${(contact.Email)!}</td>
                                <td>${(contact.Phone)!}</td>
                                <td><a href="${salesforceUrl}/${contact.Owner.Id}">${contact.Owner.Name}</a></td>
                                <td><a href="${salesforceUrl}/${contact.Account.Id}">${contact.Account.Name}</a></td>
                                <td>${contact.LastModifiedDate?datetime("yyyy-MM-dd'T'HH:mm:ss.000+0000")}</td>
                            </tr>
                        </#list>
                    </tbody>
                </table>
            </#if>
        </div>
        <div style="padding-top:10px;">
            <span style="margin-right: 20px;color:#767676;">Org: <a href="${salesforceUrl}"><b>${orgName}</b></a></span>
            <#if record.Query??><span style="margin-right: 20px;color:#767676;">Query: <i>&quot;${record.Query}&quot;</i></span></#if>
            <span style="color:#767676;">Results: <b><#if (record.TotalResults > record.Contacts?size)>${record.Contacts?size} of ${record.TotalResults}<#else>${record.Contacts?size}</#if></b></span>
        </div>

    <#elseif error??>
        <p>${error.message}</p>
        <div style="margin-top: 20px;">
            <hr />
            <div style="padding-top:10px;">
                <span style="margin-right: 20px;">Org: <a href="${salesforceUrl}"><b>${orgName}</b></a></span>
            </div>
        </div>
    </#if>
</card>