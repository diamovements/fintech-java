
package org.example;

import org.example.parser.ParserImpl;
import org.example.parser.ParserInterface;

import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException {
        ParserInterface parser = new ParserImpl();
        parser.toXML("city-error.json");
        parser.toXML("city.json");
    }
}
