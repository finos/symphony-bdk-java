package com.symphony.bdk.core.service.version;

import com.symphony.bdk.core.service.version.model.AgentVersion;
import com.symphony.bdk.gen.api.SignalsApi;
import com.symphony.bdk.gen.api.model.AgentInfo;
import com.symphony.bdk.http.api.ApiException;

import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@API(status = API.Status.INTERNAL)
public class AgentVersionService {
  private static final Pattern versionPattern = Pattern.compile("Agent-(\\d+)\\.(\\d+)\\..*");

  private final SignalsApi signalsApi;

  public AgentVersionService(SignalsApi signalsApi) {
    this.signalsApi = signalsApi;
  }



  public Optional<AgentVersion> retrieveAgentVersion() {

    try {
      AgentInfo agentInfo = signalsApi.v1InfoGet();
      return parse(agentInfo.getVersion());
    } catch (ApiException e) {
      return Optional.empty();
    }
  }


  private Optional<AgentVersion> parse(String versionString) {
    Matcher matcher = versionPattern.matcher(versionString);
    if (!matcher.find()) {
      return Optional.empty();
    }

    String maj = matcher.group(1);
    String min = matcher.group(2);

    return Optional.of(new AgentVersion(Integer.parseInt(maj), Integer.parseInt(min)));
  }


}
