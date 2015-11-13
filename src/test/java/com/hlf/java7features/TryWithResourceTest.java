package com.hlf.java7features;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by howard.fackrell on 11/6/15.
 */

class CloseableResourceFactory {
    int openResources = 0;
    CloseableResource getResource() {
        openResources++;
        return new CloseableResource(this);
    }
}

class CloseableResource implements AutoCloseable {

    CloseableResource(CloseableResourceFactory factory) {
        this.factory = factory;
    }

    CloseableResourceFactory factory = null;
    boolean isClosed = false;
    int uses = 0;
    void use() { uses++; }

    @Override
    public void close()  {
        System.out.println("Resource closing...");
        factory.openResources--;
        isClosed = true;
    }
}

public class TryWithResourceTest {

    @Test
    public void testTryWithResources() {
        CloseableResourceFactory factory = new CloseableResourceFactory();

        try (CloseableResource a = factory.getResource();
             CloseableResource b = factory.getResource(); ) {
            a.use();
            b.use();
            assertEquals(2, factory.openResources);
        }
        assertEquals(0, factory.openResources);
    }

}
