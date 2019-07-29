package it.clients.symphony.api;

import clients.symphony.api.InformationBarriersClient;
import clients.symphony.api.constants.PodConstants;
import it.commons.BotTest;
import model.InformationBarrierGroup;
import model.InformationBarrierGroupStatus;
import model.Policy;
import org.junit.Before;
import org.junit.Test;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.List;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class InformationBarriersClientTest extends BotTest {
    private InformationBarriersClient informationBarriersClient;

    @Before
    public void initClient() {
        informationBarriersClient = new InformationBarriersClient(symBotClient);
    }

    @Test
    public void listGroupsSuccess() {
        stubFor(get(urlEqualTo(PodConstants.LISTIBGROUPS))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .withBody("[\n" +
                    "    {\n" +
                    "        \"id\": \"5cf5393b8ded730e095f10cc\",\n" +
                    "        \"name\": \"Test Group Alpha\",\n" +
                    "        \"active\": true,\n" +
                    "        \"memberCount\": 0,\n" +
                    "        \"policies\": [\n" +
                    "            \"5d3188128ded730cfc5bd500\"\n" +
                    "        ],\n" +
                    "        \"createdDate\": 1559574843949,\n" +
                    "        \"modifiedDate\": 1559575230340\n" +
                    "    },\n" +
                    "    {\n" +
                    "        \"id\": \"5cf539538ded730e095f10cd\",\n" +
                    "        \"name\": \"Test Group Bravo\",\n" +
                    "        \"active\": false,\n" +
                    "        \"memberCount\": 0,\n" +
                    "        \"policies\": [\n" +
                    "            \"5d3188128ded730cfc5bd500\"\n" +
                    "        ],\n" +
                    "        \"createdDate\": 1559574867754,\n" +
                    "        \"modifiedDate\": 1563894251325\n" +
                    "    }\n" +
                    "]")));

        List<InformationBarrierGroup> groups = informationBarriersClient.listGroups();
        assertNotNull(groups);
        assertEquals("Test Group Alpha", groups.get(0).getName());
    }

    @Test
    public void listGroupMembersSuccess() {
        stubFor(get(urlEqualTo(PodConstants.LISTIBGROUPMEMBERS.replace("{gid}", "5d3188bb8ded730cfc5bd502")))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .withBody("[\n" +
                    "    351775001411707,\n" +
                    "    351775001411672,\n" +
                    "    351775001411941,\n" +
                    "    351775001411585\n" +
                    "]")));

        List<Long> groupMembers = informationBarriersClient.listGroupMembers("5d3188bb8ded730cfc5bd502");
        assertNotNull(groupMembers);
        assertEquals(351775001411707L, (long) groupMembers.get(0));
    }

    @Test
    public void removeGroupMembersSuccess() {
        stubFor(post(urlEqualTo(PodConstants.REMOVEIBGROUPMEMBERS.replace("{gid}", "5d3188bb8ded730cfc5bd502")))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .withBody("{\n" +
                    "    \"overallResult\": \"SUCCESS\",\n" +
                    "    \"results\": [\n" +
                    "        \"\"\n" +
                    "    ]\n" +
                    "}")));
        List<Long> membersToRemove = Collections.singletonList(351775001411672L);
        InformationBarrierGroupStatus ibStatus = informationBarriersClient.removeGroupMembers("5d3188bb8ded730cfc5bd502", membersToRemove);
        assertNotNull(ibStatus);
        assertEquals("SUCCESS", ibStatus.getOverallResult());
    }

    @Test
    public void addGroupMembersSuccess() {
        stubFor(post(urlEqualTo(PodConstants.ADDIBGROUPMEMBERS.replace("{gid}", "5d3188bb8ded730cfc5bd502")))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .withBody("{\n" +
                    "    \"overallResult\": \"SUCCESS\",\n" +
                    "    \"results\": [\n" +
                    "        \"\"\n" +
                    "    ]\n" +
                    "}")));
        List<Long> membersToAdd = Collections.singletonList(351775001411672L);
        InformationBarrierGroupStatus ibStatus = informationBarriersClient.addGroupMembers("5d3188bb8ded730cfc5bd502", membersToAdd);
        assertNotNull(ibStatus);
        assertEquals("SUCCESS", ibStatus.getOverallResult());
    }

    @Test
    public void listPoliciesSuccess() {
        stubFor(get(urlEqualTo(PodConstants.LISTPOLICIES))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .withBody("[\n" +
                    "    {\n" +
                    "        \"id\": \"5d3188128ded730cfc5bd500\",\n" +
                    "        \"policyType\": \"BLOCK\",\n" +
                    "        \"active\": false,\n" +
                    "        \"groups\": [\n" +
                    "            \"5cf539538ded730e095f10cd\",\n" +
                    "            \"5cf5393b8ded730e095f10cc\"\n" +
                    "        ],\n" +
                    "        \"createdDate\": 1563527186475,\n" +
                    "        \"modifiedDate\": 1563894211373\n" +
                    "    }\n" +
                    "]")));

        List<Policy> policies = informationBarriersClient.listPolicies();
        assertNotNull(policies);
        assertEquals("5d3188128ded730cfc5bd500", policies.get(0).getId());
    }
}
