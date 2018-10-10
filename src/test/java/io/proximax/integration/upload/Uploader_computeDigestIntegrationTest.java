package io.proximax.integration.upload;

import io.proximax.connection.BlockchainNetworkConnection;
import io.proximax.connection.ConnectionConfig;
import io.proximax.connection.IpfsConnection;
import io.proximax.integration.IntegrationTestConfig;
import io.proximax.upload.UploadParameter;
import io.proximax.upload.UploadResult;
import io.proximax.upload.Uploader;
import org.junit.Before;
import org.junit.Test;

import static io.proximax.integration.TestDataRepository.logAndSaveResult;
import static io.proximax.testsupport.Constants.TEST_STRING;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;

public class Uploader_computeDigestIntegrationTest {

	private Uploader unitUnderTest;

	@Before
	public void setUp() {
		unitUnderTest = new Uploader(ConnectionConfig.createWithLocalIpfsConnection(
				new BlockchainNetworkConnection(
						IntegrationTestConfig.getBlockchainNetworkType(),
						IntegrationTestConfig.getBlockchainApiHost(),
						IntegrationTestConfig.getBlockchainApiPort(),
						IntegrationTestConfig.getBlockchainApiProtocol()),
				new IpfsConnection(
						IntegrationTestConfig.getIpfsApiHost(),
						IntegrationTestConfig.getIpfsApiPort())));
	}

	@Test
	public void shouldUploadWithEnabledComputeDigest() {
		final UploadParameter param = UploadParameter
				.createForStringUpload(TEST_STRING, IntegrationTestConfig.getPrivateKey1())
				.withComputeDigest(true)
				.build();

		final UploadResult result = unitUnderTest.upload(param);

		assertThat(result, is(notNullValue()));
		assertThat(result.getData().getDigest(), is(notNullValue()));

		logAndSaveResult(result, getClass().getSimpleName() + ".shouldUploadWithEnabledComputeDigest");
	}

	@Test
	public void shouldUploadWithDisabledComputeDigest() {
		final UploadParameter param = UploadParameter
				.createForStringUpload(TEST_STRING, IntegrationTestConfig.getPrivateKey1())
				.withComputeDigest(false)
				.build();

		final UploadResult result = unitUnderTest.upload(param);

		assertThat(result, is(notNullValue()));
		assertThat(result.getData().getDigest(), is(nullValue()));


		logAndSaveResult(result, getClass().getSimpleName() + ".shouldUploadWithDisabledComputeDigest");
	}
}
