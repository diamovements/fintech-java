package org.example.parser;

import java.io.IOException;

public interface ParserInterface {
    void toXML(String filepath) throws IOException;
    void saveToFile(String filepath, String xml);
}
