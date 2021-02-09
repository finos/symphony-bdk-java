<messageML>
    <h2>${title}</h2>
    <p>${description}</p>
    <hr />
    <form id="create-step-2">

        <text-field name="option1" placeholder="Option 1..." required="true" />
        <text-field name="option2" placeholder="Option 2..." required="false" />
        <text-field name="option3" placeholder="Option 3..." required="false" />
        <text-field name="option4" placeholder="Option 4..." required="false" />

        <select name="type">
            <option selected="true" value="single">Single choice</option>
            <option value="multi">Multiple choices</option>
        </select>

        <button name="submit" type="action">Send poll!</button>
    </form>
</messageML>
