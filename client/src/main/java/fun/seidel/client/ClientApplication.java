package fun.seidel.client;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.shell.jline.PromptProvider;

@SpringBootApplication
public class ClientApplication {

	public static void main(String[] args) {
		Logger.getRootLogger().setLevel(Level.OFF);

		SpringApplication app = new SpringApplication(ClientApplication.class);
		app.setBannerMode(Banner.Mode.OFF);
		app.setLogStartupInfo(false);
		app.run(args);
	}

	@Bean
	public PromptProvider promptProvider() {
		return () -> new AttributedString("coap-chat:>", AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));
	}
}
