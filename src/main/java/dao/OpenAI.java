package dao;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.stream.Collectors;

public class OpenAI {
	public static List<AlbumDTO> getRecommendations(List<AlbumDTO> tvShowsAndRatings,
	                                                Integer numberOfSuggestions) {
		final String KEY = System.getenv("OPENAI_API_KEY");
		final String CHAT_URL = "https://api.openai.com/v1/chat/completions";
		ArrayList<AlbumDTO> recommendations = new ArrayList<>();
		String prompt = """
				Recommend me %s shows based in this evaluation:
				%sand just give me an enumerated list of titles of the show without description of your recommendations
								""".formatted(
				numberOfSuggestions,
				tvShowsAndRatings.stream()
				                 .map(show -> "%s %s/4\n".formatted(show.title(), String.format("%.2f", show.rating())))
				                 .collect(Collectors.joining())
		);
//		System.out.println(prompt);
		Map<String, String> message = new HashMap<>();
		message.put("role", "user");
		message.put("content", prompt);
		List<Map<String,String>> messages = new ArrayList<>();
		messages.add(message);
		Map<String, Object> body = new HashMap<>();
		body.put("model", "gpt-3.5-turbo");
		body.put("messages", messages);
		ObjectMapper mapper = new ObjectMapper();
		HttpClient client = HttpClient.newHttpClient();
		try {
			HttpRequest request = HttpRequest
					.newBuilder()
					.uri(URI.create(CHAT_URL))
					.headers("Content-Type", "application/json", "Authorization", "Bearer " + KEY)
					.POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body)))
				.build();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			String recomendations = mapper.readTree(response.body()).get("choices").get(0).get("message").get("content").toString();
			Arrays.stream(recomendations.split("\\\\n"))
			      .forEach(recommendation -> {
				      String trimmed = recommendation.replace("\"", "").trim();
				      AlbumDTO albumDTO = new AlbumDTO(
						      trimmed.split("\\.")[1],
						      null,
						      Integer.parseInt(trimmed.split("\\.")[0]), null, null);
							recommendations.add(albumDTO);
			      });
		} catch (Exception e) {
			System.out.println("Error");
		}
		return recommendations;
	}
}
