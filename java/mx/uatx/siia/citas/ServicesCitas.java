package mx.uatx.siia.citas;
import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import mx.uatx.siia.citas.modelo.MisCitas;
import mx.uatx.siia.serviciosUniversitarios.dto.AreasTO;
import mx.uatx.siia.serviciosUniversitarios.dto.CitasTO;
import mx.uatx.siia.serviciosUniversitarios.dto.TramitesTO;
import mx.uatx.siia.serviciosUniversitarios.dto.UserTest;
import sun.net.www.protocol.http.HttpURLConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ServicesCitas {

    private List<CitasTO> list = null;
    public List<CitasTO> getList() {
        return list;
    }
    public void setList(List<CitasTO> list) {
        this.list = list;
    }

    public ServicesCitas(){

    }

    private List<CitasTO> generarLista(String response){
        Type listType = new TypeToken<List<UserTest>>(){}.getType();

        List<CitasTO> listaCitas = new Gson().fromJson(response,listType);
//        for (int i = 0; i < listaCitas.size(); i++) {
//            System.out.println("Nombre:" + listaCitas.get(i).getName());
//        }
        return listaCitas;
    }

    public static List<AreasTO> getAreasAPI(String url){
        List<AreasTO> listaAreas;
        String strJson = readUrl(url);
        System.out.println("----- FINISH GET AREAS [ Value ] => "+strJson);
        Type listType = new TypeToken<List<AreasTO>>(){}.getType();

        listaAreas = new Gson().fromJson(strJson,listType);

        return listaAreas;
    }

    public static List<TramitesTO> getTramitesAPI(String url, String idArea){
        List<TramitesTO> listTramites;
        String strJson = readUrl(url+"?idTramite="+idArea);

        System.out.println("---- FINISH GET TRAMITES WHERE ARES = "+idArea);

        Type listType = new TypeToken<List<TramitesTO>>(){}.getType();

        listTramites = new Gson().fromJson(strJson,listType);

        return listTramites;
    }

    public static List<String> getFechasInabilesAPI(String url, String area){
        List<String> lista = null;

        try {
            String resultado = readUrl(url+"?whereFecha="+area);
            if (!resultado.equals("0")){
                lista = Arrays.asList(resultado.split(","));
            }else{
                lista = new ArrayList<>();
            }
        }catch (Exception e){
            System.out.println(e);
        }
        System.out.println("Finish Horarios Reservados => [value] = "+lista.toString());
        return lista;
    }
    public static List<String> getHorariosAPI(String link, String fecha, String idArea){

        System.out.println("--- GET HORARIOS => [Run]");

        List<String> lista;
        try {
            String resultado = readUrl(link+"?fecha="+fecha+"&idarea="+idArea);

            if (!resultado.equals("0")){
                lista = Arrays.asList(resultado.split(","));
            }else{
                lista = Collections.emptyList();
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        System.out.println("Finish Horarios Reservados => [value] = "+lista.toString());
        return lista;
    }
    public static int addValues(String link, Map<String, String> valores){
        int codeResponde = 0;
        System.out.println("---- SEND VALUES TO api save [ Run ]");
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
                   System.out.println("Buffer: "+strCurrentLine);
                   codeResponde = Integer.parseInt(strCurrentLine);
               }
           }

           //HEADERS.
//           Map<String, List<String>> header = http.getHeaderFields();
//
//           for (Map.Entry<String,List<String>> entry : header.entrySet())
//               System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
           System.out.println("Code response server and sms: "+http.getResponseCode() + " " + http.getResponseMessage() );

           http.disconnect();

       }catch (Exception e){
           System.out.println(e.toString());
       }
        return codeResponde;
    }

    public static List<MisCitas> getMisCitas(String url, String strUser){
        List<MisCitas> misCitas;

        String strJson = readUrl(url+"?id="+strUser);

        System.out.println("----- FINISH GET MIS CITAS ---");
        Type listType = new TypeToken<List<MisCitas>>(){}.getType();

        misCitas = new Gson().fromJson(strJson,listType);

        return misCitas;
    }
    private static String readUrl(String urlString)  {
        System.out.println("----- GET DATA FROM URL => ["+urlString+"]");
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            System.out.println("----- FINISH DATA FROM URL => [Value] ="+ buffer);

            return buffer.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }


}
