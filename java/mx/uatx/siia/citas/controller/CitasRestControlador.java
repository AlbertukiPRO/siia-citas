package mx.uatx.siia.citas.controller;

import mx.uatx.siia.citas.modelo.areas.business.AreasBusiness;
import mx.uatx.siia.citas.modelo.enums.URLs;
import mx.uatx.siia.citas.services.CitasServices;
import mx.uatx.siia.serviciosUniversitarios.dto.AreasTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/rest/citas")
public class CitasRestControlador {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1217243397092148938L;

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    AreasBusiness areasBusiness;

    @RequestMapping(value = "/micita", method = RequestMethod.GET, headers = "Accept=application/json")
    public @ResponseBody List<AreasTO> getCita(HttpServletRequest request){
        return (List<AreasTO>) areasBusiness.obtenerAreas(URLs.Areas.getValor()).getObjeto();
    }

    // TODO Implementar el rest template para consumir los datos en spring 100%
}
