package berlin.bothack.moodic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author vgorin
 *         file created on 11/19/16 2:01 PM
 */


@SpringBootApplication
@ComponentScan(basePackages = "berlin.bothack.moodic")
@EnableScheduling
public class MoodicApplication implements SchedulingConfigurer {
	private static final Logger log = LoggerFactory.getLogger(MoodicApplication.class);

	@Autowired
	private Conf conf;

	public static void main(String[] args) {
		SpringApplication.run(MoodicApplication.class, args);
	}

	@PostConstruct
	void postConstruct() {
		log.info("FB_VERIFY_TOKEN: {}", conf.FB_VERIFY_TOKEN);
		log.info("FB_PAGE_ACCESS_TOKEN: {}", conf.FB_PAGE_ACCESS_TOKEN);
	}

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.setScheduler(taskExecutor());
	}

	@Bean(destroyMethod="shutdown")
	public Executor taskExecutor() {
		return Executors.newScheduledThreadPool(10);
	}
}
