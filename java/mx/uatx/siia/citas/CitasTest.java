package mx.uatx.siia.citas;

import com.google.gson.Gson;
import com.sun.istack.NotNull;
import mx.uatx.siia.citas.pruebas.Eventos;
import mx.uatx.siia.comun.helper.VistasHelper;
import mx.uatx.siia.serviciosUniversitarios.dto.ResultadoTO;
import mx.uatx.siia.serviciosUniversitarios.enums.SeveridadMensajeEnum;
import net.bootsfaces.component.fullCalendar.FullCalendarEventBean;
import net.bootsfaces.component.fullCalendar.FullCalendarEventList;
import org.apache.poi.ss.formula.functions.Even;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.time.Instant;
import java.util.*;

@ManagedBean(name = "test")
@ViewScoped
public class CitasTest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String lang;
    private List<Eventos> eventos;
    private String jsonEvents;

    public CitasTest(){
        eventos = new ArrayList<>();
        Eventos eventos1 = new Eventos("Hola", Date.from(Instant.now()), "google.com");
        eventos.add(0, eventos1);
        lang = "es";
    }

    public String toJson(){
        return jsonEvents = new Gson().toJson(eventos);
    }

    public String getLang() {
        return lang;
    }
    public List<Eventos> getEventos() {
        return eventos;
    }

    public String getJsonEvents() {
        return jsonEvents;
    }

    public void setJsonEvents(String jsonEvents) {
        this.jsonEvents = jsonEvents;
    }

    public void setEventos(List<Eventos> eventos) {
        this.eventos = eventos;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
}
