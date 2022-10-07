package mx.uatx.siia.citas.test;

import com.google.gson.Gson;
import mx.uatx.siia.citas.modelo.Tramites.business.TramitesBusiness;
import mx.uatx.siia.citas.modelo.citasBusiness.MethodsGenerics;
import mx.uatx.siia.citas.models.Eventos;
import mx.uatx.siia.serviciosUniversitarios.dto.ResultadoTO;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.*;

@ManagedBean(name = "test")
@ViewScoped
public class CitasTest implements Serializable {

    private static final long serialVersionUID = 1L;


    private List<Eventos> eventos;
    private List<SelectItem> listTramites;
    private String jsonEvents;
    private String lang;
    private String strCurrentArea;
    private boolean hasDataAreas;

    @ManagedProperty("#{tramitesBusiness}")
    private TramitesBusiness tramitesBusiness;

    public void show(){
        hasDataAreas = !hasDataAreas;
    }
    public List<SelectItem> returnTramitesList(){
        if (listTramites == null){
//            ResultadoTO res = tramitesBusiness.obtenerTramites(URLs.Tramites.getValor(), getStrCurrentArea());
            ResultadoTO res = tramitesBusiness.obtenerTramites(1);
            System.out.println(res.getObjeto().toString());
            setListTramites((List<SelectItem>) res.getObjeto());
        }
        return getListTramites();
    }

    public void listenerPostAreas(){
        System.out.println("listener");
    }

    public CitasTest() {
        eventos = new ArrayList<>();

        lang = "es";
    }

    public String toJson(){
        return jsonEvents = new Gson().toJson(eventos);
    }
    public String getLang() {
        return lang;
    }
    public TramitesBusiness getTramitesBusiness() {
        return tramitesBusiness;
    }
    public void setTramitesBusiness(TramitesBusiness tramitesBusiness) {
        this.tramitesBusiness = tramitesBusiness;
    }
    public List<SelectItem> getListTramites() {
        return listTramites;
    }
    public void setListTramites(List<SelectItem> listTramites) {
        this.listTramites = listTramites;
    }
    public List<Eventos> getEventos() {
        return eventos;
    }
    public String getStrCurrentArea() {
        return strCurrentArea;
    }
    public void setStrCurrentArea(String strCurrentArea) {
        this.strCurrentArea = strCurrentArea;
    }
    public boolean isHasDataAreas() {
        return hasDataAreas;
    }
    public void setHasDataAreas(boolean hasDataAreas) {
        this.hasDataAreas = hasDataAreas;
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
