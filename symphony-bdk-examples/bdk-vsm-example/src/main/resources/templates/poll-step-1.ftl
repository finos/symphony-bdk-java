<messageML>
    <h2>${title}</h2>
    <p>${description}</p>
    <hr />
    <form id="poll-step-1">

        <span style="display: none">
            <text-field name="pollId" required="true">${id}</text-field>
        </span>

        <radio checked="true" name="choice" value="option1">${option1}</radio>
        <radio name="choice" value="option2">${option2}</radio>
        <#if option3?? && option3?has_content>
            <radio name="choice" value="option3">${option3}</radio>
        </#if>
        <#if option4?? && option4?has_content>
            <radio name="choice" value="option4">${option4}</radio>
        </#if>
        <hr />
        <button name="submit" type="action">Submit</button>
    </form>
</messageML>
