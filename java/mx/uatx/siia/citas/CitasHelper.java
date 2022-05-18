package mx.uatx.siia.citas;

import mx.uatx.siia.serviciosUniversitarios.dto.AreasTO;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
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
}
