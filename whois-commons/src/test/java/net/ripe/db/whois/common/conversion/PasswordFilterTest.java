package net.ripe.db.whois.common.conversion;

import org.glassfish.jersey.uri.UriComponent;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class PasswordFilterTest {

    @Test
    public void testRobustness() {
        assertThat(PasswordFilter.filterPasswordsInContents(null),is(nullValue()));
        assertThat(PasswordFilter.filterPasswordsInUrl(null),is(nullValue()));
    }

    @Test
    public void testFilterPasswordsInMessage() {
        final String input = "" +
                "red: adsfasdf\n" +
                "blue: asdfasdfasdf\n" +
                "yellow%3A++asdfasdfasdf\n" +
                "green: asdfasdfasdf # password: test\n" +
                "purple: password\n" +
                "password:   test1 \n" +
                "password:test2\n" +
                "password: test3\n" +
                "password%3A++test4\n" +
                "password%3A++test5\n" +
                "delete: adsf\n";

        assertThat(PasswordFilter.filterPasswordsInContents(input), containsString("" +
                "red: adsfasdf\n" +
                "blue: asdfasdfasdf\n" +
                "yellow%3A++asdfasdfasdf\n" +
                "green: asdfasdfasdf # password: test\n" +
                "purple: password\n" +
                "password:FILTERED\n" +
                "password:FILTERED\n" +
                "password:FILTERED\n" +
                "password%3AFILTERED\n" +
                "password%3AFILTERED\n" +
                "delete: adsf\n"));
    }

    @Test
    public void testFilterOverridePasswordsInMessage() {
        final String input = "" +
                "red: adsfasdf\n" +
                "blue: asdfasdfasdf\n" +
                "yellow%3A++asdfasdfasdf\n" +
                "green: asdfasdfasdf # override: test\n" +
                "purple: override\n" +
                "override:user,pass\n" +
                "override:user,pass,reason\n" +
                "override:   user,pass\n" +
                "override%3A++user,pass\n" +
                "delete: adsf\n";

        assertThat(PasswordFilter.filterPasswordsInContents(input), containsString("" +
                "red: adsfasdf\n" +
                "blue: asdfasdfasdf\n" +
                "yellow%3A++asdfasdfasdf\n" +
                "green: asdfasdfasdf # override: test\n" +
                "purple: override\n" +
                "override:user,FILTERED\n" +
                "override:user,FILTERED,reason\n" +
                "override:user,FILTERED\n" +
                "override%3A++user,FILTERED\n" +
                "delete: adsf\n"));
    }

    @Test
    public void testFilterOverrideAndPasswordsInMessage() {
        final String input = "" +
                "red: adsfasdf\n" +
                "purple: override\n" +
                "override:user,pass\n" +
                "password:test\n";

        assertThat(PasswordFilter.filterPasswordsInContents(input), containsString("" +
                "red: adsfasdf\n" +
                "purple: override\n" +
                "override:user,FILTERED\n" +
                "password:FILTERED")); // eol stripped
     }


    @Test
    public void password_filtering_in_url() {

        assertThat(PasswordFilter.filterPasswordsInUrl(uriWithParams(new Pair("password","secret"))),
                is("/some/path?password=FILTERED"));

        assertThat(PasswordFilter.filterPasswordsInUrl(uriWithParams(new Pair("password", "p%3Fssword%26"))),
                is("/some/path?password=FILTERED"));

        assertThat(PasswordFilter.filterPasswordsInUrl(uriWithParams(new Pair("password", "secret"), new Pair("param", null))),
                is("/some/path?password=FILTERED&param"));

        assertThat(PasswordFilter.filterPasswordsInUrl(uriWithParams(new Pair("password", "secret"), new Pair("password", "other"))),
                is("/some/path?password=FILTERED&password=FILTERED"));

        assertThat(PasswordFilter.filterPasswordsInUrl(uriWithParams(new Pair("password", "secret"), new Pair("password", "other"), new Pair("param", "value"))),
                is("/some/path?password=FILTERED&password=FILTERED&param=value"));

        assertThat(PasswordFilter.filterPasswordsInUrl(uriWithParams(new Pair("param","value"),new Pair("password","secret"),new Pair("password","other"))),
                is("/some/path?param=value&password=FILTERED&password=FILTERED"));

        assertThat(PasswordFilter.filterPasswordsInUrl(uriWithParams(new Pair("param", "value"), new Pair("password", "secret"), new Pair("param", "password"))),
                is("/some/path?param=value&password=FILTERED&param=password"));

        assertThat(PasswordFilter.filterPasswordsInUrl(uriWithParams(new Pair("password", "test$#@!%^*-ab"), new Pair("param", "other"))),
                is("/some/path?password=FILTERED&param=other"));

    }

    @Test
    public void testFilterWithOverrideInUrl() {
        assertThat(PasswordFilter.filterPasswordsInUrl(uriWithParams(new Pair("override","admin,secret"), new Pair("param","other"))),
                is("/some/path?override=admin,FILTERED&param=other"));

        assertThat(PasswordFilter.filterPasswordsInUrl(uriWithParams(new Pair("override","admin,secret,reason"),new Pair("param","other"))),
                is("/some/path?override=admin,FILTERED,reason&param=other"));

        assertThat(PasswordFilter.filterPasswordsInUrl(uriWithParams(new Pair("DATA", "person:  Test+Person\nsource:  TEST\n\noverride:admin,password"), new Pair("NEW", "yes"))),
                is("/some/path?DATA=person:++Test%2BPerson%0Asource:++TEST%0A%0Aoverride:admin,FILTERED&NEW=yes"));

        assertThat(PasswordFilter.filterPasswordsInUrl(uriWithParams(new Pair("DATA", "person:  Test+Person\nsource:  TEST\n\noverride:admin,password,reason"), new Pair("NEW", "yes"))),
                is("/some/path?DATA=person:++Test%2BPerson%0Asource:++TEST%0A%0Aoverride:admin,FILTERED,reason&NEW=yes"));

        assertThat( PasswordFilter.filterPasswordsInUrl(uriWithParams(new Pair("DATA", "person:  TestP\n\noverride:personadmin,team-red1234"), new Pair("NEW","yes"))),
                is("/some/path?DATA=person:++TestP%0A%0Aoverride:personadmin,FILTERED&NEW=yes"));

        assertThat( PasswordFilter.filterPasswordsInUrl("/some/path?DATA=person:++TestP%0A%0Aoverride%3Apersonadmin,team-red1234&NEW=yes"),
                is("/some/path?DATA=person:++TestP%0A%0Aoverride%3Apersonadmin,FILTERED&NEW=yes"));

        assertThat( PasswordFilter.filterPasswordsInUrl("whois/syncupdates/test?DATA=person%3A+++++++++Test+Person%0Aaddress%3A++++++++Singel+258%0Aphone%3A++++++++++%2B31+6+12345678%0Anic-hdl%3A++++++++TP2-TEST%0Amnt-by%3A+++++++++OWNER-MNT%0Achanged%3A++++++++dbtest%40ripe.net+20120101%0A"+
                        "source%3A+++++++++TEST%0Aoverride%3Apersonadmin%2Cteam-red1234&NEW=yes"),
                is("whois/syncupdates/test?DATA=person%3A+++++++++Test+Person%0Aaddress%3A++++++++Singel+258%0Aphone%3A++++++++++%2B31+6+12345678%0Anic-hdl%3A++++++++TP2-TEST%0Amnt-by%3A+++++++++OWNER-MNT%0Achanged%3A++++++++dbtest%40ripe.net+20120101%0Asource%3A+++++++++TEST%0Aoverride%3Apersonadmin,FILTERED&NEW=yes"));

    }

    private static String uriWithParams(Pair ...params){
        StringBuffer sb = new StringBuffer();
        sb.append("/some/path?");
        int idx = 0;
        for( Pair param: params ) {
            String encodedKey =  UriComponent.encode(param.key,UriComponent.Type.QUERY_PARAM, false);
            String encodedValue = param.value != null ? ("=" + UriComponent.encode(param.value, UriComponent.Type.QUERY_PARAM, false)) : "";
            sb.append(encodedKey  + encodedValue);
            if( idx<params.length-1) {
                sb.append("&");
            }
            idx++;
        }
        return sb.toString();
    }

    static class Pair {
        String key;
        String value;
        Pair(String key,String value) {
            this.key = key;
            this.value = value;
        }
    }
}
