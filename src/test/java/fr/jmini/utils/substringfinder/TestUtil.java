/*******************************************************************************
 * Copyright (c) 2018 Jeremie Bresson and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package fr.jmini.utils.substringfinder;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class TestUtil {

    public static void checkInputInFile(String name, String content) throws IOException {
        Path resources = Paths.get("src/test/resources");
        Path inputFile = resources.resolve(name);

        String existingContent;
        if (Files.isReadable(inputFile)) {
            existingContent = new String(Files.readAllBytes(inputFile), StandardCharsets.UTF_8);
        } else {
            existingContent = null;
        }
        Files.createDirectories(inputFile.getParent());
        Files.write(inputFile, content.getBytes(StandardCharsets.UTF_8));
        if (!Objects.equals(existingContent, content)) {
            fail("The file '" + inputFile + "' doesn't contain the expected content. Rerun the test and commit the file.");
        }

    }
}
