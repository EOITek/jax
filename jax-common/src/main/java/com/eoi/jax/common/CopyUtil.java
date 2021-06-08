/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eoi.jax.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;

public class CopyUtil {

    private CopyUtil() {
    }

    public static final int BUFFER_SIZE = 4096;

    /**
     * (from spring framework) Copy the contents of the given InputStream to the given OutputStream. Leaves both streams open when done.
     *
     * @param in  the InputStream to copy from
     * @param out the OutputStream to copy to
     * @return the number of bytes copied
     * @throws IOException in case of I/O errors
     */
    public static int copy(InputStream in, OutputStream out) throws IOException {
        int byteCount = 0;
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead = -1;
        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
            byteCount += bytesRead;
        }
        out.flush();
        return byteCount;
    }

    /**
     * copy a resource file into the tmpdir, return the path of the target file
     *
     * @param resourcePath the path of the resource file
     * @return the path of the target file
     * @throws IOException exception
     */
    public static String copyResource(Class callingClass, String resourcePath) throws IOException {
        String folder = System.getProperty("java.io.tmpdir");

        InputStream stream = callingClass.getResourceAsStream(resourcePath);
        String finalPath = Paths.get(folder, resourcePath).toString();
        File fileCopy = new File(finalPath);
        if (!fileCopy.exists()) {
            fileCopy.createNewFile();
        }
        FileOutputStream out = new FileOutputStream(fileCopy, false);
        copy(stream, out);
        return finalPath;
    }
}
