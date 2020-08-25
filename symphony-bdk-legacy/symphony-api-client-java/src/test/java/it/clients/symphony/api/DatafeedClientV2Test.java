package it.clients.symphony.api;

import authentication.SymBotRSAAuth;
import clients.SymBotClient;
import clients.symphony.api.constants.PodConstants;
import clients.symphony.api.DatafeedClient;
import clients.symphony.api.constants.AgentConstants;
import it.commons.BotTest;
import model.DatafeedEvent;
import model.datafeed.DatafeedV2;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DatafeedClientV2Test extends BotTest {

    private DatafeedClient datafeedClient;

    @Before
    public void initClient() throws IOException {
        stubGet(PodConstants.GETSESSIONUSER,
                readResourceContent("/response_content/authenticate/get_session_user.json"));
        config.setDatafeedVersion("v2");
        SymBotRSAAuth auth = new SymBotRSAAuth(config);
        auth.setSessionToken("eyJhbGciOiJSUzUxMiJ9.eyJzdWIiOiJ0ZXN0LWJvdCIsImlzcyI6InN5bXBob255Iiwic2Vzc2lvbklkIjoiZmRiOTAxMmQzOTgwMGE3NzNkMTJjYWFmZGY5MjU4ZjZjOWEyMTE2MmYyZDU1ODQ3M2Y5ZDU5MDUyNjA0Mjg1ZjU0MWM5Yzg0Mzc5YTE0MjZmODNiZmZkZTljYmQ5NjRjMDAwMDAxNmRmMjMyODIwNTAwMDEzZmYwMDAwMDAxZTgiLCJ1c2VySWQiOiIzNTE3NzUwMDE0MTIwNzIifQ.DlQ_-sAqZLlAcVTr7t_PaYt_Muq_P82yYrtbEEZWMpHMl-7qCciwfi3uXns7oRbc1uvOrhQd603VKQJzQxaZBZBVlUPS-2ysH0tBpCS57ocTS6ZwtQwPLCZYdT-EZ70EzQ95kG6P5TrLENH6UveohgeDdmyzSPOEiwyEUjjmzaXFE8Tu0R3xQDwl-BKbsyUAAgd1X7T0cUDC3WIDl9xaTvyxavep4ZJnZJl4qPc1Tan0yU7JrxtXeD8uwNYlKLudT3UVxduFPMQP_2jyj5Laa-YWGKvRtXkcy2d3hzf4ll1l1wVnyJc1e6hW2EnRlff_Nxge-QCJMcZ_ALrpOUtAyQ");
        symBotClient = SymBotClient.initBot(config, auth);
        datafeedClient = symBotClient.getDatafeedClient();
    }

    @Test
    public void listDatafeedIdsSuccess() throws IOException {
        stubGet(AgentConstants.LISTDATAFEEDV2,
                readResourceContent("/response_content/datafeedv2/list_datafeedv2.json"));

        List<DatafeedV2> datafeedIds = datafeedClient.listDatafeedId();

        assertNotNull(datafeedIds);
        assertEquals("2c2e8bb339c5da5711b55e32ba7c4687_f", datafeedIds.get(0).getId());
        assertEquals("4dd10564ef289e053cc59b2092080c3b_f", datafeedIds.get(1).getId());
        assertEquals("83b69942b56288a14d8625ca2c85f264_f", datafeedIds.get(2).getId());
    }

    @Test
    public void createDatafeedSuccess() throws IOException {
        stubPost(AgentConstants.CREATEDATAFEEDV2,
                readResourceContent("/response_content/datafeedv2/create_datafeedv2.json"));
        String datafeedId = datafeedClient.createDatafeed();

        assertEquals("21449143d35a86461e254d28697214b4_f", datafeedId);
    }

    @Test
    public void readDatafeedEventSuccess() throws IOException {
        stubPost(AgentConstants.READDATAFEEDV2.replace("{id}", "21449143d35a86461e254d28697214b4_f"),
                readResourceContent("/response_content/datafeedv2/read_datafeedv2.json"));
        List<DatafeedEvent> datafeedEvents = datafeedClient.readDatafeed("21449143d35a86461e254d28697214b4_f");
        assertEquals("ack_id_string", datafeedClient.getAckId());
        assertEquals(1, datafeedEvents.size());
        assertEquals("ulPr8a:eFFDL7", datafeedEvents.get(0).getId());

    }
}
