package mx.uatx.siia.citas;

import com.sun.istack.NotNull;
import mx.uatx.siia.citas.modelo.Tramites.business.TramitesBusiness;
import mx.uatx.siia.citas.modelo.areas.business.AreasBusiness;
import mx.uatx.siia.citas.modelo.citasBusiness.MethodsGenerics;
import mx.uatx.siia.citas.modelo.citasBusiness.CitaBusiness;
import mx.uatx.siia.citas.modelo.enums.Requisitos;
import mx.uatx.siia.citas.modelo.enums.URLs;
import mx.uatx.siia.comun.helper.VistasHelper;
import mx.uatx.siia.logeo.helper.SessionHelper;
import mx.uatx.siia.serviciosUniversitarios.dto.ResultadoTO;
import mx.uatx.siia.serviciosUniversitarios.enums.SeveridadMensajeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @author Alberto Noche Rosas
 * @date 02/08/2022
 * @apiNote Bean para el manejo del frontend del módulo citas.
 */

@ManagedBean(name = "citabean")
@ViewScoped
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
    private boolean hasDataLink = false;
    private boolean isCitaAgendada = false;
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
    @ManagedProperty("#{citaBusiness}")
    private CitaBusiness citaBusiness;
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


        System.out.println("[VALUE] de Fechas Reservadas => "+getStrFechasDisableCalendar());

        hasDataTramites = res.isBlnValido();
        setStrLocalArea(listAreas.get(Integer.parseInt(getStrCurrentArea())-1).getLabel());

    }
    public void listenerPostTramites(){
        System.out.println("[VALUE] de Tramites => "+getStrCurrentTramite());

        for (SelectItem list : listTramites){
            if ( list.getValue() == strCurrentTramite )
                setStrLocalTramite(list.getLabel());
        }
        hasDataLink = true;
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
        ResultadoTO resultadoH = areasBusiness.obtenerHorariosFromDB(URLs.HorariosReservados.getValor(), MethodsGenerics.formatDate(getStrCurrentCalendar()), getStrCurrentArea());
        List<String> horas = (List<String>) resultadoH.getObjeto();
        List<String> listHorarios = MethodsGenerics.generarHorarios(8,13, 25, horas);
        System.out.println("|----- New list horarios =>"+listHorarios);

        setListHorariosShow(listHorarios);

        final ResultadoTO res = new ResultadoTO();
        if (resultadoH.isBlnValido() && !horas.isEmpty())
        {
            res.agregarMensaje(SeveridadMensajeEnum.INFO, "comun.msj.citas.fechas.ok");
        }
        else{
            res.agregarMensaje(SeveridadMensajeEnum.ALERTA, "comun.msj.citas.fechas.loaderror");
        }
        vHelp.pintarMensajes(msj, res);


    }

    public String fechaFormatCita(){

        if (getStrCurrentCalendar() != null){
            strLocalFecha = getStrCurrentCalendar();
            return MethodsGenerics.formatDate(getStrCurrentCalendar());
        } else
            return "Vació";
    }

    public String getLink(){
        String link = "";
        Requisitos[] lista = Requisitos.values();

        for (Requisitos requisitos : lista) {
            String[] data = requisitos.getUrl();
            if (strCurrentTramite != null){
                if (strCurrentTramite.equals(data[1])){
                    link=data[0];
                }
            }
        }

        return link;
    }

    public void agendarCita(){
        String[] strindate = strLocalFecha.split(" ");
        if (strindate[0].equals("Sat") || strindate[0].equals("Sun")){
            try {
                final ResultadoTO res = new ResultadoTO();
                res.agregarMensaje(SeveridadMensajeEnum.ALERTA, "comun.msj.citas.daysnotfound");
                vHelp.pintarMensajes(msj, res);
            }catch (Exception e){
                System.out.println("-- EXCEPTION IN MSJ");
                System.out.println(e);
            }
        }else{
            Map<String, String> valores = new HashMap<>();

            valores.put("matricula",strLocalMatricula);
            valores.put("idtramite",strCurrentTramite);
            valores.put("idarea",strCurrentArea);
            valores.put("descripcion",strMotivoCita);
            valores.put("fecha",MethodsGenerics.formatDate(strLocalFecha));
            valores.put("hora",strCurrentHora);


            System.out.println("PUT [ Save Cita ] WITH -> "+valores);

            ResultadoTO resultado = citaBusiness.agendarCita(valores, URLs.AgendarCita.getValor());
            int codeResponse = (int) resultado.getObjeto();

            /**
             * @see  todo Metodo provicional hasta que tenga la implementacion correcta de los PDFs
             */
            HttpServletResponse res = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
            HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            String url = "http://localhost/siiaServices/reportes/generarPDF.php?id="+strLocalNombreUser+","+strLocalMatricula;
            try {
                res.getWriter().println("<script>open('" + url + "','_blank', 'location=yes,height=600,width=800,scrollbars=yes,status=yes');</script>");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            FacesContext.getCurrentInstance().responseComplete();

            if (codeResponse == 200){
                System.out.println("|------------------ CODE RESPONSE : "+codeResponse);
                isCitaAgendada = true;
                pintarMensajeCitaAgendada();
                vHelp.redireccionar("index.uat");
            }
        }

        System.out.println(
                        strCurrentArea+'\n'+
                        strCurrentTramite+'\n'+
                        strLocalMatricula+'\n'+
                        strCurrentHora+'\n'+
                        MethodsGenerics.formatDate(strLocalFecha)+'\n'+
                        strLocalNombreUser
        );
    }

    public void pintarMensajeCitaAgendada(){
        if (isCitaAgendada) {
            System.out.println("|-------------- Cita agendada --------------------|");
            final ResultadoTO res = new ResultadoTO();
            res.agregarMensaje(SeveridadMensajeEnum.INFO, "comun.msj.citas.citas.ok");
            vHelp.pintarMensajes(msj, res);
        }
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

    public CitaBusiness getCitaBusiness() {
        return citaBusiness;
    }

    public void setCitaBusiness(CitaBusiness citaBusiness) {
        this.citaBusiness = citaBusiness;
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

    public boolean isHasDataLink() {
        return hasDataLink;
    }

    public void setHasDataLink(boolean hasDataLink) {
        this.hasDataLink = hasDataLink;
    }

    public boolean isCitaAgendada() {
        return isCitaAgendada;
    }

    public void setCitaAgendada(boolean citaAgendada) {
        isCitaAgendada = citaAgendada;
    }

    public void setStrLocalDescripcion(String strLocalDescripcion) {
        this.strLocalDescripcion = strLocalDescripcion;
    }
}
