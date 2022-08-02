package mx.uatx.siia.citas;

import com.sun.istack.NotNull;
import mx.uatx.siia.citas.modelo.Tramites.business.TramitesBusiness;
import mx.uatx.siia.citas.modelo.areas.business.AreasBusiness;
import mx.uatx.siia.citas.modelo.citasBusiness.MethodsGenerics;
import mx.uatx.siia.citas.modelo.enums.Requisitos;
import mx.uatx.siia.citas.modelo.enums.URLs;
import mx.uatx.siia.comun.helper.VistasHelper;
import mx.uatx.siia.serviciosUniversitarios.dto.ResultadoTO;
import mx.uatx.siia.serviciosUniversitarios.enums.SeveridadMensajeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author Alberto Noche Rosas
 * @apiNote Bean para el manejo del frontend del módulo citas.
 */

@ManagedBean(name = "citabean")
@SessionScoped
public class CitaController implements Serializable {

    /**
     * SerialVersion
     */
    private static final long serialVersionUID = -8116508067661315434L;

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * @LocalAtributtes
     */
    private List<SelectItem> listAreas;
    private List<SelectItem> listTramites;
    private List<String> ListHorariosShow;
    private boolean hasDataAreas = false;
    private boolean hasDataTramites = false;
    @NotNull
    private String strFechasDisableCalendar = null;
    @NotNull
    private String strCurrentArea = null;
    @NotNull
    private String strCurrentTramite = null;
    @NotNull
    private String strCurrentCalendar = null;
    @NotNull
    private String strCurrentHora = null;
    @NotNull
    private String strMotivoCita = null;

    private String strLocalNombreUser;
    private String strLocalMatricula;
    private String strLocalArea;
    private String strLocalTramite;
    private String strLocalFecha;
    private String strLocalHorario;
    private String strLocalDescripcion;

    /**
     * @BusinessProperties
     */

    @ManagedProperty("#{areasBusiness}")
    private AreasBusiness areasBusiness;

    @ManagedProperty("#{tramitesBusiness}")
    private TramitesBusiness tramitesBusiness;
    @ManagedProperty("#{msj}")
    private ResourceBundle msj;
    private final VistasHelper vHelp = new VistasHelper();


    /**
     * @Methods Locales llamadas por la IU
     */


    /**
     * @Listeners para el evento de CHANGE del SelectOneMenu de la IU
     */
    public void listenerPostAreas(){
        /**
         * @return => List<SelectItem> with Object().values[id,nombre]
         */
        ResultadoTO res = tramitesBusiness.obtenerTramites(URLs.Tramites.getValor(), getStrCurrentArea());
        setListTramites((List<SelectItem>) res.getObjeto());

        /**
         * @return => List<String> with Dates saved in the DATABASE
         * Servicio para obtener horarios de la db para quitarlos de la lista.
         * Servicio es para obtener las fechas desactivadas en el calendar element BootFaces.
         */

        ResultadoTO resultadoF = areasBusiness.obtenerFechasFromDB(URLs.FechasReservadas.getValor(), getStrCurrentArea());
        setStrFechasDisableCalendar((MethodsGenerics.formattingStringFechasCalendar((List<String>) resultadoF.getObjeto())));

        ResultadoTO resultadoH = areasBusiness.obtenerHorariosFromDB(URLs.HorariosReservados.getValor(), getStrCurrentCalendar(), getStrCurrentArea());
        String cadena = (String) resultadoH.getObjeto();
        List<String> horas = Arrays.asList(cadena.split(","));
        List<String> listHorarios = MethodsGenerics.generarHorarios(8,13, 25, horas);
        setListHorariosShow(listHorarios);

        System.out.println("[VALUE] de Fechas Reservadas => "+getStrFechasDisableCalendar());
        System.out.println("[VALUE] de Horarios Reservados => "+getListHorariosShow());

        hasDataTramites = res.isBlnValido();
        setStrLocalArea(listAreas.get(Integer.parseInt(getStrCurrentArea())-1).getLabel());

    }
    public void listenerPostTramites(){
        System.out.println("[VALUE] de Tramites => "+getStrCurrentTramite());

        for (SelectItem list : listTramites){
            if ( list.getValue() == strCurrentTramite )
                setStrLocalTramite(list.getLabel());
        }
    }

    public void renderDataAlumno(String name, String matricula){

        this.strLocalNombreUser = name;
        this.strLocalMatricula = matricula;

        setListAreas(returnAreasList());

        final ResultadoTO res = new ResultadoTO();
        res.agregarMensaje(SeveridadMensajeEnum.INFO, "comun.msj.citas.areas.succesful");
        vHelp.pintarMensajes(msj, res);

        hasDataAreas = true;
    }

    /**
     *
     * @return List => SelectItem para el elemento SelectOneMenu de Bootfaces
     */
    public List<SelectItem> returnAreasList(){
        ResultadoTO resultado = areasBusiness.obtenerAreas(URLs.Areas.getValor());
        setListAreas((List<SelectItem>) resultado.getObjeto());
        return getListAreas();
    }

    /**
     * @return List => SelectItem para el elemento SelectOneMenu de Bootfaces
     */
    public List<SelectItem> returnTramitesList() {
        return getListTramites();
    }

    /**
     * @return {List} => Strings con los horarios finales y disponibles.
     */
    public List<String> returnHorariosList(){
        return getListHorariosShow();
    }

    /**
     * @apiNote  Function que comparar los horarios reservados con el horario solicitado por usuario;
     */
    public void ComprobarHorario(){
        System.out.println("Comprobar Horario");
    }

    public String getLink(){

        /*
         * TODO CREAR un servicio para obtener el url del los requisitos del tramite.
         */
        Requisitos[] lista = Requisitos.values();

        for (Requisitos requisitos : lista) {
            System.out.println(requisitos.toString());
        }

        return "google.com";
    }

    /**
     * @GettersAndSetters
     */
    public List<SelectItem> getListAreas() {
        return listAreas;
    }
    public void setListAreas(List<SelectItem> listAreas) {
        this.listAreas = listAreas;
    }

    public AreasBusiness getAreasBusiness() {
        return areasBusiness;
    }

    public void setAreasBusiness(AreasBusiness areasBusiness) {
        this.areasBusiness = areasBusiness;
    }

    public ResourceBundle getMsj() {
        return msj;
    }

    public void setMsj(ResourceBundle msj) {
        this.msj = msj;
    }

    public boolean isHasDataAreas() {
        return hasDataAreas;
    }

    public void setHasDataAreas(boolean hasDataAreas) {
        this.hasDataAreas = hasDataAreas;
    }

    public String getStrCurrentArea() {
        return strCurrentArea;
    }

    public void setStrCurrentArea(String strCurrentArea) {
        this.strCurrentArea = strCurrentArea;
    }

    public boolean isHasDataTramites() {
        return hasDataTramites;
    }

    public void setHasDataTramites(boolean hasDataTramites) {
        this.hasDataTramites = hasDataTramites;
    }

    public String getStrCurrentTramite() {
        return strCurrentTramite;
    }

    public void setStrCurrentTramite(String strCurrentTramite) {
        this.strCurrentTramite = strCurrentTramite;
    }

    public List<SelectItem> getListTramites() {
        return listTramites;
    }

    public void setListTramites(List<SelectItem> listTramites) {
        this.listTramites = listTramites;
    }

    public TramitesBusiness getTramitesBusiness() {
        return tramitesBusiness;
    }

    public void setTramitesBusiness(TramitesBusiness tramitesBusiness) {
        this.tramitesBusiness = tramitesBusiness;
    }

    public List<String> getListHorariosShow() {
        return ListHorariosShow;
    }

    public String getStrFechasDisableCalendar() {
        return strFechasDisableCalendar;
    }

    public void setStrFechasDisableCalendar(String strFechasDisableCalendar) {
        this.strFechasDisableCalendar = strFechasDisableCalendar;
    }

    public void setListHorariosShow(List<String> listHorariosShow) {
        this.ListHorariosShow = listHorariosShow;
    }

    public String getStrCurrentCalendar() {
        return strCurrentCalendar;
    }

    public void setStrCurrentCalendar(String strCurrentCalendar) {
        this.strCurrentCalendar = strCurrentCalendar;
    }

    public String getStrCurrentHora() {return strCurrentHora;}

    public String getStrMotivoCita() {
        return strMotivoCita;
    }

    public void setStrMotivoCita(String strMotivoCita) {
        this.strMotivoCita = strMotivoCita;
    }

    public void setStrCurrentHora(String strCurrentHora) {this.strCurrentHora = strCurrentHora;}

    public String getStrLocalNombreUser() {
        return strLocalNombreUser;
    }

    public void setStrLocalNombreUser(String strLocalNombreUser) {
        this.strLocalNombreUser = strLocalNombreUser;
    }

    public String getStrLocalMatricula() {
        return strLocalMatricula;
    }

    public void setStrLocalMatricula(String strLocalMatricula) {
        this.strLocalMatricula = strLocalMatricula;
    }

    public String getStrLocalArea() {
        return strLocalArea;
    }

    public void setStrLocalArea(String strLocalArea) {
        this.strLocalArea = strLocalArea;
    }

    public String getStrLocalTramite() {
        return strLocalTramite;
    }

    public void setStrLocalTramite(String strLocalTramite) {
        this.strLocalTramite = strLocalTramite;
    }

    public String getStrLocalFecha() {
        return strLocalFecha;
    }

    public void setStrLocalFecha(String strLocalFecha) {
        this.strLocalFecha = strLocalFecha;
    }

    public String getStrLocalHorario() {
        return strLocalHorario;
    }

    public void setStrLocalHorario(String strLocalHorario) {
        this.strLocalHorario = strLocalHorario;
    }

    public String getStrLocalDescripcion() {
        return strLocalDescripcion;
    }

    public void setStrLocalDescripcion(String strLocalDescripcion) {
        this.strLocalDescripcion = strLocalDescripcion;
    }
}
