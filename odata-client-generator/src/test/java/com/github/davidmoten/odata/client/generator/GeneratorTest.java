package com.github.davidmoten.odata.client.generator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Collections;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.junit.Test;
import org.oasisopen.odata.csdl.v4.TDataServices;
import org.oasisopen.odata.csdl.v4.TEdmx;

import com.github.davidmoten.guavamini.Lists;

public class GeneratorTest {

    private static final String GENERATED = "target/generated-sources/odata";

    @Test
    public void testGenerateMsgraph() throws JAXBException, IOException {
        JAXBContext c = JAXBContext.newInstance(TDataServices.class);
        Unmarshaller unmarshaller = c.createUnmarshaller();
        TEdmx t = unmarshaller.unmarshal(
                new StreamSource(new FileInputStream("src/main/odata/msgraph-metadata.xml")),
                TEdmx.class).getValue();
        SchemaOptions schemaOptions1 = new SchemaOptions("microsoft.graph",
                "microsoft.graph.generated");
        SchemaOptions schemaOptions2 = new SchemaOptions("microsoft.graph.callRecords",
                "microsoft.graph.callrecords.generated");
        SchemaOptions schemaOptions3 = new SchemaOptions("microsoft.graph.externalConnectors", 
                "microsoft.graph.externalconnectors.generated");
        Options options = new Options(GENERATED, Lists.newArrayList(schemaOptions1, schemaOptions2, schemaOptions3));
        Generator g = new Generator(options,
                t.getDataServices().getSchema());
        g.generate();
        File file = new File(GENERATED + "/microsoft/graph/generated/entity/FileAttachment.java");
        Files.copy(file.toPath(), new File("../src/docs/FileAttachment.java").toPath(),
                StandardCopyOption.REPLACE_EXISTING);
    }

    @Test
    public void testGenerateMsgraphBeta() throws JAXBException, IOException {
        JAXBContext c = JAXBContext.newInstance(TDataServices.class);
        Unmarshaller unmarshaller = c.createUnmarshaller();
        TEdmx t = unmarshaller.unmarshal(
                new StreamSource(new FileInputStream(
                        "../odata-client-msgraph-beta/src/main/odata/msgraph-beta-metadata.xml")),
                TEdmx.class).getValue();
        t.getDataServices().getSchema().forEach(s -> System.out.println(s.getNamespace()));
        SchemaOptions schemaOptions = new SchemaOptions("microsoft.graph",
                "microsoft.graph.beta.generated");
        SchemaOptions schemaOptions2 = new SchemaOptions("microsoft.graph.callRecords",
                "microsoft.graph.beta.callRecords.generated");
        SchemaOptions schemaOptions3 = new SchemaOptions("microsoft.graph.termStore",
                "microsoft.graph.beta.termStore.generated");
        SchemaOptions schemaOptions4 = new SchemaOptions("microsoft.graph.ediscovery",
                "microsoft.graph.beta.ediscovery.generated");
        SchemaOptions schemaOptions5 = new SchemaOptions("microsoft.graph.externalConnectors",
                "microsoft.graph.beta.external.connectors");
        SchemaOptions schemaOptions6 = new SchemaOptions("microsoft.graph.windowsUpdates",
                "microsoft.graph.beta.windows.updates");
        
        Options options = new Options(GENERATED, Arrays.asList(schemaOptions, schemaOptions2,
                schemaOptions3, schemaOptions4, schemaOptions5, schemaOptions6));
        Generator g = new Generator(options, t.getDataServices().getSchema());
        g.generate();
    }

    @Test
    public void testGenerateODataTestService() throws JAXBException, FileNotFoundException {
        JAXBContext c = JAXBContext.newInstance(TDataServices.class);
        Unmarshaller unmarshaller = c.createUnmarshaller();
        TEdmx t = unmarshaller.unmarshal(
                new StreamSource(
                        new FileInputStream("src/main/odata/odata-test-service-metadata.xml")),
                TEdmx.class).getValue();
        SchemaOptions schemaOptions = new SchemaOptions("ODataDemo", "odata.test.generated");
        Options options = new Options(GENERATED, Collections.singletonList(schemaOptions));
        Generator g = new Generator(options,
                Collections.singletonList(t.getDataServices().getSchema().get(0)));
        g.generate();
    }

    public static void main(String[] args) throws JAXBException, IOException {
        //noinspection InfiniteLoopStatement
        while (true) {
            new GeneratorTest().testGenerateMsgraph();
        }
    }
}
