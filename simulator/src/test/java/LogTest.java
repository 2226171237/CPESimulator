import com.sun.xml.internal.ws.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import sun.net.www.HeaderParser;

import java.util.Iterator;

@Slf4j
public class LogTest {
    public static void main(String[] args) {
        log.info("ok");
//        https://zhuanlan.zhihu.com/p/20913727
//        https://www.cnblogs.com/woshimrf/p/OAuth2.html
//        https://blog.csdn.net/u010609251/article/details/108030120
        String authHeader = "username=\"Mufasa\",\n" +
                "                     realm=\"testrealm@host.com\",\n" +
                "                     nonce=\"dcd98b71=02dd2f0e8b11d0f600bfb0c093==\",\n" +
                "                     uri=\"/dir/index.html\",\n" +
                "                     qop=\"auth,auth-int\",\n" +
                "                     nc=00000001,\n" +
                "                     cnonce=\"0a4f113b\",\n" +
                "                     response=\"6629fae49393a05397450978507c4ef1\",\n" +
                "                     opaque=\"5ccc069c403ebaf9f0171e9517f40e41\"";
        HeaderParser headerParser = new HeaderParser(authHeader);
        System.out.println(headerParser.findValue("username"));
        System.out.println(headerParser.findValue("realm"));
        System.out.println(headerParser.findValue("nonce"));
        System.out.println(headerParser.findValue("uri"));
        System.out.println(headerParser.findValue("qop"));
        System.out.println(headerParser.findValue("nc"));
        System.out.println(headerParser.findValue("cnonce"));
        System.out.println(headerParser.findValue("response"));
        System.out.println(headerParser.findValue("opaque"));
        Iterator<String> keys = headerParser.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            System.out.println(key + "=" + headerParser.findValue(key));
        }
    }
}
