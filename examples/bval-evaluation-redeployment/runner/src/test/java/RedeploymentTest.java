/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import org.apache.cxf.jaxrs.client.WebClient;
import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.MediaType;
import java.io.File;

@RunWith(Arquillian.class)
public class RedeploymentTest {

    public RedeploymentTest() {
    }

    @Deployment(name = "webapp1", managed = false)
    public static Archive<?> webapp1() {
        return ShrinkWrap.createFromZipFile(WebArchive.class, new File("../WebApp1/target/WebApp1-1.1.0-SNAPSHOT.war"));
    }

    @Deployment(name = "webapp2", managed = false)
    public static Archive<?> webapp2() {
        return ShrinkWrap.createFromZipFile(WebArchive.class, new File("../WebApp2/target/WebApp2-1.1.0-SNAPSHOT.war"));
    }

    @ArquillianResource
    private Deployer deployer;

    @Test
    public void validateTest() throws Exception {

        final String port = System.getProperty("server.http.port");
        System.out.println("");
        System.out.println("===========================================");
        System.out.println("Running test on port: " + port);

        deployer.deploy("webapp1");
        int result = WebClient.create("http://localhost:" + port + "/WebApp1/test/")
            .type(MediaType.APPLICATION_JSON_TYPE).post("validd").getStatus();
        System.out.println(result);
        Assert.assertEquals(406, result);

        //Not interested in webapp2 output
        deployer.undeploy("webapp2");
        deployer.deploy("webapp2");

        result = WebClient.create("http://localhost:" + port + "/WebApp1/test/")
            .type(MediaType.APPLICATION_JSON_TYPE).post("validd").getStatus();
        System.out.println(result);
        Assert.assertEquals(406, result);
        deployer.undeploy("webapp2");
        System.out.println("===========================================");
        System.out.println("");
    }

}