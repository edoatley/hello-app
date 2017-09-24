package edoatley.ds;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;

@RestController
public class ServiceInstanceRestController {

    private static final String HELLO_API = "HELLO-WORLD-API";

    @Autowired
    private DiscoveryClient discoveryClient;

    @RequestMapping("/service-instances/{applicationName}")
    public List<ServiceInstance> serviceInstancesByApplicationName(
            @PathVariable String applicationName) {
        return this.discoveryClient.getInstances(applicationName);
    }

    @RequestMapping("/hello-app/{name}")
    public String callHelloWorldIndirectly (
            @PathVariable String name) throws MalformedURLException {
        List<ServiceInstance> instances = this.discoveryClient.getInstances(HELLO_API);

        if(CollectionUtils.isEmpty(instances)) {
            return "Error";
        }
        else {
            URI host = instances.get(0).getUri();
            URL endpoint = new URL(host.toURL(), "/hello/"+name);
            RestTemplate restTemplate = new RestTemplate();
            return restTemplate.getForObject(endpoint.toString(), String.class);
        }
    }
}