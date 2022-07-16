package io.github.rjtmahinay.javabatch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class JavaBatchApplication implements CommandLineRunner {

	@Autowired private JobLauncher jobLauncher;
	@Autowired private Job batchJob;

	public static void main(String[] args) {
		SpringApplication.run(JavaBatchApplication.class, args);

		log.info("Java Batch");
	}

	@Override
	public void run(String... args) throws Exception {

		JobParameters jobParameters = new JobParametersBuilder()
				.addLong("time", System.currentTimeMillis()).toJobParameters();

		JobExecution jobExecution = jobLauncher.run(batchJob, jobParameters);

		ExitStatus exitStatus = jobExecution.getExitStatus();

		if (ExitStatus.FAILED.getExitCode().equals(exitStatus.getExitCode())) {
			log.info("Job Failed");
		} else {
			log.info("Job Succeeded");
		}
	}
}
