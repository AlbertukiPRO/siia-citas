package mx.uatx.siia.citas;
import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import com.microsoft.graph.models.extensions.User;
import mx.uatx.siia.logeo.controlador.Citas;
import mx.uatx.siia.serviciosUniversitarios.dto.CitasTO;
import mx.uatx.siia.serviciosUniversitarios.dto.UserTest;
import sun.net.www.protocol.http.HttpURLConnection;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

public class ServicesCitas {

    private List<CitasTO> list = null;

    public List<CitasTO> getList() {
        return list;
    }
    public void setList(List<CitasTO> list) {
        this.list = list;
    }

    ServicesCitas(String url){
        list = new ArrayList<CitasTO>();
        String strJson = obtenerJson(url);
        list = generarLista(strJson); //as Response
    }

    public ServicesCitas(){

    }

    public String obtenerJson(String url){
        String response;
        try {
            response = readUrl("https://jsonplaceholder.typicode.com/users");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return response;
    }

    private static List<CitasTO> generarLista(String response){
        Type listType = new TypeToken<List<UserTest>>(){}.getType();

        List<CitasTO> listaCitas = new Gson().fromJson(response,listType);

//        for (int i = 0; i < listaCitas.size(); i++) {
//            System.out.println("Nombre:" + listaCitas.get(i).getName());
//        }

        return listaCitas;
    }

    public int addValues(String link, Map<String, String> valores){
        int codeResponde = 0;
       try {
           URL url = new URL(link);
           HttpURLConnection http = (HttpURLConnection)url.openConnection();
           http.setRequestMethod("PUT");
           http.setDoOutput(true);
           http.setRequestProperty("Content-Type", "application/json");

           String strJson = new Gson().toJson(valores);

           byte[] out = strJson.getBytes(StandardCharsets.UTF_8);

           OutputStream stream = http.getOutputStream();
           stream.write(out);

           BufferedReader br = null;
           if (http.getResponseCode() == 200) {
               br = new BufferedReader(new InputStreamReader(http.getInputStream()));
               String strCurrentLine;
               while ((strCurrentLine = br.readLine()) != null) {
                   System.out.println(strCurrentLine);
               }
           } else {
               br = new BufferedReader(new InputStreamReader(http.getErrorStream()));
               String strCurrentLine;
               while ((strCurrentLine = br.readLine()) != null) {
                   System.out.println(strCurrentLine);
               }
           }

           //HEADERS.
//           Map<String, List<String>> header = http.getHeaderFields();
//
//           for (Map.Entry<String,List<String>> entry : header.entrySet())
//               System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());

           codeResponde = http.getResponseCode();
           System.out.println(http.getResponseCode() + " " + http.getResponseMessage() );

           http.disconnect();

       }catch (Exception e){
           System.out.println(e.toString());
       }
        return codeResponde;
    }

    private static String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
        }
    }
}
