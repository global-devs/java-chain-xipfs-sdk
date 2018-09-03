package io.proximax.upload;

import io.proximax.exceptions.UploadParameterBuildFailureException;
import io.proximax.model.PrivacyType;
import io.proximax.privacy.strategy.PlainPrivacyStrategy;
import io.proximax.privacy.strategy.SecuredWithNemKeysPrivacyStrategy;
import io.proximax.privacy.strategy.SecuredWithPasswordPrivacyStrategy;
import io.proximax.privacy.strategy.SecuredWithShamirSecretSharingPrivacyStrategy;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static io.proximax.model.Constants.SCHEMA_VERSION;
import static io.proximax.privacy.strategy.SecuredWithShamirSecretSharingPrivacyStrategyTest.SECRET_MINIMUM_PART_COUNT_TO_BUILD;
import static io.proximax.privacy.strategy.SecuredWithShamirSecretSharingPrivacyStrategyTest.SECRET_PARTS;
import static io.proximax.privacy.strategy.SecuredWithShamirSecretSharingPrivacyStrategyTest.SECRET_TOTAL_PART_COUNT;
import static io.proximax.testsupport.Constants.HTML_FILE;
import static io.proximax.testsupport.Constants.IMAGE_FILE;
import static io.proximax.testsupport.Constants.PATH_FILE;
import static io.proximax.testsupport.Constants.PDF_FILE1;
import static io.proximax.testsupport.Constants.SMALL_FILE;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;

public class UploadParameterTest {

    public static final byte[] SAMPLE_DATA = "the quick brown fox jumps over the lazy dog".getBytes();

    private static final String SAMPLE_SIGNER_PRIVATE_KEY = "CDB825EBFED7ABA031E19AB6A91B637E5A6B13DACF50F0EA579885F68BED778C";
    private static final String SAMPLE_RECIPIENT_PUBLIC_KEY = "E9F6576AF9F05E6738CD4E55B875A823CC75B4E8AE8984747DF7B235685C1577";

    @Test(expected = IllegalArgumentException.class)
    public void failWhenNullSignerPrivateKey() {
        UploadParameter.create(null, SAMPLE_RECIPIENT_PUBLIC_KEY);
    }

    @Test(expected = IllegalArgumentException.class)
    public void failWhenNullRecipientPublicKey() {
        UploadParameter.create(SAMPLE_SIGNER_PRIVATE_KEY, null);
    }

    @Test(expected = UploadParameterBuildFailureException.class)
    public void failWhenDataNotAdded() {
        UploadParameter.create(SAMPLE_SIGNER_PRIVATE_KEY, SAMPLE_RECIPIENT_PUBLIC_KEY).build();
    }

    @Test
    public void createWithOneData() {
        final UploadParameter param = UploadParameter.create(SAMPLE_SIGNER_PRIVATE_KEY, SAMPLE_RECIPIENT_PUBLIC_KEY)
                .addByteArray(ByteArrayParameterData.create(SAMPLE_DATA).build())
                .build();

        assertThat(param, is(notNullValue()));
        assertThat(param.getDescription(), is(nullValue()));
        assertThat(param.getRecipientPublicKey(), is(SAMPLE_RECIPIENT_PUBLIC_KEY));
        assertThat(param.getSignerPrivateKey(), is(SAMPLE_SIGNER_PRIVATE_KEY));
        assertThat(param.getVersion(), is(SCHEMA_VERSION));
        assertThat(param.getComputeDigest(), is(true));
        assertThat(param.getPrivacyStrategy().getPrivacyType(), is(PrivacyType.PLAIN.getValue()));
        assertThat(param.getDataList(), hasSize(1));
    }

    @Test
    public void createWithAllDataTypes() throws IOException {
        final UploadParameter param = UploadParameter.create(SAMPLE_SIGNER_PRIVATE_KEY, SAMPLE_RECIPIENT_PUBLIC_KEY)
                .addByteArray(ByteArrayParameterData.create(FileUtils.readFileToByteArray(PDF_FILE1)).build())
                .addFile(FileParameterData.create(SMALL_FILE).build())
                .addUrlResource(UrlResourceParameterData.create(IMAGE_FILE.toURI().toURL()).build())
                .addFilesAsZip(FilesAsZipParameterData.create(asList(SMALL_FILE, HTML_FILE)).build())
                .addString(StringParameterData.create("dasdasdsa ewqe wq dsa sadsads").build())
                .addPath(PathParameterData.create(PATH_FILE).build())
                .computeDigest(false)
                .description("root description")
                .privacyStrategy(SecuredWithNemKeysPrivacyStrategy.create(SAMPLE_SIGNER_PRIVATE_KEY, SAMPLE_RECIPIENT_PUBLIC_KEY))
                .build();

        assertThat(param, is(notNullValue()));
        assertThat(param.getDescription(), is("root description"));
        assertThat(param.getRecipientPublicKey(), is(SAMPLE_RECIPIENT_PUBLIC_KEY));
        assertThat(param.getSignerPrivateKey(), is(SAMPLE_SIGNER_PRIVATE_KEY));
        assertThat(param.getVersion(), is(SCHEMA_VERSION));
        assertThat(param.getComputeDigest(), is(false));
        assertThat(param.getPrivacyStrategy().getPrivacyType(), is(PrivacyType.NEMKEYS.getValue()));
        assertThat(param.getDataList(), hasSize(6));
    }

    @Test(expected = IllegalArgumentException.class)
    public void failWhenRootDescriptionExceedsCharacterLimit() {
        UploadParameter.create(SAMPLE_SIGNER_PRIVATE_KEY, SAMPLE_RECIPIENT_PUBLIC_KEY)
                .addByteArray(ByteArrayParameterData.create(SAMPLE_DATA).build())
                .description("djshakh nm,cnxz, nkjdhsajkh kjdhsahdiuyqwuieywqj dkhsakjh kjdhwquyeiqwuyeuiwqhjk dnkjsah kdjsa" +
                        "eqwyuioeywoquydhsajk n,mxnz,mn ,mcnsayduioyqweywoqy dhsahdksjahdkshakjowyqoieqwoieuowiquowuqoejwqljleq" +
                        "eywhqkdsanc,xzn ,mcnzx,mn,mdnsakjhdwqioiupquepwqeoqwjekjqwlkejlwqkjelkqwjlkejwqlkejqlkjelkwjlejwqkleq" +
                        "ekwjqhekjqwheuiowqueoiqwuioeuwqidklna,mcnzx,nc,mzxndyoqiyueiowqueioqwuoejwqlkjewlkqjelkwqjlkejwqlkjeqwl" +
                        "eoiuwqyeoiwqhkjehqwjknem,nsm,and,masn,dnasewqhjelwqhlehqwjehwqkhekqjwheklqwjelkqwjlkeqjlkejklwqjlekwqj" +
                        "hekwqhkehwqkehwqjknm,dnz,mnc,zxnodisaoidujlijwqlkejwqlkjelwkqjeklqwjklejwqkjeqwlkjelqwkjew,am,.zmc.mzx");
    }

    @Test
    public void createWithAllDataTypesUsingShortcutMethod() throws IOException {
        final UploadParameter param = UploadParameter.create(SAMPLE_SIGNER_PRIVATE_KEY, SAMPLE_RECIPIENT_PUBLIC_KEY)
                .addByteArray(FileUtils.readFileToByteArray(PDF_FILE1))
                .addFile(SMALL_FILE)
                .addUrlResource(IMAGE_FILE.toURI().toURL())
                .addFilesAsZip(asList(SMALL_FILE, HTML_FILE))
                .addString("dasdasdsa ewqe wq dsa sadsads")
                .addPath(PATH_FILE)
                .computeDigest(false)
                .description("root description")
                .privacyStrategy(SecuredWithNemKeysPrivacyStrategy.create(SAMPLE_SIGNER_PRIVATE_KEY, SAMPLE_RECIPIENT_PUBLIC_KEY))
                .build();

        assertThat(param, is(notNullValue()));
        assertThat(param.getDescription(), is("root description"));
        assertThat(param.getRecipientPublicKey(), is(SAMPLE_RECIPIENT_PUBLIC_KEY));
        assertThat(param.getSignerPrivateKey(), is(SAMPLE_SIGNER_PRIVATE_KEY));
        assertThat(param.getVersion(), is(SCHEMA_VERSION));
        assertThat(param.getComputeDigest(), is(false));
        assertThat(param.getPrivacyStrategy().getPrivacyType(), is(PrivacyType.NEMKEYS.getValue()));
        assertThat(param.getDataList(), hasSize(6));
    }

    @Test
    public void createWithAllDataTypesUsingExplicitMethod() throws IOException {
        final UploadParameter param = UploadParameter.create(SAMPLE_SIGNER_PRIVATE_KEY, SAMPLE_RECIPIENT_PUBLIC_KEY)
                .addByteArray(FileUtils.readFileToByteArray(PDF_FILE1), null, null, null, null)
                .addFile(SMALL_FILE, null, null, null, null)
                .addUrlResource(IMAGE_FILE.toURI().toURL(), null, null, null, null)
                .addFilesAsZip(asList(SMALL_FILE, HTML_FILE), null, null, null)
                .addString("dasdasdsa ewqe wq dsa sadsads", null, null, null, null, null)
                .addPath(PATH_FILE, null, null, null)
                .computeDigest(false)
                .description("root description")
                .privacyStrategy(SecuredWithNemKeysPrivacyStrategy.create(SAMPLE_SIGNER_PRIVATE_KEY, SAMPLE_RECIPIENT_PUBLIC_KEY))
                .build();

        assertThat(param, is(notNullValue()));
        assertThat(param.getDescription(), is("root description"));
        assertThat(param.getRecipientPublicKey(), is(SAMPLE_RECIPIENT_PUBLIC_KEY));
        assertThat(param.getSignerPrivateKey(), is(SAMPLE_SIGNER_PRIVATE_KEY));
        assertThat(param.getVersion(), is(SCHEMA_VERSION));
        assertThat(param.getComputeDigest(), is(false));
        assertThat(param.getPrivacyStrategy().getPrivacyType(), is(PrivacyType.NEMKEYS.getValue()));
        assertThat(param.getDataList(), hasSize(6));
    }

    @Test
    public void shouldCreateWithPlainPrivacy() throws UnsupportedEncodingException {
        final UploadParameter param = UploadParameter.create(SAMPLE_SIGNER_PRIVATE_KEY, SAMPLE_RECIPIENT_PUBLIC_KEY)
                .addString("hkdhskahdsakjhdkjas")
                .plainPrivacy()
                .build();

        assertThat(param.getPrivacyStrategy(), instanceOf(PlainPrivacyStrategy.class));
    }

    @Test
    public void shouldCreateWithSecuredWithNemKeysPrivacy() throws UnsupportedEncodingException {
        final UploadParameter param = UploadParameter.create(SAMPLE_SIGNER_PRIVATE_KEY, SAMPLE_RECIPIENT_PUBLIC_KEY)
                .addString("hkdhskahdsakjhdkjas")
                .securedWithNemKeysPrivacy()
                .build();

        assertThat(param.getPrivacyStrategy(), instanceOf(SecuredWithNemKeysPrivacyStrategy.class));
    }

    @Test
    public void shouldCreateWithSecuredWithPasswordPrivacy() throws UnsupportedEncodingException {
        final UploadParameter param = UploadParameter.create(SAMPLE_SIGNER_PRIVATE_KEY, SAMPLE_RECIPIENT_PUBLIC_KEY)
                .addString("hkdhskahdsakjhdkjas")
                .securedWithPasswordPrivacy("hdksahjkdhsakjhdsajhdkjhsajkdsbajjdhsajkhdjksahjkdahjkhdkjsahjdsadasdsadas")
                .build();

        assertThat(param.getPrivacyStrategy(), instanceOf(SecuredWithPasswordPrivacyStrategy.class));
    }

    @Test
    public void shouldCreateWithSecuredWithShamirSecretSharingMapStrategy() throws UnsupportedEncodingException {
        final Map<Integer, byte[]> minimumSecretParts = new HashMap<>();
        minimumSecretParts.put(1, SECRET_PARTS.get(1));
        minimumSecretParts.put(3, SECRET_PARTS.get(3));
        minimumSecretParts.put(5, SECRET_PARTS.get(5));
        final UploadParameter param = UploadParameter.create(SAMPLE_SIGNER_PRIVATE_KEY, SAMPLE_RECIPIENT_PUBLIC_KEY)
                .addString("hkdhskahdsakjhdkjas")
                .securedWithShamirSecretSharing(SECRET_TOTAL_PART_COUNT, SECRET_MINIMUM_PART_COUNT_TO_BUILD,
                        minimumSecretParts)
                .build();

        assertThat(param.getPrivacyStrategy(), instanceOf(SecuredWithShamirSecretSharingPrivacyStrategy.class));
    }

    @Test
    public void shouldCreateWithSecuredWithShamirSecretSharingArrayStrategy() throws UnsupportedEncodingException {
        final UploadParameter param = UploadParameter.create(SAMPLE_SIGNER_PRIVATE_KEY, SAMPLE_RECIPIENT_PUBLIC_KEY)
                .addString("hkdhskahdsakjhdkjas")
                .securedWithShamirSecretSharing(SECRET_TOTAL_PART_COUNT, SECRET_MINIMUM_PART_COUNT_TO_BUILD,
                        new SecuredWithShamirSecretSharingPrivacyStrategy.SecretPart(1, SECRET_PARTS.get(1)),
                        new SecuredWithShamirSecretSharingPrivacyStrategy.SecretPart(3, SECRET_PARTS.get(3)),
                        new SecuredWithShamirSecretSharingPrivacyStrategy.SecretPart(5, SECRET_PARTS.get(5)))
                .build();

        assertThat(param.getPrivacyStrategy(), instanceOf(SecuredWithShamirSecretSharingPrivacyStrategy.class));
    }

    @Test
    public void shouldCreateWithSecuredWithShamirSecretSharingListStrategy() throws UnsupportedEncodingException {
        final UploadParameter param = UploadParameter.create(SAMPLE_SIGNER_PRIVATE_KEY, SAMPLE_RECIPIENT_PUBLIC_KEY)
                .addString("hkdhskahdsakjhdkjas")
                .securedWithShamirSecretSharing(SECRET_TOTAL_PART_COUNT, SECRET_MINIMUM_PART_COUNT_TO_BUILD,
                        asList(
                                new SecuredWithShamirSecretSharingPrivacyStrategy.SecretPart(1, SECRET_PARTS.get(1)),
                                new SecuredWithShamirSecretSharingPrivacyStrategy.SecretPart(3, SECRET_PARTS.get(3)),
                                new SecuredWithShamirSecretSharingPrivacyStrategy.SecretPart(5, SECRET_PARTS.get(5))))
                .build();

        assertThat(param.getPrivacyStrategy(), instanceOf(SecuredWithShamirSecretSharingPrivacyStrategy.class));
    }
}
