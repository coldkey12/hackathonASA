package kz.nomads.hackathonASA;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class HackathonAsaApplication {

	@Value("${openai.api.key}")
	private String openAiKey;

	public static void main(String[] args) {
		SpringApplication.run(HackathonAsaApplication.class, args);
	}

	@Bean
	public RestTemplate RestTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getInterceptors().add((((request, body, execution) -> {
			request.getHeaders().add("Authorization",
					"Bearer " + openAiKey);
			return execution.execute(request, body);
		})));
		return restTemplate;
	}

}
