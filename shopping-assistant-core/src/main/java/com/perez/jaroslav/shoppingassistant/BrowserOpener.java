package com.perez.jaroslav.shoppingassistant;

import java.net.URI;

public class BrowserOpener {
    public static void open(String url) throws Exception{
        URI u = new URI(url);
        java.awt.Desktop.getDesktop().browse(u);
    }
}
