    package com.capitalone.dashboard.collector;

    import com.capitalone.dashboard.model.CatalystRepo;
    import org.apache.commons.codec.binary.Base64;
    import org.apache.commons.logging.Log;
    import org.apache.commons.logging.LogFactory;
    import org.json.simple.JSONArray;
    import org.json.simple.JSONObject;
    import org.json.simple.parser.JSONParser;
    import org.json.simple.parser.ParseException;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpEntity;
    import org.springframework.http.HttpHeaders;
    import org.springframework.http.HttpMethod;
    import org.springframework.http.ResponseEntity;
    import org.springframework.stereotype.Component;
    import org.springframework.web.client.RestOperations;
    import com.capitalone.dashboard.util.Supplier;

    import com.capitalone.dashboard.util.ClientUtil;
    import com.capitalone.dashboard.model.CatalystTaskHistory;


    import java.nio.charset.StandardCharsets;
    import java.util.ArrayList;
    import java.util.List;

    /**
     * Created by Vinod on 19/9/16.
     */
        @Component
        public class DefaultCatalystClient implements CatalystClient{private static final Log LOG = LogFactory.getLog(DefaultCatalystClient.class);
        private final CatalystSettings settings;
        private final RestOperations restOperations;

        private static final ClientUtil TOOLS = ClientUtil.getInstance();

        @Autowired
        public DefaultCatalystClient(CatalystSettings settings,Supplier<RestOperations> restOperationsSupplier){
            this.settings = settings;
            this.restOperations = restOperationsSupplier.get();
        }


       @Override
       public  List<CatalystRepo> getTasks(){
           List<CatalystRepo> catalystRepos = new ArrayList<CatalystRepo>();
           try {
               ResponseEntity<String> response = getToken(settings.getCatalystBaseUrl() + "/auth/signin",settings.getCatalystUserName(),settings.getCatalystPassword());
               JSONObject jtoken = paresAsObject(response);
               LOG.info(response.toString());
           }catch (Exception e){
               LOG.info(e.getMessage());
               return null;
           }

           return catalystRepos;
       }

       @Override
        public List<CatalystTaskHistory> getTaskHistory(CatalystRepo catalystRepo,boolean firstrun){
           return  null;

       }

        private JSONArray paresAsArray(ResponseEntity<String> response) {
            try {
                return (JSONArray) new JSONParser().parse(response.getBody());
            } catch (ParseException pe) {
                LOG.error(pe.getMessage());
            }
            return new JSONArray();
        }

        private JSONObject paresAsObject(ResponseEntity<String> response) {
            try {
                return (JSONObject) new JSONParser().parse(response.getBody());
            } catch (ParseException pe) {
                LOG.error(pe.getMessage());
            }
            return new JSONObject();
        }

        private ResponseEntity<String> getToken(String url, String userName,
                                                    String password) {
            JSONObject request = new JSONObject();
            request.put("username",userName);
            request.put("pass",password);
            request.put("authType","token");
            LOG.info("About to get token ->" + userName + " " + password + " for " + url);
            // Basic Auth only.
            if (!"".equals(userName) && !"".equals(password)) {
                //LOG.info("Call with userid and password");
                return restOperations.exchange(url, HttpMethod.POST,
                        new HttpEntity<>(request.toString()),String.class);

            } else {
                return restOperations.exchange(url, HttpMethod.GET, null,
                        String.class);
            }

        }

        private HttpHeaders createHeaders(final String token) {
            HttpHeaders headers = new HttpHeaders();
            headers.set("x-catalyst-auth",token);
            return headers;
        }


    }
