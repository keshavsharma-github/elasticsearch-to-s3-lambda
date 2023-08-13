package com.keshav.elasticsearchtos3lambda.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;

@ExtendWith(MockitoExtension.class)
public class S3ServiceTest {
	@InjectMocks
	private S3Service underTest;

	@Mock
	private AmazonS3 s3Client;

	private final String s3FileName = "elasticsearchdata";
	private final String s3BucketName = "elasticsearch-to-s3-lambda-bucket";

	@BeforeEach
	public void setUp() {
		ReflectionTestUtils.setField(underTest, "s3FileName", s3FileName);
		ReflectionTestUtils.setField(underTest, "s3BucketName", s3BucketName);
	}

	@Test
	public void testPutObjectTOS3_Success() {
		String jsonData = "Test JSON data";
		when(s3Client.putObject(eq(s3BucketName), eq(s3FileName + ".json"), any(ByteArrayInputStream.class),
				any(ObjectMetadata.class))).thenReturn(null);
		underTest.putObjectTOS3(jsonData);

		// Verify that putObject method was called
		verify(s3Client).putObject(eq(s3BucketName), eq(s3FileName + ".json"), any(ByteArrayInputStream.class),
				any(ObjectMetadata.class));
	}

	@Test
	public void testPutObjectTOS3_SdkClientException() {
		String jsonData = "Test JSON data";

		when(s3Client.putObject(eq(s3BucketName), eq(s3FileName + ".json"), any(ByteArrayInputStream.class),
				any(ObjectMetadata.class))).thenThrow(new SdkClientException("Test Exception"));

		underTest.putObjectTOS3(jsonData);

		// Verify that putObject method was called and the exception was logged
		verify(s3Client).putObject(eq(s3BucketName), eq(s3FileName + ".json"), any(ByteArrayInputStream.class),
				any(ObjectMetadata.class));
	}
}