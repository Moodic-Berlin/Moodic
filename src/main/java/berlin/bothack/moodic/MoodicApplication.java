package berlin.bothack.moodic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author vgorin
 *         file created on 11/19/16 2:01 PM
 */


@SpringBootApplication
@ComponentScan(basePackages = "berlin.bothack.moodic")
public class MoodicApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoodicApplication.class, args);
	}

}
