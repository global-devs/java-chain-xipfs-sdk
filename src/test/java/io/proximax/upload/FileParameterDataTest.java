package io.proximax.upload;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static io.proximax.testsupport.Constants.IMAGE_FILE;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;

public class FileParameterDataTest {

    private static final File NON_EXISTENT_FILE = new File("src//test//resources//pdf_non_existent.pdf");

    @Test(expected = IllegalArgumentException.class)
    public void failWhenNullFile() throws IOException {
        FileParameterData.create(null).build();
    }

    @Test(expected = IOException.class)
    public void failWhenFileDoesNotExist() throws IOException {
        FileParameterData.create(NON_EXISTENT_FILE).build();
    }

    @Test
    public void createWithFileOnly() throws IOException {
        final FileParameterData param = FileParameterData.create(IMAGE_FILE).build();

        assertThat(param, is(notNullValue()));
        assertThat(param.getData(), is(FileUtils.readFileToByteArray(IMAGE_FILE)));
        assertThat(param.getContentType(), is(nullValue()));
        assertThat(param.getDescription(), is(nullValue()));
        assertThat(param.getMetadata(), is(emptyMap()));
        assertThat(param.getName(), is("test_image.png"));
    }

    @Test
    public void createWithCompleteDetails() throws IOException {
        final FileParameterData param = FileParameterData.create(IMAGE_FILE)
                .description("describe me")
                .metadata(singletonMap("mykey", "myvalue"))
                .name("name here")
                .contentType("text/plain")
                .build();

        assertThat(param, is(notNullValue()));
        assertThat(param.getData(), is(FileUtils.readFileToByteArray(IMAGE_FILE)));
        assertThat(param.getContentType(), is("text/plain"));
        assertThat(param.getDescription(), is("describe me"));
        assertThat(param.getMetadata(), is(singletonMap("mykey", "myvalue")));
        assertThat(param.getName(), is("name here"));
    }
}