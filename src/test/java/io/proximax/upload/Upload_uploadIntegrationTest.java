package io.proximax.upload;

import com.google.common.io.Files;
import io.nem.sdk.model.blockchain.NetworkType;
import io.proximax.connection.BlockchainNetworkConnection;
import io.proximax.connection.ConnectionConfig;
import io.proximax.connection.IpfsConnection;
import io.proximax.model.PrivacyType;
import io.proximax.model.StoreType;
import org.junit.Before;
import org.junit.Test;

import static io.proximax.testsupport.Constants.BLOCKCHAIN_ENDPOINT_URL;
import static io.proximax.testsupport.Constants.HTML_FILE;
import static io.proximax.testsupport.Constants.IMAGE_FILE;
import static io.proximax.testsupport.Constants.IPFS_MULTI_ADDRESS;
import static io.proximax.testsupport.Constants.PDF_FILE1;
import static io.proximax.testsupport.Constants.PRIVATE_KEY_1;
import static io.proximax.testsupport.Constants.PUBLIC_KEY_2;
import static io.proximax.testsupport.Constants.SMALL_FILE;
import static io.proximax.testsupport.Constants.STRING_TEST;
import static io.proximax.testsupport.TestHelper.logAndSaveResult;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonMap;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

public class Upload_uploadIntegrationTest {

	private Upload unitUnderTest;

	@Before
	public void setUp() {
		unitUnderTest = new Upload(ConnectionConfig.create(
				new BlockchainNetworkConnection(NetworkType.MIJIN_TEST, BLOCKCHAIN_ENDPOINT_URL),
				new IpfsConnection(IPFS_MULTI_ADDRESS)));
	}

	@Test
	public void shouldUploadByteArray() throws Exception {
		final UploadParameter param = UploadParameter.create(PRIVATE_KEY_1, PUBLIC_KEY_2)
				.addByteArray(Files.toByteArray(PDF_FILE1), "byte array description", "byte array", "application/pdf",
						singletonMap("bytearraykey", "bytearrayval"))
				.build();

		final UploadResult result = unitUnderTest.upload(param);

		assertThat(result, is(notNullValue()));
		assertThat(result.getTransactionHash(), is(notNullValue()));
		assertThat(result.getDigest(), is(notNullValue()));
		assertThat(result.getRootDataHash(), is(notNullValue()));
		assertThat(result.getRootData(), is(notNullValue()));
		assertThat(result.getRootData().getDataList(), hasSize(1));
		assertThat(result.getRootData().getDataList().get(0).getContentType(), is("application/pdf"));
		assertThat(result.getRootData().getDataList().get(0).getDataHash(), is(notNullValue()));
		assertThat(result.getRootData().getDataList().get(0).getDigest(), is(notNullValue()));
		assertThat(result.getRootData().getDataList().get(0).getDescription(), is("byte array description"));
		assertThat(result.getRootData().getDataList().get(0).getName(), is("byte array"));
		assertThat(result.getRootData().getDataList().get(0).getMetadata(), is(singletonMap("bytearraykey", "bytearrayval")));
		assertThat(result.getRootData().getDataList().get(0).getTimestamp(), is(notNullValue()));

		logAndSaveResult(result, getClass().getSimpleName() + ".shouldUploadByteArray");
	}

	@Test
	public void shouldUploadFile() throws Exception {
		final UploadParameter param = UploadParameter.create(PRIVATE_KEY_1, PUBLIC_KEY_2)
				.addFile(SMALL_FILE, "file description", "file name", "text/plain",
						singletonMap("filekey", "filename"))
				.build();

		final UploadResult result = unitUnderTest.upload(param);

		assertThat(result, is(notNullValue()));
		assertThat(result.getTransactionHash(), is(notNullValue()));
		assertThat(result.getDigest(), is(notNullValue()));
		assertThat(result.getRootDataHash(), is(notNullValue()));
		assertThat(result.getRootData(), is(notNullValue()));
		assertThat(result.getRootData().getDataList(), hasSize(1));
		assertThat(result.getRootData().getDataList().get(0).getContentType(), is("text/plain"));
		assertThat(result.getRootData().getDataList().get(0).getDataHash(), is(notNullValue()));
		assertThat(result.getRootData().getDataList().get(0).getDigest(), is(notNullValue()));
		assertThat(result.getRootData().getDataList().get(0).getDescription(), is("file description"));
		assertThat(result.getRootData().getDataList().get(0).getName(), is("file name"));
		assertThat(result.getRootData().getDataList().get(0).getMetadata(), is(singletonMap("filekey", "filename")));
		assertThat(result.getRootData().getDataList().get(0).getTimestamp(), is(notNullValue()));

		logAndSaveResult(result, getClass().getSimpleName() + ".shouldUploadFile");
	}

	@Test
	public void shouldUploadUrlResource() throws Exception {
		final UploadParameter param = UploadParameter.create(PRIVATE_KEY_1, PUBLIC_KEY_2)
				.addUrlResource(IMAGE_FILE.toURI().toURL(), "url description", "url name", "image/png",
						singletonMap("urlkey", "urlval"))
				.build();

		final UploadResult result = unitUnderTest.upload(param);

		assertThat(result, is(notNullValue()));
		assertThat(result.getTransactionHash(), is(notNullValue()));
		assertThat(result.getDigest(), is(notNullValue()));
		assertThat(result.getRootDataHash(), is(notNullValue()));
		assertThat(result.getRootData(), is(notNullValue()));
		assertThat(result.getRootData().getDataList(), hasSize(1));
		assertThat(result.getRootData().getDataList().get(0).getContentType(), is("image/png"));
		assertThat(result.getRootData().getDataList().get(0).getDataHash(), is(notNullValue()));
		assertThat(result.getRootData().getDataList().get(0).getDigest(), is(notNullValue()));
		assertThat(result.getRootData().getDataList().get(0).getDescription(), is("url description"));
		assertThat(result.getRootData().getDataList().get(0).getName(), is("url name"));
		assertThat(result.getRootData().getDataList().get(0).getMetadata(), is(singletonMap("urlkey", "urlval")));
		assertThat(result.getRootData().getDataList().get(0).getTimestamp(), is(notNullValue()));

		logAndSaveResult(result, getClass().getSimpleName() + ".shouldUploadUrlResource");
	}

	@Test
	public void shouldUploadFilesAsZip() throws Exception {
		final UploadParameter param = UploadParameter.create(PRIVATE_KEY_1, PUBLIC_KEY_2)
				.addFilesAsZip(asList(SMALL_FILE, HTML_FILE), "zip description", "zip name",
						singletonMap("zipkey", "zipvalue"))
				.build();

		final UploadResult result = unitUnderTest.upload(param);

		assertThat(result, is(notNullValue()));
		assertThat(result.getTransactionHash(), is(notNullValue()));
		assertThat(result.getDigest(), is(notNullValue()));
		assertThat(result.getRootDataHash(), is(notNullValue()));
		assertThat(result.getRootData(), is(notNullValue()));
		assertThat(result.getRootData().getDataList(), hasSize(1));
		assertThat(result.getRootData().getDataList().get(0).getContentType(), is("application/zip"));
		assertThat(result.getRootData().getDataList().get(0).getDataHash(), is(notNullValue()));
		assertThat(result.getRootData().getDataList().get(0).getDigest(), is(notNullValue()));
		assertThat(result.getRootData().getDataList().get(0).getDescription(), is("zip description"));
		assertThat(result.getRootData().getDataList().get(0).getName(), is("zip name"));
		assertThat(result.getRootData().getDataList().get(0).getMetadata(), is(singletonMap("zipkey", "zipvalue")));
		assertThat(result.getRootData().getDataList().get(0).getTimestamp(), is(notNullValue()));

		logAndSaveResult(result, getClass().getSimpleName() + ".shouldUploadFilesAsZip");
	}

	@Test
	public void shouldUploadString() throws Exception {
		final UploadParameter param = UploadParameter.create(PRIVATE_KEY_1, PUBLIC_KEY_2)
				.addString(STRING_TEST, null, "string description", "string name",
						"text/plain", singletonMap("keystring", "valstring"))
				.build();

		final UploadResult result = unitUnderTest.upload(param);

		assertThat(result, is(notNullValue()));
		assertThat(result.getTransactionHash(), is(notNullValue()));
		assertThat(result.getDigest(), is(notNullValue()));
		assertThat(result.getRootDataHash(), is(notNullValue()));
		assertThat(result.getRootData(), is(notNullValue()));
		assertThat(result.getRootData().getDataList(), hasSize(1));
		assertThat(result.getRootData().getDataList().get(0).getContentType(), is("text/plain"));
		assertThat(result.getRootData().getDataList().get(0).getDataHash(), is(notNullValue()));
		assertThat(result.getRootData().getDataList().get(0).getDigest(), is(notNullValue()));
		assertThat(result.getRootData().getDataList().get(0).getDescription(), is("string description"));
		assertThat(result.getRootData().getDataList().get(0).getName(), is("string name"));
		assertThat(result.getRootData().getDataList().get(0).getMetadata(), is(singletonMap("keystring", "valstring")));
		assertThat(result.getRootData().getDataList().get(0).getTimestamp(), is(notNullValue()));

		logAndSaveResult(result, getClass().getSimpleName() + ".shouldUploadString");
	}

	@Test
	public void shouldUploadWithAllDetails() throws Exception {
		final UploadParameter param = UploadParameter.create(PRIVATE_KEY_1, PUBLIC_KEY_2)
				.addByteArray(ByteArrayParameterData.create(Files.toByteArray(PDF_FILE1))
						.name("byte array")
						.description("byte array description")
						.contentType("text/plain")
						.metadata(singletonMap("key1", "val1"))
						.build())
				.description("root description")
				.securedWithNemKeysPrivacyStrategy("nemkeys")
				.computeDigest(true)
				.storeType(StoreType.BLOCK)
				.build();

		final UploadResult result = unitUnderTest.upload(param);

		assertThat(result, is(notNullValue()));
		assertThat(result.getTransactionHash(), is(notNullValue()));
		assertThat(result.getDigest(), is(notNullValue()));
		assertThat(result.getRootDataHash(), is(notNullValue()));
		assertThat(result.getRootData(), is(notNullValue()));
		assertThat(result.getRootData().getDescription(), is("root description"));
		assertThat(result.getRootData().getPrivacyType(), is(PrivacyType.NEMKEYS.getValue()));
		assertThat(result.getRootData().getPrivacySearchTag(), is("nemkeys"));
		assertThat(result.getRootData().getStoreType(), is(StoreType.BLOCK));
		assertThat(result.getRootData().getDataList(), hasSize(1));
		assertThat(result.getRootData().getDataList().get(0).getContentType(), is("text/plain"));
		assertThat(result.getRootData().getDataList().get(0).getDataHash(), is(notNullValue()));
		assertThat(result.getRootData().getDataList().get(0).getDigest(), is(notNullValue()));
		assertThat(result.getRootData().getDataList().get(0).getDescription(), is("byte array description"));
		assertThat(result.getRootData().getDataList().get(0).getName(), is("byte array"));
		assertThat(result.getRootData().getDataList().get(0).getMetadata(), is(singletonMap("key1", "val1")));
		assertThat(result.getRootData().getDataList().get(0).getTimestamp(), is(notNullValue()));

		logAndSaveResult(result, getClass().getSimpleName() + ".shouldUploadWithAllDetails");
	}

	@Test
	public void shouldUploadMultipleData() throws Exception {
		final UploadParameter param = UploadParameter.create(PRIVATE_KEY_1, PUBLIC_KEY_2)
				.addByteArray(Files.toByteArray(PDF_FILE1))
				.addFile(SMALL_FILE)
				.addUrlResource(IMAGE_FILE.toURI().toURL())
				.addFilesAsZip(asList(SMALL_FILE, HTML_FILE))
				.addString(STRING_TEST)
				.build();

		final UploadResult result = unitUnderTest.upload(param);

		assertThat(result, is(notNullValue()));
		assertThat(result.getTransactionHash(), is(notNullValue()));
		assertThat(result.getDigest(), is(notNullValue()));
		assertThat(result.getRootDataHash(), is(notNullValue()));
		assertThat(result.getRootData(), is(notNullValue()));
		assertThat(result.getRootData().getDataList(), hasSize(5));
		result.getRootData().getDataList().forEach(data -> {
			assertThat(data.getContentType(), is(notNullValue()));
			assertThat(data.getDataHash(), is(notNullValue()));
			assertThat(data.getDigest(), is(notNullValue()));
			assertThat(data.getTimestamp(), is(notNullValue()));
		});

		logAndSaveResult(result, getClass().getSimpleName() + ".shouldUploadMultipleData");
	}
}
