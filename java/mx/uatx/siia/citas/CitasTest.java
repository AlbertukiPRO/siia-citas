package mx.uatx.siia.citas;

import com.sun.istack.NotNull;
import mx.uatx.siia.comun.helper.VistasHelper;
import mx.uatx.siia.serviciosUniversitarios.dto.ResultadoTO;
import mx.uatx.siia.serviciosUniversitarios.enums.SeveridadMensajeEnum;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;

@ManagedBean(name = "test")
@ViewScoped
public class CitasTest implements Serializable {
    public CitasHelper citasHelper;
    private boolean hasDataAreas = false;
    private boolean hasDataTramites = false;
    @NotNull
    private String valueComboAreas = null;
    @NotNull
    private String valueComboTramites = null;
    @NotNull
    private String strCalendarValue = null;
    private List<SelectItem> listaAreas;
    private List<SelectItem> listaTramites;
    private List<String> listHorarios;
    private String fechasDisable;

    /* --------------------------------------------
    // VHelper for local actions already defined */

        private final VistasHelper vHelp = new VistasHelper();

        @ManagedProperty("#{msj}")
        private ResourceBundle msj;
        public ResourceBundle getMsj() {return msj;}
        public void setMsj(ResourceBundle msj) {this.msj = msj;}

    /* ------------------------------------------ */

    /*-----------CONSTRUCTOR */
    public CitasTest(){
        citasHelper = new CitasHelper();
    }


    /* Methods that @return the list for the element<SelectOneMenu>   */
    public List<SelectItem> obtenerAreas(){return listaAreas;}
    public List<SelectItem> obtenerTramites(){ return listaTramites;}

    public void updateForm(){
        setListaAreas(citasHelper.getSelectAreas());

        final ResultadoTO res = new ResultadoTO();
        res.agregarMensaje(SeveridadMensajeEnum.INFO, "comun.msj.citas.areas.succesful");
        vHelp.pintarMensajes(msj, res);

        hasDataAreas = true;
    }

    public void listenerPostAreas(){
        setListaTramites(citasHelper.getSelectTramites(getValueComboAreas())); // @return => List<SelectItem> with Object().values[id,nombre]
        setListHorarios(getFechasFromDB()); // @return => List<String> with Dates saved in the DB


        System.out.println("[VALUE] de Areas => "+getValueComboAreas());
        System.out.println("[VALUE] de Horarios Reservados => "+getListHorarios());

        hasDataTramites = true;
    }

    public void listenerPostTramites(){
        System.out.println("[VALUE] de Tramites => "+getValueComboTramites());
    }

    public void ComprobarFecha(){
        System.out.println("------------Comprobando las disponibilidad de las fechas-----------");

        listHorarios = CitasHelper.generarHorarios(8,13,25, listHorarios);

//        final ResultadoTO res = new ResultadoTO();
//        res.agregarMensaje(SeveridadMensajeEnum.INFO, "comun.msj.citas.fechas.ok");
//        vHelp.pintarMensajes(msj, res);

        setStrCalendarValue(strCalendarValue);
    }

    /*
    *
    * TODO: darle seguimiento al tema de fechas.
    *
    * */

    private List<String> getFechasFromDB(){
        System.out.println("------ CALLING HORARIOS FROM DB  => [ Run ]");
        return ServicesCitas.getHorariosAPI("http://localhost/siiaServices/apis/getFechasReservadas.php",strCalendarValue,valueComboAreas);
    }

    // Building string for datepicker dates disables;
    public String buildStringDisableDates(){
        StringBuilder cadena = new StringBuilder();
        List<String> value = ServicesCitas.getFechasInabilesAPI("http://localhost/siiaServices/apis/getFechasReservadas.php",""+valueComboAreas.replace(" ","%20"));

        for (String item: value){
            cadena.append("'").append(item).append("',");
        }
        return removeLastChar(String.valueOf(cadena));
    }

    public static String removeLastChar(String s) {
        return (s == null || s.length() == 0)
                ? null
                : (s.substring(0, s.length() - 1));
    }

    /*----- GETTERS AND SETTERS  ----*/

    public String getValueComboAreas() {return valueComboAreas;}

    public void setValueComboAreas(String valueComboAreas) {this.valueComboAreas = valueComboAreas;}
    public List<SelectItem> getListaAreas() {return listaAreas;}
    public void setListaAreas(List<SelectItem> listaAreas) { this.listaAreas = listaAreas;}
    public boolean isHasDataAreas() {
        return hasDataAreas;
    }
    public void setHasDataAreas(boolean hasDataAreas) {
        this.hasDataAreas = hasDataAreas;
    }
    public CitasHelper getCitasHelper() {
        return citasHelper;
    }
    public void setCitasHelper(CitasHelper citasHelper) {
        this.citasHelper = citasHelper;
    }
    public boolean isHasDataTramites() {return hasDataTramites;}
    public void setHasDataTramites(boolean hasDataTramites) {this.hasDataTramites = hasDataTramites;}
    public String getValueComboTramites() {return valueComboTramites;}
    public void setValueComboTramites(String valueComboTramites) {this.valueComboTramites = valueComboTramites;}
    public List<SelectItem> getListaTramites() {return listaTramites;}
    public void setListaTramites(List<SelectItem> listaTramites) {this.listaTramites = listaTramites;}
    public String getStrCalendarValue() { return strCalendarValue;}
    public void setStrCalendarValue(String strCalendarValue) {this.strCalendarValue = CitasHelper.formatDate(strCalendarValue);}
    public List<String> getListHorarios() {return listHorarios;}
    public void setListHorarios(List<String> listHorarios) {this.listHorarios = listHorarios;}
    public String getFechasDisable() {return fechasDisable;}
    public void setFechasDisable(String fechasDisable) {this.fechasDisable = fechasDisable;}
}
