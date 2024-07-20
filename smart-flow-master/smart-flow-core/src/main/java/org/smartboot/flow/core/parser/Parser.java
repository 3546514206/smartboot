package org.smartboot.flow.core.parser;

import java.io.InputStream;

/**
 * @author yamikaze
 * @date 2022/11/13
 */
public interface Parser {

    /**
     * Parse config file
     * @param is      is
     * @param streams other multiple is.
     */
    void parse(InputStream is, InputStream ... streams);

    /**
     * Parse file and other multiple files.
     *
     * @param f      file location
     * @param files  other multiple file location
     */
    void parse(String f, String... files);

}
