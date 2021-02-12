<messageML>
    <h2>${title}</h2>
    <p>${description}</p>
    <hr />
    This poll is now finished, please check the results out below:
    <table>
        <#list results?keys as option>
        <tr>
            <td><b>${option}</b></td>
            <td>${results[option]}</td>
        </tr>
        </#list>
    </table>
</messageML>
