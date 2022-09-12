package mx.uatx.siia.citas;

import com.sun.istack.NotNull;
import mx.uatx.siia.citas.modelo.Tramites.business.TramitesBusiness;
import mx.uatx.siia.citas.modelo.areas.business.AreasBusiness;
import mx.uatx.siia.citas.modelo.areas.business.SiPaAreasConfiguraciones;
import mx.uatx.siia.citas.modelo.citasBusiness.CitaBusiness;
import mx.uatx.siia.citas.modelo.citasBusiness.CitaInstance;
import mx.uatx.siia.citas.modelo.citasBusiness.MethodsGenerics;
import mx.uatx.siia.citas.modelo.enums.Requisitos;
import mx.uatx.siia.citas.modelo.enums.URLs;
import mx.uatx.siia.comun.helper.VistasHelper;
import mx.uatx.siia.reportes.FieldsNuevaCita;
import mx.uatx.siia.serviciosUniversitarios.dto.ResultadoTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.beans.beancontext.BeanContext;
import java.io.Serializable;
import java.util.*;

/**
 * @author Alberto Noche Rosas
 * @date 02/08/2022
 * @apiNote Bean para el manejo del frontend del módulo citas.
 */

@ViewScoped
@ManagedBean(name = "citabean")
public class CitaController extends CitaBusiness implements Serializable {

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
    private boolean hasHorarios = false;
    private boolean isCitaAgendada = false;
    private boolean hasCalendar = false;
    private boolean isForHidden = false;
    private boolean btnLookNewCita = false;
    public boolean flagHorarioReservado = false;
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
    private String tomaxDate;
    private String tominDate;

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
    final private VistasHelper vHelp = new VistasHelper();
    /**
     * @Listeners para el evento de CHANGE del SelectOneMenu de la IU
     */
    public void listenerPostAreas(){
        /**
         * @return  => List<SelectItem> with Object().values[id,nombre]
         */
        ResultadoTO res = tramitesBusiness.obtenerTramites(URLs.Tramites.getValor(), getStrCurrentArea());
        setListTramites((List<SelectItem>) res.getObjeto());

        hasDataTramites = res.isBlnValido();
        setStrLocalArea(listAreas.get(Integer.parseInt(getStrCurrentArea())-1).getLabel());

        inicializarSettings();
    }

    public void listenerPostTramites() {
        System.out.println("[VALUE] de Tramites => " + getStrCurrentTramite());

        for (SelectItem list : listTramites) {
            if (list.getValue() == strCurrentTramite)
                setStrLocalTramite(list.getLabel());
        }

        ResultadoTO resultadoF = areasBusiness.obtenerFechasFromDB(URLs.FechasReservadas.getValor(), getStrCurrentArea());
        /**
         * @apiNote Se crea el string con las fechas desactivadas.
         */
        setStrFechasDisableCalendar((
                MethodsGenerics.formattingStringFechasCalendar(
                        (List<String>) resultadoF.getObjeto()
                )
        ));

        tomaxDate = MethodsGenerics.lessOneDay((long) 60, false);
        tominDate = MethodsGenerics.lessOneDay((long) 30, true);

        hasHorarios = true;
        hasCalendar = true;
    }


    public void listenerPostHorario(){
        System.out.println("--- Reservando hora => ["+strCurrentHora+"]");

        if (!flagHorarioReservado){
            HashMap<String, Object> datacollect = new HashMap<>();
            datacollect.put("idarea", strCurrentArea);
            datacollect.put("user", strLocalMatricula);
            datacollect.put("fecha", strCurrentCalendar);
            datacollect.put("hora", strCurrentHora);

            ResultadoTO resultado = citaBusiness.reservarHorario(datacollect);
            flagHorarioReservado = (boolean) resultado.getObjeto();

            if (flagHorarioReservado) {
                mostrarNotification(FacesMessage.SEVERITY_INFO, "INF:", "Hora reservada tienes 10 min");
                flagHorarioReservado=false;
            } else mostrarNotification(FacesMessage.SEVERITY_FATAL, "ERROR:", "No se pudo reservar tu horarios");
        }else{
            mostrarNotification(FacesMessage.SEVERITY_INFO, "INF:", "Ya tienes un horario reservado, si quieres otra fecha reinicia el formulario");
        }


    }

    public void inicializarSettings(){
        CitaInstance settings = CitaInstance.getInstance();
        ResultadoTO resultado = areasBusiness.obtenerConfiguracionArea(strCurrentArea);
        List<SiPaAreasConfiguraciones> lista = (List<SiPaAreasConfiguraciones>) resultado.getObjeto();
        settings.asignar(lista);
    }

    public void renderDataAlumno(String name, String matricula){

        if (matricula != null){

            this.strLocalNombreUser = name;
            this.strLocalMatricula = matricula;
            hasDataAreas = true;
        }
    }

    public void generarPDF(){
        System.out.println("--- GENERANDO PDF ---");
        try {
            final String foliocita = "CIA7B";
            final String datePrint = MethodsGenerics.getCurrentDate();

            ArrayList<FieldsNuevaCita> datos = new ArrayList<>();
            datos.add(new FieldsNuevaCita(
                    getStrLocalNombreUser(),
                    MethodsGenerics.formatDate(strLocalFecha),
                    strLocalTramite,
                    "Ingenieria en computación - FCBIyT",
                    strLocalMatricula,
                    getStrCurrentHora(),
                    strMotivoCita,
                    foliocita,
                    getLink(),
                    datePrint
            ));

            System.out.println(getStrLocalNombreUser()+strLocalFecha+fechaFormatCita()+strLocalMatricula+getStrCurrentHora()+getStrLocalDescripcion()+foliocita+getLink()+datePrint);

            String sourFileName = "Comprobante";
            String rutaFiles = "resources/reportes/citas";
            HashMap<String, Object> parameters = new HashMap<>();

            parameters.put("nombreuser", "nombreuser");
            parameters.put("fechacita", "fechacita");
            parameters.put("tramite", "tramite");
            parameters.put("programaedu", "programaedu");
            parameters.put("matricula","matricula");
            parameters.put("hora", "hora");
            parameters.put("descripcion", "descripcion");
            parameters.put("foliocita", "foliocita");
            parameters.put("link", "link");
            parameters.put("fechaprint", "fechaprint");
            parameters.put("qrvalue", getStrLocalNombreUser()+","+strLocalMatricula);

            vHelp.llenarYObtenerBytesReporteJasperPDF(rutaFiles, sourFileName, datos, parameters);

        } catch (Exception e) {
            mostrarNotification(FacesMessage.SEVERITY_WARN, "ERROR:", "no se pudo generar el pdf");
            throw new RuntimeException("It's not possible to generate the pdf report.", e);
        }
    }

    /**
     * @return List => SelectItem para el elemento SelectOneMenu de Bootfaces
     */
    public List<SelectItem> returnAreasList(){

        if (listAreas == null){
            ResultadoTO resultado = areasBusiness.obtenerAreas(URLs.Areas.getValor());
            setListAreas((List<SelectItem>) resultado.getObjeto());
        }
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
        System.out.println("Comprobar Horario para la fecha => [ "+ getStrCurrentCalendar() +" ]");
        ResultadoTO resultadoH = areasBusiness.obtenerHorariosFromDB(URLs.HorariosReservados.getValor(), MethodsGenerics.formatDate(getStrCurrentCalendar()), getStrCurrentArea());
        List<String> horas = (List<String>) resultadoH.getObjeto();

        CitaInstance instance = CitaInstance.getInstance();

        List<String> listHorarios = MethodsGenerics.generarHorarios(
                Integer.parseInt(instance.configuraciones.get(0).getHoraServicioInicio().replace(':','0'))/1000,
                Integer.parseInt(instance.configuraciones.get(0).getHoraServicioFin().replace(':','0'))/1000,
                Integer.parseInt(instance.configuraciones.get(0).getDuracionCitas()),
                horas);

        System.out.println("|----- New list horarios =>"+listHorarios);

        if (resultadoH.isBlnValido() && !horas.isEmpty()) {
            mostrarNotification(FacesMessage.SEVERITY_INFO, "INF:", "Fechas temporal reservada correctamente.");
        } else
           mostrarNotification(FacesMessage.SEVERITY_WARN, "WARN:", "No se pudo obtener los horarios para esta fecha intenta con otra");
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

    public void agendarCitabtn(){

        ResultadoTO resultado = citaBusiness.numeroCitas(URLs.Commun.getValor(), strLocalMatricula, strCurrentTramite);

        if (resultado.getObjeto().equals("0")){
            String[] strindate = strLocalFecha.split(" ");
            if (strindate[0].equals("Sat") || strindate[0].equals("Sun"))
                mostrarNotification(FacesMessage.SEVERITY_WARN, "WARN:", "Lo sentimos los sabados y domingos no damos servicio intenta otro dia.");
            else{
                Map<String, Object> valores = new HashMap<>();

                valores.put("matricula",strLocalMatricula);
                valores.put("idtramite",strCurrentTramite);
                valores.put("idarea",strCurrentArea);
                valores.put("descripcion",strMotivoCita);
                valores.put("fecha",MethodsGenerics.formatDate(strLocalFecha));
                valores.put("hora",strCurrentHora);

                System.out.println("PUT [ Save Cita ] WITH -> "+valores);

                ResultadoTO resultadonew = citaBusiness.saveDataDB(valores, URLs.InsertData.getValor());
                Map<String, String> response = (Map<String, String>) resultadonew.getObjeto();

                if (response.get("responsecode").equals("OK") && response.get("codefromservice").equals("1")){
                    System.out.println("|------------------ CODE RESPONSE : "+response);
                    isCitaAgendada = true;
                    mostrarNotification(FacesMessage.SEVERITY_INFO, "INF:", "Tu cita se agendo correctamente, espera el comprobante");

                }else{
                    mostrarNotification(FacesMessage.SEVERITY_ERROR, "ERROR:", "No se pudo agendar tu cita");
                }
            }
        } else {
            isForHidden = true;
            mostrarNotification(FacesMessage.SEVERITY_FATAL, "ERROR:", " Ups! Parece que ya tienes una cita.");
        }
    }

    public void mostrarNotification(FacesMessage.Severity severity, String title, String msg){
        FacesContext.getCurrentInstance().
                addMessage(null, new FacesMessage(severity, title, msg));
    }

    public void reload(){
        vHelp.redireccionar("/vistas/citas/cita.uat");
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

    public boolean isHasCalendar() {
        return hasCalendar;
    }

    public void setHasCalendar(boolean hasCalendar) {
        this.hasCalendar = hasCalendar;
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

    public boolean isHasHorarios() {
        return hasHorarios;
    }

    public void setHasHorarios(boolean hasHorarios) {
        this.hasHorarios = hasHorarios;
    }

    public boolean isCitaAgendada() {
        return isCitaAgendada;
    }

    public void setCitaAgendada(boolean citaAgendada) {
        isCitaAgendada = citaAgendada;
    }

    public boolean isForHidden() {
        return isForHidden;
    }

    public void setForHidden(boolean forHidden) {
        isForHidden = forHidden;
    }

    public void setStrLocalDescripcion(String strLocalDescripcion) {
        this.strLocalDescripcion = strLocalDescripcion;
    }

    public String getTomaxDate() {
        return tomaxDate;
    }
    public void setTomaxDate(String tomaxDate) {
        this.tomaxDate = tomaxDate;
    }

    public String getTominDate() {
        return tominDate;
    }

    public void setTominDate(String tominDate) {
        this.tominDate = tominDate;
    }

    public boolean isBtnLookNewCita() {
        return btnLookNewCita;
    }

    public void setBtnLookNewCita(boolean btnLookNewCita) {
        this.btnLookNewCita = btnLookNewCita;
    }
}
