package mx.uatx.siia.citas;

import mx.uatx.siia.citas.modelo.citasBusiness.citaBusiness;
import mx.uatx.siia.comun.helper.VistasHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.List;
import java.util.*;

@ManagedBean(name = "calendar")
@SessionScoped

public class CitasCalendar implements Serializable  {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -2621529818431646329L;

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @ManagedProperty("#{citaBusiness}")
    private citaBusiness citaBusiness;

    private final VistasHelper vHelp = new VistasHelper();

    private String strLanguaje = "es";
    private List<String> fechasDisable = Arrays.asList("10/05/2022");

    public List<String> getFechasDisable() {
        return fechasDisable;
    }

    public void setFechasDisable(List<String> fechasDisable) {
        this.fechasDisable = fechasDisable;
    }

    public String getStrLanguaje() {
        return strLanguaje;
    }

    public citaBusiness getCitaBusiness() {
        return citaBusiness;
    }

    public void setCitaBusiness(citaBusiness citaBusiness) {
        this.citaBusiness = citaBusiness;
    }

    public void setStrLanguaje(String strLanguaje) {
        this.strLanguaje = strLanguaje;
    }

    public CitasCalendar() {
        FechasBuild();
    }

    public void FechasBuild(){
        fechasDisable = Arrays.asList("10/05/2022");
    }
}
