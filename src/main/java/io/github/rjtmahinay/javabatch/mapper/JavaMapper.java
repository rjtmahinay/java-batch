package io.github.rjtmahinay.javabatch.mapper;

import io.github.rjtmahinay.javabatch.model.BatchResult;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;


public class JavaMapper implements FieldSetMapper<BatchResult> {
	@Override
	public BatchResult mapFieldSet(FieldSet fieldSet) throws BindException {
		BatchResult batchResult = new BatchResult();

		batchResult.setId(fieldSet.readString(0));
		batchResult.setName(fieldSet.readString(1));

		return batchResult;
	}
}
