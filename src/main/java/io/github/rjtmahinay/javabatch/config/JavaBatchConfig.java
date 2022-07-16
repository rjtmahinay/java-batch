package io.github.rjtmahinay.javabatch.config;

import io.github.rjtmahinay.javabatch.mapper.JavaMapper;
import io.github.rjtmahinay.javabatch.model.BatchResult;
import io.github.rjtmahinay.javabatch.processor.JavaProcessor;
import io.github.rjtmahinay.javabatch.writer.JavaWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import java.util.concurrent.Future;

@Configuration
@EnableBatchProcessing
@Slf4j
public class JavaBatchConfig {

	@Autowired private JobBuilderFactory jobBuilderFactory;
	@Autowired private StepBuilderFactory stepBuilderFactory;

	@Autowired private JavaProcessor javaProcessor;

	// TODO uncomment when using custom writer
	// @Autowired private JavaWriter javaWriter;

	@Bean
	public FlatFileItemReader<BatchResult> flatFileItemReader() {
		DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
		delimitedLineTokenizer.setDelimiter(",");

		FlatFileItemReader<BatchResult> flatFileItemReader = new FlatFileItemReaderBuilder<BatchResult>()
				.name("batchReader")
				.resource(new FileSystemResource("input_file/INPUT_BATCH_FILE.txt"))
				.linesToSkip(1)
				.fieldSetMapper(new JavaMapper())
				.lineTokenizer(delimitedLineTokenizer)
				.build();

		try {
			flatFileItemReader.afterPropertiesSet();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return flatFileItemReader;
	}

	@Bean
	public FlatFileItemWriter<BatchResult> flatFileItemWriter() {
		FlatFileItemWriter<BatchResult> flatFileItemWriter = new FlatFileItemWriterBuilder<BatchResult>()
				.name("batchWriter")
				.resource(new FileSystemResource("output_file/OUTPUT_BATCH_FILE.txt"))
				.delimited()
				.delimiter("|")
				.names("name","id")
				.headerCallback(writer -> writer.write("NAME|ID"))
				.build();

		try {
			flatFileItemWriter.afterPropertiesSet();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return flatFileItemWriter;
	}

	@Bean
	public AsyncItemWriter<BatchResult> asyncItemWriter() {
		AsyncItemWriter<BatchResult> asyncItemWriter = new AsyncItemWriter<>();
		asyncItemWriter.setDelegate(flatFileItemWriter());

		return asyncItemWriter;
	}

	@Bean
	public AsyncItemProcessor<BatchResult, BatchResult> asyncItemProcessor() {
		AsyncItemProcessor<BatchResult, BatchResult> asyncItemProcessor = new AsyncItemProcessor<>();
		asyncItemProcessor.setDelegate(javaProcessor);
		asyncItemProcessor.setTaskExecutor(taskExecutor());

		return asyncItemProcessor;
	}

	@Bean
	public TaskExecutor taskExecutor() {
		return new SimpleAsyncTaskExecutor("java-batch-");
	}

	@Bean
	public Step batchStep() {
		return stepBuilderFactory
				.get("batchStep")
				.<BatchResult, Future<BatchResult>>chunk(3)
				.reader(flatFileItemReader())
				.processor(asyncItemProcessor())
				.writer(asyncItemWriter())
				.build();
	}

	@Bean
	public Job batchJob() {
		return jobBuilderFactory
				.get("batchJob")
				.start(batchStep())
				.incrementer(new RunIdIncrementer())
				.build();
	}
}
