package dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

public class OpenAITest {
	@Test
	void parser() {
		String response =
				"{\"id\":\"chatcmpl-7BU7jWsRsDyCW75ZqINoAv6AZZ4uf\"," +
				"\"object\":\"chat.completion\"," +
				"\"created\":1682971759," +
				"\"model\": \"gpt-3.5-turbo-0301\"," +
				"\"usage\":{\"prompt_tokens\":55,\"completion_tokens\":27,\"total_tokens\":82}," +
				"\"choices\":[" +
				"{\"message\":" +
				"{\"role\":\"assistant\",\"content\":\"1. Breaking Bad\\n2. Stranger Things\\n3. The Crown\\n4. Narcos\\n5. " +
				"The Handmaid's Tale\"}," +
				"\"finish_reason\":\"stop\"," +
				"\"index\":0}]}";
		ObjectMapper mapper = new ObjectMapper();
		try {
			String content = mapper.readTree(response).get("choices").get(0).get("message").get("content").toString();
			System.out.println(content);
			String[] split = content.split("\\\\n");
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
}
