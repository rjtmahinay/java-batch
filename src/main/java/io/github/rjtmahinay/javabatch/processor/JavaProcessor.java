package io.github.rjtmahinay.javabatch.processor;

import io.github.rjtmahinay.javabatch.model.BatchResult;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class JavaProcessor implements ItemProcessor<BatchResult, BatchResult> {

	@Override
	public BatchResult process(BatchResult item) throws Exception {
		return item;
	}
}
