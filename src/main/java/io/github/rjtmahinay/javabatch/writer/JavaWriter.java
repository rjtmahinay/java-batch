package io.github.rjtmahinay.javabatch.writer;

import io.github.rjtmahinay.javabatch.model.BatchResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class JavaWriter implements ItemWriter<BatchResult> {

	@Override
	public void write(List<? extends BatchResult> items) throws Exception {
		// Succeeding logic
	}
}
