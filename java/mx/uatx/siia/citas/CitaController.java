package mx.uatx.siia.citas;

import com.sun.istack.NotNull;
import mx.uatx.siia.citas.modelo.Tramites.business.TramitesBusiness;
import mx.uatx.siia.citas.modelo.areas.business.AreasBusiness;
import mx.uatx.siia.citas.modelo.citasBusiness.MethodsGenerics;
import mx.uatx.siia.citas.modelo.enums.URLs;
import mx.uatx.siia.comun.helper.VistasHelper;
import mx.uatx.siia.serviciosUniversitarios.dto.AreasTO;
import mx.uatx.siia.serviciosUniversitarios.dto.ResultadoTO;
import mx.uatx.siia.serviciosUniversitarios.dto.TramitesTO;
import mx.uatx.siia.serviciosUniversitarios.enums.SeveridadMensajeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
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
    private String strFechasDisableCalendar;
    @NotNull
    private String strCurrentArea = null;
    @NotNull
    private String strCurrentTramite = null;
    @NotNull
    private String strCurrentCalendar = null;

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
         */
        setListHorariosShow((List<String>) areasBusiness.obtenerHorariosFromDB(URLs.FechasReservadas.getValor(), getStrCurrentArea()));

        List<String> listHorarios = MethodsGenerics.generarHorarios(8,13, 25, getListHorariosShow());
        setStrFechasDisableCalendar(MethodsGenerics.formattingStringFechasCalendar(listHorarios));

        System.out.println("[VALUE] de Areas => "+getStrCurrentArea());
        System.out.println("[VALUE] de Horarios Reservados => "+getStrFechasDisableCalendar());

        hasDataTramites = res.isBlnValido();
    }
    public void listenerPostTramites(){
        System.out.println("[VALUE] de Tramites => "+getStrCurrentTramite());
    }

    public void renderDataAlumno(){
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
     * @apiNote  Function que comparar los horarios reservados con el horario solicitado por usuario;
     */
    public void ComprobarHorario(){
        /**
         * TODO: Implementar la logica para reservar temporalmente la reserva del usuario.
         */
        System.out.println("Comprobar Horario");
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
}
