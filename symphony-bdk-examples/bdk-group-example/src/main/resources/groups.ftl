<messageML>
    <h3>List of groups</h3>
    <i>The following list contains the group name and the group id</i>
    <br/>
    <ul>
    <#list data as group>
        <li>${group.name} (<b>${group.id}</b>)</li>
    </#list>
    </ul>
    <hr/>
    You can add a new member to an existing group using the following command:
    <code>
        /groups {groupId} add @member
    </code>
</messageML>
