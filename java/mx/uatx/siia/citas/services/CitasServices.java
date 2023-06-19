package mx.uatx.siia.citas.services;

import mx.uatx.siia.citas.areas.business.AreasBusiness;
import mx.uatx.siia.citas.enums.URLs;
import mx.uatx.siia.serviciosUniversitarios.dto.ResultadoTO;
import org.springframework.stereotype.Service;

import java.util.List;

// TODO SERVICES

@Service
public class CitasServices {

    public List<String> getLista() {

        AreasBusiness areasBusiness = new AreasBusiness();
        ResultadoTO areas = areasBusiness.obtenerAreas();

        return (List<String>) areas.getObjeto();
    }


}
