package mx.uatx.siia.citas;

import com.ibm.icu.text.SimpleDateFormat;
import mx.uatx.siia.serviciosUniversitarios.dto.AreasTO;
import mx.uatx.siia.serviciosUniversitarios.dto.TramitesTO;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CitasHelper {

    public List<SelectItem> getSelectAreas(){

        List<SelectItem> listAreas = new ArrayList<SelectItem>();
        List<AreasTO> areas = ServicesCitas.getAreasAPI("http://localhost/siiaServices/apis/getAreas.php");


        for ( AreasTO area : areas){
            SelectItem item = new SelectItem(area.getIntIdAreas(), area.getStrNombreAreas());
            listAreas.add(item);
        }

        return listAreas;
    }

    public List<SelectItem> getSelectTramites(String idAreaSelected){
        List<SelectItem> listTramites = new ArrayList<>();
        List<TramitesTO> tramites = ServicesCitas.getTramitesAPI("http://localhost/siiaServices/apis/getTramites.php",idAreaSelected);

        for ( TramitesTO tramite : tramites ){
            SelectItem item = new SelectItem(tramite.getIntIdTramite(), tramite.getStrNombreTramite());
            listTramites.add(item);
        }

        return listTramites;
    }

    public static String formatDate(String fechaValue){
        java.util.Date date = new Date(fechaValue);
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        return formatter.format(date);
    }

    public static List<String> generarHorarios(int horaInicio, int HoraFin, int DuracionCitas, List<String> horariosReservados){

        List<String> listHorarios = new ArrayList<String>();

        int hora = horaInicio;
        int minuto = 0;
        int residuo = 0;
        while (hora<HoraFin){
            if ( minuto < 59 && hora != HoraFin){
                String item = formatHora(Integer.toString(hora))+":"+formatHora(Integer.toString(minuto));
                listHorarios.add(item);
            }
            if(minuto<=60){
                minuto+=DuracionCitas;
            }else{
                residuo=minuto-60;
                minuto=residuo;
                hora+=1;
            }
        }

        if (!(horariosReservados.size() == 0)){
            horariosReservados.stream().forEach((item)->{
                for (int i = 0; i < listHorarios.size(); i++) {
                    if (item.equals(listHorarios.get(i))){
                        listHorarios.remove(i);
                    }
                }
            });
        }

        return listHorarios;
    }

    public static String formatHora(String number){
        return number.length() == 1 ? "0"+number : number;
    }

}
