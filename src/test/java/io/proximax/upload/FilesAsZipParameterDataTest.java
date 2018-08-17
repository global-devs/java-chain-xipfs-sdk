package io.proximax.upload;

import org.junit.Test;

import java.io.IOException;

import static io.proximax.testsupport.Constants.HTML_FILE;
import static io.proximax.testsupport.Constants.IMAGE_FILE;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;

public class FilesAsZipParameterDataTest {

    @Test(expected = IllegalArgumentException.class)
    public void failWhenNullFiles() throws IOException {
        FilesAsZipParameterData.create(null).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void failWhenEmptyFiles() throws IOException {
        FilesAsZipParameterData.create(emptyList()).build();
    }

    @Test
    public void createWithFilesOnly() throws IOException {
        final FilesAsZipParameterData param = FilesAsZipParameterData.create(asList(IMAGE_FILE, HTML_FILE)).build();

        assertThat(param, is(notNullValue()));
        assertThat(param.getData(), is(notNullValue()));
        assertThat(param.getContentType(), is("application/zip"));
        assertThat(param.getDescription(), is(nullValue()));
        assertThat(param.getMetadata(), is(emptyMap()));
        assertThat(param.getName(), is(nullValue()));
    }

    @Test
    public void createWithCompleteDetails() throws IOException {
        final FilesAsZipParameterData param = FilesAsZipParameterData.create(asList(IMAGE_FILE, HTML_FILE))
                .description("describe me")
                .metadata(singletonMap("mykey", "myvalue"))
                .name("name here")
                .contentType("text/plain")
                .build();

        assertThat(param, is(notNullValue()));
        assertThat(param.getData(), is(notNullValue()));
        assertThat(param.getContentType(), is("application/zip"));
        assertThat(param.getDescription(), is("describe me"));
        assertThat(param.getMetadata(), is(singletonMap("mykey", "myvalue")));
        assertThat(param.getName(), is("name here"));
    }
}