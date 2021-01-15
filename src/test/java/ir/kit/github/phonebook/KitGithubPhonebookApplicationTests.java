package ir.kit.github.phonebook;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import io.restassured.response.Response;
import ir.kit.github.phonebook.KitGithubPhonebookApplication;
import ir.kit.github.phonebook.service.dto.KitGithubAccountDTO;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {KitGithubPhonebookApplication.class})
class KitGithubPhonebookApplicationTests {

    @LocalServerPort
    int port;

    private static String kitIdForUpdate;

    @Test
    public void testCreateAccount() {
        given().port(port).basePath("/api/account/create")
                .header("Content-Type", "application/json")
                .body(testJson("KitCreateGithubAccountTest.json")).post().then().statusCode(200);
    }

    @Test
    public void testSearchLastCreatedAccount() {
        Response response = given().port(port).basePath("/api/account/search")
                .queryParam("name", "Arta")
                .get();
        String searchResponse = response.getBody().print();
        Type listType = new TypeToken<List<KitGithubAccountDTO>>(){}.getType();
        List list = new Gson().fromJson(searchResponse, listType);
        KitGithubAccountDTO kitGithubAccountDTO = (KitGithubAccountDTO) list.get(0);
        kitIdForUpdate = kitGithubAccountDTO.getId();
        response.then().statusCode(200).assertThat().body("name", hasItem("Arta Salahesh"));
    }


    @Test
    public void testUpdateAccount() {
        given().port(port).basePath("/api/account/update")
                .header("Content-Type", "application/json")
                .body(new KitGithubAccountDTO(kitIdForUpdate
                        , "Arta Salahesh"
                        , "09122080268"
                        , "artasalahesh68@gmail.com"
                        , "Kit"
                        ,"artgithub"))
                .put().then().statusCode(200)
        .assertThat().body("github",equalTo("artgithub"));
    }




    public String testJson(String fileName) {
        StringBuilder contentBuilder = new StringBuilder();
        String st = null;
        try {
            File testJsonFile = new File(this.getClass().getClassLoader().getResource("mock/"+fileName).getPath());
            BufferedReader br = new BufferedReader(new FileReader(testJsonFile));
            while ((st = br.readLine()) != null)
                contentBuilder.append(st);
            return contentBuilder.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return st;
    }

}