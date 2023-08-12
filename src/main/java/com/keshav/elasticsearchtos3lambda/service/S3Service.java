package com.keshav.elasticsearchtos3lambda.service;

import java.io.ByteArrayInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;

@Service
public class S3Service {

	private final Logger LOGGER = LogManager.getLogger(S3Service.class);

	@Value("${aws.s3.elasticsearch.data.file.name:elasticsearchdata}")
	private String s3FileName;
	@Value("${aws.s3.elasticsearch.data.bucket.name:elasticsearch-to-s3-lambda-bucket}")
	private String s3BucketName;

	@Autowired
	private AmazonS3 s3Client;

	public void putObjectTOS3(String jsonData) {
		LOGGER.info("putObjectTOS3 jsonData: {}", jsonData);
		String objectKey = s3FileName + ".json";
		// Upload the JSON string to S3
        byte[] contentBytes = jsonData.getBytes();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(contentBytes.length);
        try {
        	s3Client.putObject(s3BucketName, objectKey, new ByteArrayInputStream(contentBytes), metadata);
        	LOGGER.info("s3FileName:{} updated successfully", s3FileName);
		} catch (SdkClientException e) {
			LOGGER.error("Unable to putObjectTOS3", e);
		}
	}
}