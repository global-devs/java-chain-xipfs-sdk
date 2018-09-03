package io.proximax.service;

import io.proximax.download.DownloadDataParameter;
import io.proximax.download.DownloadParameter;
import io.proximax.model.PrivacyType;
import io.proximax.model.ProximaxDataModel;
import io.proximax.model.ProximaxRootDataModel;
import io.proximax.privacy.strategy.PrivacyStrategy;
import io.proximax.utils.DigestUtils;
import io.reactivex.Observable;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.BDDMockito.given;

public class RetrieveProximaxDataServiceTest {

    private static final String DUMMY_DATA_HASH_1 = "Qmdahdksadjksahjk";
    private static final String DUMMY_DATA_HASH_2 = "Qmcxzczxczxczxcxz";
    private static final byte[] DUMMY_DOWNLOADED_DATA_1 = "dopsaipdlsnlxnz,cn,zxnclznxlnldsaldslkaj;as.".getBytes();
    private static final byte[] DUMMY_DOWNLOADED_DATA_2 = "oidpsaipdsakl;elwqnem,nq,mnjksahciuxhzkjcdsa".getBytes();
    private static final String DUMMY_DIGEST_1 = "iowuqoieuqowueoiqw";
    private static final String DUMMY_DIGEST_2 = "sadasdsadsadasdads";
    private static final byte[] DUMMY_DECRYPTED_DATA_1 = "dsajhjdhaskhdksahkdsaljkjlxnzcm,nxz".getBytes();
    private static final byte[] DUMMY_DECRYPTED_DATA_2 = "icjoiuewoiqueioqwjekwq,.eqwn,mnqwio".getBytes();
    private static final String DUMMY_ROOT_DESCRIPTION = "ewqeqwewqeqweqw";
    private static final String DUMMY_VERSION = "1.0";

    private RetrieveProximaxDataService unitUnderTest;

    @Mock
    private IpfsDownloadService mockIpfsDownloadService;

    @Mock
    private DigestUtils mockDigestUtils;

    @Mock
    private PrivacyStrategy mockPrivacyStrategy;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        unitUnderTest = new RetrieveProximaxDataService(mockIpfsDownloadService, mockDigestUtils);
    }

    @Test(expected = IllegalArgumentException.class)
    public void failOnGetDataWhenNullDownloadDataParameter() {
        unitUnderTest.getData(null);
    }

    @Test
    public void shouldReturnByteArrayOnGetData() {
        given(mockIpfsDownloadService.download(DUMMY_DATA_HASH_1)).willReturn(Observable.just(DUMMY_DOWNLOADED_DATA_1));
        given(mockIpfsDownloadService.download(DUMMY_DATA_HASH_2)).willReturn(Observable.just(DUMMY_DOWNLOADED_DATA_2));
        given(mockDigestUtils.validateDigest(DUMMY_DOWNLOADED_DATA_1, DUMMY_DIGEST_1)).willReturn(Observable.just(true));
        given(mockDigestUtils.validateDigest(DUMMY_DOWNLOADED_DATA_2, DUMMY_DIGEST_2)).willReturn(Observable.just(true));
        given(mockPrivacyStrategy.decryptData(DUMMY_DOWNLOADED_DATA_1)).willReturn(DUMMY_DECRYPTED_DATA_1);
        given(mockPrivacyStrategy.decryptData(DUMMY_DOWNLOADED_DATA_2)).willReturn(DUMMY_DECRYPTED_DATA_2);

        final byte[] result = unitUnderTest.getData(sampleDownloadDataParameter()).blockingFirst();

        assertThat(result, is(DUMMY_DECRYPTED_DATA_1));
    }


    @Test(expected = IllegalArgumentException.class)
    public void failOnGetDataListWhenNullDownloadParameter() {
        unitUnderTest.getDataList(null, sampleRootData());
    }

    @Test(expected = IllegalArgumentException.class)
    public void failOnGetDataListWhenNullRootData() {
        unitUnderTest.getDataList(sampleDownloadParameter(), null);
    }

    @Test
    public void shouldReturnByteArrayListOnGetDataList() {
        given(mockIpfsDownloadService.download(DUMMY_DATA_HASH_1)).willReturn(Observable.just(DUMMY_DOWNLOADED_DATA_1));
        given(mockIpfsDownloadService.download(DUMMY_DATA_HASH_2)).willReturn(Observable.just(DUMMY_DOWNLOADED_DATA_2));
        given(mockDigestUtils.validateDigest(DUMMY_DOWNLOADED_DATA_1, DUMMY_DIGEST_1)).willReturn(Observable.just(true));
        given(mockDigestUtils.validateDigest(DUMMY_DOWNLOADED_DATA_2, DUMMY_DIGEST_2)).willReturn(Observable.just(true));
        given(mockPrivacyStrategy.decryptData(DUMMY_DOWNLOADED_DATA_1)).willReturn(DUMMY_DECRYPTED_DATA_1);
        given(mockPrivacyStrategy.decryptData(DUMMY_DOWNLOADED_DATA_2)).willReturn(DUMMY_DECRYPTED_DATA_2);

        final List<byte[]> result = unitUnderTest.getDataList(sampleDownloadParameter(), sampleRootData()).blockingFirst();

        assertThat(result, is(notNullValue()));
        assertThat(result, hasSize(2));
        assertThat(result.get(0), is(DUMMY_DECRYPTED_DATA_1));
        assertThat(result.get(1), is(DUMMY_DECRYPTED_DATA_2));
    }

    private DownloadDataParameter sampleDownloadDataParameter() {
        return DownloadDataParameter.create(DUMMY_DATA_HASH_1)
                .digest(DUMMY_DIGEST_1)
                .privacyStrategy(mockPrivacyStrategy)
                .build();
    }

    private DownloadParameter sampleDownloadParameter() {
        return DownloadParameter.createWithTransactionHash("hdksjahdkhsakjdhkjsahkdsahjkdsa")
                .privacyStrategy(mockPrivacyStrategy)
                .build();
    }

    private ProximaxRootDataModel sampleRootData() {
        return new ProximaxRootDataModel(PrivacyType.PLAIN.getValue(), DUMMY_ROOT_DESCRIPTION,
                DUMMY_VERSION, asList(
                new ProximaxDataModel(DUMMY_DIGEST_1, DUMMY_DATA_HASH_1, "data 1",
                        singletonMap("key1", "value1"), 1000L, "data name 1", "text/plain"),
                new ProximaxDataModel(DUMMY_DIGEST_2, DUMMY_DATA_HASH_2, "data 2",
                        singletonMap("key2", "value2"), 2000L, "data name 2", "text/html")
        ));
    }


}
